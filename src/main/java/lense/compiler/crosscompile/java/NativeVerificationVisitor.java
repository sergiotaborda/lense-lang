package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.CallableMember;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.Constructor;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Visibility;

/**
 * 
 *  - Verify correct contract of native implementations
 *  - convert arithmetic operators to method calls  
 */
public final class NativeVerificationVisitor implements Visitor<AstNode>{

    private final Map<String, File> nativeTypes;
    private final Map<String, LenseTypeDefinition> nativeLoadedTypes = new HashMap<>();
    private final ByteCodeTypeDefinitionReader asmReader;
	private final SemanticContext semanticContext;

    public NativeVerificationVisitor(SemanticContext semanticContext, Map<String, File> nativeTypes, UpdatableTypeRepository typeContainer) {
    	this.semanticContext = semanticContext;
        this.nativeTypes = nativeTypes;
        this.asmReader = new ByteCodeTypeDefinitionReader(typeContainer);
    }

    @Override
    public VisitorNext visitBeforeChildren(AstNode node) {
        return VisitorNext.Children;
    }


    private LenseTypeDefinition loadByName(String name) throws IOException {

        LenseTypeDefinition def = nativeLoadedTypes.get(name);
        if (def != null) {
            return def;
        }

        File classFile = nativeTypes.get(name);
        def =  (LenseTypeDefinition)asmReader.readNative(classFile);

        loadDependencies(def);

        nativeLoadedTypes.put(name, def);
        return def;
    }

    private void loadDependencies(LenseTypeDefinition type) throws IOException {
        if (type.getSuperDefinition() != null && !type.getSuperDefinition().getName().equals("lense.core.lang.Any")) {
            LenseTypeDefinition superType = this.loadByName(type.getSuperDefinition().getName());
            
            if (!superType.isPlataformSpecific()){
                type.setSuperTypeDefinition(superType);
            } else {
                type.setSuperTypeDefinition(this.loadByName("lense.core.lang.Any"));
            }
          
        }
    }

    List<Constructor> construtors = new  LinkedList<Constructor>();
    
