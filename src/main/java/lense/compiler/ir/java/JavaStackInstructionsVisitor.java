package lense.compiler.ir.java;

import lense.compiler.crosscompile.java.ast.BooleanValue;
import lense.compiler.crosscompile.java.ast.MethodDeclarationNode;
import lense.compiler.crosscompile.java.ast.MethodInvocationNode;
import lense.compiler.crosscompile.java.ast.NumericValue;
import lense.compiler.crosscompile.java.ast.PosUnaryExpression;
import lense.compiler.crosscompile.java.ast.ReturnNode;
import lense.compiler.crosscompile.java.ast.StringValue;
import lense.compiler.crosscompile.java.ast.TypedNode;
import lense.compiler.ir.InstructionType;
import lense.compiler.ir.stack.ArithmeticOperate;
import lense.compiler.ir.stack.AssignmentNode;
import lense.compiler.ir.stack.InvokeInterface;
import lense.compiler.ir.stack.InvokeVirtual;
import lense.compiler.ir.stack.LoadFromConstantPool;
import lense.compiler.ir.stack.Operation;
import lense.compiler.ir.stack.PushConstantValue;
import lense.compiler.ir.stack.ReturnInstruction;
import lense.compiler.ir.stack.StackInstructionList;
import lense.compiler.ir.stack.StoreToVariable;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import compiler.typesystem.TypeKind;

public class JavaStackInstructionsVisitor implements Visitor<AstNode> {

	StackInstructionList currentList;
	
	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		
		if (node instanceof MethodDeclarationNode){
			StackInstructionList list = new StackInstructionList();
			node.setProperty("instructionsList", list);
			currentList = list;
		}

		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		// TODO Auto-generated method stub
		if (node instanceof BooleanValue){
			currentList.add(new PushConstantValue(((BooleanValue)node).isValue() ? 1 : 0, InstructionType.BOOLEAN));
		} else if (node instanceof ReturnNode){
			currentList.add(new ReturnInstruction(new InstructionType(((ReturnNode)node).getTypeDefinition().getName())));
		} else if (node instanceof StringValue){
			currentList.add(new LoadFromConstantPool(0 /* TODO calculate index*/));
		} else if (node instanceof NumericValue){
			currentList.add(new PushConstantValue(((NumericValue)node).getValue(), new InstructionType(((NumericValue)node).getTypeDefinition().getName())));
		} else if (node instanceof MethodInvocationNode){
			MethodInvocationNode method = (MethodInvocationNode)node;
			TypeKind kind = ((TypedNode)method.getAccess()).getTypeDefinition().getKind();
			if (kind.equals(lense.compiler.typesystem.Kind.Interface)){
				currentList.add(new InvokeInterface());
			} else if (kind.equals(lense.compiler.typesystem.Kind.Class)){
				/*TODO needs access to called method to determine if it is static or not*/
				currentList.add(new InvokeVirtual());
			}	
		}else if (node instanceof AssignmentNode){
			currentList.add(new StoreToVariable(0 /*TODO calculate index*/));
		} else if (node instanceof PosUnaryExpression){
			switch (((PosUnaryExpression)node).getOperation()){
			case BitNegate:
			case LogicNegate:
				currentList.add(new ArithmeticOperate(Operation.NEGATE, InstructionType.BOOLEAN));
			default:
				throw new RuntimeException("Operation " + ((PosUnaryExpression)node).getOperation().name() + " is not supported as PosUnaryException" );
			}
			
		}
		
		
//		ClassInstanceCreation.java
//		ComparisonNode
//		WhileNode.java
//		VariableReadNode.java
//		VariableWriteNode.java
//		VariableDeclarationNode.java
//		TernaryConditionalExpressionNode.java

	
	}

}
