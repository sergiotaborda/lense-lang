/**
 * 
 */
package lense.compiler.phases;

import java.util.Map;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.TypeAlreadyDefinedException;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.InferedTypeNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeMemberDeclaringTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
/**
 * Read the classe members and fills a SenseType object
 */
public class StructureVisitor extends AbstractScopedVisitor {


	private LenseTypeDefinition currentType;

	public StructureVisitor (LenseTypeDefinition currentType, SemanticContext semanticContext){
		super(semanticContext);
		this.currentType = currentType;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorNext doVisitBeforeChildren(AstNode node) {
	    if (node instanceof FormalParameterNode) {
			FormalParameterNode formal = ((FormalParameterNode) node);

			try {
				this.getSemanticContext().currentScope().defineVariable(formal.getName(), formal.getTypeVariable(), node);
			} catch (TypeAlreadyDefinedException e) {

			}
		}  else if (node instanceof VariableDeclarationNode) {
			VariableDeclarationNode var = ((VariableDeclarationNode) node);

			try {
				this.getSemanticContext().currentScope().defineVariable(var.getName(), var.getTypeVariable(), node);
			} catch (TypeAlreadyDefinedException e) {

			}
		}else if (node instanceof InstanceOfNode) {
			InstanceOfNode n = (InstanceOfNode)node;

			TypeNode typeNode = n.getTypeNode();
			Map<Integer, TypeDefinition> map = this.getSemanticContext().typeAllForName( typeNode.getName());

			if (map.size() == 1){
				FixedTypeVariable f = new FixedTypeVariable(map.values().iterator().next());
				typeNode.setTypeVariable(f);
				typeNode.setTypeParameter(f.toIntervalTypeVariable());
			}

			TreeTransverser.transverse(n.getExpression(), this);

			if (n.getExpression() instanceof VariableReadNode) {
				VariableReadNode var = (VariableReadNode) n.getExpression();

				propagateType(node, typeNode, var.getName());
			} else if (n.getExpression() instanceof FieldOrPropertyAccessNode){
				FieldOrPropertyAccessNode var = (FieldOrPropertyAccessNode) n.getExpression();

				propagateType(node, typeNode, var.getName());
				
			} 

			return VisitorNext.Siblings;
		}  
		return VisitorNext.Children;
	}
	

	private void propagateType(AstNode node, TypeNode typeNode, String name) {
		AstNode parent = node.getParent();
		if (parent instanceof BooleanOperatorNode){
			BooleanOperatorNode b = (BooleanOperatorNode)parent ;

			if (b.getChildren().get(0) == node){ // this the left side
				if (b.getOperation() == BooleanOperation.LogicShortAnd){
					TreeTransverser.transverse(b.getChildren().get(1), new AutoCastVisitor(this.getSemanticContext(), name,typeNode.getTypeVariable() ));
				}
			}
		}
		
		parent = parent.getParent();

		while (parent instanceof BooleanOperatorNode){
			BooleanOperatorNode b = (BooleanOperatorNode)parent ;

			if (b.getOperation() == BooleanOperation.LogicShortAnd){
				TreeTransverser.transverse(b.getChildren().get(1), new AutoCastVisitor(this.getSemanticContext(),name,typeNode.getTypeVariable() ));
			}
			parent = parent.getParent();
		}
		// IF 
		parent = node.getParent();
		if (parent instanceof DecisionNode){
			DecisionNode b = (DecisionNode)parent ;

			TreeTransverser.transverse(b.getTrueBlock(), new AutoCastVisitor(this.getSemanticContext(), name,typeNode.getTypeVariable() ));
		
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doVisitAfterChildren(AstNode node) {
		if (node instanceof TypeNode) {
			TypeNode t = (TypeNode)node;
			if (t.needsInference()){
				//t = inferType(t);
				//node.getParent().replace(node, t);
			} else {
				resolveTypeDefinition((TypeNode)node);
			}

		} else if (node instanceof ConstructorDeclarationNode){
			ConstructorDeclarationNode f = (ConstructorDeclarationNode)node;

			ConstructorParameter[] params = asConstructorParameters(f.getParameters());

			currentType.addConstructor(f.isImplicit(), f.getName(), params);

		} else 	if (node instanceof FieldDeclarationNode){
			FieldDeclarationNode f = (FieldDeclarationNode)node;

			resolveTypeDefinition(f.getTypeNode());

			currentType.addField(f.getName(), f.getTypeNode().getTypeVariable(), f.getImutabilityValue());
	
		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;

			if (m.getReturnType().needsInference()){
				Optional<ReturnNode> op = m.findChild(ReturnNode.class);
				
				if (op.isPresent()){
					
					ReturnNode r = op.get();
					
					TreeTransverser.transverse( node, new SemanticVisitor(this.getSemanticContext()));;
					
					m.replace(m.getReturnType(), new TypeNode(r.getTypeVariable()));
				} else {
					// void 
					m.replace(m.getReturnType(),new TypeNode(LenseTypeSystem.Void()));
				}
			}

			IntervalTypeVariable typeParameter = resolveTypeDefinition(m.getReturnType());

			lense.compiler.type.variable.TypeVariable returnTypeVariable;
			if (typeParameter.getLowerBound().equals(typeParameter.getUpperBound())){
				returnTypeVariable = typeParameter;
			} else {
				String typeName = m.getReturnType().getName();
				Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);


				if (!opIndex.isPresent()){
					throw new CompilationError( m.getReturnType(), typeName + " is not a generic type parameter in type " + currentType.getName());
				}

				returnTypeVariable = new TypeMemberDeclaringTypeVariable(null, opIndex.get());
			}


			ParametersListNode parameters = m.getParameters();
			MethodParameter[] params = asMethodParameters(parameters);

			Method method = new Method(m.getName(), new MethodReturn(returnTypeVariable), params);
			currentType.addMethod(method);
			
		} else if (node instanceof PropertyDeclarationNode){
			PropertyDeclarationNode p = (PropertyDeclarationNode)node;

			String typeName = p.getType().getName();
			VariableInfo genericParameter = this.getSemanticContext().currentScope().searchVariable(typeName);

			if (genericParameter != null && genericParameter.isTypeVariable()){

				Optional<Integer> index =currentType.getGenericParameterIndexBySymbol(typeName);
				if (!index.isPresent()){
					throw new CompilationError(node, typeName + " is not a valid type or generic parameter");
				}

				TypeVariable pp = new TypeMemberDeclaringTypeVariable(null, index.get());

				if (p.isIndexed()){
					lense.compiler.type.variable.TypeVariable[] params = new  lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode)p).getIndexes().getChildren().size()];
					int i =0;
					for (AstNode n :  ((IndexerPropertyDeclarationNode)p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeVariable();
					}

					currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}

			} else {
				if (p.isIndexed()){
					lense.compiler.type.variable.TypeVariable[] params = new  lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode)p).getIndexes().getChildren().size()];
					int i =0;
					for (AstNode n :  ((IndexerPropertyDeclarationNode)p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeVariable();
					}

					currentType.addIndexer(p.getType().getTypeVariable(), p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), p.getType().getTypeVariable(), p.getAcessor() != null, p.getModifier() != null);
				}
			}
		}  

	}

	private TypeNode inferType(TypeNode t) {

		if (t.getParent().getParent() instanceof  lense.compiler.ast.ForEachNode){
			lense.compiler.ast.ForEachNode f = (lense.compiler.ast.ForEachNode)t.getParent().getParent();

			return new InferedTypeNode( () -> f.getContainer());
		} else {
			throw new CompilationError(t, "Impossible to infer type with parent " + t.getParent());
		}

	}


	private ConstructorParameter[] asConstructorParameters(ParametersListNode parameters) {
		MethodParameter[] params = asMethodParameters(parameters);
		ConstructorParameter[] cparams = new ConstructorParameter[params.length];

		for (int i =0; i < params.length; i++){
			cparams[i] = new ConstructorParameter(params[i].getType(), params[i].getName());
		}

		return cparams;
	}

	private MethodParameter[] asMethodParameters(ParametersListNode parameters) {

		for (AstNode p : parameters.getChildren()){
			FormalParameterNode f = (FormalParameterNode)p;
			resolveTypeDefinition(f.getTypeNode());
		}


		MethodParameter[] params = (parameters == null) ? new MethodParameter[0] : new MethodParameter[parameters.getChildren().size()];

		for (int i = 0; i < params.length; i++) {
			FormalParameterNode var = (FormalParameterNode) parameters.getChildren().get(i);
			if (var.getTypeVariable() == null){

				Optional<Integer> opIndex = var.getTypeNode().getTypeParameter().getSymbol().flatMap(s -> currentType.getGenericParameterIndexBySymbol(s));


				if (!opIndex.isPresent()){
					throw new CompilationError(parameters, var.getTypeNode().getTypeParameter().getSymbol() + " is not a generic type parameter in type " + currentType.getName());
				}
				lense.compiler.type.variable.TypeVariable tv = new TypeMemberDeclaringTypeVariable(null, opIndex.get());

				params[i] = new MethodParameter(tv, var.getName());
			} else {
				params[i] = new MethodParameter(var.getTypeVariable(), var.getName());
			}

		}
		return params;
	}



}
