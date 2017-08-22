package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.context.SemanticContext;
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
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 *  - Verify correct contract of native implementations
 *  - convert arithmetic operators to method calls  
 */
public class JavalizeVisitor implements Visitor<AstNode>{

    private SemanticContext semanticContext;
    private Map<String, File> nativeTypes;
    private Map<String, LenseTypeDefinition> nativeLoadedTypes = new HashMap<>();
    private ByteCodeTypeDefinitionReader asmReader = new ByteCodeTypeDefinitionReader();

    public JavalizeVisitor(SemanticContext semanticContext, Map<String, File> nativeTypes) {
        this.semanticContext = semanticContext;
        this.nativeTypes = nativeTypes;
    }

    @Override
    public void startVisit() {	}

    @Override
    public void endVisit() {}

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
            type.setSuperTypeDefinition(this.loadByName(type.getSuperDefinition().getName()));
        }
    }

    @Override
    public void visitAfterChildren(AstNode node) {	

        if (node instanceof lense.compiler.ast.ClassTypeNode){
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



                    TypeDefinition type = n.getSemanticContext().resolveTypeForName(n.getName(), n.getGenericParametersCount()).get();


                    List<Constructor> construtors = type.getMembers().stream().filter( c -> c.isConstructor()).map(c -> (Constructor)c).collect(Collectors.toList());
                    List<Constructor> nativeConstrutors = nativeType.getMembers().stream().filter( c -> c.isConstructor()).map(c -> (Constructor)c).collect(Collectors.toList());

                    List<Method> methods = type.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).collect(Collectors.toList());
                    List<Method> nativeMethods = nativeType.getAllMembers().stream().filter( c -> c.isMethod()).map(c -> (Method)c).collect(Collectors.toList());


                    if (nativeConstrutors.isEmpty() && type.getKind() == LenseUnitKind.Class && !type.isAbstract()){
                        throw new lense.compiler.CompilationError(node, "No native constructor implemented");
                    }

                    if (!type.isAbstract()){
                        for(Constructor c : construtors){

                            checkMatch(node,
                                    c, 
                                    isContainedIn(c,nativeConstrutors).orElseThrow(() -> 
                                    new lense.compiler.CompilationError(node, "Native implementation does not contain constructor " + c.getName() + "(" + c.getParameters() +")")
                                            )
                                    );

                        }
                    }


                    for(Method c : methods){
                        checkMatch(node,
                                c, 
                                isContainedIn(c,nativeMethods).orElseThrow(() -> 
                                new lense.compiler.CompilationError(node, "Native implementation does not contain method " + c)
                                        )
                                );

                    }

                    List<Property> properties = type.getMembers().stream().filter( c -> c.isProperty()).map(c -> (Property)c).collect(Collectors.toList());
                    List<Property> nativeProperties = nativeType.getMembers().stream().filter( c -> c.isProperty()).map(c -> (Property)c).collect(Collectors.toList());


                    for(Property c : properties){
                        checkMatch(node,
                                c, 
                                isContainedIn(c,nativeProperties).orElseThrow(() -> 
                                new lense.compiler.CompilationError(node, "Native implementation does not contain property " + c.getName())
                                        )
                                );

                    }

                    List<IndexerProperty> indexers = type.getMembers().stream().filter( c -> c.isIndexer()).map(c -> (IndexerProperty)c).collect(Collectors.toList());
                    List<IndexerProperty> nativeIndexers = nativeType.getMembers().stream().filter( c -> c.isIndexer()).map(c -> (IndexerProperty)c).collect(Collectors.toList());


                    for(IndexerProperty c : indexers){

                        checkMatch(node,
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
        else if (node instanceof MethodInvocationNode){
            MethodInvocationNode m = (MethodInvocationNode)node;


            if (m.getAccess() != null && !((lense.compiler.ast.TypedNode)m.getAccess()).getTypeVariable().getGenericParameters().isEmpty() ){

                if ( m.getTypeVariable() instanceof FixedTypeVariable){
                    return ;
                }
                AstNode parent = m.getParent();
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


    private void checkMatch(AstNode node,TypeMember c, TypeMember n) {
        if (n.isAbstract() && !c.isAbstract()){
            throw new lense.compiler.CompilationError(node, "Native implementation abstractness does not match for member " + c.getName() + "(found " + n.isAbstract() + ", expected " + c.isAbstract() + ")");
        } else if (c.getVisibility() != n.getVisibility()){
            throw new lense.compiler.CompilationError(node, "Native implementation visibility does not match for member " + c.getName() + "(found " + n.getVisibility() + ", expected " + c.getVisibility() + ")");
        }
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
