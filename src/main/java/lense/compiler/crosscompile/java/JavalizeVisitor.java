package lense.compiler.crosscompile.java;

import java.util.List;
import java.util.ListIterator;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.TypeAssistant;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.ContraVariantTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

/**
 * 
 *  - Verify correct contract of native implementations
 *  - convert arithmetic operators to method calls  
 */
public final class JavalizeVisitor implements Visitor<AstNode>{

    private final TypeAssistant typeAssistant;
   
    public JavalizeVisitor(SemanticContext semanticContext, UpdatableTypeRepository typeContainer) {
        this.typeAssistant = new LenseTypeAssistant(semanticContext);
    }

    @Override
    public void visitAfterChildren(AstNode node) {	

        if (node instanceof lense.compiler.ast.ClassTypeNode){
            ClassTypeNode n = (ClassTypeNode)node;

            if (n.getKind() == lense.compiler.type.LenseUnitKind.Object ){
            	TypeDefinition type = n.getSemanticContext().resolveTypeForName(n.getFullname(), n.getGenericParametersCount()).get().getTypeDefinition();

            	 boolean hashValue = type.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).filter(m -> m.isOverride() && m.getName().equals("hashValue")).findFirst().isPresent();
            	 boolean equals = type.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).filter(m -> m.isOverride() &&m.getName().equals("equalsTo")).findFirst().isPresent();
            	 boolean asString = type.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).filter(m -> m.isOverride() && m.getName().equals("asString")).findFirst().isPresent();
            	 
            	 if (!(hashValue ^ equals) && hashValue !=  equals) {
            		   throw new lense.compiler.CompilationError(node, "Methods hashValue and equalsTo are corelated. You have to override both or none");
            	 }
            	 
            	 if (!(hashValue && equals)) {
            		 // both are missing
            		 node.setProperty(JavalizePhase.AutoGenerateHashCodeAndEquals, true);
            	 }
            	 
            	 if (!asString) {
            		 // both are missing
            		 
            		 node.setProperty(JavalizePhase.AutoGenerateAsString, true);
            		 

            	 }
            }
        } 
        else if (node instanceof MethodInvocationNode){
            MethodInvocationNode m = (MethodInvocationNode)node;


            if (m.getAccess() != null 
            		&& ((lense.compiler.ast.TypedNode)m.getAccess()).getTypeVariable() != null
            		&& !((lense.compiler.ast.TypedNode)m.getAccess()).getTypeVariable().getGenericParameters().isEmpty() ){

                if ( m.getTypeVariable().isFixed()){
                    return ;
                }
                AstNode parent = m.getParent();
                
                if (m.getTypeVariable().isSingleType()) {
                    TypeDefinition typeDefinition = m.getTypeVariable().getTypeDefinition();
                    if (typeDefinition.getName().equals("lense.core.lang.Void")){
                        return;
                    }

                    if (parent instanceof BooleanOperatorNode && typeDefinition.getName().equals("lense.core.lang.Boolean")){
                        // lense.core.lang.Boolean is already been erased inside BooleanOperatorNode
                        return;
                    }
                    CastNode cast = new CastNode(m, typeDefinition);
                    parent.replace(node, cast);
                }
            
            }
        }
        else if (node instanceof MethodDeclarationNode){
        	MethodDeclarationNode m = (MethodDeclarationNode)node;

        	if (m.getSuperMethod() != null && !m.isAbstract()) {
        		
        		Method superMethod = m.getSuperMethod();
        		
        		List<CallableMemberMember<Method>> params = superMethod.getParameters();
        		
        		for (int i=0; i < params.size(); i++) {
        			
        			MethodParameter superParameter = (MethodParameter) params.get(i);
        			FormalParameterNode n = (FormalParameterNode) m.getParameters().getChildren().get(i);
        			
        			// TODO should not be necessary to compare types at this point
        			if (!typeAssistant.isAssignableTo(superParameter.getType(), n.getTypeVariable()).matches() ) {
        				
        				
        				
        				// no match
        				
        				String newName = "_" + n.getName();
        						
        				
        				VariableReadNode read = new VariableReadNode(newName);
        				VariableDeclarationNode variable = new VariableDeclarationNode(n.getName() ,n.getTypeVariable(),  new CastNode(read, n.getTypeVariable()));
        				variable.setImutability(new ImutabilityNode(Imutability.Imutable));
        				
        				n.setTypeVariable(superParameter.getType());
        				n.setName(newName);
        				
        				m.getBlock().addFirst(variable);
        			}
        				
        		}
        	}
        	
        	if (m.getMethod() != null) {
            	ListIterator<FormalParameterNode> itFormal = m.getParameters().getChildren(FormalParameterNode.class).listIterator(m.getParameters().getChildren().size());
            	ListIterator<CallableMemberMember<Method>> itMethod = m.getMethod().getParameters().listIterator( m.getMethod().getParameters().size());
            	
            	// iterate in reverse because formal parameters may have special parameters like reification types
            	while (itMethod.hasPrevious()) {
            		FormalParameterNode f = itFormal.previous();
            		MethodParameter p = (MethodParameter)itMethod.previous();
            		
            		if (p.getVariance() == Variance.ContraVariant && !(f.getTypeVariable() instanceof ContraVariantTypeVariable)) {
            			
            			TypeVariable originalType = f.getTypeVariable();
            			
            			
            			f.setTypeVariable(new ContraVariantTypeVariable(originalType));
            			f.getTypeNode().setTypeVariable(new ContraVariantTypeVariable(originalType));
            			
            			if (!m.isAbstract()) {
            				String paramName = f.getName();
                			String newName = "$$" + paramName;
                			f.setName(newName);
                			f.setTypeVariable(LenseTypeSystem.Any());
                			f.getTypeNode().setTypeVariable(LenseTypeSystem.Any());
                			f.getTypeNode().setName(new QualifiedNameNode(LenseTypeSystem.Any().getName()));
                			// newName will be Any , so a cast is needed
                			
                			CastNode cast = new CastNode(new VariableReadNode(newName), originalType);
                			VariableDeclarationNode variable =  new VariableDeclarationNode(paramName, originalType, cast);
                			m.getBlock().addFirst(variable);
                		
            			}
            		
            		}
            	}
        	}

        }
    }


}
