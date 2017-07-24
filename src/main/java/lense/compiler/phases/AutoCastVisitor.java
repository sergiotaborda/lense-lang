package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.variable.TypeVariable;

public class AutoCastVisitor extends AbstractLenseVisitor  {

	private String variableName;
	private TypeVariable typeVariable;
	private SemanticContext context;

	public AutoCastVisitor(SemanticContext context, String name, TypeVariable typeVariable) {
		this.context = context;
		this.variableName = name;
		this.typeVariable = typeVariable;
	}
	
	@Override
	protected SemanticContext getSemanticContext() {
		return context;
	}

	@Override
	public void startVisit() {
	}

	@Override
	public void endVisit() {
	}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof VariableReadNode){
			VariableReadNode var = (VariableReadNode)node;
			if (var.getName().equals(variableName)){
				CastNode cast = new CastNode(var, this.typeVariable.getTypeDefinition());
				
				node.getParent().replace(var, cast);
			}
		} else if (node instanceof FieldOrPropertyAccessNode){
			FieldOrPropertyAccessNode p = (FieldOrPropertyAccessNode)node;
			if (p.getPrimary() instanceof QualifiedNameNode){
				QualifiedNameNode q = (QualifiedNameNode)p.getPrimary();
				if (q.getName().equals(variableName)){
					CastNode cast = new CastNode((LenseAstNode) p.getPrimary() , this.typeVariable.getTypeDefinition());
					
					p.replace(p.getPrimary() , cast);
				}
			} else if (p.getPrimary() == null){
				// variable or field ?
				VariableInfo variable = this.getSemanticContext().currentScope().searchVariable(p.getName());
				
				if (variable == null){
					p.setPrimary(new VariableReadNode("this"));
				} else {
					VariableReadNode var = new VariableReadNode(p.getName());
					CastNode cast = new CastNode(var, this.typeVariable.getTypeDefinition());
					
					node.getParent().replace(p, cast);
					
				}
				
			}
		}
		
	}





}
