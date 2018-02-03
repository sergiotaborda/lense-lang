/**
 * 
 */
package lense.compiler.phases;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.TypeAlreadyDefinedException;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
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
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;
/**
 * Read the classe members and fills a SenseType object
 */
public class StructureVisitor extends AbstractScopedVisitor {


	private LenseTypeDefinition currentType;

	public StructureVisitor (LenseTypeDefinition currentType, SemanticContext semanticContext){
		super(semanticContext);
		this.currentType = currentType;
	}
	
    @Override
    protected Optional<LenseTypeDefinition> getCurrentType() {
        return Optional.of(currentType);
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
                TypeVariable f = map.values().iterator().next();
                typeNode.setTypeVariable(f);
                typeNode.setTypeParameter(f);
            }

            
            TreeTransverser.transverse(n.getExpression(), this);

        }   
		return VisitorNext.Children;
	}
	

	private void propagateType(AstNode node, TypeNode typeNode, String name) {
		AstNode parent = node.getParent();
		
	     if (parent instanceof BooleanOperatorNode){
			BooleanOperatorNode b = (BooleanOperatorNode)parent ;

			if (b.getChildren().get(0) == node){ // this the left side
				if (b.getOperation() == BooleanOperation.LogicShortAnd){
				    TypeVariable type = typeNode.getTypeVariable();
				    if (type == null){
				        throw new IllegalStateException("type cannot be null");
				    }
					TreeTransverser.transverse(b.getChildren().get(1), new AutoCastVisitor(this.getSemanticContext(), name, type));
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
		
		} else if (parent instanceof AssertNode) {
		    
		    List<AstNode> siblings = parent.getParent().getChildren();
		    
		    boolean found = false;
		    for (AstNode a : siblings){
		        
		        if (found){
		            TreeTransverser.transverse(a, new AutoCastVisitor(this.getSemanticContext(), name,typeNode.getTypeVariable() ));
                }

		        if (a == parent){
		            found = true;
		        }
		        
		        
		    }
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
				resolveTypeDefinition((TypeNode)node, Variance.Invariant); // TODO read parent node to determine variance
			}

		} else if (node instanceof ConstructorDeclarationNode){
			ConstructorDeclarationNode f = (ConstructorDeclarationNode)node;

			ConstructorParameter[] params = asConstructorParameters(f.getParameters());

			currentType.addConstructor(f.isImplicit(), f.getName(), params);

		} else 	if (node instanceof FieldDeclarationNode){
			FieldDeclarationNode f = (FieldDeclarationNode)node;

			resolveTypeDefinition(f.getTypeNode(),Variance.Invariant);

			currentType.addField(f.getName(), f.getTypeNode().getTypeVariable(), f.getImutabilityValue());
	
		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;

			if (m.getReturnType().needsInference()){
				Optional<ReturnNode> op = m.findFirstChild(ReturnNode.class);
				
				if (op.isPresent()){
					
					ReturnNode r = op.get();
					
					TreeTransverser.transverse( node, new SemanticVisitor(this.getSemanticContext()));;
					
					m.replace(m.getReturnType(), new TypeNode(r.getTypeVariable()));
				} else {
					// void 
					m.replace(m.getReturnType(),new TypeNode(LenseTypeSystem.Void()));
				}
			}

			TypeVariable returnTypeVariable = resolveTypeDefinition(m.getReturnType(), Variance.Covariant);
//
//			lense.compiler.type.variable.TypeVariable returnTypeVariable;
//			if (!typeParameter.getGenericParameters().isEmpty()) {
//				List<TypeVariable> list = typeParameter.getGenericParameters();
//				
//				if (list.size() == 1) {
//					TypeDefinition typeDefinition = typeParameter.getTypeDefinition();
//					
//					if (currentType.getGenericParameters().isEmpty()) {
//						TypeVariable f = currentType.getGenericParameters().get(0);
//						
//						returnTypeVariable = new GenericTypeBoundToDeclaringTypeVariable(typeDefinition, this.currentType, 0,  f.getSymbol().get(), Variance.ContraVariant);
//						
//					} else if (currentType.getGenericParameters().size() == 1) {
//						
//						TypeVariable f = currentType.getGenericParameters().get(0);
//						
//						returnTypeVariable = new GenericTypeBoundToDeclaringTypeVariable(typeDefinition, this.currentType, 0,  f.getSymbol().get(), Variance.ContraVariant);
//						
//					} else {
//						TypeVariable firstType = list.get(0);
//						
//						if (firstType.isFixed()) {
//							returnTypeVariable = firstType;
//						} else {
//							Optional<String> symbol = firstType.getSymbol();
//							Optional<Integer> opIndex =  symbol.flatMap( n -> currentType.getGenericParameterIndexBySymbol(n));
//
//							if (!opIndex.isPresent()){
//								throw new CompilationError( m.getReturnType(), symbol.get() + " is not a generic type parameter in type " + currentType.getName());
//							}
//							
//							
//							if (typeDefinition.getName().equals(this.currentType.getName())) {
//								typeDefinition= this.currentType;
//							}
//							
//							returnTypeVariable = new GenericTypeBoundToDeclaringTypeVariable(typeDefinition, this.currentType, opIndex.get().intValue(),  symbol.get(), Variance.ContraVariant);
//							
//						}
//					}
//
//				} else {
//					throw new UnsupportedOperationException("More than one generic parameter is not suppoerted yet");
//				}
//			} else if (typeParameter.getLowerBound().equals(typeParameter.getUpperBound())){
//				returnTypeVariable = typeParameter;
//			} else {
//				String typeName = m.getReturnType().getName();
//				Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);
//
//
//				if (!opIndex.isPresent()){
//					throw new CompilationError( m.getReturnType(), typeName + " is not a generic type parameter in type " + currentType.getName());
//				}
//
//				// TODO recover the member
//				returnTypeVariable = new DeclaringTypeBoundedTypeVariable(currentType, opIndex.get(),typeName,  Variance.Covariant);
//			}


			ParametersListNode parameters = m.getParameters();
			MethodParameter[] params = asMethodParameters(parameters);

			Visibility visiblity = m.getVisibility();
			
			if (visiblity == null){
			    if (this.currentType.getKind().equals(LenseUnitKind.Interface)){
			        visiblity = Visibility.Public;
			    } else {
			        visiblity = Visibility.Protected;
			    }
			}
			Method method = new Method(visiblity, m.getName(), new MethodReturn(returnTypeVariable), params);
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

				TypeVariable pp = new DeclaringTypeBoundedTypeVariable(this.currentType, index.get(),  typeName, Variance.Covariant);

				if (p.isIndexed()){
					lense.compiler.type.variable.TypeVariable[] params = new  lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode)p).getIndexes().getChildren().size()];
					int i =0;
					for (AstNode n :  ((IndexerPropertyDeclarationNode)p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeParameter();
					}

					currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}

			} else {
				
				TypeVariable pp = p.getType().getTypeVariable();
				
				if (pp == null) {
					pp = p.getType().getTypeParameter();
					
					if (pp == null) {
						visitAfterChildren(p.getType());
						
						pp = p.getType().getTypeVariable();
					}
				}
				if (p.isIndexed()){
					lense.compiler.type.variable.TypeVariable[] params = new  lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode)p).getIndexes().getChildren().size()];
					int i =0;
					for (AstNode n :  ((IndexerPropertyDeclarationNode)p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeParameter();
					}

					currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}
			}
		}  else if (node instanceof InstanceOfNode) {
            InstanceOfNode n = (InstanceOfNode)node;

            TypeNode typeNode = n.getTypeNode();
            Map<Integer, TypeDefinition> map = this.getSemanticContext().typeAllForName( typeNode.getName());

            if (map.size() == 1){
               TypeVariable f = map.values().iterator().next();
                typeNode.setTypeVariable(f);
                typeNode.setTypeParameter(f);
            }

            if (n.getExpression() instanceof VariableReadNode) {
                VariableReadNode var = (VariableReadNode) n.getExpression();

                propagateType(node, typeNode, var.getName());
            } else if (n.getExpression() instanceof FieldOrPropertyAccessNode){
                FieldOrPropertyAccessNode var = (FieldOrPropertyAccessNode) n.getExpression();

                propagateType(node, typeNode, var.getName());
            } 
            
            TreeTransverser.transverse(n.getExpression(), this);

        }   

	}

