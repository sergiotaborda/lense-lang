package lense.compiler.ir.java;

import java.util.Optional;

import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PreExpression;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ir.InstructionType;
import lense.compiler.ir.Operation;
import lense.compiler.ir.stack.ArithmeticOperate;
import lense.compiler.ir.stack.InvokeVirtual;
import lense.compiler.ir.stack.LoadFromVariable;
import lense.compiler.ir.stack.PushNumberValue;
import lense.compiler.ir.stack.PushStringValue;
import lense.compiler.ir.stack.StackInstructionList;
import lense.compiler.ir.stack.StackVariableMapping;
import lense.compiler.ir.stack.StoreToVariable;
import lense.compiler.ir.tac.Assign;
import lense.compiler.ir.tac.AssignAfterBinaryOperation;
import lense.compiler.ir.tac.AssignAfterUnaryOperation;
import lense.compiler.ir.tac.LocalVariable;
import lense.compiler.ir.tac.NumericConstant;
import lense.compiler.ir.tac.Operand;
import lense.compiler.ir.tac.ReturnInstruction;
import lense.compiler.ir.tac.StringConstant;
import lense.compiler.ir.tac.TacInstruction;
import lense.compiler.ir.tac.TacInstructionList;
import lense.compiler.ir.tac.TemporaryVariable;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;

public class TacInstructionsVisitor implements Visitor<AstNode> {

	TacInstructionList currentList;

	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {

		if (node instanceof MethodDeclarationNode){
			TacInstructionList list = new TacInstructionList();
			node.setProperty("instructionsList", list);
			currentList = list;
		}

		return VisitorNext.Children;
	}

	private int tempIndex =0;

	@Override
	public void visitAfterChildren(AstNode node) {

		if (node instanceof MethodDeclarationNode){
			MethodDeclarationNode n = (MethodDeclarationNode)node;

			TacInstructionList list = node.getProperty("instructionsList", TacInstructionList.class).get();

			list = optimize(list);

			System.out.println("METHOD " + ((MethodDeclarationNode)node).getName() + "{");
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

			StackInstructionList stack = ToStack(list,variablesMapping);

			System.out.println("METHOD " + ((MethodDeclarationNode)node).getName() + "{");
			System.out.print(stack.toString());
			System.out.println("}");
			System.out.println();

			currentList = null;

		} else if (node instanceof ArithmeticNode){
			ArithmeticNode n = (ArithmeticNode)node;

			Operand left = n.getLeft().getProperty("tempVal", Operand.class).get();
			Operand right = n.getRight().getProperty("tempVal", Operand.class).get();

			TemporaryVariable target = new TemporaryVariable(tempIndex++);

			emit(new AssignAfterBinaryOperation(target, left, operationFor(n.getOperation()), right));

			n.setProperty("tempVal", target);

		} else if (node instanceof VariableReadNode){
			VariableReadNode n =(VariableReadNode)node;

			Operand target = new LocalVariable(n.getName(),new InstructionType(n.getTypeDefinition().getName()));

			n.setProperty("tempVal", target);

		} else if (node instanceof VariableDeclarationNode){
			VariableDeclarationNode n =(VariableDeclarationNode)node;

			Operand right = n.getInitializer().getProperty("tempVal", Operand.class).get();

			Operand target = new LocalVariable(n.getInfo().getName(),new InstructionType(n.getTypeDefinition().getName()));

			emit(new Assign(target, right));

			n.setProperty("tempVal", target);

		} else if (node instanceof NumericValue){

			NumericValue n =(NumericValue)node;
			Operand target = new TemporaryVariable(tempIndex++);

			emit(new Assign(target, new NumericConstant(n.getValue(), new InstructionType(n.getTypeDefinition().getName()))));

			n.setProperty("tempVal", target);
		} else if (node instanceof ReturnNode){
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
				Operand target = new TemporaryVariable(tempIndex++);
				if (op == Operation.SUBTRACT){
					op = Operation.SYMETRIC;
				}
				emit(new AssignAfterUnaryOperation(target, op , operand));
				
				n.setProperty("tempVal", target);
			}

		} else if (node instanceof MethodInvocationNode){
			MethodInvocationNode n =(MethodInvocationNode)node;

			if (n.getAccess() == null){
				emit(new PrepareParameter(new LocalVariable("this", null)));
			} else {
				Operand access = n.getAccess().getProperty("tempVal", Operand.class).get();
				emit(new PrepareParameter(access));
			}

			for( AstNode paramNode : n.getCall().getArgumentListNode().getChildren()){
				Operand param = paramNode.getProperty("tempVal", Operand.class).get();
				emit(new PrepareParameter(param));
			}

			Operand target = new TemporaryVariable(tempIndex++);
			emit(new Assign(target,new CallInstruction(n.getCall().getName(), null, new InstructionType(n.getTypeDefinition().getName()))));

			n.setProperty("tempVal", target);
		}

