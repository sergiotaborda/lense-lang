package lense.compiler.ir.java;

import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ir.InstructionType;
import lense.compiler.ir.tac.ReturnInstruction;
import lense.compiler.ir.tac.Assign;
import lense.compiler.ir.tac.AssignAfterBinaryOperation;
import lense.compiler.ir.tac.LocalVariable;
import lense.compiler.ir.tac.NumericConstant;
import lense.compiler.ir.tac.Operation;
import lense.compiler.ir.tac.Reference;
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
			TacInstructionList list = node.getProperty("instructionsList", TacInstructionList.class).get();
			
			list = optimize(list);
			
			System.out.println("METHOD " + ((MethodDeclarationNode)node).getName() + "{");
			System.out.print(list.toString());
			System.out.println("}");
			System.out.println();
			
			currentList = null;
			
		} else if (node instanceof ArithmeticNode){
			ArithmeticNode n = (ArithmeticNode)node;
			
			Reference left = n.getLeft().getProperty("tempVal", Reference.class).get();
			Reference right = n.getRight().getProperty("tempVal", Reference.class).get();
			
			TemporaryVariable target = new TemporaryVariable(tempIndex++);
			
			emit(new AssignAfterBinaryOperation(target, left, operationFor(n.getOperation()), right));
			
			n.setProperty("tempVal", target);
			
		} else if (node instanceof VariableReadNode){
			VariableReadNode n =(VariableReadNode)node;
			
			Reference target = new LocalVariable(n.getName(),new InstructionType(n.getTypeDefinition().getName()));
			//tempIndex--;
			
			n.setProperty("tempVal", target);
			
		} else if (node instanceof VariableDeclarationNode){
			VariableDeclarationNode n =(VariableDeclarationNode)node;
			
			Reference right = n.getInitializer().getProperty("tempVal", Reference.class).get();
			
			Reference target = new LocalVariable(n.getInfo().getName(),new InstructionType(n.getTypeDefinition().getName()));
			
			emit(new Assign(target, right));
			
			n.setProperty("tempVal", target);
			
		} else if (node instanceof NumericValue){
			
			NumericValue n =(NumericValue)node;
			Reference target = new TemporaryVariable(tempIndex++);
			
			emit(new Assign(target, new NumericConstant(n.getValue(), new InstructionType(n.getTypeDefinition().getName()))));
			
			n.setProperty("tempVal", target);
		} else if (node instanceof ReturnNode){
			ReturnNode n =(ReturnNode)node;
			
			if (n.getValue() == null){
				emit(new ReturnInstruction());
			} else {
				Reference right = n.getValue().getProperty("tempVal", Reference.class).get();
				emit(new ReturnInstruction(right));
			}
		} else if (node instanceof MethodInvocationNode){
			MethodInvocationNode n =(MethodInvocationNode)node;
			
			if (n.getAccess() == null){
				emit(new PrepareParameter(new LocalVariable("this", null)));
			} else {
				Reference access = n.getAccess().getProperty("tempVal", Reference.class).get();
				emit(new PrepareParameter(access));
			}

			for( AstNode paramNode : n.getCall().getArgumentListNode().getChildren()){
				Reference param = paramNode.getProperty("tempVal", Reference.class).get();
				emit(new PrepareParameter(param));
			}
			
			
			Reference target = new TemporaryVariable(tempIndex++);
			
			emit(new CallInstruction(target, n.getCall().getName()));
			
			n.setProperty("tempVal", target);
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
//				 AssignAfterBinaryOperation assignBinary = (AssignAfterBinaryOperation)instruction;
//				 
//				 for (int j =i; j< list.size(); j++){
//					 TacInstruction next = list.get(j);
//					 if (next instanceof Assign){
//						 if (assignBinary.getTarget().equals(((Assign)next).getSource())){
//							 assignBinary.replace(assignBinary.getTarget(), ((Assign)next).getSource());
//							 list.removeAt(j);
//							 j--;
//							 changed = true;
//						 }
//					 }
//				
//				 }
//			
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
		default:
			return Operation.DIVIDE;
		}
	}

}
