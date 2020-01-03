/**
 * 
 */
package lense.compiler.phases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.CompilerListener;
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
import lense.compiler.ast.GenericTypeParameterNode;
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
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.MethodSignature;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

/**
 * Read the classe members and fills a SenseType object
 */
public class StructureVisitor extends AbstractScopedVisitor {


	private LenseTypeDefinition currentType;
	private boolean secondPass;
	private CompilerListener listener;

	public StructureVisitor (CompilerListener listener, LenseTypeDefinition currentType, SemanticContext semanticContext , boolean secondPass){
		super(semanticContext);
		this.currentType = currentType;
		this.secondPass = secondPass;
		this.listener = listener;
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

        }   else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode n = (MethodDeclarationNode)node;
			
        	for(AstNode a : n.getMethodScopeGenerics().getChildren()) {
				GenericTypeParameterNode g = (GenericTypeParameterNode)a;
				
				RangeTypeVariable range = new RangeTypeVariable(g.getTypeNode().getName(), g.getVariance(), LenseTypeSystem.Any(), LenseTypeSystem.Nothing());
				
				this.getSemanticContext().currentScope().defineTypeVariable(g.getTypeNode().getName(), range, n);

			}
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
				    
				    if (b.getChildren().size() == 1) {
				    	b.getParent().replace(b, node);
				    } else {
				    	TreeTransverser.transverse(b.getChildren().get(1), new AutoCastVisitor(this.getSemanticContext(), name, type));
				    }
					
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

			Constructor ctr = currentType.addConstructor(f.isImplicit(), f.getName(), params);
			
			f.setAssignedConstructor(ctr);

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
					
					SemanticVisitor sv = new SemanticVisitor(this.getSemanticContext(), this.listener);

					TreeTransverser.transverse( node, sv);
					
					m.replace(m.getReturnType(), new TypeNode(r.getTypeVariable()));
				} else {
					// void 
					m.replace(m.getReturnType(),new TypeNode(LenseTypeSystem.Void()));
				}
			}

			TypeVariable returnTypeVariable = resolveTypeDefinition(m.getReturnType(), Variance.Covariant);


			ParametersListNode parameters = m.getParameters();
			MethodParameter[] params = asMethodParameters(parameters);

			for (GenericTypeParameterNode g : m.getMethodScopeGenerics().getChildren(GenericTypeParameterNode.class)) {
				for (MethodParameter p : params) {
					if (!p.isMethodTypeBound()) {
						p.setMethodTypeBound(isMethodTypeBound(g.getTypeVariable(), p.getType()));
					}
				}
			}
			
		
						
			Visibility visiblity = m.getVisibility();
			
			if (visiblity == null){
			    if (this.currentType.getKind().equals(LenseUnitKind.Interface)){
			        visiblity = Visibility.Public;
			    } else {
			        visiblity = Visibility.Protected;
			    }
			}
			
			MethodSignature signature = new MethodSignature(m.getName(), params);
			
			Method method = new Method(m.isProperty(), visiblity, m.getName(), new MethodReturn(returnTypeVariable), params);
			
			Optional<Method> declaredMethodBySignature = currentType.getDeclaredMethodBySignature(signature);
			if (declaredMethodBySignature.isPresent()){
				if (secondPass) {
					method = declaredMethodBySignature.get();
				} else {
					throw new CompilationError(m, "Method "  + signature + " is already defined in " + currentType.getName());
				}
			} else {
				method.setAbstract(m.isAbstract());
				method.setDefault(m.isDefault());
				method.setOverride(m.isOverride());
				method.setNative(m.isNative());
				
				currentType.addMethod(method);
			}

			m.setMethod(method);
			
	
			
		} else if (node instanceof PropertyDeclarationNode){
			PropertyDeclarationNode p = (PropertyDeclarationNode)node;

			String typeName = p.getType().getName();
			VariableInfo genericParameter = this.getSemanticContext().currentScope().searchVariable(typeName);

			TypeMember property;
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

					property = currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					property = currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
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

					property = currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					property = currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}
			}
			
			property.setAbstract(p.isAbstract());
			property.setDefault(p.isDefault());
			property.setOverride(p.isOverride());
			property.setNative(p.isNative());
			
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



	private boolean isMethodTypeBound(TypeVariable genericParameter, TypeVariable parameterType) {
		
		if (parameterType.getSymbol().flatMap( ps ->  genericParameter.getSymbol().map( gs -> gs.equals(ps))).orElse(false)) {
			return true;
		}
		
		if (!parameterType.getGenericParameters().isEmpty()) {
			for (TypeVariable t : parameterType.getGenericParameters()) {
				if (isMethodTypeBound(genericParameter, t)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private ConstructorParameter[] asConstructorParameters(ParametersListNode parameters) {
		MethodParameter[] params = asMethodParameters(parameters);
		List<ConstructorParameter> cparams = new ArrayList<>(params.length);

		for (int i =0; i < params.length; i++){
			
			if (!params[i].getType().toString().equals("lense.core.lang.reflection.ReifiedArguments")){
				cparams.add( new ConstructorParameter(params[i].getType(), params[i].getName()));
			}
		}

		return cparams.toArray(new ConstructorParameter[cparams.size()]);
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

				MethodParameter mp  = new MethodParameter(tv, var.getName());
				mp.setMethodTypeBound(var.isMethodTypeBound());
				params[i] = mp;
			} else {
				MethodParameter mp  = new MethodParameter(var.getTypeVariable(), var.getName());
				mp.setMethodTypeBound(var.isMethodTypeBound());
				params[i] = mp;
			}

		}
		return params;
	}






}