//	private TypeNode inferType(TypeNode t) {
//
//		if (t.getParent().getParent() instanceof  lense.compiler.ast.ForEachNode){
//			lense.compiler.ast.ForEachNode f = (lense.compiler.ast.ForEachNode)t.getParent().getParent();
//
//			return new InferedTypeNode( () -> f.getContainer());
//		} else {
//			throw new CompilationError(t, "Impossible to infer type with parent " + t.getParent());
//		}
//
//	}


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
			resolveTypeDefinition(f.getTypeNode(), Variance.ContraVariant);
		}


		MethodParameter[] params = (parameters == null) ? new MethodParameter[0] : new MethodParameter[parameters.getChildren().size()];

		for (int i = 0; i < params.length; i++) {
			FormalParameterNode var = (FormalParameterNode) parameters.getChildren().get(i);
			if (var.getTypeVariable() == null){

				Optional<Integer> opIndex = var.getTypeNode().getTypeParameter().getSymbol().flatMap(s -> currentType.getGenericParameterIndexBySymbol(s));


				if (!opIndex.isPresent()){
					throw new CompilationError(parameters, var.getTypeNode().getTypeParameter().getSymbol() + " is not a generic type parameter in type " + currentType.getName());
				}
				lense.compiler.type.variable.TypeVariable tv = new DeclaringTypeBoundedTypeVariable(currentType, opIndex.get(), var.getTypeNode().getTypeParameter().getSymbol().get(), Variance.ContraVariant);

				params[i] = new MethodParameter(tv, var.getName());
			} else {
				params[i] = new MethodParameter(var.getTypeVariable(), var.getName());
			}

		}
		return params;
	}






}
