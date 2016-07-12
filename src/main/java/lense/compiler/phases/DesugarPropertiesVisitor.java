package lense.compiler.phases;

import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.AssignmentNode.Operation;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ObjectReadNode;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;

public class DesugarPropertiesVisitor extends AbstractLenseVisitor{

	private SemanticContext semanticContext;

	public DesugarPropertiesVisitor (SemanticContext sc){
		this.semanticContext = sc;
	}

	@Override
	protected SemanticContext getSemanticContext() {
		return semanticContext;
	}

	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {

		if (node instanceof IndexerPropertyDeclarationNode){
			IndexerPropertyDeclarationNode n = (IndexerPropertyDeclarationNode)node;
			AstNode parent = node.getParent();

			parent.remove(node);

			if (n.getAcessor() != null){
				AccessorNode a = n.getAcessor();


				MethodDeclarationNode getter = new MethodDeclarationNode();
				getter.setSetter(false);
				getter.setProperty(true);
				getter.setIndexer(true);
				getter.setName("get");
				
				if (n.isNative()){
					getter.setNative(n.isNative());
				} else if (n.isAbstract()){
					// TODO ensure is abstract if type is not class
					getter.setAbstract(n.isAbstract());
				}else {
					getter.setBlock(a.getBlock());
				}
				getter.setReturnType(n.getType());
				getter.setVisibility(a.getVisibility() == null ? n.getVisibility(): a.getVisibility());

				getter.setParameters(n.getIndexes());
				parent.add(getter);
			}

			if (n.getModifier() != null){
				ModifierNode a = n.getModifier();


				MethodDeclarationNode setter = new MethodDeclarationNode();
				
				setter.setSetter(true);
				setter.setProperty(true);
				setter.setIndexer(true);
				setter.setName("set");
				
				String parameterName  = a.getValueVariableName();
				
				if (n.isNative()){
					parameterName = "value";
					setter.setNative(n.isNative());
				} else if (n.isAbstract()){
					parameterName = "value";
					setter.setAbstract(n.isAbstract());
				}else {
					setter.setBlock(a.getBlock());
				}

				
				
				FormalParameterNode valueParameter = new FormalParameterNode();
				valueParameter.setTypeNode(n.getType());
				valueParameter.setName(parameterName); 

				ParametersListNode parameters = new ParametersListNode();

				for(AstNode p : n.getIndexes().getChildren()){
					parameters.add(p);
					
				}
				parameters.add(valueParameter);

				setter.setParameters(parameters);
				setter.setVisibility(a.getVisibility() == null ? n.getVisibility(): a.getVisibility());

				setter.setReturnType(new TypeNode(LenseTypeSystem.Void()));

				parent.add(setter);
			}

		} else if (node instanceof PropertyDeclarationNode){
			PropertyDeclarationNode n = (PropertyDeclarationNode)node;

			String propertyName = resolvePropertyName(n.getName());
			String privateFieldName = "_" + n.getName();

			AstNode parent = node.getParent();

			parent.remove(node);

			if (n.getAcessor() != null){
				AccessorNode a = n.getAcessor();


				MethodDeclarationNode getter = new MethodDeclarationNode();
				getter.setSetter(false);
				getter.setPropertyName(n.getName());
				getter.setProperty(true);
	
				
				getter.setName("get" + propertyName);
				if (n.isNative()){
					getter.setNative(n.isNative());
				} else if (n.isAbstract()){
					// TODO ensure is abstract if type is not class
					getter.setAbstract(n.isAbstract());
				}else {
					BlockNode block = a.getBlock();
					if (a.isImplicit()){
						// TODO validate in semantics that is accessor is implicit modifier also is implicit
						block = new BlockNode();
						ReturnNode rnode = new ReturnNode();
						block.add(rnode);

						FieldOrPropertyAccessNode field = new FieldOrPropertyAccessNode(privateFieldName);
						field.setType(n.getType().getTypeVariable());
						rnode.add(field);
					} 
					getter.setBlock(block);
				}
				getter.setReturnType(n.getType());
				getter.setVisibility(a.getVisibility());

				parent.add(getter);
			}

			if (n.getModifier() != null){
				ModifierNode a = n.getModifier();


				MethodDeclarationNode setter = new MethodDeclarationNode();
				setter.setName("set" + propertyName);
				setter.setSetter(false);
				setter.setPropertyName(n.getName());
				setter.setProperty(true);
	
				String parameterName  = a.getValueVariableName();
				if (n.isNative()){
					setter.setNative(n.isNative());
				} else if (n.isAbstract()){
					setter.setAbstract(n.isAbstract());
				}else {
					BlockNode block = a.getBlock();
					if (a.isImplicit()){
						block = new BlockNode();
						AssignmentNode assign = new AssignmentNode(Operation.SimpleAssign);
						FieldOrPropertyAccessNode field = new FieldOrPropertyAccessNode(privateFieldName);
						field.setType(n.getType().getTypeVariable());
						assign.setLeft(field);
						assign.setRight(new VariableReadNode(parameterName, new VariableInfo(parameterName, n.getType().getTypeVariable(),setter, false, false)));

						block.add(assign);
					} 
					setter.setBlock(block);
				}

				FormalParameterNode valueParameter = new FormalParameterNode();
				valueParameter.setTypeNode(n.getType());
				valueParameter.setName(parameterName); 

				ParametersListNode parameters = new ParametersListNode();
				parameters.add(valueParameter);
				
				setter.setParameters(parameters);
				setter.setVisibility(a.getVisibility());
				setter.setReturnType(new TypeNode(LenseTypeSystem.Void()));

				parent.add(setter);
			}
		} else if (node instanceof FieldOrPropertyAccessNode){
			FieldOrPropertyAccessNode n= (FieldOrPropertyAccessNode)node;


			if (n.getKind() == FieldKind.PROPERTY){
				String propertyName = resolvePropertyName(n.getName());

				if (n.getParent()  instanceof AssignmentNode && ((AssignmentNode)n.getParent()).getLeft() == node){
					ExpressionNode value = ((AssignmentNode)n.getParent()).getRight();
					// is write access
					MethodInvocationNode invokeSet = new MethodInvocationNode(n.getPrimary(), "set" + propertyName, value);
					invokeSet.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.Void()));
					node.getParent().getParent().replace(n.getParent() , invokeSet);
				} else {
					// is read acesss
					MethodInvocationNode invokeGet = new MethodInvocationNode(n.getPrimary(), "get" + propertyName);
					invokeGet.setTypeVariable(n.getTypeVariable());
					node.getParent().replace(node, invokeGet);
				}
			}
		}else if (node instanceof IndexedAccessNode){
			IndexedAccessNode n= (IndexedAccessNode)node;



			if (n.getParent()  instanceof AssignmentNode && ((AssignmentNode)n.getParent()).getLeft() == node){
				ExpressionNode value = ((AssignmentNode)n.getParent()).getRight();
				// is write access
				
				
				
				MethodInvocationNode invokeSet = new MethodInvocationNode(n.getAccess(), "set", n.getIndexExpression(), value);
				invokeSet.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.Void()));
				node.getParent().getParent().replace(n.getParent() , invokeSet);
			} else {
				// is read acesss
				MethodInvocationNode invokeGet = new MethodInvocationNode(n.getAccess(), "get", n.getIndexExpression());
				invokeGet.setTypeVariable(n.getTypeVariable());
				node.getParent().replace(node, invokeGet);
			}

		} else if (node instanceof ComparisonNode) {
			ComparisonNode n = (ComparisonNode) node;
			// convert to compareTo or equals call

			if (n.getOperation() == ComparisonNode.Operation.EqualTo) {
				MethodInvocationNode equalsTo = new MethodInvocationNode(n.getLeft(), "equalsTo", n.getRight());
				equalsTo.setTypeVariable(n.getTypeVariable());
				node.getParent().replace(node, equalsTo);
			} else if (n.getOperation() == ComparisonNode.Operation.Different) {
				MethodInvocationNode equalsTo = new MethodInvocationNode(n.getLeft(), "equalsTo", n.getRight());
				equalsTo.setTypeVariable(n.getTypeVariable());
			
				PreBooleanUnaryExpression not = new PreBooleanUnaryExpression(BooleanOperation.LogicNegate,equalsTo);
		
				node.getParent().replace(node, not);
				
			} else if (n.getOperation() == ComparisonNode.Operation.ReferenceEquals) {
				//no-op
			} else if (n.getOperation() == ComparisonNode.Operation.ReferenceDifferent) {
				//no-op
			} else {
				// TODO return options LessThan , Equals , GreaterThan 
			
				MethodInvocationNode compareTo = new MethodInvocationNode(n.getLeft(), "compareTo", n.getRight());
				compareTo.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.Natural()));
				
				ObjectReadNode parameter = new ObjectReadNode(semanticContext.resolveTypeForName("lense.core.math.Equal",0).get(), "Equal");
				boolean negate = false;
				if (n.getOperation() == ComparisonNode.Operation.LessThan){
					parameter = new ObjectReadNode(semanticContext.resolveTypeForName("lense.core.math.Smaller",0).get(), "Smaller");
				} else if (n.getOperation() == ComparisonNode.Operation.LessOrEqualTo){
					parameter = new ObjectReadNode(semanticContext.resolveTypeForName("lense.core.math.Greater",0).get(), "Greater");
					negate = true;
				}  else if (n.getOperation() == ComparisonNode.Operation.GreaterOrEqualTo){
					parameter = new ObjectReadNode(semanticContext.resolveTypeForName("lense.core.math.Smaller",0).get(), "Smaller");
					negate = true;
				}  else if (n.getOperation() == ComparisonNode.Operation.GreaterThan){
					parameter = new ObjectReadNode(semanticContext.resolveTypeForName("lense.core.math.Greater",0).get(), "Greater");
				}
				
				MethodInvocationNode equalsTo = new MethodInvocationNode(compareTo, "equalsTo", parameter);
				equalsTo.setTypeVariable(n.getTypeVariable());
				
				if (negate){
					PreBooleanUnaryExpression not = new PreBooleanUnaryExpression(BooleanOperation.LogicNegate,equalsTo);
					node.getParent().replace(node, not);
				} else {
					node.getParent().replace(node, equalsTo);
				}
				
				

			}
		}
		
	}

	private String resolvePropertyName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}



}