		Optional<TacInstruction> emitAfter = node.getProperty("emitAfter", TacInstruction.class);
		if (emitAfter.isPresent()){
			emit(emitAfter.get());
			node.setProperty("emitAfter", null);
		}

	}




	private StackInstructionList ToStack(TacInstructionList list,StackVariableMapping  variablesMapping) {
		StackInstructionList stack = new StackInstructionList();


		for(TacInstruction instruction : list){
			toStack(instruction, stack, variablesMapping);
		}

		return stack;
	}

	private void toStack(TacInstruction instruction, StackInstructionList stack, StackVariableMapping  variablesMapping) {
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

			stack.add(new LoadFromVariable(index.intValue()));
		} else if (ref instanceof TemporaryVariable){
			TemporaryVariable c = (TemporaryVariable)ref;

			Integer index = variablesMapping.get(c.getName());

			stack.add(new LoadFromVariable(index.intValue()));
		} else if (ref instanceof CallInstruction){
			CallInstruction c = (CallInstruction)ref;

			stack.add(new InvokeVirtual(c.getName()));
		}
	}

	private void store(Operand ref, StackInstructionList stack,StackVariableMapping variablesMapping) {
		if (ref instanceof LocalVariable){
			LocalVariable c = (LocalVariable)ref;

			Integer index = variablesMapping.get(c.getName());
			if (index == null){
				index = variablesMapping.putIncrement(c.getName());
			}

			stack.add(new StoreToVariable(index.intValue()));
		} else if (ref instanceof TemporaryVariable){
			TemporaryVariable c = (TemporaryVariable)ref;

			Integer index = variablesMapping.get(c.getName());
			if (index == null){
				index = variablesMapping.putIncrement(c.getName());
			}

			stack.add(new StoreToVariable(index.intValue()));
		}
	}

	private TacInstructionList optimize(TacInstructionList list) {
		boolean changed = true;
		while (changed){
			changed = false;

			changed = optimizeCopyPropagation(list);
		}

		return list;
	}

	private boolean optimizeCopyPropagation(TacInstructionList list) {
		boolean changed = false;
		for (int i =0; i < list.size()- 1; i++){
			TacInstruction instruction = list.get(i);
			if (instruction instanceof Assign){
				Assign assign = (Assign)instruction;
				if (assign.getTarget().isTemporary()){
					list.removeAt(i);
					for (int j =i; j< list.size(); j++){
						changed = list.get(j).replace(assign.getTarget(), assign.getSource());
					}
				}

			} else  if (instruction instanceof AssignAfterBinaryOperation){
				AssignAfterBinaryOperation assignBinary = (AssignAfterBinaryOperation)instruction;

				if (assignBinary.getTarget().isTemporary()){
					for (int j =i+1; j< list.size(); j++){
						TacInstruction next = list.get(j);
						if (next instanceof Assign){
							Assign assign = (Assign)next;
							if (assignBinary.getTarget().equals(assign.getSource())){
								assignBinary.replace(assignBinary.getTarget(), assign.getTarget());
								list.removeAt(j);
								j--;
								changed = true;
								break;
							}
						}

					}
				}
			}
		}

		return changed;
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
		case FractionDivision:
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
