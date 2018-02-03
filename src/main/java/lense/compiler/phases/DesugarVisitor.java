package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.AssignmentNode.Operation;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ObjectReadNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.PropertyOperation;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * Desugar properties to method calls. Desugar comparison operations to a call
 * to compareTo
 */
public class DesugarVisitor extends AbstractLenseVisitor {

    private SemanticContext semanticContext;
    private String innerPropertyPrefix;

    public DesugarVisitor(SemanticContext sc, String innerPropertyPrefix) {
        this.semanticContext = sc;
        this.innerPropertyPrefix = innerPropertyPrefix;
    }

    @Override
    protected SemanticContext getSemanticContext() {
        return semanticContext;
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

        if (node instanceof IndexerPropertyDeclarationNode) {
            IndexerPropertyDeclarationNode n = (IndexerPropertyDeclarationNode) node;
            AstNode parent = node.getParent();

            parent.remove(node);

            if (n.getAcessor() != null) {
                AccessorNode a = n.getAcessor();

                MethodDeclarationNode getter = new MethodDeclarationNode();
                getter.setSetter(false);
                getter.setProperty(true);
                getter.setIndexer(true);
                getter.setName("get");

                if (n.isNative()) {
                    getter.setNative(n.isNative());
                } else if (n.isAbstract()) {
                    // TODO ensure is abstract if type is not class
                    getter.setAbstract(n.isAbstract());
                } else {
                    getter.setBlock(a.getBlock());
                }
                getter.setReturnType(n.getType());
                getter.setVisibility(a.getVisibility() == null ? n.getVisibility() : a.getVisibility());

                getter.setParameters(n.getIndexes());
                parent.add(getter);
            }

            if (n.getModifier() != null) {
                ModifierNode a = n.getModifier();

                MethodDeclarationNode setter = new MethodDeclarationNode();

                setter.setSetter(true);
                setter.setProperty(true);
                setter.setIndexer(true);
                setter.setName("set");

                String parameterName = a.getValueVariableName();

                if (n.isNative()) {
                    parameterName = "value";
                    setter.setNative(n.isNative());
                } else if (n.isAbstract()) {
                    parameterName = "value";
                    setter.setAbstract(n.isAbstract());
                } else {
                    setter.setBlock(a.getBlock());
                }

                FormalParameterNode valueParameter = new FormalParameterNode();
                valueParameter.setTypeNode(n.getType());
                valueParameter.setName(parameterName);

                ParametersListNode parameters = new ParametersListNode();

                for (AstNode p : n.getIndexes().getChildren()) {
                    parameters.add(p);

                }
                parameters.add(valueParameter);

                setter.setParameters(parameters);
                setter.setVisibility(a.getVisibility() == null ? n.getVisibility() : a.getVisibility());

                setter.setReturnType(new TypeNode(LenseTypeSystem.Void()));

                parent.add(setter);
            }

        } else if (node instanceof PropertyDeclarationNode) {
            PropertyDeclarationNode prp = (PropertyDeclarationNode) node;

            String propertyName = resolvePropertyName(prp.getName());
            String privateFieldName = resolvePropertyInnerName(prp.getName());

            AstNode parent = node.getParent();

            if (!prp.isAbstract() && !prp.isNative() && prp.getModifier() != null && prp.getInitializer() == null
                    && !prp.isInicializedOnConstructor()) {
                if (!prp.getType().getTypeVariable().getTypeDefinition().getName()
                        .equals(LenseTypeSystem.Maybe().getName())) {
                    throw new CompilationError(prp, "Property " + prp.getName()
                            + " is not initialized. Initialize it or consider making it optional");
                }

                prp.setInitializer(new ObjectReadNode(LenseTypeSystem.None(), "NONE"));

            }

            TypeVariable typeVariable = prp.getType().getTypeVariable();

            if (typeVariable == null) {
                typeVariable = prp.getType().getTypeParameter();
            }

            FieldOrPropertyAccessNode backingField = new FieldOrPropertyAccessNode(privateFieldName);
            backingField.setType(typeVariable);
            backingField.setPrimary(new QualifiedNameNode("this"));
            
            boolean shoudlGenerateBackingField = false;

            if (prp.getAcessor() != null) {

                AccessorNode a = prp.getAcessor();

                MethodDeclarationNode getter = new MethodDeclarationNode();
                getter.setSetter(false);
                getter.setPropertyName(prp.getName());
                getter.setProperty(true);

                getter.setName("get" + propertyName);
                if (prp.isNative()) {
                    getter.setNative(prp.isNative());
                } else if (prp.isAbstract()) {
                    // TODO ensure is abstract if type is not class
                    getter.setAbstract(prp.isAbstract());
                } else {
                    BlockNode block = a.getBlock();

                    if (a.isImplicit()) { // create access to backing-field
                        shoudlGenerateBackingField = true;

                        // TODO validate in semantics that is accessor is
                        // implicit modifier also is implicit
                        block = new BlockNode();
                        ReturnNode rnode = new ReturnNode();
                        block.add(rnode);

                        rnode.add(backingField);

                    } else {
                        shoudlGenerateBackingField = translateBackingFieldReference(block, prp.getName(), backingField);
                    }
                    getter.setBlock(block);
                }
                getter.setReturnType(prp.getType());
                getter.setVisibility(a.getVisibility());

                parent.add(getter);
            }

            if (prp.getModifier() != null) {
                ModifierNode a = prp.getModifier();

                MethodDeclarationNode setter = new MethodDeclarationNode();
                setter.setName("set" + propertyName);
                setter.setSetter(false);
                setter.setPropertyName(prp.getName());
                setter.setProperty(true);

                String parameterName = a.getValueVariableName();

                if (parameterName.equals(prp.getName())) {
                    throw new CompilationError(a,
                            "Modifier parameter cannot have the same name as the property (" + prp.getName() + ")");
                }

                if (prp.isNative()) {
                    setter.setNative(prp.isNative());
                } else if (prp.isAbstract()) {
                    setter.setAbstract(prp.isAbstract());
                } else {
                    BlockNode block = a.getBlock();

                    if (a.isImplicit()) {
                        shoudlGenerateBackingField = true;

                        block = new BlockNode();
                        AssignmentNode assign = new AssignmentNode(Operation.SimpleAssign);

                        assign.setLeft(backingField);
                        assign.setRight(new VariableReadNode(parameterName,
                                new VariableInfo(parameterName, typeVariable, setter, false, false)));

                        block.add(assign);
                    } else {
                        translateBackingFieldReference(block, prp.getName(), backingField);
                    }
                    setter.setBlock(block);
                }

                FormalParameterNode valueParameter = new FormalParameterNode();
                valueParameter.setTypeNode(prp.getType());
                valueParameter.setName(parameterName);

                ParametersListNode parameters = new ParametersListNode();
                parameters.add(valueParameter);

                setter.setParameters(parameters);
                setter.setVisibility(a.getVisibility());
                setter.setReturnType(new TypeNode(LenseTypeSystem.Void()));

                parent.add(setter);
            }

            if (shoudlGenerateBackingField && !prp.isAbstract()) {
                FieldDeclarationNode privateField = new FieldDeclarationNode(privateFieldName, prp.getType(),
                        NewInstanceCreationNode.of(prp.getType()));
                privateField.setTypeNode(prp.getType());
                privateField.setImutability( new ImutabilityNode(prp.getImutability()));
                
                if (prp.isInicializedOnConstructor()) {
                    privateField.setImutability(new ImutabilityNode(Imutability.Imutable));
                    privateField.setInitializedOnConstructor(true);
                } else {
                    if (prp.getInitializer() != null) {
                        privateField.setInitializer(prp.getInitializer());
                    }
                }

                parent.add(privateField);
            }

            parent.remove(node);

        } else if (node instanceof FieldOrPropertyAccessNode) {
            FieldOrPropertyAccessNode n = (FieldOrPropertyAccessNode) node;

            if (n.getKind() == FieldKind.PROPERTY) {
                String propertyName = resolvePropertyName(n.getName());

                if (n.getParent() instanceof AssignmentNode && ((AssignmentNode) n.getParent()).getLeft() == node) {
                    ExpressionNode value = ((AssignmentNode) n.getParent()).getRight();

                    ArgumentListItemNode arg = new ArgumentListItemNode(0, value);
                    arg.setExpectedType(n.getTypeVariable());

                    // is write access
                    MethodInvocationNode invokeSet = new MethodInvocationNode(n.getPrimary(), "set" + propertyName, arg);
                    invokeSet.setPropertyDerivedMethod(true);
                    invokeSet.setPropertyDerivedName(propertyName);
                    invokeSet.setPropertyOperation(PropertyOperation.WRITE);
                    invokeSet.setTypeVariable(LenseTypeSystem.Void());
                    invokeSet.setAccess(n.getPrimary());

                    n.getParent().getParent().replace(n.getParent(), invokeSet);

                } else {

                    // is read acesss
                    MethodInvocationNode invokeGet = new MethodInvocationNode(n.getPrimary(), "get" + propertyName);
                    invokeGet.setPropertyDerivedMethod(true);
                    invokeGet.setPropertyDerivedName(propertyName);
                    invokeGet.setPropertyOperation(PropertyOperation.READ);
                    invokeGet.setTypeVariable(n.getTypeVariable());
                    invokeGet.setAccess(n.getPrimary());

                    n.getParent().replace(node, invokeGet);
                }
            }
        } else if (node instanceof IndexedAccessNode) {
            IndexedAccessNode n = (IndexedAccessNode) node;

            ArgumentListNode list = new ArgumentListNode();
            for (AstNode a : n.getArguments().getChildren()) {
                ArgumentListItemNode arg = (ArgumentListItemNode) a;
                list.add(arg);
            }

            if (n.getParent() instanceof AssignmentNode && ((AssignmentNode) n.getParent()).getLeft() == node) {
                ExpressionNode value = ((AssignmentNode) n.getParent()).getRight();
                // is write access

                ArgumentListItemNode arg = new ArgumentListItemNode(n.getArguments().getChildren().size() + 1, value);
                arg.setExpectedType(n.getTypeVariable());
                list.add(arg);

                MethodInvocationNode invokeSet = new MethodInvocationNode(n.getAccess(), "set", list);
                invokeSet.setIndexDerivedMethod(true);
                invokeSet.setPropertyOperation(PropertyOperation.WRITE);
                invokeSet.setTypeVariable(LenseTypeSystem.Void());
                node.getParent().getParent().replace(n.getParent(), invokeSet);

            } else {
                // is read access
                MethodInvocationNode invokeGet = new MethodInvocationNode(n.getAccess(), "get", list);
                invokeGet.setIndexDerivedMethod(true);
                invokeGet.setPropertyOperation(PropertyOperation.READ);
                invokeGet.setTypeVariable(n.getTypeVariable());
                node.getParent().replace(node, invokeGet);

            }

        } else if (node instanceof ComparisonNode) {
            ComparisonNode n = (ComparisonNode) node;
            // convert to compareTo or equals call

            ArgumentListItemNode arg = new ArgumentListItemNode(0, n.getRight());
            arg.setExpectedType(n.getRight().getTypeVariable());

            if (n.getOperation() == ComparisonNode.Operation.EqualTo) {

                MethodInvocationNode invocation;

                if (isZero(n.getRight())) {
                    invocation = new MethodInvocationNode(n.getLeft(), "isZero");
                    invocation.setTypeVariable(n.getTypeVariable());

                } else if (isZero(n.getLeft())) {
                    invocation = new MethodInvocationNode(n.getRight(), "isZero");
                    invocation.setTypeVariable(n.getTypeVariable());

                } else {
                    invocation = new MethodInvocationNode(n.getLeft(), "equalsTo", arg);
                    invocation.setTypeVariable(n.getTypeVariable());
                }

                node.getParent().replace(node, invocation);

            } else if (n.getOperation() == ComparisonNode.Operation.Different) {

                MethodInvocationNode invocation;

                if (isZero(n.getRight())) {
                    invocation = new MethodInvocationNode(n.getLeft(), "isZero");
                    invocation.setTypeVariable(n.getTypeVariable());

                } else if (isZero(n.getLeft())) {
                    invocation = new MethodInvocationNode(n.getRight(), "isZero");
                    invocation.setTypeVariable(n.getTypeVariable());

                } else {
                    invocation = new MethodInvocationNode(n.getLeft(), "equalsTo", arg);
                    invocation.setTypeVariable(n.getTypeVariable());
                }

                PreBooleanUnaryExpression not = new PreBooleanUnaryExpression(BooleanOperation.LogicNegate, invocation);

                node.getParent().replace(node, not);

            } else if (n.getOperation() == ComparisonNode.Operation.ReferenceEquals) {
                // no-op
            } else if (n.getOperation() == ComparisonNode.Operation.ReferenceDifferent) {
                // no-op
            } else {

                MethodInvocationNode compareTo = new MethodInvocationNode(n.getLeft(), "compareWith", arg);
                compareTo.setTypeVariable(LenseTypeSystem.Natural());

                if (n.getOperation() == ComparisonNode.Operation.Compare) {
                    node.getParent().replace(node, compareTo);
                } else {

                    boolean rightIsZero = isZero(n.getRight());
                    boolean leftIsZero = isZero(n.getLeft());

                    if (rightIsZero || leftIsZero) {
                        String methodName = "isZero";

                        boolean negate = false;
                        switch (n.getOperation()) {
                        case GreaterOrEqualTo:
                            negate = true;
                        case LessThan:
                            methodName = "isNegative";
                            break;
                        case LessOrEqualTo:
                            negate = true;
                        case GreaterThan:
                            methodName = "isPositive";
                            break;
                        default:
                            // no-op
                        }

                        MethodInvocationNode invocation = new MethodInvocationNode(
                                rightIsZero ? n.getLeft() : n.getRight(), methodName);
                        invocation.setTypeVariable(n.getTypeVariable());

                        if (negate) {
                            PreBooleanUnaryExpression not = new PreBooleanUnaryExpression(BooleanOperation.LogicNegate,
                                    invocation);
                            node.getParent().replace(node, not);
                        } else {
                            node.getParent().replace(node, invocation);
                        }

                    } else {

                        ObjectReadNode parameter = new ObjectReadNode(
                                semanticContext.resolveTypeForName("lense.core.math.Equal", 0).get(), "Equal");

                        boolean negate = false;
                        switch (n.getOperation()) {
                        case GreaterOrEqualTo:
                            negate = true;
                        case LessThan:
                            parameter = new ObjectReadNode(
                                    semanticContext.resolveTypeForName("lense.core.math.Smaller", 0).get(), "Smaller");
                            break;
                        case LessOrEqualTo:
                            negate = true;
                        case GreaterThan:
                            parameter = new ObjectReadNode(
                                    semanticContext.resolveTypeForName("lense.core.math.Greater", 0).get(), "Greater");
                            break;
                        default:
                            // no-op
                        }

                        ArgumentListItemNode parg = new ArgumentListItemNode(0, parameter);
                        parg.setExpectedType(parameter.getTypeVariable());

                        MethodInvocationNode equalsTo = new MethodInvocationNode(compareTo, "equalsTo", parg);
                        equalsTo.setTypeVariable(n.getTypeVariable());

                        if (negate) {
                            PreBooleanUnaryExpression not = new PreBooleanUnaryExpression(BooleanOperation.LogicNegate,
                                    equalsTo);
                            node.getParent().replace(node, not);
                        } else {
                            node.getParent().replace(node, equalsTo);
                        }
                    }

                }

            }
        }

    }

    private boolean isZero(ExpressionNode right) {
        return right instanceof NumericValue && ((NumericValue) right).isZero();
    }

    public String resolvePropertyName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String resolvePropertyInnerName(String propertyName) {
        if (innerPropertyPrefix == null) {
            return propertyName;
        } else {
            return innerPropertyPrefix + propertyName;
        }

    }

    private boolean translateBackingFieldReference(BlockNode block, String propertyName,
            FieldOrPropertyAccessNode backingField) {

        final ExplicitBackingFieldReferenceVisitor visitor = new ExplicitBackingFieldReferenceVisitor(propertyName,
                backingField);
        TreeTransverser.transverse(block, visitor);

        return visitor.didReplacedProperty();
    }

}