    @Override
    public void visitAfterChildren(AstNode node) {	
    	
    	if (node instanceof ConstructorDeclarationNode) {
    		ConstructorDeclarationNode cn = ((ConstructorDeclarationNode)node);
    		

    		construtors.add(cn.getAssignedConstructor());
    		
    	} else if (node instanceof lense.compiler.ast.ClassTypeNode){
            ClassTypeNode n = (ClassTypeNode)node;

            if (n.isNative()){
                if (!nativeTypes.containsKey(n.getName())){
                    throw new lense.compiler.CompilationError(node, "Native implementation for type " + n.getName() + " is missing");
                }

                if (n.getName().equals("lense.core.lang.Any")){
                    return;
                }

                // verify correct contract

                try {
                    TypeDefinition nativeType = loadByName(n.getName());


                     Optional<TypeVariable> stype = semanticContext.resolveTypeForName(n.getName(), n.getTypeDefinition().getGenericParameters().size());
                    
                     
                     if (!stype.isPresent()) {
                    	 stype = Optional.of(n.getTypeDefinition());
                     }
                     
                     TypeDefinition type = stype.get().getTypeDefinition();


                    List<Constructor> construtors = type.getMembers().stream().filter( c -> c.isConstructor()).map(c -> (Constructor)c).collect(Collectors.toList());
                    List<Constructor> nativeConstrutors = nativeType.getMembers().stream().filter( c -> c.isConstructor()).map(c -> (Constructor)c).collect(Collectors.toList());

                    List<Method> methods = type.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).collect(Collectors.toList());
                    List<Method> nativeMethods = nativeType.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).collect(Collectors.toList());


                    if (nativeConstrutors.isEmpty() && n.getKind() == LenseUnitKind.Class && !n.isAbstract()){
                        throw new lense.compiler.CompilationError(node, "No native constructor implemented");
                    }

                    if (!n.isAbstract()){
                        for(Constructor c : construtors){

                            checkMatch(n,
                                    c, 
                                    isContainedIn(c,nativeConstrutors).orElseThrow(() -> 
                                    new lense.compiler.CompilationError(node, "Native implementation does not contain constructor " + c.getName() + "(" + c.getParameters() +")")
                                            )
                                    );

                        }
                    }


                    for(Method c : methods){
                        checkMatch(n,
                                c, 
                                isContainedIn(c,nativeMethods).orElseThrow(() -> 
                                new lense.compiler.CompilationError(node, "Native implementation does not contain method " + c)
                                        )
                                );

                    }

                    List<Property> properties = type.getMembers().stream().filter( c -> c.isProperty()).map(c -> (Property)c).collect(Collectors.toList());
                    List<Property> nativeProperties = nativeType.getMembers().stream().filter( c -> c.isProperty()).map(c -> (Property)c).collect(Collectors.toList());


                    for(Property c : properties){
                        checkMatch(n,
                                c, 
                                isContainedIn(c,nativeProperties).orElseThrow(() -> 
                                new lense.compiler.CompilationError(node, "Native implementation does not contain property " + c.getName())
                                        )
                                );

                    }

                    List<IndexerProperty> indexers = type.getMembers().stream().filter( c -> c.isIndexer()).map(c -> (IndexerProperty)c).collect(Collectors.toList());
                    List<IndexerProperty> nativeIndexers = nativeType.getMembers().stream().filter( c -> c.isIndexer()).map(c -> (IndexerProperty)c).collect(Collectors.toList());


                    for(IndexerProperty c : indexers){

                        checkMatch(n,
                                c, 
                                isContainedIn(c,nativeIndexers).orElseThrow(() -> 
                                new lense.compiler.CompilationError(node, "Native implementation does not contain indexer " + c.getName())
                                        )
                                );

                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else if (n.getKind() == lense.compiler.type.LenseUnitKind.Class ){
                if (nativeTypes.containsKey(n.getName())){
   
                    try {
                        LenseTypeDefinition nativeType = loadByName(n.getName());

                        if (nativeType.isNative()){
                            throw new lense.compiler.CompilationError(node, "Found native implementation for type " + n.getName() + " but type is not marked as native. Did you intended to mark " + n.getName() + " as native ?");	
                        }

                        if (nativeType.getKind() != n.getKind()){ 
                            throw new lense.compiler.CompilationError(node, "Native implementation is not of the same kind (expected " + n.getKind() + ", found " + nativeType.getKind());
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            } else if (n.getKind() == lense.compiler.type.LenseUnitKind.Interface ){
                if (nativeTypes.containsKey(n.getName())){
   
                    try {
                        LenseTypeDefinition nativeType = loadByName(n.getName());

                        if (nativeType.isNative()){
                            throw new lense.compiler.CompilationError(node, "Found native implementation for type " + n.getName() + " but type is not marked as native. Did you intended to mark " + n.getName() + " as native ?"); 
                        }

                        if (nativeType.getKind() != n.getKind()){ 
                            throw new lense.compiler.CompilationError(node, "Native implementation is not of the same kind (expected " + n.getKind() + ", found " + nativeType.getKind());
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            } 
        }
    }


    private void checkMatch(ClassTypeNode node,TypeMember declaredMember, TypeMember nativeMember) {
    	
    	// allow that native visibility can be greater than declared visibility
        if (!isVisibilityCompatible(declaredMember.getVisibility() ,nativeMember.getVisibility())){
            throw new lense.compiler.CompilationError(node, "Native implementation visibility does not match for member '" + declaredMember.getName() + "'(found " + nativeMember.getVisibility() + ", expected " + declaredMember.getVisibility() + ")");
        }
        
        // allow matching abstractness
        // allow for the native to be abstract if the source is not
        // NOT allow for the native to be NOT abstract if the source is 
        
        if ( declaredMember.getDeclaringType().getName().equals(node.getName()) 
        		&& declaredMember.getDeclaringType().getKind() != LenseUnitKind.Interface 
        		&& declaredMember.isAbstract() 
        		&& !nativeMember.isAbstract()
        ){
            //throw new lense.compiler.CompilationError(node, "Native implementation of an abstract method must also be abstract '" + declaredMember.getName() + "'(found " + nativeMember.isAbstract() + ", expected " + declaredMember.isAbstract() + ")");
        } 
    }

    private boolean isVisibilityCompatible(Visibility declaredVisibility, Visibility nativeVisibility) {
		if (declaredVisibility == null) {
			return true;
		}
		
		switch(declaredVisibility) {
			case Protected:
				return nativeVisibility == Visibility.Protected 
				|| nativeVisibility == Visibility.Public;
			case Public:
				return nativeVisibility == Visibility.Public;
			case Private:
			case Undefined:
				return true;
		}
		
		return false;
	}

	private Optional<IndexerProperty> isContainedIn(IndexerProperty c, List<IndexerProperty> nativeIndexers) {
        for (IndexerProperty n : nativeIndexers){
            if (n.getReturningType().getTypeDefinition().getName().equals(c.getReturningType().getTypeDefinition().getName()) && c.getIndexes().length == n.getIndexes().length ){
                for ( int i =0 ; i < c.getIndexes().length; i++){
                    TypeVariable a = c.getIndexes()[i];
                    TypeVariable b = n.getIndexes()[i];

                    if (!a.getTypeDefinition().getName().equals(b.getTypeDefinition().getName())){
                        return Optional.empty();
                    }
                }
                return Optional.of(n);
            }
        }
        return Optional.empty();
    }

    private Optional<Property> isContainedIn(Property c, List<Property> nativeProperties) {
        for (Property n : nativeProperties){
            if (n.getName().equals(c.getName()) && n.getReturningType().getSymbol().equals(c.getReturningType().getSymbol())){
                return Optional.of(n);
            }
        }
        return Optional.empty();
    }

    private <T extends CallableMember<T>> Optional<T> isContainedIn(T c, List<T> nativeConstrutors) {
        outter : for (T n : nativeConstrutors){
            if (n.getName().equals(c.getName()) && n.getParameters().size() == c.getParameters().size()){

                for ( int i =0 ; i < c.getParameters().size(); i++){
                    CallableMemberMember<T> a = c.getParameters().get(i);
                    CallableMemberMember<T> b = n.getParameters().get(i);

                    if (!(LenseTypeSystem.getInstance().areNomallyEquals(a.getType().getTypeDefinition(),b.getType().getTypeDefinition())  || LenseTypeSystem.getInstance().isPromotableTo( b.getType(), a.getType()))){
                        continue outter;
                    }
                }
                return Optional.of(n);
            }
        }
    return Optional.empty();
    }



}
