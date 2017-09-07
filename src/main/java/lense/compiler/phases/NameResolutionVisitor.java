/**
 * 
 */
package lense.compiler.phases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.Import;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.LiteralAssociationInstanceCreation;
import lense.compiler.ast.LiteralIntervalNode;
import lense.compiler.ast.LiteralSequenceInstanceCreation;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StaticAccessNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * Distinguishes names of variables and types. Also pre-sets the variable to its
 * scope
 */
public class NameResolutionVisitor extends AbstractScopedVisitor {

    final ClassTypeNode ct;
    final Set<String> genericNames = new HashSet<String>();

    final List<FieldOrPropertyAccessNode> possibleSelfAccess = new LinkedList<>();

    public NameResolutionVisitor(ClassTypeNode ct) {
        super(ct.getSemanticContext());
        this.ct = ct;
    }


    @Override
    protected Optional<LenseTypeDefinition> getCurrentType() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    public VisitorNext doVisitBeforeChildren(AstNode node) {

        if (node instanceof ClassTypeNode) {
            ClassTypeNode n = (ClassTypeNode) node;

            //n.setName(this.getSemanticContext().getCurrentPackageName() + "." + n.getName());

            String name = n.getName();
            Optional<TypeDefinition> type = this.getSemanticContext().resolveTypeForName(name, n.getGenericParametersCount());

            LenseTypeDefinition currentType;
            if (!type.isPresent()){

                List<IntervalTypeVariable> genericVariables = new ArrayList<>(0);

                if (n.getGenerics() != null) {


                    genericVariables = new ArrayList<>(n.getGenerics().getChildren().size());

                    for (AstNode a : n.getGenerics().getChildren()) {
                        GenericTypeParameterNode g = (GenericTypeParameterNode)a;

                        TypeNode tn = g.getTypeNode();

                        genericVariables.add(new RangeTypeVariable(tn.getName(), lense.compiler.typesystem.Variance.Invariant , LenseTypeSystem.Any(), LenseTypeSystem.Nothing()));

                    }

                }

                currentType =  new LenseTypeDefinition(name, n.getKind(), null, genericVariables);

                if (n.getGenericParametersCount() != currentType.getGenericParameters().size()){
                    throw new IllegalStateException("Generics parameters count does not match");
                }


                if (n.getSuperType() != null){


                    Optional<Import> match = matchImports(ct, n.getSuperType().getName());

                    if (!match.isPresent()) {
                        handleTypeMissing(n.getSuperType().getName(),n, n.getSuperType());

                        type = this.getSemanticContext().resolveTypeForName( n.getSuperType().getName(),n.getSuperType().getTypeParametersCount());

                        if (!type.isPresent()) {
                            throw new CompilationError(node,
                                    "Type " + n.getSuperType().getName() + " was not imported in " + n.getName());
                        }

                        this.getSemanticContext().currentScope().defineVariable("super", new FixedTypeVariable((LenseTypeDefinition)type.get()), node);
                    } else {
                        String typeName = match.get().getTypeName().toString();

                        type = this.getSemanticContext().resolveTypeForName( typeName,n.getSuperType().getTypeParametersCount());

                        if (!type.isPresent()) {
                            LenseTypeDefinition defType = new LenseTypeDefinition( match.get().getTypeName().toString(),LenseUnitKind.Class , null);
                            type = Optional.of(this.getSemanticContext().registerType(defType, n.getSuperType().getTypeParametersCount()));
                        }


                        this.getSemanticContext().currentScope().defineVariable("super", new FixedTypeVariable((LenseTypeDefinition)type.get()), node);
                    }


                }
                currentType =  (LenseTypeDefinition) this.getSemanticContext().registerType(currentType, n.getGenericParametersCount());
            } else {

                currentType = (LenseTypeDefinition) type.get();
            }

            this.getSemanticContext().currentScope().defineVariable("this", new FixedTypeVariable(currentType), node);


        } else if (node instanceof NumericValue){
            NumericValue n = (NumericValue)node;

            if (!ct.getName().equals(n.getTypeVariable().getTypeDefinition().getSimpleName())){
                Optional<Import> match = matchImports(ct, n.getTypeVariable().getTypeDefinition().getSimpleName());

                if (match.isPresent()) {
                    match.get().setMemberCalled(true);
                    return VisitorNext.Siblings;
                } else {
                    ct.imports().add(new Import( 
                            new QualifiedNameNode( 
                                    n.getTypeVariable().getTypeDefinition().getName()) , 
                            n.getTypeVariable().getTypeDefinition().getSimpleName(), false
                            ));

                    matchImports(ct, n.getTypeVariable().getTypeDefinition().getSimpleName())
                    .orElseThrow(() -> new CompilationError(node,"Cannot find " +  n.getTypeVariable().getTypeDefinition().getSimpleName()))
                    .setMemberCalled(true);

                }

            } 

        } else if (node instanceof LiteralSequenceInstanceCreation) {
            LiteralSequenceInstanceCreation array = ((LiteralSequenceInstanceCreation) node);
            ArgumentListNode args = array.getArguments();

            TreeTransverser.transverse(args, this);

            TypedNode type = (TypedNode) args.getFirst().getFirstChild();
            array.getTypeNode().addParametricType(
                    new GenericTypeParameterNode(
                            new TypeNode(type.getTypeVariable()),
                            lense.compiler.typesystem.Variance.Covariant
                            )
                    );

            return VisitorNext.Siblings;

        } else if (node instanceof LiteralAssociationInstanceCreation) {
            LiteralAssociationInstanceCreation map = ((LiteralAssociationInstanceCreation) node);
            ArgumentListNode args = map.getArguments();

            TypeNode typeNode = ((NewInstanceCreationNode) args.getFirstChild().getFirstChild()).getTypeNode();
            // make all arguments have the same type.
            for (int i = 1; i < args.getChildren().size(); i++) {
                final NewInstanceCreationNode classInstanceCreation = (NewInstanceCreationNode) args.getChildren()
                        .get(i).getFirstChild();
                classInstanceCreation.replace(classInstanceCreation.getTypeNode(), typeNode);
            }
            TypeDefinition pairType = this.getSemanticContext().resolveTypeForName(typeNode.getName(), 2).get();
            typeNode.setTypeVariable(new FixedTypeVariable(pairType));

            TreeTransverser.transverse(args, this);

            NewInstanceCreationNode instance = (NewInstanceCreationNode) args.getChildren().get(0).getFirstChild();

            TypedNode key = (TypedNode) instance.getArguments().getChildren().get(0).getFirstChild();
            TypedNode value = (TypedNode) instance.getArguments().getChildren().get(1).getFirstChild();

            map.getTypeNode().addParametricType(new GenericTypeParameterNode(new TypeNode(key.getTypeVariable()),
                    lense.compiler.typesystem.Variance.Invariant));
            map.getTypeNode().addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable()),
                    lense.compiler.typesystem.Variance.Invariant));
            typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(key.getTypeVariable()),
                    lense.compiler.typesystem.Variance.Invariant));
            typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable()),
                    lense.compiler.typesystem.Variance.Invariant));
            typeNode.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.specify(typeNode.getTypeVariable(),
                    key.getTypeVariable(), value.getTypeVariable())));

            return VisitorNext.Siblings;
        }  else if (node instanceof FieldDeclarationNode) {
            FieldDeclarationNode fieldDeclarationNode = (FieldDeclarationNode) node;

            Optional<Import> match = matchImports(ct, fieldDeclarationNode.getTypeNode().getName());

            if (match.isPresent()) {
                match.get().setMemberCalled(true);

                fieldDeclarationNode.getTypeNode().setName(match.get().getTypeName());
                return VisitorNext.Siblings;
            }

            handleTypeMissing(ct.getName(), node, fieldDeclarationNode.getTypeNode());

        } else if (node instanceof TypeNode) {
            TypeNode typeNode = (TypeNode) node;

            if (typeNode.needsInference()){
                return VisitorNext.Siblings;
            }

            if (typeNode.getTypeVariable() != null) {
                return VisitorNext.Siblings;
            }
            // generic type variables are ignored
            if (genericNames.contains(typeNode.getName())) {
                return VisitorNext.Siblings;
            }

            // match the type to one of the imports.
            Optional<Import> match = matchImports(ct, typeNode.getName());

            if (match.isPresent()) {
                if (node.getParent() instanceof ImplementedInterfacesNode
                        || (ct.getSuperType() != null && ct.getSuperType().getName().equals(typeNode.getName()))) {
                    match.get().setMemberCalled(true);
                }
                typeNode.setName(match.get().getTypeName());
                return VisitorNext.Children;
            }

            // match the type to one of the global imports, namely
            // lense.core.lang
            for (Import i : ct.imports()) {
                if (i.isContainer()) {

                    Optional<TypeDefinition> libraryType = this.getSemanticContext().resolveTypeForName(
                            i.getTypeName().getName() + "." + typeNode.getName(), typeNode.getTypeParametersCount());

                    if (libraryType.isPresent()) {
                        typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
                        final Import implicitType = Import.singleType(new QualifiedNameNode(""), typeNode.getName());
                        implicitType.setUsed(true);
                        ct.addImport(implicitType);
                        return VisitorNext.Children;
                    }

                }
            }

            // match implicit imports
            Optional<TypeDefinition> libraryType = this.getSemanticContext().resolveTypeForName(typeNode.getName(),
                    typeNode.getTypeParametersCount());

            if (libraryType.isPresent()) {
                typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
                final Import implicitType = Import.singleType(new QualifiedNameNode(typeNode.getName()),
                        typeNode.getName());
                implicitType.setUsed(true);
                ct.addImport(implicitType);
                return VisitorNext.Children;
            }

            if (!ct.getName().endsWith(typeNode.getName())) {
                // its not the type it self.

                if (node.getParent() instanceof GenericTypeParameterNode) {
                    genericNames.add(typeNode.getName());
                    return VisitorNext.Siblings;
                }

                this.handleTypeMissing(ct.getName(), node.getParent(), typeNode);

            }

        } else if (node instanceof MethodInvocationNode) {
            MethodInvocationNode invokeNode = (MethodInvocationNode) node;

            if (invokeNode.getAccess() instanceof QualifiedNameNode) {
                QualifiedNameNode original = ((QualifiedNameNode) invokeNode.getAccess());
                resolveName(ct, node, invokeNode.getAccess(), original);
            }

            return VisitorNext.Children;
        } else if (node instanceof FieldOrPropertyAccessNode) {
            FieldOrPropertyAccessNode fieldNode = (FieldOrPropertyAccessNode) node;

            if (fieldNode.getPrimary() instanceof QualifiedNameNode) {
                QualifiedNameNode original = ((QualifiedNameNode) fieldNode.getPrimary());
                return resolveName(ct, node, fieldNode.getPrimary(), original);
            }

            return VisitorNext.Children;

        } else if (node instanceof InstanceOfNode){
            InstanceOfNode n = (InstanceOfNode)node;
            TypeNode typeNode = (TypeNode) n.getChildren().get(1);

            Optional<Import> match = matchImports(ct, typeNode.getName());

            if (match.isPresent()){
                typeNode.setName(match.get().getTypeName());
            }
        } 

        return VisitorNext.Children;
    }

    private void handleTypeMissing(String name, AstNode node, TypeNode typeNode) {

        if (typeNode.getName().equals(name)){
            Optional<TypeDefinition> libraryType = this.getSemanticContext().resolveTypeForName(name,typeNode.getTypeParametersCount());
            if (libraryType.isPresent()) {
                typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
                final Import implicitType = Import.singleType(new QualifiedNameNode(name),typeNode.getName());
                implicitType.setUsed(true);
                ct.addImport(implicitType);
                return;
            }
        } 

        if (LenseTypeSystem.Any().getName().equals(name)){
            final Import implicitType = Import.singleType(new QualifiedNameNode(name),typeNode.getName());
            implicitType.setUsed(true);
            ct.addImport(implicitType);
            return;
        }


        // try to find it in the core

        if (!tryDefaultPath(typeNode)){
            throw new CompilationError(node, "Type " + typeNode.getName() + " was not imported in " + name);
        }


    }

    static final List<String> paths = Arrays.asList("lense.core.collections","lense.core.math","lense.core.lang");

    private boolean tryDefaultPath(TypeNode typeNode){

        for(String path : paths) {

            String fullType = path + "." + typeNode.getName();

            Optional<TypeDefinition> libraryType = this.getSemanticContext().resolveTypeForName(fullType,typeNode.getTypeParametersCount());

            if (libraryType.isPresent()) {
                typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
                final Import implicitType = Import.singleType(new QualifiedNameNode(fullType),typeNode.getName());
                implicitType.setUsed(true);
                ct.addImport(implicitType);
                return true;
            }
        }

        return false;
    }


    private VisitorNext resolveName(ClassTypeNode ct, AstNode parent, AstNode child, QualifiedNameNode original) {

        QualifiedNameNode qn = original;
        // Optional<TypeDefinition> maybeType =
        // this.getSemanticContext().resolveTypeForName(qn.getName(), 0);
        //
        // while(!maybeType.isPresent()){
        // qn = qn.getPrevious();
        // if (qn != null){
        // maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);
        // } else {
        // break;
        // }
        // }
        //
        // if (maybeType.isPresent()){
        // return VisitorNext.Children;
        //
        // }

        Optional<Import> match = matchImports(ct, original.getName());

        if (match.isPresent()) {
            match.get().setMemberCalled(true);
            return VisitorNext.Children;
        }

        String name = original.getFirst().getName();

        VariableInfo varInfo = this.getSemanticContext().currentScope().searchVariable(name);

        if (varInfo == null) {
            // references a type

            match = matchImports(ct, name);
            if (!match.isPresent()) {
                VariableInfo varSelf = this.getSemanticContext().currentScope().searchVariable("this");

                if (!varSelf.getTypeVariable().getSymbol().map(s  -> s.equals(name)).orElse(false)) {
                    // throw new CompilationError(child, "Type " + name + " was
                    // not imported");
                }

            }


            qn = original.getNext();

            if (qn != null) {
                StaticAccessNode sn = new StaticAccessNode(new TypeNode(original.getFirst()));

                FieldOrPropertyAccessNode fieldAccess = new FieldOrPropertyAccessNode(qn.getFirst().getName());
                fieldAccess.setPrimary(sn);
                fieldAccess.setScanPosition(child.getScanPosition());

                qn = qn.getNext();
                while (qn != null) {
                    FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(qn.getFirst().getName());
                    f.setPrimary(fieldAccess);
                    fieldAccess = f;

                    qn = qn.getNext();
                }

                TypeVariable type = fieldAccess.getTypeVariable();

                if (type == null){

                }
                VariableInfo varSelf = this.getSemanticContext().currentScope().searchVariable("this");

                if (!type.equals(varSelf.getTypeVariable())) {
                    match = matchImports(ct, type);

                    if (match.isPresent()) {
                        match.get().setMemberCalled(true);
                    }
                }

                original.getParent().replace(original, fieldAccess);
            } else {
                FieldOrPropertyAccessNode selfPropertyAccess = new FieldOrPropertyAccessNode(original.getFirst().toString());
                selfPropertyAccess.setKind(FieldKind.PROPERTY);

                TypeDefinition selfType = this.getSemanticContext().currentScope().searchVariable("this").getTypeVariable().getTypeDefinition();
                Optional<Property> property = selfType.getPropertyByName(selfPropertyAccess.getName());

                if (!property.isPresent() && selfType.getSuperDefinition() != null){
                    property = selfType.getSuperDefinition().getPropertyByName(selfPropertyAccess.getName());
                }

                if (property.isPresent()){
                    selfPropertyAccess.setType(property.get().getReturningType());
                } else {
                    this.possibleSelfAccess.add(selfPropertyAccess);
                }


                original.getParent().replace(original, selfPropertyAccess);
            }

            return VisitorNext.Children;
        } else {
            // references a variable

            // This is maybe a field.
            VariableReadNode vn = new VariableReadNode(varInfo.getName(), varInfo);
            qn = original.getNext();

            if (qn == null) {
                TypeVariable type = vn.getTypeVariable();
                VariableInfo varSelf = this.getSemanticContext().currentScope().searchVariable("this");

                if (!type.equals(varSelf.getTypeVariable())) {
                    match = matchImports(ct, type);

                    if (match.isPresent()) {
                        match.get().setMemberCalled(true);
                    }
                }

            } else {
                FieldOrPropertyAccessNode fieldAccess = new FieldOrPropertyAccessNode(qn.getFirst().getName());
                fieldAccess.setPrimary(vn);

                qn = qn.getNext();
                while (qn != null) {
                    FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(qn.getFirst().getName());
                    f.setPrimary(fieldAccess);
                    fieldAccess = f;

                    qn = qn.getNext();
                }

                TypeVariable type = fieldAccess.getTypeVariable();
                VariableInfo varSelf = this.getSemanticContext().currentScope().searchVariable("this");

                if (!type.equals(varSelf.getTypeVariable())) {
                    match = matchImports(ct, type);

                    if (match.isPresent()) {
                        match.get().setMemberCalled(true);
                    }
                }
            }

            return VisitorNext.Children;
        }

    }

    private Optional<Import> matchImports(ClassTypeNode ct, String nameAlias) {

        if (ct.getSimpleName().equals(nameAlias)){
            return Optional.of(new Import(new QualifiedNameNode(ct.getName()), nameAlias, false));
        }

        for (Import i : ct.imports()) {
            if (i.getMatchAlias().equals(nameAlias)) {
                i.setUsed(true);
                return Optional.of(i);
            }
        }
        int pos = nameAlias.lastIndexOf('.');
        if (pos > 0){
            nameAlias = nameAlias.substring(pos + 1);
            return matchImports(ct, nameAlias);
        }
        return Optional.empty();
    }

    private Optional<Import> matchImports(ClassTypeNode ct, TypeVariable type) {
        for (Import i : ct.imports()) {
            if (type.getSymbol().map( s -> s.equals(i.getTypeName().toString())).orElse(Boolean.FALSE)) {
                i.setUsed(true);
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doVisitAfterChildren(AstNode node) {

        if (node instanceof ImplementedInterfacesNode){

            for(AstNode a : node.getChildren()){
                TypeNode t = (TypeNode)a;

                Optional<Import> match = matchImports(ct, t.getName());

                if (match.isPresent()) {
                    match.get().setMemberCalled(true);
                }
            }
        } else if (node instanceof ForEachNode){

            ct.addImport(Import.singleType(new QualifiedNameNode("lense.core.collections.Iterable"), "Iterable"));

            Optional<Import> match = matchImports(ct, "lense.core.collections.Iterable");

            if (match.isPresent()) {
                match.get().setMemberCalled(true);
            }

        } else if (node instanceof MethodDeclarationNode) {
            MethodDeclarationNode m = (MethodDeclarationNode)node;

            if (m.getReturnType().getName() != null && m.getBlock() != null && !genericNames.contains(m.getReturnType().getName()) && !this.getSemanticContext().currentScope().getCurrentType().getName().endsWith(m.getReturnType().getName())){
                Optional<Import> match = matchImports(ct, m.getReturnType().getName());

                if (match.isPresent()) {
                    match.get().setMemberCalled(true);

                    m.getReturnType().setName(match.get().getTypeName());
                    return;
                }

                this.handleTypeMissing(ct.getName(), node, m.getReturnType());

            }

        }else if (node instanceof PropertyDeclarationNode){
            PropertyDeclarationNode n = (PropertyDeclarationNode)node;

            String name = n.getType().getName();
            if ((n.getAcessor() != null || n.getModifier() != null) && !genericNames.contains(name) && !this.getSemanticContext().currentScope().getCurrentType().getName().endsWith(name)){
                Optional<Import> match = matchImports(ct, name);

                if (match.isPresent()) {
                    match.get().setMemberCalled(true);

                    n.getType().setName(match.get().getTypeName());
                    return;
                }
                this.handleTypeMissing(ct.getName(), node, n.getType());

            }

        } else if (node instanceof ScopedVariableDefinitionNode) {
            ScopedVariableDefinitionNode variableDeclaration = (ScopedVariableDefinitionNode) node;

            TypeNode typeNode = variableDeclaration.getTypeNode();
            if (typeNode.needsInference()){
                return;
            }
            Optional<TypeDefinition> type = this.getSemanticContext().resolveTypeForName(typeNode.getName(),
                    typeNode.getTypeParametersCount());

            if (!type.isPresent()) {
                LenseTypeDefinition defType = new LenseTypeDefinition(typeNode.getName(), null, null);
                type = Optional.of(this.getSemanticContext().registerType(defType, typeNode.getTypeParametersCount()));
            }
            VariableInfo info = this.getSemanticContext().currentScope().defineVariable(variableDeclaration.getName(),
                    new FixedTypeVariable(type.get()), node);

            variableDeclaration.setInfo(info);

        } else if (node instanceof FormalParameterNode) {
            FormalParameterNode variableDeclaration = (FormalParameterNode) node;

            TypeNode typeNode = variableDeclaration.getTypeNode();
            if (typeNode != null) {
                LenseTypeDefinition type = new LenseTypeDefinition(typeNode.getName(), null, null);
                this.getSemanticContext().currentScope().defineVariable(variableDeclaration.getName(),
                        new FixedTypeVariable(type), node);
            }

        } else if (node instanceof LiteralTupleInstanceCreation) {
            LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation) node);
            AstNode t = tuple.getChildren().get(1).getFirstChild();
            TypedNode value = (TypedNode) t.getChildren().get(0);

            TypeNode typeNode = new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple"));
            typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable())));

            TypedNode nextTuple;
            if (tuple.getChildren().get(1).getFirstChild().getChildren().size() == 2) {
                nextTuple = (TypedNode) t.getChildren().get(1);
                typeNode.addParametricType(
                        new GenericTypeParameterNode(new TypeNode(new QualifiedNameNode("lense.core.lang.Any"))));

            } else {
                nextTuple = new TypeNode(LenseTypeSystem.Nothing());
                typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(nextTuple.getTypeVariable())));
            }

            tuple.replace(tuple.getTypeNode(), typeNode);
        } else if (node instanceof LiteralIntervalNode) {


            Optional<Import> match = matchImports(ct, "lense.core.math.Interval");

            if (match.isPresent()) {
                match.get().setMemberCalled(true);
            }


        } else if (node instanceof NewInstanceCreationNode) {
            NewInstanceCreationNode constructorCallNode = (NewInstanceCreationNode) node;


            Optional<Import> match = matchImports(ct, constructorCallNode.getTypeNode().getName());

            if (match.isPresent()) {
                match.get().setMemberCalled(true);
            }


        }

    }

}
