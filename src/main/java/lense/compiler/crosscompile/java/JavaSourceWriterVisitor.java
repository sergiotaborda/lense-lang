/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.PrintWriter;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.AnnotationNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ClassInstanceCreationNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.FieldAccessNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NullValue;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.StatementNode;
import lense.compiler.ast.StringConcatenationNode;
import lense.compiler.ast.StringValue;
import lense.compiler.ast.SwitchNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.TernaryConditionalExpressionNode;
import lense.compiler.ast.TryStatement;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public class JavaSourceWriterVisitor implements Visitor<AstNode>  {

	private PrintWriter writer;

	/**
	 * Constructor.
	 * @param writer
	 */
	public JavaSourceWriterVisitor(PrintWriter writer) {
		this.writer = writer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {
		writer.flush();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		try {
			if (node instanceof NullValue){
				writer.print("null");
			} else if (node instanceof StringValue){
				writer.print("\"");
				writer.print(((StringValue)node).getValue());
				writer.print("\"");
			} else if (node instanceof BooleanValue){
				writer.print(((BooleanValue)node).isValue() ? "true" : "false");
			} else if (node instanceof TernaryConditionalExpressionNode){	
				TernaryConditionalExpressionNode t = (TernaryConditionalExpressionNode)node;
				writer.print("(");
				TreeTransverser.transverse(t.getCondition(), this);
				writer.print(") ? (");
				TreeTransverser.transverse(t.getThenExpression(), this);
				writer.print(") : (");
				TreeTransverser.transverse(t.getElseExpression(), this);
				writer.print(")");
				return VisitorNext.Siblings;
			} else if (node instanceof ClassTypeNode){
				ClassTypeNode t = (ClassTypeNode)node;

				int pos = t.getName().lastIndexOf('.');
				if (pos >0){
					writer.print("package ");
					writer.print(t.getName().substring(0, pos));
					writer.print(";\n");
				}

				writer.println();

				if (t.getAnnotations()!=null){
					for(AstNode n : t.getAnnotations().getChildren()){
						AnnotationNode anot = (AnnotationNode)n;
						writer.print(anot.getName());
						writer.print(" ");
					}
				}

				switch (t.getKind()) {
					case Annotation:
					case Class:
						writer.print("class ");
						break;
					case Enum:
						writer.print("enum ");
						break;
					case Interface:
						writer.print("interface ");
						break;
					
				}
		
				writer.print(t.getName().substring(pos+1));

				if (t.getSuperType() != null && t.getSuperType().getTypeVariable().getName().length() > 0){
					writer.print(" extends ");
					writer.print(t.getSuperType().getTypeVariable().getName());

				}

				writer.println("{");
				TreeTransverser.transverse(t.getBody(), this);
				writer.println();
				writer.println("}");

				return VisitorNext.Siblings;
			} else if (node instanceof TryStatement ){
				TryStatement t = (TryStatement)node;

				writer.print("try");

				if (t.getResource() != null){
					writer.print("(");
					TreeTransverser.transverse(t.getResource(), this);
					writer.print(")");
				}

				TreeTransverser.transverse(t.getInstructions(), this);

				if (t.getCatchOptions() != null){
					for (AstNode a : t.getCatchOptions().getChildren()){
						CatchOptionNode c = (CatchOptionNode)a;

						writer.print(" catch (");

						TreeTransverser.transverse(c.getExceptions(), this);

						writer.println(")");

						TreeTransverser.transverse(c.getInstructions(), this);

						writer.println();
					}
				}

				if (t.getfinalInstructions() != null){
					writer.print(" finally ");
					TreeTransverser.transverse(t.getfinalInstructions(), this);
				
				}

				return VisitorNext.Siblings;
//			} else if (node instanceof RangeNode ){
//				RangeNode r = (RangeNode)node;
//				
//				writer.print(" new IntProgressable(");
//				TreeTransverser.tranverse(r.getStart(), this);
//				writer.print(",");
//				TreeTransverser.tranverse(r.getEnd(), this);
//				writer.print(")");
//				return VisitorNext.Siblings;
			} else if (node instanceof ForEachNode ){
				ForEachNode f = (ForEachNode)node;

				writer.print(" for (");

				visitBeforeChildren(f.getVariableDeclarationNode());
				writer.print(" : ");

				TreeTransverser.transverse(f.getContainer(), this);
				writer.println(")");

				TreeTransverser.transverse(f.getBlock(), this);

				return VisitorNext.Siblings;
			} 
//			else if (node instanceof ForEachNode ){
//				ForEachNode f = (ForEachNode)node;
//
//				writer.print(" for (");
//
//				visitBeforeChildren(f.getVariableDeclarationNode());
//				
//				writer.print(" ; ");
//
//				visitBeforeChildren(f.getConditional());
//				
//				writer.print(" ; ");
//
//
//				visitBeforeChildren(f.getIncrement());
//				
//				//TreeTransverser.tranverse(f.getContainer(), this);
//				writer.println(")");
//
//				TreeTransverser.transverse(f.getBlock(), this);
//
//				return VisitorNext.Siblings;
//			} 
			else if (node instanceof WhileNode ){
				WhileNode f = (WhileNode)node;

				writer.println();
				writer.print(" while (");
				
				TreeTransverser.transverse(f.getCondition(), this);
				
				writer.println(")");

				TreeTransverser.transverse(f.getStatements(), this);

				return VisitorNext.Siblings;
			} else if (node instanceof SwitchNode ){
				SwitchNode n = (SwitchNode)node;

				writer.println();
				writer.print(" switch (");
				TreeTransverser.transverse(n.getCandidate(), this);
				writer.println(") { ");

				for (AstNode a : n.getOptions().getChildren()){
					SwitchOption op = (SwitchOption)a;
					if (op.isDefault()){
						writer.print(" default");
					} else {
						writer.print(" case ");
						TreeTransverser.transverse(op.getValue(), this);
						writer.println(":");
					}
	
					TreeTransverser.transverse(op.getActions(), this);
					writer.println("break;");
				}
			
				writer.println("}");
				
				return VisitorNext.Siblings;
			} else if (node instanceof DecisionNode ){
				DecisionNode n = (DecisionNode)node;

				if (n.getCondition() != null){
					writer.print("if (");	
					TreeTransverser.transverse(n.getCondition(), this);	
					writer.println(")");

				}
			
				TreeTransverser.transverse(n.getTrueBlock(), this);

				if (n.getFalseBlock() != null){
					writer.print(" else ");
					TreeTransverser.transverse(n.getFalseBlock(), this);

				}
				return VisitorNext.Siblings;
			} else if (node instanceof IdentifierNode ){
				writer.print(((IdentifierNode)node).getId());
			} else if (node instanceof QualifiedNameNode ){
				writer.print(((QualifiedNameNode)node).getName());
			} else if (node instanceof MethodInvocationNode ){
				MethodInvocationNode n = (MethodInvocationNode)node;

				if (n.getAccess() != null){
					TreeTransverser.transverse(n.getAccess(), this);
					writer.print(".");
				}
				
				writer.print(n.getCall().getName());
				writer.print("(");
				if (n.getCall().getArgumentListNode()!= null){
					TreeTransverser.transverse(n.getCall().getArgumentListNode(), this);
				}
			
				writer.print(")");
				
				if (node.getParent() instanceof BlockNode){
					writer.println(";");
				}
				
				return VisitorNext.Siblings;
			} else if (node instanceof AssignmentNode){
				AssignmentNode n = (AssignmentNode)node;
				
				TreeTransverser.transverse((AstNode)n.getLeft(), this);
				writer.print(" = ");
				TreeTransverser.transverse(n.getRight(), this);
				writer.println(";");
				
				return VisitorNext.Siblings;
			} else if (node instanceof ClassInstanceCreationNode){
				ClassInstanceCreationNode n = (ClassInstanceCreationNode)node;
				
				writer.print(" new ");
				if (n.getTypeVariable() == null){
					writer.print(n.getTypeNode().getName());
				} else {
					writer.print(n.getTypeVariable().getName());
				}
				writer.print(" (");
				TreeTransverser.transverse(n.getArguments(), this);
				writer.print(")");

				return VisitorNext.Siblings;
			} else if (node instanceof StringConcatenationNode){
				writer.print(" new StringBuilder()");
				
				for(AstNode n : node.getChildren()){
					writer.print(".append(");
					TreeTransverser.transverse(n, this);
					writer.print(")");
				}
				
				return VisitorNext.Siblings;
			}  else if (node instanceof ArgumentListNode){
				ArgumentListNode n = (ArgumentListNode)node;
				
				boolean first = true;
				for(AstNode a : n.getChildren()){
					if (!first){
						writer.print(",");
					} else {
						first = false;
					}
					TreeTransverser.transverse(a, this);
				
				}
		

				return VisitorNext.Siblings;
			} else if (node instanceof ContinueNode ){
				
				writer.println("continue;");
				
				return VisitorNext.Siblings;
			} else if (node instanceof TypeNode ){

					TypeNode t = (TypeNode)node;
					if (t.getName().equals("lense.core.lang.Void")){
						writer.print("void");
					} else {
						final TypeVariable type = t.getTypeVariable();
						if (type == null){
							writer.print(t.getTypeParameter().getName());
						} else if (type instanceof FixedTypeVariable){
							
							TypeDefinition def = ((FixedTypeVariable)type).getTypeDefinition();
							writer.print(def.getName());
							writeGenerics(def);
							
						} else {
							IntervalTypeVariable interval = ((IntervalTypeVariable)type);
							writer.print(interval.getName());
							writer.print(" extends ");
							writer.print(interval.getUpperbound().getName());
							
						}
						
					}
					
					writer.print(" ");
					return VisitorNext.Siblings;
			} else if (node instanceof VariableDeclarationNode ){
				VariableDeclarationNode t = (VariableDeclarationNode)node;
				
				if (t.getInfo() !=null){
					if (t.getInfo().isEfectivlyFinal()){
						writer.print("final ");
					}
				}
				if (t.getTypeVariable() == null){
					writer.print("????");
				} else {
					writer.print(t.getTypeVariable().getName());
				}
				writer.print(" ");
				writer.print(t.getName());
				
				if (t.getInitializer() != null){
					writer.print(" = ");
					TreeTransverser.transverse(t.getInitializer(), this);
				}

				if (t.getInfo() !=null){

					if (t.getInfo().doesEscape()){
						writer.print("/* doesEscape */");
					}

				}
				
				if (node.getParent() instanceof BlockNode){
					writer.println(";");
				}

				return VisitorNext.Siblings;
			} else if (node instanceof VariableWriteNode ){
				VariableWriteNode t = (VariableWriteNode)node;
				writer.print(t.getName());
				
				return VisitorNext.Siblings;
			} else if (node instanceof VariableReadNode ){
				VariableReadNode t = (VariableReadNode)node;
				writer.print(t.getName());
			} else if (node instanceof BlockNode ){
				writer.print("{\n");
			} else if (node instanceof ReturnNode ){
				writer.print("return ");
			} else if (node instanceof NumericValue ){
				NumericValue n = (NumericValue)node;
				writer.print(n.toString());

			} else if (node instanceof AssignmentNode){
				AssignmentNode n = (AssignmentNode)node;

				visitBeforeChildren((AstNode)n.getLeft());
				writer.print(" ");
				writer.print(n.getOperation().symbol());
				writer.print(" ");
				visitBeforeChildren(n.getRight());
				writer.print(";\n");
				
				return VisitorNext.Siblings;
			} else if (node instanceof PosExpression ){
				PosExpression n = (PosExpression)node;

				writer.print(n.getOperation().symbol());
	

			} else if (node instanceof PosExpression ){
				PosExpression n = (PosExpression)node;

				writer.print(n.getOperation().symbol());
				

			}  else if (node instanceof ArithmeticNode ){
				ArithmeticNode n = (ArithmeticNode)node;

				TreeTransverser.transverse(n.getLeft(), this);

				writer.print(" ");
				writer.print(n.getOperation().symbol());
				writer.print(" ");

				TreeTransverser.transverse(n.getRight(), this);

				
				return VisitorNext.Siblings;
			} else if (node instanceof BooleanOperatorNode ){
				BooleanOperatorNode n = (BooleanOperatorNode)node;

				TreeTransverser.transverse(n.getLeft(), this);

				writer.print(" ");
				writer.print(n.getOperation().symbol());
				writer.print(" ");

				TreeTransverser.transverse(n.getRight(), this);
				
				
				return VisitorNext.Siblings;
			} else if (node instanceof ComparisonNode ){
				ComparisonNode n = (ComparisonNode)node;

				
				if (n.getOperation() == Operation.EqualTo){
					TreeTransverser.transverse(n.getLeft(), this);
					writer.print(".equals(");
					TreeTransverser.transverse(n.getRight(), this);
					writer.print(")");
				} else if (n.getOperation() == Operation.Different){
					writer.print("!");
					TreeTransverser.transverse(n.getLeft(), this);
					writer.print(".equals(");
					TreeTransverser.transverse(n.getRight(), this);
					writer.print(")");
				} else if (n.getOperation() == Operation.ReferenceEquals){
					TreeTransverser.transverse(n.getLeft(), this);
					writer.print(" == ");
					TreeTransverser.transverse(n.getRight(), this);
				} else if (n.getOperation() == Operation.ReferenceDifferent){
					TreeTransverser.transverse(n.getLeft(), this);
					writer.print(" != ");
					TreeTransverser.transverse(n.getRight(), this);
				}else {
					TreeTransverser.transverse(n.getLeft(), this);
					writer.print(" ");
					writer.print(n.getOperation().symbol());
					writer.print(" ");
					TreeTransverser.transverse(n.getRight(), this);
				}
					
				

				
	
				return VisitorNext.Siblings;
			} else if (node instanceof FieldAccessNode ){
				FieldAccessNode n = (FieldAccessNode)node;
				
				writer.print(n.getName());
				
			}else if (node instanceof IndexedAccessNode ){
				IndexedAccessNode n = (IndexedAccessNode)node;
				
				TreeTransverser.transverse(n.getAccess(), this);
				writer.print("[");
				TreeTransverser.transverse(n.getIndexExpression(), this);
				writer.print("]");
			} else if (node instanceof MethodDeclarationNode ){
			
				MethodDeclarationNode m = (MethodDeclarationNode)node;

		
				writer.println("\t");

				if ( m.getAnnotations() != null){
					for(AstNode n : m.getAnnotations().getChildren()){
						AnnotationNode anot = (AnnotationNode)n;
						writer.print(anot.getName());
						writer.print(" ");
					}
				}
				
				if (m.getReturnType() == null){
					writer.print("null");
				} else if (m.getReturnType().getTypeVariable() == null){
					writer.print("null");
				} else {
					if (m.getReturnType().getTypeVariable().getName().equals(LenseTypeSystem.Void().getName())){
						writer.print("void");
					} else {
						TreeTransverser.transverse(m.getReturnType(), this);
					}
				}
				
				
				
				
				writer.print(" ");
				writer.print(m.getName());
				writer.print("(");

				if (m.getParameters() != null){
					boolean isFirst = true;
					for (AstNode n : m.getParameters().getChildren()){
						FormalParameterNode p = (FormalParameterNode)n;

						if(isFirst){
							isFirst = false;
						} else {
							writer.print(", ");
						}

						TreeTransverser.transverse(p.getTypeNode(), this);

						writer.print(" ");
						writer.print(p.getName());



					}
				}

				writer.print(")");
				TreeTransverser.transverse(m.getBlock(), this);

				return VisitorNext.Siblings;
			} else if (node instanceof ParametersListNode){
				return VisitorNext.Siblings;
			} else if (node instanceof FieldDeclarationNode ){
				FieldDeclarationNode m = (FieldDeclarationNode)node;

				writer.print("\n");
				writer.print(m.getTypeVariable().getName());
				writer.print(" ");
				writer.print(m.getName());


				if (m.getInitializer() != null){
					writer.print(" = ");
					TreeTransverser.transverse(m.getInitializer(), this);	
				}
				writer.print(";");
				return VisitorNext.Siblings;
			} 

			return VisitorNext.Children;
		} finally {
			writer.flush();
		}

	}

	private void writeGenerics(final TypeDefinition type) {
		if (!type.getGenericParameters().isEmpty()){
			writer.print("<");
			boolean first = true;
			for(IntervalTypeVariable p : type.getGenericParameters()){
				if (first){
					first = false;
				} else {
					writer.print(",");
				}
				writer.print(p.getName());
				if (p.getUpperbound() != null){
					if (!p.getUpperbound().getName().equals("java.lang.Object")){
						writer.print(" extends ");
						writer.print(p.getUpperbound().getName());
						//writeGenerics(p.getUpperbound());
					} else {
						writer.print(" super ");
						writer.print(p.getLowerBound().getName());
					//	writeGenerics(p.getLowerBound());
					}
					
				}
				
			}
			writer.print(">");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof BlockNode ){
			writer.print("}\n");
		} else if (node instanceof ClassTypeNode){
			writer.print("}\n");
		} else if (node instanceof StatementNode ){
			writer.print(";\n");
		}
	}

}
