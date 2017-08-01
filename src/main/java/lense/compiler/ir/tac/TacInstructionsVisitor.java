package lense.compiler.ir.tac;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BreakNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.LiteralCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PreExpression;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.StringValue;
import lense.compiler.ast.SwitchNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.ir.CallInstruction;
import lense.compiler.ir.CreateNewInstruction;
import lense.compiler.ir.Operation;
import lense.compiler.ir.ReadFieldInstruction;
import lense.compiler.ir.WriteFieldInstruction;
import lense.compiler.ir.stack.ArithmeticOperate;
import lense.compiler.ir.stack.InvokeConstructor;
import lense.compiler.ir.stack.InvokeVirtual;
import lense.compiler.ir.stack.LoadFromVariable;
import lense.compiler.ir.stack.PushNumberValue;
import lense.compiler.ir.stack.PushStringValue;
import lense.compiler.ir.stack.ReadFieldFromObject;
import lense.compiler.ir.stack.StackInstructionList;
import lense.compiler.ir.stack.StackInstructionListOptimizer;
import lense.compiler.ir.stack.StackVariableMapping;
import lense.compiler.ir.stack.StoreToVariable;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class TacInstructionsVisitor implements Visitor<AstNode> {	

	
	private TacInstructionListOptimizer tacOptimizer = new TacInstructionListOptimizer();
	private StackInstructionListOptimizer stackOptimizer = new StackInstructionListOptimizer();
	private TacInstructionList currentList;
	private int nextLabel = 1;
	private int tempIndex =0;
	private int continueAt;
	private int breakAt;
	
	private final Map<String,FieldDeclarationNode> fields = new HashMap<String,FieldDeclarationNode>();
	
	private final SemanticContext semanticContext;
	private final TypeDefinition classType;

	private final TypeDefinition BOOLEAN;
	public TacInstructionsVisitor(SemanticContext sc, TypeDefinition classType){
		this.semanticContext = sc;
		this.classType = classType;
		BOOLEAN = sc.typeForName("lense.core.lang.Boolean", 0);
	}


	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {

	}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {

		if (node instanceof ClassTypeNode) {
			fields.clear();
		} else if (node instanceof MethodDeclarationNode || node instanceof ConstructorDeclarationNode){
			TacInstructionList list = new TacInstructionList();
			tempIndex = 0;
			node.setProperty("instructionsList", list);
			currentList = list;
		} else if (node instanceof FieldDeclarationNode){
			FieldDeclarationNode n = (FieldDeclarationNode)node;
			fields.put(n.getName(), n);
			
			return VisitorNext.Siblings;
			
		} else if (node instanceof DecisionNode){
			DecisionNode n = (DecisionNode) node;

			TreeTransverser.transverse(n.getCondition(), this);

			int thisLabel =  nextLabel++;
			int truePathLabel =  nextLabel++;
			int falsePathLabel =  nextLabel++;
			int endPathLabel =  nextLabel++;

			emit(new IfJump(n.getCondition().getProperty("tempVal", Operand.class).get(), false, thisLabel,falsePathLabel));
			emit(new Nop(truePathLabel));
			TreeTransverser.transverse(n.getTrueBlock(), this);
			emit(new GoTo(endPathLabel));
			emit(new Nop(falsePathLabel));
			TreeTransverser.transverse(n.getFalseBlock(), this);
			emit(new GoTo(endPathLabel));
			emit(new Nop(endPathLabel));

			return VisitorNext.Siblings;
		} else if (node instanceof WhileNode){
			WhileNode n = (WhileNode) node;

			int startLabel =  nextLabel++;
			int endLabel =  nextLabel++;
			
			this.continueAt = startLabel;
			this.breakAt = endLabel;
			
			emit(new Nop(startLabel));
			TreeTransverser.transverse(n.getCondition(), this);

			emit(new IfJump(n.getCondition().getProperty("tempVal", Operand.class).get(), false, 0,endLabel));
			
			TreeTransverser.transverse(n.getStatements(), this);
			emit(new GoTo(startLabel));
			emit(new Nop(endLabel));

			this.continueAt = 0;
			this.breakAt = 0;
			
			return VisitorNext.Siblings;
		}else if (node instanceof ForEachNode){
			ForEachNode n = (ForEachNode) node;

			int startLabel =  nextLabel++;
			int endLabel =  nextLabel++;
			
			this.continueAt = startLabel;
			this.breakAt = endLabel;
			
			TreeTransverser.transverse(n.getContainer(), this);
			
			Operand container = n.getContainer().getProperty("tempVal", Operand.class).get();
		    TypeDefinition containedType = extractType(n.getContainer().getTypeVariable().getGenericParameters().get(0).getUpperBound());
			
		    TypeDefinition iterableType = LenseTypeSystem.Iterable();  // semanticContext.typeForName("lense.core.lang.Iterable", 1);
		    TypeDefinition iteratorType =  LenseTypeSystem.Iterator(); //semanticContext.typeForName("lense.core.lang.Iterator", 1);;
		    TypeDefinition booleanType =  LenseTypeSystem.Boolean();   //semanticContext.typeForName("lense.core.lang.Boolean", 0);;
		    
			Operand it = new TemporaryVariable(tempIndex++,iteratorType);
			emit(new PrepareParameter(container));
			emit(new Assign(it,new CallInstruction("iterator", iterableType, iteratorType)));

			emit(new Nop(startLabel));
			emit(new PrepareParameter(it));
			Operand condition = new TemporaryVariable(tempIndex++,booleanType);
			emit(new Assign(condition,new CallInstruction("hasNext", iteratorType, booleanType)));
		
			emit(new IfJump(condition, false, 0,endLabel));
			Operand next = new LocalVariable(n.getVariableDeclarationNode().getName(), containedType);
			emit(new PrepareParameter(it));
			emit(new Assign(next,new CallInstruction("next",iteratorType, containedType)));

			TreeTransverser.transverse(n.getBlock(), this);
			emit(new GoTo(startLabel));
			emit(new Nop(endLabel));

			this.continueAt = 0;
			this.breakAt = 0;
			
			return VisitorNext.Siblings;
		} else if (node instanceof SwitchNode){
			SwitchNode n = (SwitchNode) node;


			TreeTransverser.transverse(n.getCandidate(), this);

			Operand value = n.getCandidate().getProperty("tempVal", Operand.class).get();
			
			int endLabel = nextLabel++;
			
			Deque<Integer> labels = new LinkedList<>();
			
			labels.add(nextLabel++);
			labels.add(nextLabel++);
			
			for (AstNode op : n.getOptions().getChildren()){
				SwitchOption option = (SwitchOption)op;
				int currentSwitchLabel = labels.pop();
				int nextSwitchLabel = labels.peek();
				
				// add future label for next iteration
				labels.add(nextLabel++);
				
				emit(new Nop(currentSwitchLabel));
				
				if (option.isDefault()){
					
					TreeTransverser.transverse(option.getActions(), this);
					
				} else {
					TreeTransverser.transverse(option.getValue(), this);

					Operand exp = option.getValue().getProperty("tempVal", Operand.class).get();
					TemporaryVariable target = new TemporaryVariable(tempIndex++, BOOLEAN);
					
					emit(new AssignAfterBinaryOperation(target, value, Operation.IS_EQUAL_TO, exp));
					emit(new IfJump(target, false, 0,nextSwitchLabel));
					
					TreeTransverser.transverse(option.getActions(), this);
					
				}
				emit(new GoTo(endLabel));
			}
			emit(new Nop(endLabel));
			
			return VisitorNext.Siblings;
		}

		return VisitorNext.Children;
	}



	private TypeDefinition extractType(TypeVariable type) {
		return type.getTypeDefinition();
	}


	@Override
	public void visitAfterChildren(AstNode node) {

		if (node instanceof MethodDeclarationNode){
			MethodDeclarationNode n = (MethodDeclarationNode)node;
			String name = ((MethodDeclarationNode)node).getName();
			
			TacInstructionList list = n.getTacInstructionsList().get();

			list = tacOptimizer.optimize(list);

			n.setTacInstructionsList(list);
			
			
			System.out.println("METHOD " + name + "{");
			System.out.print(list.toString());
			System.out.println("}");
			System.out.println();

			StackVariableMapping  variablesMapping = new StackVariableMapping();
			if (!n.isStatic()){
				variablesMapping.putIncrement("this");
			}

			for (AstNode a : n.getParameters().getChildren()){
				FormalParameterNode fp = (FormalParameterNode)a;
				variablesMapping.putIncrement(fp.getName());
			}

			StackInstructionList stack = toStack(list,variablesMapping);
			
			System.out.println("METHOD " + name + "{");
			System.out.print(stack.toString());
			System.out.println("}");
			System.out.println();
			
			stack = stackOptimizer.optimize(stack);
			
			n.setStackInstructionsList(stack);

			System.out.println("METHOD " + name + "{");
			System.out.print(stack.toString());
			System.out.println("}");
			System.out.println();

			currentList = null;

		} else {
			emitNode(node);
		}

		Optional<TacInstruction> emitAfter = node.getProperty("emitAfter", TacInstruction.class);
		if (emitAfter.isPresent()){
			emit(emitAfter.get());
			node.setProperty("emitAfter", null);
		}

	}



	private Optional<Operand> emitNode(AstNode node) {
		if (node instanceof ContinueNode){

			emit(new GoTo(this.continueAt));

		} else  if (node instanceof BreakNode){

			emit(new GoTo(this.breakAt));

		} else if (node instanceof ArithmeticNode){
			ArithmeticNode n = (ArithmeticNode)node;

			Operand left = n.getLeft().getProperty("tempVal", Operand.class).get();
			Operand right = n.getRight().getProperty("tempVal", Operand.class).get();

			
			TemporaryVariable target = new TemporaryVariable(tempIndex++, extractType(n.getTypeVariable()));

			emit(new AssignAfterBinaryOperation(target, left, operationFor(n.getOperation()), right));

			n.setProperty("tempVal", target);

		} else if (node instanceof BooleanOperatorNode){
			BooleanOperatorNode n = (BooleanOperatorNode)node;

			Operand left = n.getLeft().getProperty("tempVal", Operand.class).get();
			Operand right = n.getRight().getProperty("tempVal", Operand.class).get();

			Operation op = logicOperationFor(n.getOperation());
			
			if (op == Operation.LOGICAL_SHORT_AND || op == Operation.LOGICAL_SHORT_OR){
				// TODO decision making with jumps 
				TemporaryVariable target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
				emit(new AssignAfterBinaryOperation(target, left, op, right));
				n.setProperty("tempVal", target);
			} else {
				TemporaryVariable target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
				emit(new AssignAfterBinaryOperation(target, left, op, right));
				n.setProperty("tempVal", target);
			}

			

		}else if (node instanceof AssignmentNode){
			AssignmentNode n = (AssignmentNode)node;

			Operand target = ((AstNode)n.getLeft()).getProperty("tempVal", Operand.class).get();
			Operand source = n.getRight().getProperty("tempVal", Operand.class).get();

			emit(new Assign(target, source));

			n.setProperty("tempVal", target);

		} else if (node instanceof ComparisonNode){
			ComparisonNode n = (ComparisonNode)node;

			Operand left = n.getLeft().getProperty("tempVal", Operand.class).get();
			Operand right = n.getRight().getProperty("tempVal", Operand.class).get();

			TemporaryVariable target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));

			emit(new AssignAfterBinaryOperation(target, left, comparisonFor(n.getOperation()), right));

			n.setProperty("tempVal", target);

		} else if (node instanceof VariableReadNode){
			VariableReadNode n =(VariableReadNode)node;
	
			if (n.getVariableInfo().getDeclaringNode() instanceof FieldDeclarationNode){
				n.setProperty("tempVal",  new ReadFieldInstruction(n.getName(), false, classType, extractType(n.getTypeVariable())));
			} else {
				n.setProperty("tempVal", new LocalVariable(n.getName(),extractType(n.getTypeVariable())));
			}
		} else if (node instanceof FieldOrPropertyAccessNode){
			FieldOrPropertyAccessNode n =(FieldOrPropertyAccessNode)node;

			TypeDefinition ownerType = null;
			if (n.getPrimary() == null){
				ownerType = this.classType;
				emit(new PrepareParameter(new LocalVariable("this", classType)));
			} else {
				ownerType = extractType(((TypedNode)n.getPrimary()).getTypeVariable());
				Operand access = n.getPrimary().getProperty("tempVal", Operand.class).get();
				
				Operand target = new TemporaryVariable(tempIndex++, access.getOperandType());
				emit(new Assign(target, access));
				emit(new PrepareParameter(target));
			}
			
			if (n.getKind() == FieldKind.FIELD){
				if (node.getParent() instanceof AssignmentNode){
					Operand field = new WriteFieldInstruction(n.getName(), false, ownerType,extractType(n.getTypeVariable()));
					
					n.setProperty("tempVal", field);
				} else {
					Operand field = new ReadFieldInstruction(n.getName(), false, ownerType,extractType(n.getTypeVariable()));
					Operand target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
					
					emit(new Assign(target, field));
					n.setProperty("tempVal", target);
				
				}
			} else {
				// TODO invoke get
				String name = n.getName().substring(0, 1).toUpperCase() + n.getName().substring(1);
				if (node.getParent() instanceof AssignmentNode){
					
					Operand target = new CallInstruction("set" + name, ownerType, LenseTypeSystem.Void());
					n.setProperty("tempVal", target);

				} else {
					Operand field = new CallInstruction("get" + name, ownerType,extractType(n.getTypeVariable()));
					Operand target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
					
					emit(new Assign(target, field));
					n.setProperty("tempVal", target);
				
				}
			}
			
		} else if (node instanceof VariableDeclarationNode){
			VariableDeclarationNode n =(VariableDeclarationNode)node;

			Operand right = n.getInitializer().getProperty("tempVal", Operand.class).get();

			Operand target = new LocalVariable(n.getInfo().getName(),extractType(n.getTypeVariable()));

			emit(new Assign(target, right));

			n.setProperty("tempVal", target);

		}else if (node instanceof NumericValue){

			NumericValue n =(NumericValue)node;
			Operand target = new TemporaryVariable(tempIndex++, extractType(n.getTypeVariable()));

			emit(new Assign(target, new NumericConstant(n.getValue(), extractType(n.getTypeVariable()))));

			n.setProperty("tempVal", target);
		} else if (node instanceof StringValue){

			StringValue n =(StringValue)node;
			Operand target = new TemporaryVariable(tempIndex++, extractType(n.getTypeVariable()));

			emit(new Assign(target, new StringConstant(n.getValue())));

			n.setProperty("tempVal", target);
		}else if (node instanceof ReturnNode){
			ReturnNode n =(ReturnNode)node;

			if (n.getValue() == null){
				emit(new ReturnInstruction());
			} else {
				Operand right = n.getValue().getProperty("tempVal", Operand.class).get();
				emit(new ReturnInstruction(right));
			}
		} else if (node instanceof PosExpression){ // a++
			PosExpression n =(PosExpression)node;

			Operand operand = n.getChildren().get(0).getProperty("tempVal", Operand.class).get();

			TacInstruction emitAfter = new AssignAfterUnaryOperation(operand, operationFor(n.getOperation()) , operand);

			n.getParent().setProperty("emitAfter", emitAfter);
			n.setProperty("tempVal", operand);

		} else if (node instanceof PreExpression){ // ++a
			PreExpression n =(PreExpression)node;

			Operand operand = n.getChildren().get(0).getProperty("tempVal", Operand.class).get();

			Operation op = operationFor(n.getOperation());

			if (op == Operation.DECREMENT || op == Operation.INCREMENT){
				emit(new AssignAfterUnaryOperation(operand, op, operand));

				n.setProperty("tempVal", operand);

			} else {
				Operand target = new TemporaryVariable(tempIndex++, extractType(n.getTypeVariable()));
				if (op == Operation.SUBTRACT){
					op = Operation.SYMETRIC;
				}
				emit(new AssignAfterUnaryOperation(target, op , operand));

				n.setProperty("tempVal", target);
			}
		} else if (node instanceof IndexedAccessNode){
			// access[expr]
			IndexedAccessNode n =(IndexedAccessNode)node;
//			
//			Operand access = n.getAccess().getProperty("tempVal", Operand.class).get();
//			Operand expr = n.getIndexExpression().getProperty("tempVal", Operand.class).get();
//			
//			TypeDefinition ownerType = extractType(((TypedNode)n.getAccess()).getTypeVariable());
//			
//			Operand target = new TemporaryVariable(tempIndex++, access.getOperandType());
//			emit(new Assign(target, access));
//			emit(new PrepareParameter(target));
//			
//			if (!(node.getParent() instanceof AssignmentNode)){
//
//				emit(new PrepareParameter(expr));
//				
//				target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
//				emit(new Assign(target,new CallInstruction("get", ownerType,extractType(n.getTypeVariable()))));
//
//				n.setProperty("tempVal", target);
//			} else {
//				n.setProperty("tempVal", new CallInstruction("set", ownerType,extractType(n.getTypeVariable())));
//			}
//			
			
		} else if (node instanceof MethodInvocationNode){
			MethodInvocationNode n =(MethodInvocationNode)node;

			TypeDefinition ownerType = null;
			if (n.getAccess() == null){
				ownerType = classType;
				emit(new PrepareParameter(new LocalVariable("this", classType)));
			} else {
				ownerType = extractType(((TypedNode)n.getAccess()).getTypeVariable());
				Operand access = n.getAccess().getProperty("tempVal", Operand.class).get();
				
				Operand target = new TemporaryVariable(tempIndex++, access.getOperandType());
				emit(new Assign(target, access));
				emit(new PrepareParameter(target));
			}

			for( AstNode paramNode : n.getCall().getArgumentListNode().getChildren()){
				Operand param = paramNode.getProperty("tempVal", Operand.class).get();
				emit(new PrepareParameter(param));
			}

			Operand target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
			emit(new Assign(target,new CallInstruction(n.getCall().getName(), ownerType,extractType(n.getTypeVariable()))));

			n.setProperty("tempVal", target);
		}  else if (node instanceof NewInstanceCreationNode){
			NewInstanceCreationNode n =(NewInstanceCreationNode)node;

			if ( n.getArguments() != null && n.getArguments().getChildren() != null){
				for( AstNode paramNode : n.getArguments().getChildren()){
					Operand param = paramNode.getProperty("tempVal", Operand.class).get();
					emit(new PrepareParameter(param));
				}
			}
			

			Operand target = new TemporaryVariable(tempIndex++,extractType(n.getTypeVariable()));
			emit(new Assign(target,new CreateNewInstruction(extractType(n.getTypeVariable()), n.getName(), node instanceof LiteralCreation)));

			n.setProperty("tempVal", target);

		}

		return node.getProperty("tempVal", Operand.class);
	}



	private Operation logicOperationFor(lense.compiler.ast.BooleanOperation operation) {
		switch(operation){
		case BitAnd:
			return Operation.BITWISE_AND;
		case BitOr:
			return Operation.BITWISE_OR;
		case BitXor:
			return Operation.BITWISE_XOR;
		case LogicNegate:
		case BitNegate:
			return Operation.BITWISE_NEGATE;
		case LogicShortAnd:
			return Operation.LOGICAL_SHORT_AND;
		case LogicShortOr:
			return Operation.LOGICAL_SHORT_OR;
		default:
			throw new RuntimeException();
		}
	}


	private Operation comparisonFor(
			lense.compiler.ast.ComparisonNode.Operation operation) {
		switch(operation){
		case Different:
			return Operation.IS_DIFFERENT_FROM;
		case EqualTo:
			return Operation.IS_EQUAL_TO; 
		case GreaterOrEqualTo:
			return Operation.IS_GREATER_OR_EQUAL;
		case GreaterThan:
			return Operation.IS_GREATER_THAN;
		case LessOrEqualTo:
			return Operation.IS_LESS_OR_EQUAL_TO;
		case LessThan:
			return Operation.IS_LESS_THAN;
		case ReferenceDifferent:
			return Operation.IS_REF_DIFFERENT;
		case ReferenceEquals:
			return Operation.IS_REF_EQUAL_TO;
		default:
			throw new RuntimeException();
		}
	}

	private StackInstructionList toStack(TacInstructionList list,StackVariableMapping  variablesMapping) {
		StackInstructionList stack = new StackInstructionList();


		for(TacInstruction instruction : list){
			toStack(instruction, stack, variablesMapping);
		}

		return stack;
	}

	private void toStack(TacInstruction instruction, StackInstructionList stack, StackVariableMapping  variablesMapping) {
		
		if (instruction.isLabeled()){
			stack.add(new lense.compiler.ir.stack.Label(instruction.getLabel()));
		}
		
		if (instruction instanceof Assign){
			Assign n = (Assign)instruction;

			load(n.getSource(), stack, variablesMapping);
			store(n.getTarget(), stack, variablesMapping);
		} else if (instruction instanceof AssignAfterBinaryOperation){
			AssignAfterBinaryOperation n = (AssignAfterBinaryOperation)instruction;

			load(n.getRight(), stack, variablesMapping);
			load(n.getLeft(), stack, variablesMapping);
			operate(n.getOperation(), stack);
			store(n.getTarget(), stack, variablesMapping);
		} else if (instruction instanceof AssignAfterUnaryOperation){
			AssignAfterUnaryOperation n = (AssignAfterUnaryOperation)instruction;

			load(n.getRight(), stack, variablesMapping);
			operate(n.getOperation(), stack);
			store(n.getTarget(), stack, variablesMapping);
		} else if (instruction instanceof ReturnInstruction){
			ReturnInstruction n = (ReturnInstruction)instruction;

			load(n.getRight(), stack, variablesMapping);

			stack.add(new lense.compiler.ir.stack.ReturnInstruction());
		} else if (instruction instanceof PrepareParameter){
			PrepareParameter n = (PrepareParameter)instruction;

			load(n.getRight(), stack, variablesMapping);
		} else if (instruction instanceof IfJump){
			IfJump n = (IfJump)instruction;

			load(n.getCondition(), stack, variablesMapping);
			
			stack.add(new lense.compiler.ir.stack.IfJumpTo(n.isConditionTrue(), n.getTargetLabel()));
		}else if (instruction instanceof GoTo){
			GoTo n = (GoTo)instruction;

			stack.add(new lense.compiler.ir.stack.GoTo(n.getTargetLabel()));
		}
	}


	private void operate(Operation operation, StackInstructionList stack) {
		stack.add(new ArithmeticOperate(operation));	
	}

	private void load(Operand ref, StackInstructionList stack,StackVariableMapping variablesMapping) {
		if (ref instanceof NumericConstant){
			NumericConstant c = (NumericConstant)ref;
			stack.add(new PushNumberValue(c.getValue(), c.getType()));
		} else if (ref instanceof StringConstant){
			StringConstant c = (StringConstant)ref;
			stack.add(new PushStringValue(c.getValue()));
		} else if (ref instanceof LocalVariable){
			LocalVariable c = (LocalVariable)ref;

			Integer index = variablesMapping.get(c.getName());

			if (index == null){
				throw new RuntimeException();
			}
			stack.add(new LoadFromVariable(index.intValue(), c.getType()));
		} else if (ref instanceof TemporaryVariable){
			TemporaryVariable c = (TemporaryVariable)ref;

			Integer index = variablesMapping.get(c.getName());

			stack.add(new LoadFromVariable(index.intValue(), c.getType()));
		} else if (ref instanceof CallInstruction){
			CallInstruction c = (CallInstruction)ref;

			stack.add(new InvokeVirtual(c.getOwner(), c.getName(), c.getReturnType()));
		} else if (ref instanceof ReadFieldInstruction){
			ReadFieldInstruction c = (ReadFieldInstruction)ref;

			stack.add(new ReadFieldFromObject(c.getOwnerType(), c.getName(), c.getReturnType()));
		} else if (ref instanceof CreateNewInstruction){
			CreateNewInstruction c = (CreateNewInstruction)ref;
			
			stack.add(new InvokeConstructor(c.getType(), c.getName()));
		} else {
			throw new RuntimeException("Unrecognized Operand " + ref.getClass() );
		}
	}

	private void store(Operand ref, StackInstructionList stack,StackVariableMapping variablesMapping) {
		if (ref instanceof LocalVariable){
			LocalVariable c = (LocalVariable)ref;

			Integer index = variablesMapping.get(c.getName());
			if (index == null){
				index = variablesMapping.putIncrement(c.getName());
			}

			stack.add(new StoreToVariable(index.intValue(), c.getType()));
		} else if (ref instanceof TemporaryVariable){
			TemporaryVariable c = (TemporaryVariable)ref;

			Integer index = variablesMapping.get(c.getName());
			if (index == null){
				index = variablesMapping.putIncrement(c.getName());
			}

			stack.add(new StoreToVariable(index.intValue(), c.getType()));
		}
	}

	
	

	private void emit(TacInstruction tac) {
		currentList.add(tac);
	}

	private Operation operationFor(ArithmeticOperation operation) {
		switch (operation) {
		case Addition:
			return Operation.ADD;
		case Subtraction:
			return Operation.SUBTRACT;
		case Multiplication:
			return Operation.MULTIPLY;
		case Increment:
			return Operation.INCREMENT;
		case Decrement:
			return Operation.DECREMENT;
		case Division:
			return Operation.DIVIDE;
		case IntegerDivision:
			return Operation.DIVIDE;
		case Remainder:
			return Operation.REMAINDER;
		case LeftShift:
			return Operation.SHIFT_LEFT;
		case RightShift:
			return Operation.SHIFT_RIGHT;
		case SignedRightShift:
			return Operation.LOGICAL_SHIFT_RIGHT;
		default:
			throw new RuntimeException(operation.name() + "Not supported");
		}
	}

}
