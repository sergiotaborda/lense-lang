/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArgumentTypeResolverNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ChildTypeNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ConstructorExtentionNode;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.FieldAccessNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.IndexedPropertyReadNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.LiteralIntervalNode;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NewTypeResolverNode;
import lense.compiler.ast.NoneValue;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ObjectReadNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.StatementNode;
import lense.compiler.ast.StringConcatenationNode;
import lense.compiler.ast.StringValue;
import lense.compiler.ast.SwitchNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.TernaryConditionalExpressionNode;
import lense.compiler.ast.ThowNode;
import lense.compiler.ast.TryStatement;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeParameterTypeResolverNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableReadTypeResolverNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.context.VariableInfo;
import lense.compiler.crosscompile.ErasedTypeDefinition;
import lense.compiler.crosscompile.ErasurePointNode;
import lense.compiler.crosscompile.ErasurePointNode.ErasureOperation;
import lense.compiler.crosscompile.MethodInvocationOnPrimitiveNode;
import lense.compiler.crosscompile.PrimitiveArithmeticOperationsNode;
import lense.compiler.crosscompile.PrimitiveBooleanOperationsNode;
import lense.compiler.crosscompile.PrimitiveBooleanValue;
import lense.compiler.crosscompile.PrimitiveBox;
import lense.compiler.crosscompile.PrimitiveComparisonNode;
import lense.compiler.crosscompile.PrimitiveTypeDefinition;
import lense.compiler.crosscompile.PrimitiveUnbox;
import lense.compiler.phases.ReificationVisitor;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.CalculatedTypeVariable;
import lense.compiler.type.variable.ContraVariantTypeVariable;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Visibility;
import lense.compiler.utils.Strings;

/**
 * 
 */
public class JavaSourceWriterVisitor implements Visitor<AstNode> {

    private PrintWriter writer;

    /**
     * Constructor.
     * 
     * @param writer
     */
    public JavaSourceWriterVisitor(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startVisit() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endVisit() {
        writer.flush();
    }

    private static Set<String> reservedWords = new HashSet<>(
            Arrays.asList("byte", "int", "long", "short", "double", "void", "float"));

    private static String sanitize(String identifier) {
        if (reservedWords.contains(identifier)) {
            return "$" + identifier;
        }
        return identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VisitorNext visitBeforeChildren(AstNode node) {
        try {
            if (node instanceof NoneValue) {
                writer.print("lense.core.lang.None.NONE");
            } else if (node instanceof ThowNode) {
                writer.print("throw ");
            } else if (node instanceof AssertNode) {

                AssertNode a = (AssertNode)node;

                writer.append("if (");
                if (a.getReferenceValue()){
                    writer.append("!");
                }
                writer.append("(");
                TreeTransverser.transverse(a.getCondition(), this);

                if (a.getText().isPresent()) {
                    writer.append(")){ throw lense.core.lang.AssertionException.constructor(");

                    TreeTransverser.transverse(a.getText().get(), this);

                    writer.append(".asString()); }").println();
                } else {
                    writer.append(")){ throw lense.core.lang.AssertionException.constructor(); }").println();
                }


                return VisitorNext.Siblings;
            } else if (node instanceof StringValue) {
                writer.append("lense.core.lang.String.valueOfNative(").append("\"")
                .append(((StringValue) node).getValue()).append("\")");
            } else if (node instanceof BooleanValue) {

                if (node instanceof PrimitiveBooleanValue) {
                    writer.print(((BooleanValue) node).getLiteralValue());
                } else {
                    writer.print(((BooleanValue) node).isValue() ? "lense.core.lang.Boolean.TRUE" : "lense.core.lang.Boolean.FALSE");
                }
            } else if (node instanceof PrimitiveComparisonNode){
                PrimitiveComparisonNode c = (PrimitiveComparisonNode)node;

                if (c.getOperation() == Operation.Compare){
                    // TODO test
                    writer.print("(");
                    TreeTransverser.transverse(node.getChildren().get(0), this);
                    writer.print(" - ");  
                    TreeTransverser.transverse(node.getChildren().get(1), this);
                    writer.print(")");
                } else {
                    TreeTransverser.transverse(node.getChildren().get(0), this);

                    switch (c.getOperation()){
                    case GreaterOrEqualTo:
                        writer.print(" >= ");
                        break;
                    case GreaterThan:
                        writer.print(" > ");
                        break;
                    case LessOrEqualTo:
                        writer.print(" <= ");
                        break;
                    case LessThan:
                        writer.print(" < ");
                        break;
                    case Different:
                    case ReferenceDifferent:
                        writer.print(" != ");
                        break;
                    case EqualTo:
                    case ReferenceEquals:
                        writer.print(" == ");
                        break;
                    case InstanceOf:
                    case Compare:
                        //no-op
                    }

                    TreeTransverser.transverse(node.getChildren().get(1), this);
                }


                return VisitorNext.Siblings;
            } else if (node instanceof PrimitiveBooleanOperationsNode) {
                PrimitiveBooleanOperationsNode n = (PrimitiveBooleanOperationsNode) node;

                if (n.getOperation() == BooleanOperation.LogicNegate) {
                    writer.print("!");
                    TreeTransverser.transverse(n.getChildren().get(0), this);
                } else if (n.getChildren().size() == 2) {

                    TreeTransverser.transverse(n.getChildren().get(0), this);

                    if (n.getOperation() == BooleanOperation.BitOr) {
                        writer.print(" | ");
                    } else if (n.getOperation() == BooleanOperation.BitAnd) {
                        writer.print(" & ");
                    } else if (n.getOperation() == BooleanOperation.BitXor) {
                        writer.print(" ^ ");
                    }

                    TreeTransverser.transverse(n.getChildren().get(1), this);

                } else {
                    throw new UnsupportedOperationException();
                }
                return VisitorNext.Siblings;
            } else if (node instanceof PrimitiveArithmeticOperationsNode){
                PrimitiveArithmeticOperationsNode n = (PrimitiveArithmeticOperationsNode) node;


                switch (n.getOperation()){
                case Addition:
                case Power:
                case Subtraction:
                case Multiplication:
                case Division:
                case IntegerDivision:
                    applyReboxOperator(writer, n.getOperation() , node);
                    break;
                case Symmetric:
                    applyPrePrimitiveOperator(writer, "-", node);
                    break;
                case Decrement:
                    applyPrePrimitiveOperator(writer, "--", node);
                    break;
                case Increment:
                    applyPrePrimitiveOperator(writer, "++", node);
                    break;
                case Complement:
                    applyPrePrimitiveOperator(writer, "~", node);
                    break;
                case BitAnd:
                    applyPrimitiveOperator(writer, "&", node);
                    break;
                case BitOr:
                    applyPrimitiveOperator(writer, "|", node);
                    break;
                case BitXor:
                    applyPrimitiveOperator(writer, "^", node);
                    break;
                case Remainder:
                    applyPrimitiveOperator(writer, "%", node);
                    break;
                case LeftShift:
                    applyPrimitiveOperator(writer, "<<", node);
                    break;
                case RightShift:
                    applyPrimitiveOperator(writer, ">>", node);
                    break;
                case SignedRightShift:
                    applyPrimitiveOperator(writer, ">>>", node);
                    break;
                case WrapAddition:
                    applyPrimitiveOperator(writer, "+", node);
                    break;
                case WrapMultiplication:
                    applyPrimitiveOperator(writer, "*", node);
                    break;
                case WrapSubtraction:
                    applyPrimitiveOperator(writer, "-", node);
                    break;
                case Positive:
                case Concatenation:
                    //no-op
                }

                return VisitorNext.Siblings;

            } else if (node instanceof MethodInvocationOnPrimitiveNode){
                MethodInvocationOnPrimitiveNode p = (MethodInvocationOnPrimitiveNode)node;
                MethodInvocationNode m = p.getMethodInvocation();


                writer.append("lense.core.lang.java.Primitives.").append(m.getCall().getName()).append("(");
                TreeTransverser.transverse(m.getAccess(), this);
                writer.print(")");


                return VisitorNext.Siblings;


            } else if (node instanceof PrimitiveUnbox) {

                PrimitiveUnbox pu = (PrimitiveUnbox)node;

					
                AstNode n = pu.getChildren().get(0);

                if (n instanceof MethodInvocationNode) {
                    MethodInvocationNode m = (MethodInvocationNode) n;
                    if (m.getTypeVariable().isSingleType()) {
                        TreeTransverser.transverse(n, this);
                        return VisitorNext.Siblings;
                    }
                }

                writer.print("/* PRIMITIVE BOXING OUT */");
    

                if (pu.getTypeVariable().equals(PrimitiveTypeDefinition.BOOLEAN)){
                    writer.print("((lense.core.lang.Boolean)");
                    TreeTransverser.transverse(n, this);
                    writer.print(").toPrimitiveBoolean()");
                } else if (pu.getTypeVariable().equals(PrimitiveTypeDefinition.INT)){
                    
                    if (n instanceof NumericValue){
                        writer.print(((NumericValue) n).getValue().toString());
                    } else {
                        writer.print("((lense.core.math.Int32)");
                        TreeTransverser.transverse(n, this);
                        writer.print(").toPrimitiveInt()");
                    }
                    
                } else if (pu.getTypeVariable().equals(PrimitiveTypeDefinition.LONG)){
                    
                    if (n instanceof NumericValue){
                        writer.print(((NumericValue) n).getValue().toString());
                    } else {
                        writer.print("((lense.core.math.Int64)");
                        TreeTransverser.transverse(n, this);
                        writer.print(").toPrimitiveLong()");
                    }
                    
                }  else {
                    throw new UnsupportedOperationException("not implemented yet ");
                }



                return VisitorNext.Siblings;
            } else if (node instanceof PrimitiveBox) {
                PrimitiveBox pu = (PrimitiveBox)node;


                if (pu.getTypeVariable().equals(PrimitiveTypeDefinition.BOOLEAN)){
                    writer.print("/* PRIMITIVE BOXING IN */ lense.core.lang.Boolean.valueOfNative(");
                    TreeTransverser.transverse(node.getChildren().get(0), this);
                    writer.print(")");
                } else if (pu.getTypeVariable().equals(PrimitiveTypeDefinition.INT)){
                    writer.print("/* PRIMITIVE BOXING IN */  lense.core.math.Int32.valueOfNative(");
                    TreeTransverser.transverse(node.getChildren().get(0), this);
                    writer.print(")");
                } else if (pu.getTypeVariable().equals(PrimitiveTypeDefinition.LONG)){
                    writer.print("/* PRIMITIVE BOXING IN */  lense.core.math.Int64.valueOfNative(");
                    TreeTransverser.transverse(node.getChildren().get(0), this);
                    writer.print(")");
                } else {
                    throw new UnsupportedOperationException("not implemented yet ");
                }



                return VisitorNext.Siblings;
            } else if (node instanceof ErasurePointNode) {
                ErasurePointNode box = ((ErasurePointNode) node);
                ErasureOperation operation = box.getErasureOperation();
         
                TypeVariable typeVariable = box.getTypeVariable();

                if (typeVariable != null) {
                    writer.append("/*"  + operation + ( operation == ErasureOperation.CONVERTION ?  "" : (box.isBoxingDirectionOut() ? " OUT" : " IN")) + " to " + typeVariable.getTypeDefinition().getName() + "*/ ");

                } else {
                    writer.append("/*"  + operation + ( operation == ErasureOperation.CONVERTION ?  "" : (box.isBoxingDirectionOut() ? " OUT" : " IN")) + " to ? */ ");

                }
                
                TreeTransverser.transverse(node.getChildren().get(0), this);
                
                return VisitorNext.Siblings;

            } else if (node instanceof IdentifierNode) {
                String identifier = ((IdentifierNode) node).getName();
                writer.print(sanitize(identifier));
            } else if (node instanceof QualifiedNameNode) {
                writer.print(sanitize(((QualifiedNameNode) node).getName()));
            } else if (node instanceof NumericValue) {


                NumericValue n = (NumericValue) node;

                if (n.getTypeVariable() instanceof PrimitiveTypeDefinition){
                    writer.append(n.getValue().toString());
                } else {
                    String name = n.getTypeVariable().getTypeDefinition().getName();
                    if (name.equals("lense.core.lang.Binary")) {

                        writer.append(
                                "lense.core.lang.BitArray.constructor(lense.core.collections.Array.booleanArrayfromNativeNumberString(\"")
                        .append(node.toString()).append("\"))");
                    } else if (name.equals("lense.core.math.Rational") || name.equals("lense.core.math.Real")) {
                        // TODO test bounds (number could be to big , should use string
                        int pos = n.getLiteralValue().indexOf('.');
                        if (pos >= 0) {
                            String intPart = n.getLiteralValue().substring(0, pos);
                            String decPart = n.getLiteralValue().substring(pos + 1);

                            int decPos = decPart.length();
                            StringBuilder decimalPart = new StringBuilder("1");
                            for (int i = 0; i < decPos; i++) {
                                decimalPart.append("0");
                            }
                            if (intPart.equals("0")) {
                                intPart = "";
                            }

                            writer.append("lense.core.math.Rational")
                            .append(".constructor(lense.core.math.NativeNumberFactory.newInteger(").append(intPart)
                            .append(decPart).append("),lense.core.math.NativeNumberFactory.newInteger(")
                            .append(decimalPart).append("))");
	    				} else {
							writer.append("lense.core.math.Rational.valueOf(lense.core.math.NativeNumberFactory.newInteger(").append(n.toString()).append("))");
	    				}
                    } else {
                        // TODO test bounds (number could be to big , should use string

                        QualifiedNameNode qn = new QualifiedNameNode(n.getTypeVariable().getTypeDefinition().getName());
                        String typeName = qn.getLast().getName();
                        if (typeName.equals("Whole") || typeName.equals("Any")) {
                            typeName = "Natural";
                        }

                        if ("Natural".equals(typeName)) {
    						if (n.isZero()) {
    							writer.append("lense.core.math.NativeNumberFactory.naturalZero()");
    						} else if (n.isOne()) {
    							writer.append("lense.core.math.NativeNumberFactory.naturalOne()");
    						} else {
    							writer.append("lense.core.math.NativeNumberFactory.newNatural(\"").append(n.toString()).append("\")");
    						}
    					} else {
    						writer.append("lense.core.math.NativeNumberFactory.new").append(typeName).append("(\"").append(n.toString()).append("\")");
    					}
                    }

                }


            } else if (node instanceof lense.compiler.ast.LiteralSequenceInstanceCreation) {
                writer.append("lense.core.collections.Array.fromAnyArray("); // TODO generate reification

                PrintWriter p = writer;
                StringWriter s = new StringWriter();
                writer = new PrintWriter(s);

                List<AstNode> children = ((lense.compiler.ast.LiteralSequenceInstanceCreation) node).getArguments()
                        .getChildren();

                TreeTransverser.transverse(((ArgumentListItemNode) children.get(0)).getFirstChild(), this);

                writer.print(",");

                Iterator<AstNode> it = children.subList(1, children.size()).iterator();
                while (it.hasNext()) {
                    TreeTransverser.transverse(it.next(), this);
                    if (it.hasNext()) {
                        writer.print(",");
                    }
                }

                writer.append(")");

                writer = p;
                writer.append(s.toString());

                return VisitorNext.Siblings;
            } else if (node instanceof LiteralTupleInstanceCreation) {
                LiteralTupleInstanceCreation tuple = (LiteralTupleInstanceCreation) node;

                writer.append("lense.core.collections.Tuple.valueOf(");

                TreeTransverser.transverse(tuple.getArguments(), this);

                writer.append(")");
                return VisitorNext.Siblings;
            } else if (node instanceof lense.compiler.ast.LiteralAssociationInstanceCreation) {

                writer.append("lense.core.collections.Dictionary.fromKeyValueArray(");

                TreeTransverser.transverse(
                        ((lense.compiler.ast.LiteralAssociationInstanceCreation) node).getArguments(), this);

                writer.append(")");
                return VisitorNext.Siblings;
            } else if (node instanceof TernaryConditionalExpressionNode) {
                TernaryConditionalExpressionNode t = (TernaryConditionalExpressionNode) node;
                writer.print("(");
                TreeTransverser.transverse(t.getCondition(), this);
                writer.print(") ? (");
                TreeTransverser.transverse(t.getThenExpression(), this);
                writer.print(") : (");
                TreeTransverser.transverse(t.getElseExpression(), this);
                writer.print(")");
                return VisitorNext.Siblings;
            } else if (node instanceof LiteralIntervalNode) {
                LiteralIntervalNode interval = (LiteralIntervalNode) node;

                TypeVariable innerType = interval.getTypeVariable().getGenericParameters().get(0);

                String reificationArgs = "lense.core.lang.reflection.JavaReifiedArguments.getInstance().addTypeByName(\""
                        + innerType.getTypeDefinition().getName() + "\")";
                writer.append("lense.core.math.Interval.constructor(").append(reificationArgs).append(",");

                if (interval.isStartInf()) {
                    writer.append("lense.core.lang.None.None");
                } else {
                    writer.append("lense.core.lang.Some.constructor(").append(reificationArgs).append(",");
                    TreeTransverser.transverse(interval.getStart(), this);
                    writer.append(")");
                }

                writer.append(",");

                if (interval.isEndInf()) {
                    writer.append("lense.core.lang.None.None");
                } else {
                    writer.append("lense.core.lang.Some.constructor(").append(reificationArgs).append(",");

                    TreeTransverser.transverse(interval.getEnd(), this);
                    writer.append(")");
                }

                writer.append(");");
                return VisitorNext.Siblings;
            } else if (node instanceof ClassTypeNode) {
                ClassTypeNode t = (ClassTypeNode) node;

                int pos = t.getName().lastIndexOf('.');
                if (pos > 0) {
                    writer.print("package ");
                    writer.print(t.getName().substring(0, pos));
                    writer.print(";\n");
                }

                writer.println();

                // TODO define generic signature
                // variable_name:class_type_bound:interface_type_bounds
                // MapPair<in K,out V> => K:: ; V::
                // MapPair<in K,out V> extends Pair<K,V>=> K:Pair<KA,_>: ; V:Pair<_,VB>:

                StringBuilder generics = new StringBuilder();

                List<AstNode> list = Optional.ofNullable(t.getGenerics()).map(g -> g.getChildren())
                        .orElse(Collections.emptyList());
                if (!list.isEmpty()) {
                    generics.append("[");
                    for (int i = 0; i < list.size(); i++) {
                        lense.compiler.ast.GenericTypeParameterNode n = (lense.compiler.ast.GenericTypeParameterNode) list
                                .get(i);
                        if (i > 0) {
                            generics.append(",");
                        }

                        switch (n.getVariance()) {
                        case ContraVariant:
                            generics.append("-");
                            break;
                        case Covariant:
                            generics.append("+");
                            break;
                        case Invariant:
                            generics.append("=");
                            break;
                        }
                        Optional<String> symbol = n.getTypeNode().getTypeParameter().getUpperBound().getSymbol();
                        if (!symbol.isPresent()) {
                            symbol = Optional.of(LenseTypeSystem.Any().getName());
                        }

                        generics.append(n.getTypeNode().getName()).append("<").append(symbol.get());
                    }
                    generics.append("]");
                }
                generics.append(":");

                if (t.getSuperType() != null) {

                    generics.append(t.getSuperType().getTypeVariable().getTypeDefinition().getName());
                    if (t.getSuperType().getTypeVariable().getTypeDefinition().getGenericParameters().size() > 0) {
                        generics.append("<");
                        for (TypeVariable p : t.getSuperType().getTypeVariable().getTypeDefinition()
                                .getGenericParameters()) {
                            generics.append(p.getSymbol().orElse(p.getUpperBound().getTypeDefinition().getName()));
                            generics.append(",");
                        }
                        generics.deleteCharAt(generics.length() - 1);
                        generics.append(">");
                    }

                }

                generics.append(":");

                if (t.getInterfaces() != null && !t.getInterfaces().getChildren().isEmpty()) {

                    LenseTypeDefinition typeDefinition = t.getTypeDefinition();
                    for (TypeDefinition td : typeDefinition.getInterfaces()) {

                        if (LenseTypeSystem.getInstance().isAny(td)) {
                            continue;
                        }
                        if (td.isGeneric()) {
                            generics.append(td.getName()).append("<");
                            for (TypeVariable p : td.getGenericParameters()) {

                                appendGenerics(generics, typeDefinition, p);
                                generics.append(",");
                            }
                            generics.deleteCharAt(generics.length() - 1);
                            generics.append(">&");
                        } else {
                        	 generics.append(td.getName()).append("&");
                        }

                    }
                    generics.deleteCharAt(generics.length() - 1);
                }

                writer.append("@lense.core.lang.java.Signature(value = \"").append(generics).append("\"");

                if (t.isAlgebric()) {

                    StringBuilder values = new StringBuilder();
                    StringBuilder types = new StringBuilder();

                    for (AstNode a : t.getAlgebricChildren().getChildren()) {
                        ChildTypeNode ctn = (ChildTypeNode) a;

                        if (ctn.getType().getTypeVariable().getTypeDefinition().getKind().isObject()) {
                            if (values.length() != 0) {
                                values.append(",");
                            }
                            values.append(ctn.getType().getTypeVariable().getTypeDefinition().getName());
                        } else {
                            if (types.length() != 0) {
                                types.append(",");
                            }
                            types.append(ctn.getType().getTypeVariable().getTypeDefinition().getName());
                        }
                    }

                    if (values.length() != 0) {
                        writer.append(", caseValues = \"").append(values).append("\"");
                    }

                    if (types.length() != 0) {
                        writer.append(", caseTypes = \"").append(types).append("\"");
                    }

                }

                writer.println(")");

                if (t.getKind().isObject()) {
                    writer.append("@lense.core.lang.java.SingletonObject()").println();
                }
                if (t.getAnnotations() != null) {
                    // TODO

                    // for (AstNode n : t.getAnnotations().getChildren()) {
                    // AnnotationNode anot = (AnnotationNode) n;
                    // writer.print(anot.getName());
                    // writer.print(" ");
                    // }
                }

                writeVisibility(t.getVisibility());
                writer.append(" ");

                if (t.isAbstract()) {
                    writer.print(" abstract ");
                } else if (t.isFinal() || t.getKind().isObject()) {
                    writer.print(" final ");
                }

                String name = t.getName().substring(pos + 1);
                String className = name;
                switch (t.getKind()) {
                case Enum:
                    writer.print("enum ");
                    break;
                case Interface:
                    writer.print("interface ");
                    break;
                default:
                    writer.print("class ");
                    className = Strings.cammelToPascalCase(className);
                    break;
                }

                writer.print(className);

                if (!t.getKind().isInterface()) {
                    writer.print(" extends ");
                    if (t.getSuperType() != null
                            && t.getSuperType().getTypeVariable().getTypeDefinition().getName().length() > 0) {
                        writer.print(t.getSuperType().getTypeVariable().getTypeDefinition().getName());
                    } else {
                        writer.print("lense.core.lang.java.Base");
                    }
                } 

                if (t.getInterfaces() != null) {
                    if (t.getKind() == LenseUnitKind.Interface) {
                        writer.print(" extends ");
                    } else {
                        writer.print(" implements ");
                    }

                    int count = 0;
                    for (AstNode n : t.getInterfaces().getChildren()) {
                        TypeNode tn = (TypeNode) n;
                        writer.print(tn.getName());
                        count++;
                        if (count > 0) {
                            writer.print(" , ");
                        }
                    }
                    writer.print("lense.core.lang.Any");
                } else if (t.getKind().isInterface()){
                    writer.print(" extends lense.core.lang.Any");
                }

                writer.println("{");

                if (t.getKind().isObject()) {
                    // special singleton object code
                    // singleton instance

                    String instanceName = className.toUpperCase();
                    writer.append(" public static ").append(className).append(" ").append(instanceName)
                    .append(" = new ").append(className).append("();").println();

                    // constructor factory
                    writer.append(" \n@lense.core.lang.java.Constructor(paramsSignature = \"\")").println();
                    writer.append(" public static ").append(className).append(" constructor (){\n return ")
                    .append(instanceName).append(";\n }").println();

                    // private constructor
                    writer.append(" private ").append(className).append("(){}").println();

                    if (t.getProperty(JavalizePhase.AutoGenerateAsString, Boolean.class).orElse(false)) {
                        writer.append(
                                "@Override public lense.core.lang.String asString() { return lense.core.lang.String.valueOfNative(\"")
                        .append(name).append("\");}").println();
                    }

                    if (t.getProperty(JavalizePhase.AutoGenerateHashCodeAndEquals, Boolean.class).orElse(false)) {
                        writer.append(
                                "@Override public boolean equalsTo(lense.core.lang.Any other) {return other instanceof ")
                        .append(className).append("; }").println();

                        writer.append(
                                "@Override public lense.core.lang.HashValue hashValue() { return new lense.core.lang.HashValue(0); }")
                        .println();

                    }

                }

                TreeTransverser.transverse(t.getBody(), this);
                writer.println();
                writer.println("}");

                return VisitorNext.Siblings;
            } else if (node instanceof TryStatement) {
                TryStatement t = (TryStatement) node;

                writer.print("try");

                if (t.getResource() != null) {
                    writer.print("(");
                    TreeTransverser.transverse(t.getResource(), this);
                    writer.print(")");
                }

                TreeTransverser.transverse(t.getInstructions(), this);

                if (t.getCatchOptions() != null) {
                    for (AstNode a : t.getCatchOptions().getChildren()) {
                        CatchOptionNode c = (CatchOptionNode) a;

                        writer.append(" catch (");

                        FormalParameterNode p = c.getExceptions();

                        TreeTransverser.transverse(p.getTypeNode(), this);

                        writer.append(" ").append(p.getName());

                        writer.append(")").println();
                        ;

                        TreeTransverser.transverse(c.getInstructions(), this);

                        writer.println();
                    }
                }

                if (t.getfinalInstructions() != null) {
                    writer.print(" finally ");
                    TreeTransverser.transverse(t.getfinalInstructions(), this);

                }

                return VisitorNext.Siblings;
                // } else if (node instanceof RangeNode ){
                // RangeNode r = (RangeNode)node;
                //
                // writer.print(" new IntProgressable(");
                // TreeTransverser.tranverse(r.getStart(), this);
                // writer.print(",");
                // TreeTransverser.tranverse(r.getEnd(), this);
                // writer.print(")");
                // return VisitorNext.Siblings;
            } else if (node instanceof ObjectReadNode) {
                ObjectReadNode n = (ObjectReadNode) node;

                String[] javaClassNames = Strings.split(n.getTypeVariable().getTypeDefinition().getName(), ".");
                javaClassNames[javaClassNames.length - 1] = Strings
                        .cammelToPascalCase(javaClassNames[javaClassNames.length - 1]);

                writer.append(Strings.join(javaClassNames, ".")).append(".").append(n.getObjectName());
            } else if (node instanceof ForEachNode) {
                ForEachNode f = (ForEachNode) node;

                VariableInfo varInfo = f.getVariableDeclarationNode().getInfo();
                String typeName = varInfo.getTypeVariable().getTypeDefinition().getName();

                if (varInfo.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive && varInfo.getMaximum().isPresent() && varInfo.getMinimum().isPresent()){
                    // use a count for each
                    
                    MethodInvocationNode range = (MethodInvocationNode)f.getContainer();
                    
                    String varName = f.getVariableDeclarationNode().getName();
                    writer.append("for ( ").append(typeName).append(" ").append(varName).append(" = ");
                    
                    TreeTransverser.transverse(range.getAccess(), this);
                    
                    writer.append("; ").append(varName).append("<").append((varInfo.isIncludeMaximum() ? "=" : ""));
                   
                    TreeTransverser.transverse(range.getCall().getArguments().getFirstArgument().getFirstChild(), this);
    
                    writer.append("; ").append(varName).append("++ ) {");

                    StringWriter w = new StringWriter();
                    PrintWriter sp = new PrintWriter(w);
                    JavaSourceWriterVisitor visitor = new JavaSourceWriterVisitor(sp);
                    TreeTransverser.transverse(f.getBlock(), visitor);

                    String str = w.toString();
                    str = str.substring(str.indexOf('{') + 1, str.lastIndexOf("}"));
                    writer.append(str);

                    writer.println("}");
                } else {
                    // use an iterator for each

                    writer.append("\nlense.core.collections.Iterator $it = ((lense.core.collections.Iterable)");
                    TreeTransverser.transverse(f.getContainer(), this);
                    writer.println(").getIterator();");
                    writer.println("while ( $it.moveNext()) {");

                    TreeTransverser.transverse(f.getVariableDeclarationNode(), this);


                    writer.append("= (").append(typeName).println(") $it.current();");

                    StringWriter w = new StringWriter();
                    PrintWriter sp = new PrintWriter(w);
                    JavaSourceWriterVisitor visitor = new JavaSourceWriterVisitor(sp);
                    TreeTransverser.transverse(f.getBlock(), visitor);

                    String str = w.toString();
                    str = str.substring(str.indexOf('{') + 1, str.lastIndexOf("}"));
                    writer.append(str);
                    writer.append("}\n");
                }




                return VisitorNext.Siblings;
            }
            // else if (node instanceof ForEachNode ){
            // ForEachNode f = (ForEachNode)node;
            //
            // writer.print(" for (");
            //
            // visitBeforeChildren(f.getVariableDeclarationNode());
            //
            // writer.print(" ; ");
            //
            // visitBeforeChildren(f.getConditional());
            //
            // writer.print(" ; ");
            //
            //
            // visitBeforeChildren(f.getIncrement());
            //
            // //TreeTransverser.tranverse(f.getContainer(), this);
            // writer.println(")");
            //
            // TreeTransverser.transverse(f.getBlock(), this);
            //
            // return VisitorNext.Siblings;
            // }
            else if (node instanceof WhileNode) {
                WhileNode f = (WhileNode) node;

                writer.println();
                writer.print(" while (");

                TreeTransverser.transverse(f.getCondition(), this);

                writer.println(")");

                TreeTransverser.transverse(f.getStatements(), this);

                return VisitorNext.Siblings;
            } else if (node instanceof SwitchNode) {
                SwitchNode n = (SwitchNode) node;

                writer.println();
                // writer.print(" switch (");
                // TreeTransverser.transverse(n.getCandidate(), this);
                // writer.println(") { ");
                //
                // for (AstNode a : n.getOptions().getChildren()) {
                // SwitchOption op = (SwitchOption) a;
                // if (op.isDefault()) {
                // writer.print(" default");
                // } else {
                // writer.print(" case ");
                // TreeTransverser.transverse(op.getValue(), this);
                // writer.println(":");
                // }
                //
                // TreeTransverser.transverse(op.getActions(), this);
                // writer.println("break;");
                // }
                //
                // writer.println("}");

                boolean first = true;
                Optional<SwitchOption> defaultOption = Optional.empty();
                for (AstNode a : n.getOptions().getChildren()) {

                    SwitchOption op = (SwitchOption) a;
                    if (op.isDefault()) {
                        defaultOption = Optional.of(op);
                        continue; // do at the end
                    }
                    if (!first) {
                        writer.append(" else ");
                    }
                    writer.append("if (");
                    TreeTransverser.transverse(op.getValue(), this);
                    writer.append(".equalsTo(");
                    TreeTransverser.transverse(n.getCandidate(), this);
                    writer.append("))").println();
                    TreeTransverser.transverse(op.getActions(), this);
                    writer.println();

                    first = false;
                }

                if (defaultOption.isPresent()) {
                    writer.append("else ").println();
                    TreeTransverser.transverse(defaultOption.get().getActions(), this);
                    writer.println();
                } else {
                    writer.append("else {  throw new RuntimeException(\"Value is not completly covered\"); } ");
                }

                return VisitorNext.Siblings;
            } else if (node instanceof DecisionNode) {
                DecisionNode n = (DecisionNode) node;

                if (n.getCondition() != null) {
                    writer.print("if (");
                    TreeTransverser.transverse(n.getCondition(), this);
                    writer.println(")");

                }

                TreeTransverser.transverse(n.getTrueBlock(), this);

                if (n.getFalseBlock() != null) {
                    writer.print(" else ");
                    TreeTransverser.transverse(n.getFalseBlock(), this);

                }
                return VisitorNext.Siblings;
            } else if (node instanceof MethodInvocationNode) {
                MethodInvocationNode n = (MethodInvocationNode) node;

                if (n.isStaticInvocation()) {
                    writer.print(n.getTypeMember().getDeclaringType().getName());
                    writer.print(".");
                    writer.print(sanitize(n.getCall().getName()));
                    writer.print("(");

                    if (n.getAccess() != null) {
                        TreeTransverser.transverse(n.getAccess(), this);
                    }

                    if (n.getCall().getArguments() != null && !n.getCall().getArguments().getChildren().isEmpty()) {
                        writer.print(",");
                        TreeTransverser.transverse(n.getCall().getArguments(), this);
                    }

                    writer.print(")");
                } else {
                    
                    final TypeDefinition type = n.getTypeVariable().getUpperBound().getTypeDefinition();
                    // TODO should be resolve at errasure time
//                    boolean needsCast = n.isIndexDerivedMethod() 
//                            && !LenseTypeSystem.getInstance().isVoid(type) 
//                            && !LenseTypeSystem.getInstance().isBoolean(type) 
//                            && !LenseTypeSystem.getInstance().isAny(type);
//                    
//                    if (needsCast){
//                     
//                        writer.append("((").append(type.getName()).append(")");
//                            
//                    }
                    if (n.getAccess() != null) {
                        TreeTransverser.transverse(n.getAccess(), this);
                        writer.print(".");
                    }

                    writer.print(sanitize(n.getCall().getName()));
                    writer.print("(");
                    if (n.getCall().getArguments() != null) {
                        TreeTransverser.transverse(n.getCall().getArguments(), this);
                    }

                    writer.print(")");
                    
//                    if (needsCast){
//                        writer.print(")");
//                    }
                }

                if (node.getParent() instanceof BlockNode) {
                    writer.println(";");
                }

                return VisitorNext.Siblings;
            } else if (node instanceof CastNode) {
                CastNode n = (CastNode) node;

                if (n.getTypeVariable().getTypeDefinition().getName().equals(LenseTypeSystem.Any().getName())) {

                    TreeTransverser.transverse(n.getInner(), this);

                } else {
                    writer.append("((").append(n.getTypeVariable().getTypeDefinition().getName()).append(")");

                    TreeTransverser.transverse(n.getInner(), this);

                    writer.append(")");
                }

                return VisitorNext.Siblings;
            } else if (node instanceof AssignmentNode) {
                AssignmentNode n = (AssignmentNode) node;

                TreeTransverser.transverse((AstNode) n.getLeft(), this);
                writer.print(" = ");
                TreeTransverser.transverse(n.getRight(), this);

                if (n.getParent() instanceof BlockNode) {
                    writer.println(";");
                }

                return VisitorNext.Siblings;
            } else if (node instanceof NewInstanceCreationNode) {
                NewInstanceCreationNode n = (NewInstanceCreationNode) node;

                if (n.getTypeVariable() == null) {
                    writer.print(n.getTypeNode().getName());
                } else {
                    writer.print(n.getTypeVariable().getTypeDefinition().getName());
                }

                if (n.getName() == null) {
                    writer.append(".constructor");
                } else {
                    writer.append(".").append(n.getName());
                }

                writer.print("(");

                StringBuilder reified = buildGenericTypeArguments(n.getTypeNode());

                if (reified != null) {
                    writer.append(reified).append(',');

                }

                TreeTransverser.transverse(n.getArguments(), this);
                writer.print(")");

                return VisitorNext.Siblings;
            } else if (node instanceof StringConcatenationNode) {
                writer.print(" lense.core.lang.String.valueOfNative(new StringBuilder()");

                for (AstNode n : node.getChildren()) {

                    if (n instanceof StringValue) {
                        String str = ((StringValue) n).getLiteralValue();
                        if (str.length() > 0) {
                            writer.print(".append(");
                            writer.append("\"").append(str).append("\"");
                            writer.print(")");
                        }
                    } else {
                        writer.print(".append(");

                        if (n instanceof MethodInvocationNode) {
                            MethodInvocationNode mi = (MethodInvocationNode) n;

                            if (((TypedNode) mi.getAccess()).getTypeVariable() instanceof PrimitiveTypeDefinition) {
                                PrimitiveTypeDefinition primitive = (PrimitiveTypeDefinition) ((TypedNode) mi
                                        .getAccess()).getTypeVariable();

                                writer.print("java.lang." + primitive.getWrapperName() + ".toString(");
                                TreeTransverser.transverse(mi.getAccess(), this);
                                writer.print("))");
                                continue;
                            }
                        }

                        TreeTransverser.transverse(n, this);
                        writer.print(")");

                    }

                }
                writer.print(".toString())");
                return VisitorNext.Siblings;
            } else if (node instanceof CaptureReifiedTypesNode) {
                CaptureReifiedTypesNode capture = (CaptureReifiedTypesNode) node;

                if (!capture.getChildren().isEmpty()) {
                    writer.append("lense.core.lang.reflection.JavaReifiedArguments.getInstance()");

                    for (AstNode c : capture.getChildren()) {

                        writer.append(".addType(");

                        TreeTransverser.transverse(c, this);

                        writer.append(")");

                    }
                }

                return VisitorNext.Siblings;
            } else if (node instanceof NewTypeResolverNode) {
                NewTypeResolverNode capture = (NewTypeResolverNode) node;

                writer.append("lense.core.lang.reflection.TypeResolver.byName(\"").append(capture.getTypeName())
                .append("\")");

                if (capture.hasParameters()) {

                    writer.append(".withGenerics(");

                    boolean first = true;
                    for (AstNode c : capture.getChildren()) {

                        if (first) {
                            first = false;
                        } else {
                            writer.append(",");
                        }

                        TreeTransverser.transverse(c, this);

                    }

                    writer.append(")");

                }
                return VisitorNext.Siblings;

            } else if (node instanceof TypeParameterTypeResolverNode) {

                TypeParameterTypeResolverNode c = (TypeParameterTypeResolverNode) node;

                writer.append("lense.core.lang.reflection.TypeResolver.byGenericParameter(");

                TreeTransverser.transverse(c.getOriginal(), this);

                writer.append(",");
                writer.print(c.getIndex());
                writer.append(")");

            } else if (node instanceof ArgumentTypeResolverNode) {
                ArgumentTypeResolverNode arg = (ArgumentTypeResolverNode) node;

                List<ArgumentListItemNode> invocation = arg.getArgumentItem().getFirstChild()
                        .getChildren(ArgumentListNode.class).get(0).getChildren(ArgumentListItemNode.class);
                AstNode base = invocation.get(arg.getArgumentItem().getIndex() + 1).getFirstChild();

                writer.append("lense.core.lang.reflection.TypeResolver.of(");
                TreeTransverser.transverse(base, this);
                writer.append(".type())");

            } else if (node instanceof VariableReadTypeResolverNode) {
                VariableReadTypeResolverNode vr = (VariableReadTypeResolverNode) node;

                writer.append("lense.core.lang.reflection.TypeResolver.of(").append(vr.getVariableName())
                .append(".type())");

            } else if (node instanceof ArgumentListNode) {
                ArgumentListNode n = (ArgumentListNode) node;

                boolean first = true;
                for (AstNode a : n.getChildren()) {

                    if (!first) {
                        writer.print(",");
                    } else {
                        first = false;
                    }

                    ArgumentListItemNode item = (ArgumentListItemNode) a;

                    TreeTransverser.transverse(item.getFirstChild(), this);

                }

                return VisitorNext.Siblings;
            } else if (node instanceof ContinueNode) {

                writer.println("continue;");

                return VisitorNext.Siblings;
            } else if (node instanceof TypeNode) {

                TypeNode t = (TypeNode) node;

                writeType(t);

                writer.print(" ");

                return VisitorNext.Siblings;
            } else if (node instanceof VariableDeclarationNode) {
                VariableDeclarationNode t = (VariableDeclarationNode) node;

                // if (t.getInfo() != null) {
                // if (t.getInfo().isEfectivlyFinal()) {
                // writer.print("final ");
                // }
                // }
                if (t.getTypeVariable() == null) {
                    writer.print("????");
                } else {
                    writeType(t.getTypeVariable());
                    //writer.print(t.getTypeVariable().getTypeDefinition().getName());
                }
                writer.print(" ");
                writer.print(sanitize(t.getName()));

                if (t.getInitializer() != null) {
                    writer.print(" = ");
                    TreeTransverser.transverse(t.getInitializer(), this);
                }

                if (t.getInfo() != null) {

                    if (t.getInfo().doesEscape()) {
                        writer.print("/* doesEscape */");
                    }

                }

                if (node.getParent() instanceof BlockNode) {
                    writer.println(";");
                }

                return VisitorNext.Siblings;
            } else if (node instanceof VariableWriteNode) {
                VariableWriteNode t = (VariableWriteNode) node;
                writer.print(sanitize(t.getName()));

                return VisitorNext.Siblings;
            } else if (node instanceof VariableReadNode) {
                VariableReadNode t = (VariableReadNode) node;
                writer.print(sanitize(t.getName()));

            } else if (node instanceof BlockNode) {
                writer.println("{");

                return VisitorNext.Children;
            } else if (node instanceof ReturnNode) {
                writer.print("return ");
            } else if (node instanceof AssignmentNode) {
                AssignmentNode n = (AssignmentNode) node;

                visitBeforeChildren((AstNode) n.getLeft());
                writer.print(" ");
                writer.print(n.getOperation().symbol());
                writer.print(" ");
                visitBeforeChildren(n.getRight());
                writer.print(";\n");

                return VisitorNext.Siblings;
            } else if (node instanceof PosExpression) {
                PosExpression n = (PosExpression) node;

                writer.print(n.getOperation().symbol());

            } else if (node instanceof PosExpression) {
                PosExpression n = (PosExpression) node;

                writer.print(n.getOperation().symbol());

            } else if (node instanceof ArithmeticNode) {
                // TODO convert arithmetic to method call
                // TODO convert foraeach of progression into a common foreach
                ArithmeticNode n = (ArithmeticNode) node;

                TreeTransverser.transverse(n.getLeft(), this);

                writer.print(" ");
                writer.print(n.getOperation().symbol());
                writer.print(" ");

                TreeTransverser.transverse(n.getRight(), this);

                return VisitorNext.Siblings;
            } else if (node instanceof BooleanOperatorNode) {
                BooleanOperatorNode n = (BooleanOperatorNode) node;

                TreeTransverser.transverse(n.getLeft(), this);

                writer.print(" ");
                writer.print(n.getOperation().symbol());
                writer.print(" ");

                TreeTransverser.transverse(n.getRight(), this);

                return VisitorNext.Siblings;
            } else if (node instanceof PreBooleanUnaryExpression) {

                PreBooleanUnaryExpression p = (PreBooleanUnaryExpression) node;
                if (p.getOperation() == BooleanOperation.LogicNegate) {
                    writer.print("!");
                }

                return VisitorNext.Children;
            } else if (node instanceof ComparisonNode) {
                ComparisonNode n = (ComparisonNode) node;

                if (n.getOperation() == Operation.EqualTo) {
                    TreeTransverser.transverse(n.getLeft(), this);
                    writer.print(".equals(");
                    TreeTransverser.transverse(n.getRight(), this);
                    writer.print(")");
                } else if (n.getOperation() == Operation.Different) {
                    writer.print("!");
                    TreeTransverser.transverse(n.getLeft(), this);
                    writer.print(".equals(");
                    TreeTransverser.transverse(n.getRight(), this);
                    writer.print(")");
                } else if (n.getOperation() == Operation.ReferenceEquals) {
                    TreeTransverser.transverse(n.getLeft(), this);
                    writer.print(" == ");
                    TreeTransverser.transverse(n.getRight(), this);
                } else if (n.getOperation() == Operation.ReferenceDifferent) {
                    TreeTransverser.transverse(n.getLeft(), this);
                    writer.print(" != ");
                    TreeTransverser.transverse(n.getRight(), this);
                } else {
                    TreeTransverser.transverse(n.getLeft(), this);
                    writer.print(" ");
                    writer.print(n.getOperation().symbol());
                    writer.print(" ");
                    TreeTransverser.transverse(n.getRight(), this);
                }

                return VisitorNext.Siblings;
            } else if (node instanceof FieldAccessNode) {
                FieldAccessNode n = (FieldAccessNode) node;

                if (n.getPrimary() != null) {
                    TreeTransverser.transverse(n.getPrimary(), this);
                    writer.print('.');
                }

                writer.print(sanitize(n.getName()));

            } else if (node instanceof FieldOrPropertyAccessNode) {
                FieldOrPropertyAccessNode n = (FieldOrPropertyAccessNode) node;

                if (n.getPrimary() != null) {
                    TreeTransverser.transverse(n.getPrimary(), this);
                    writer.print('.');
                }

                if (n.getKind() == FieldKind.FIELD) {

                    writer.print(sanitize(n.getName()));
                } else {

                    String propertyName = n.getName().substring(0, 1).toUpperCase() + n.getName().substring(1);
                    writer.append("get").append(propertyName).append("(").append(")");

                }

                return VisitorNext.Siblings;
            } else if (node instanceof lense.compiler.ast.IndexedPropertyReadNode){
                IndexedPropertyReadNode n = (IndexedPropertyReadNode)node;
                
              
                if (n.getAccess() != null) {
                    TreeTransverser.transverse(n.getAccess(), this);
                    writer.print('.');
                }

                writer.append("get(");
                
                TreeTransverser.transverse(n.getArguments(), this);
                
                writer.append(")");
                
                return VisitorNext.Siblings;
            } else if (node instanceof ConstructorDeclarationNode) {
                ConstructorDeclarationNode ctr = (ConstructorDeclarationNode) node;

                writer.println("\n");

                writer.append("@lense.core.lang.java.Constructor");

                writer.append("(");
                if (ctr.isImplicit()) {
                    writer.append("isImplicit=true,");
                }
                writer.append("paramsSignature=\"");

                boolean isFirst = true;
                for (AstNode p : ctr.getParameters().getChildren()) {
                    FormalParameterNode t = ((FormalParameterNode) p);

                    if (t.getName().equals(ReificationVisitor.TYPE_REIFICATION_INFO)) {
                        continue;
                    }
                    if (!isFirst) {
                        writer.print(",");
                    }
                    isFirst = false;

                    TypeVariable typeVar = t.getTypeVariable();

                    printTypeSignature(writer, typeVar);

                }

                writer.append("\")");

                writer.println();

                writeVisibility(ctr.getVisibility());

                writer.print(" static ");

                writeType(ctr.getReturnType());

                writer.print(" ");
                if (ctr.getName() == null) {
                    writer.print("constructor");
                } else {
                    writer.print(sanitize(ctr.getName()));
                }

                writer.print("(");

                isFirst = true;

                // if (m.getReturnType().getTypeParametersCount() > 0) {
                // writer.append("ReifiedArguments").append(' ').append("reification");
                // isFirst = false;
                // }

                if (ctr.getParameters() != null) {

                    for (AstNode n : ctr.getParameters().getChildren()) {

                        FormalParameterNode p = (FormalParameterNode) n;

                        if (isFirst) {
                            isFirst = false;
                        } else {
                            writer.print(", ");
                        }

                        TreeTransverser.transverse(p.getTypeNode(), this);

                        writer.print(" ");
                        writer.print(sanitize(p.getName()));

                    }
                }

                ClassTypeNode astnode = (ClassTypeNode) ctr.getParent().getParent();

                writer.print(")");

                if (ctr.isNative() || astnode.isAbstract()) {
                    // TODO write native peer call
                    writer.println("{ return null; // native \n }");
                } else if (ctr.isPrimary()) {
                    writer.println("{ /* primary*/");

                    writer.append(" return new ");

                    writeType(ctr.getReturnType());
                    writer.append("(");
                    if (ctr.getParameters() != null) {

                        Iterator<AstNode> it = ctr.getParameters().getChildren().iterator();
                        while (it.hasNext()) {
                            AstNode n = it.next();
                            FormalParameterNode p = (FormalParameterNode) n;

                            writer.append(sanitize(p.getName()));
                            if (it.hasNext()) {
                                writer.append(", ");
                            }
                        }
                    }
                    writer.append(");").println();

                    writer.println("}");

                    // inner constructor
                    writer.append("protected ");

                    String name = ctr.getReturnType().getName();

                    writer.append(name.substring(name.lastIndexOf('.') + 1));

                    writer.append(" (");

                    if (ctr.getParameters() != null) {

                        Iterator<AstNode> it = ctr.getParameters().getChildren().iterator();
                        while (it.hasNext()) {
                            AstNode n = it.next();
                            FormalParameterNode p = (FormalParameterNode) n;
                            writeType(p.getTypeNode());
                            writer.append(" ").append(p.getName());
                            if (it.hasNext()) {
                                writer.append(", ");
                            }
                        }
                    }
                    writer.append("){\n");

                    if (ctr.getExtention() != null) {
                        ConstructorExtentionNode extention = ctr.getExtention();
                        writer.append(extention.getCallLevel()).append("(");

                        Iterator<AstNode> it = extention.getArguments().getChildren().iterator();
                        while (it.hasNext()) {
                            ArgumentListItemNode arg = (ArgumentListItemNode) it.next();

                            TreeTransverser.transverse(arg, this);

                            if (it.hasNext()) {
                                writer.append(",");
                            }
                        }

                        writer.append(");").println();

                    } else if (ctr.getParameters() != null) {

                        Iterator<AstNode> it = ctr.getParameters().getChildren().iterator();
                        while (it.hasNext()) {
                            AstNode n = it.next();
                            FormalParameterNode p = (FormalParameterNode) n;

                            // if (p.getVisibility() != Visibility.Undefined && p.getVisibility() !=
                            // Visibility.Private ){
                            // if (p.getImutabilityValue() == Imutability.Imutable){
                            // // set the field directly
                            // writer.append("this.").append(p.getName()).append(" =
                            // ").append(p.getName()).append(";").println();
                            // } else {
                            // writer.append("set" +
                            // PropertyNamesSpecification.resolvePropertyName(p.getName()))
                            // .append("(").append(p.getName()).append(")");
                            // }
                            //
                            // } else {
                            writer.append("this.").append(p.getName()).append(" = ").append(p.getName()).append(";")
                            .println();
                            // }

                        }
                    }

                    writer.println("}");
                } else {
                    TreeTransverser.transverse(ctr.getBlock(), this);
                }
                writer.println("\n");
                return VisitorNext.Siblings;
            } else if (node instanceof MethodDeclarationNode) {

                MethodDeclarationNode m = (MethodDeclarationNode) node;

                writer.println("\t");

                if (m.isProperty()) {
                    writer.print("@lense.core.lang.java.Property(");
                    if (m.isIndexer()) {
                        writer.append(" indexed = true");
                    } else {
                        writer.append(" name = \"").append(m.getPropertyName()).append("\"");
                    }
                    if (m.isSetter()) {
                        writer.append(", setter = ");
                        writer.print(m.isSetter());
                    }
                    writer.println(")");
                }

                boolean isFirst;
                writeMethodSignature(m);

                if (m.isOverride()) {
                    writer.print(" @java.lang.Override ");
                }

                writeVisibility(m.getVisibility());

                if (m.isAbstract()) {
                    writer.print(" abstract ");
                } else if (!m.isDefault()) {
                    writer.print(" final ");
                }

                if (m.isStatic()) {
                    writer.print(" static ");
                }
                // writeAnnotations(m);

                if (m.getReturnType() == null) {
                    writer.print("null");
                } else {
                    writeType(m.getReturnType());
                }

                writer.print(" ");
                writer.print(sanitize(m.getName()));
                writer.print("(");

                if (m.getParameters() != null) {
                    isFirst = true;
                    for (AstNode n : m.getParameters().getChildren()) {
                        FormalParameterNode p = (FormalParameterNode) n;

                        if (isFirst) {
                            isFirst = false;
                        } else {
                            writer.print(", ");
                        }

                        if (p.getTypeVariable() instanceof ContraVariantTypeVariable) {
                            writer.print(LenseTypeSystem.Any().getName());
                        } else {
                            TreeTransverser.transverse(p.getTypeNode(), this);
                        }


                        writer.print(" ");
                        writer.print(p.getName());

                    }
                }

                writer.print(")");

                if (m.isAbstract() || m.getBlock() == null) {
                    writer.print(";");
                } else if (m.isNative()) {
                    // TODO write native peer call
                } else {
                    TreeTransverser.transverse(m.getBlock(), this);
                }

                return VisitorNext.Siblings;
            } else if (node instanceof ParametersListNode) {
                return VisitorNext.Siblings;
            } else if (node instanceof InstanceOfNode) {
                InstanceOfNode n = (InstanceOfNode) node;

                if (n.getMandatoryEvaluation().isPresent()){
                    writer.append( n.getMandatoryEvaluation().get().toString());
                } else {
                    writer.append("(");

                    TreeTransverser.transverse(n.getExpression(), this);
                    writer.append(" instanceof ");

                    if (n.getTypeNode().getTypeVariable().getTypeDefinition().getKind().isObject()) {
                        String[] names = Strings.split(n.getTypeNode().getTypeVariable().getTypeDefinition().getName(),
                                ".");
                        names[names.length - 1] = Strings.cammelToPascalCase(names[names.length - 1]);

                        writer.append(Strings.join(names, "."));
                    } else {
                        TreeTransverser.transverse(n.getTypeNode(), this);
                    }

                    writer.append(")");
                }


                return VisitorNext.Siblings;
            } else if (node instanceof FieldDeclarationNode) {
                FieldDeclarationNode m = (FieldDeclarationNode) node;

                writer.print("\n");

                if (m.getInitializedOnConstructor()) {
                    writer.println("/* Init in constructor*/");
                }

                // TODO write annotations
                writeVisibility(Visibility.Private);

                if (m.getImutability().getImutability() == Imutability.Imutable) {
                    writer.print("final ");
                }
                writeType(m.getTypeNode());

                writer.print(" ");
                writer.print(m.getName());

                if (!m.getInitializedOnConstructor() && m.getInitializer() != null) {
                    writer.print(" = ");
                    TreeTransverser.transverse(m.getInitializer(), this);
                }
                writer.print(";\n");

                return VisitorNext.Siblings;
            }

            return VisitorNext.Children;
        } finally {
            writer.flush();
        }

    }

    private void applyReboxOperator(PrintWriter writer, ArithmeticOperation operation, AstNode node) {


        TypedNode right = (TypedNode) node.getChildren().get(1).getFirstChild();

        if (right.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive){
            writer.append("lense.core.math.NativeNumberFactory." + operation.equivalentMethod() + "AndCreateInteger(");

            TreeTransverser.transverse( node.getChildren().get(0), this);

            writer.append(",");

            TreeTransverser.transverse(node.getChildren().get(1), this);
            writer.append(")");
        } else {
            writer.append("lense.core.math.NativeNumberFactory.newInteger(");

            TreeTransverser.transverse( node.getChildren().get(0), this);

            writer.append(").").append(operation.equivalentMethod()).append("(");

            TreeTransverser.transverse(node.getChildren().get(1), this);
            writer.append(")");
        }

    }

    private void applyPrePrimitiveOperator(PrintWriter writer, String operator, AstNode node) {
        writer.append(" ").append(operator);
        TreeTransverser.transverse(node.getChildren().get(0), this);
    }

    private void applyPrimitiveOperator(PrintWriter writer, String operator, AstNode node) {
        TreeTransverser.transverse(node.getChildren().get(0), this);
        writer.append(" ").append(operator).append(" ");
        TreeTransverser.transverse(node.getChildren().get(1), this);
    }

    private void writeMethodSignature(MethodDeclarationNode m) {
        writer.print("@lense.core.lang.java.MethodSignature( returnSignature = \"");

        // signature is type<A,B>
        TypeVariable typeVar = m.getReturnType().getTypeVariable();
        if (typeVar == null) {
            writer.print(signatureNameOf(m.getReturnType().getName()));
        } else if (typeVar.isFixed()) {
            writer.print(typeVar.getTypeDefinition().getName());
            if (typeVar.getTypeDefinition().isGeneric()) {
                writer.print("<");
                boolean first = true;
                for (TypeVariable a : typeVar.getTypeDefinition().getGenericParameters()) {
                    if (!first) {
                        writer.print(",");
                    }
                    first = false;

                    if (a instanceof GenericTypeBoundToDeclaringTypeVariable) {
                        writer.print(signatureNameOf(a.getSymbol().get()));
                    } else {
                        writer.print(signatureNameOf(
                                a.getUpperBound().getSymbol().orElse(a.getUpperBound().getTypeDefinition().getName())));
                    }
                }
                writer.print(">");
            }
        } else if (typeVar instanceof CalculatedTypeVariable) {
            CalculatedTypeVariable c = (CalculatedTypeVariable) typeVar;
            writer.print(signatureNameOf(c.getSymbol().get()));
        } else if (typeVar instanceof RangeTypeVariable) {
            RangeTypeVariable c = (RangeTypeVariable) typeVar;
            writer.print(signatureNameOf(c.getSymbol().get()));
        } else {
            throw new RuntimeException(
                    "Type signature is not defined for type variable " + typeVar.getClass().getName());
        }

        writer.print("\" , paramsSignature = \"");

        boolean isFirst = true;
        for (AstNode p : m.getParameters().getChildren()) {
            FormalParameterNode t = ((FormalParameterNode) p);

            if (!isFirst) {
                writer.print(",");
            }
            isFirst = false;
            typeVar = t.getTypeVariable();
            if (typeVar instanceof CalculatedTypeVariable) {
                CalculatedTypeVariable c = (CalculatedTypeVariable) typeVar;
                writer.print(signatureNameOf(c.getSymbol().get()));
            } else if (typeVar instanceof RangeTypeVariable) {
                RangeTypeVariable c = (RangeTypeVariable) typeVar;
                writer.print(signatureNameOf(c.getSymbol().get()));
            } else {
                writer.print(signatureNameOf(typeVar.getTypeDefinition().getName())); // TODO, recursive generic parameters
            }
        }

        if (m.getSuperMethod() != null) {
            writer.append("\" , overloaded = true , declaringType = \"")
            .append(signatureNameOf(m.getSuperMethod().getDeclaringType().getTypeDefinition().getName()));
        }

        if (!m.getMethodScopeGenerics().getChildren().isEmpty()) {

            StringBuilder sb = new StringBuilder();

            for (GenericTypeParameterNode a : m.getMethodScopeGenerics().getChildren(GenericTypeParameterNode.class)) {
                sb.append(signatureNameOf(a.getTypeNode().getName())).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);

            writer.append("\" , boundedTypes = \"").append(sb);
        }

        writer.print("\")\n");
    }

    private String signatureNameOf(String name) {
        if ( "int".equals(name)){
            return "lense.core.lang.Int32";
        } else  if ( "long".equals(name)){
            return "lense.core.lang.Int64";
        } else  if ( "boolean".equals(name)){
            return "lense.core.lang.Boolean";
        }
        return name;
    }

    private void printTypeSignature(Appendable pwriter, TypeVariable typeVar) {
        if (typeVar.getSymbol().isPresent()) {
            writer.print(signatureNameOf(typeVar.getSymbol().get()));
        } else if (typeVar.isSingleType()) {
            
   
            writer.print(signatureNameOf(typeVar.getTypeDefinition().getName()));

            if (typeVar.getTypeDefinition().isGeneric()) {
                writer.append("<");

                boolean isFirst = true;
                for (TypeVariable gp : typeVar.getGenericParameters()) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        writer.print(", ");
                    }
                    printTypeSignature(pwriter, gp);
                }

                writer.append(">");
            }

        } else {
            throw new IllegalStateException("Cannot print this type");
        }
    }

    private StringBuilder buildGenericTypeArguments(TypeNode t) {
        return null;
        //
        // if (t.getTypeParametersCount() == 0) {
        // return null;
        // }
        //
        //
        // StringBuilder builder = new StringBuilder("new
        // lense.core.lang.reflection.JavaReifiedArguments(");
        //
        // boolean hasTypes = false;
        // for (TypeVariable g : t.getTypeVariable().getGenericParameters()) {
        // if (g.getSymbol().isPresent()) {
        // builder.append("lense.core.lang.reflection.JavaReifiedArguments.pair(").append(g.getSymbol().get()).append(",").append("null").append("),");
        // // TODO generate Type
        // hasTypes = true;
        // }
        //
        // }
        //
        // if (hasTypes) {
        // builder.deleteCharAt(builder.length() - 1);
        // }
        //
        // return builder.append(")");
    }

    private void appendGenerics(StringBuilder generics, LenseTypeDefinition typeDefinition, TypeVariable p) {
        if (p == null) {
            return;
        }
        if (p instanceof DeclaringTypeBoundedTypeVariable) {
            DeclaringTypeBoundedTypeVariable d = (DeclaringTypeBoundedTypeVariable) p;
            generics.append(typeDefinition.getGenericParameterSymbolByIndex(d.getParameterIndex()).get());
        } else if (!p.isFixed()) {
            appendGenerics(generics, typeDefinition, ((TypeVariable) p).getUpperBound());
        } else {
            TypeDefinition td = p.getTypeDefinition();
            generics.append(td.getName());
            if (td.isGeneric()) {
                generics.append("<");
                for (TypeVariable iv : td.getGenericParameters()) {

                    appendGenerics(generics, typeDefinition, iv);
                    generics.append(",");
                }
                generics.deleteCharAt(generics.length() - 1);
                generics.append(">");
            }

        }
    }

    private void writeVisibility(Visibility visibility) {
        if (visibility == null) {
            return;
        }
        switch (visibility) {
        case Public:
            writer.print("public ");
            break;
        case Private:
            writer.print("private ");
            break;
        case Protected:
            writer.print("protected ");
            break;
        case Undefined:
            break;
        
        }
    }

    private void writeType(TypeNode t) {
        if (t.getName().equals("lense.core.lang.Void")) {
            writer.print("void");

        } else if (t.getName().equals("lense.core.lang.reflection.ReifiedArguments")) {
            writer.print(t.getName());
       
       
        } else {
            final TypeVariable type = t.getTypeVariable();
            if (type == null) {
                TypeVariable upper = t.getTypeParameter().getUpperBound();

                writer.print(upper.getTypeDefinition().getName());
            } else if (type instanceof ErasedTypeDefinition) {
                writer.print(((ErasedTypeDefinition) type).getPrimitiveType().getName());
            } else if (type.isFixed()) {

                if (t.getParent() instanceof FormalParameterNode
                        && ((FormalParameterNode) t.getParent()).isMethodTypeBound()) {
                    writer.print("lense.core.lang.Any");
                } else {
                    TypeDefinition def = type.getTypeDefinition();
                    writer.print(def.getName());
                    writeGenerics(def);
                }

            } else if (type instanceof RangeTypeVariable) {

                TypeDefinition def = type.getUpperBound().getTypeDefinition();
                writer.print(def.getName());
                writeGenerics(def);

            } else {

                writer.print(type.getUpperBound().getTypeDefinition().getName());

            }

        }
    }
    
    private void writeType(TypeVariable type) {
        if (type instanceof ErasedTypeDefinition) {
            writer.print(((ErasedTypeDefinition) type).getPrimitiveType().getName());
        } else {
            writer.print(type.getUpperBound().getTypeDefinition().getName());
        }
    }

    private void writeGenerics(final TypeDefinition type) {
        // if (!type.getGenericParameters().isEmpty()){
        // writer.print("<");
        // boolean first = true;
        // for(IntervalTypeVariable p : type.getGenericParameters()){
        // if (first){
        // first = false;
        // } else {
        // writer.print(",");
        // }
        // writer.print(p.getName());
        // if (p.getUpperbound() != null &&
        // !p.getUpperbound().getName().equals(p.getName())){
        // if (!p.getUpperbound().getName().equals("java.lang.Object")){
        // writer.print(" extends ");
        // writer.print(p.getUpperbound().getName());
        // //writeGenerics(p.getUpperbound());
        // } else {
        // writer.print(" super ");
        // writer.print(p.getLowerBound().getName());
        // // writeGenerics(p.getLowerBound());
        // }
        //
        // }
        //
        // }
        // writer.print(">");
        // }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitAfterChildren(AstNode node) {
        if (node instanceof BlockNode) {
            writer.println("}");
        } else if (node instanceof ClassTypeNode) {
            writer.print("}\n");
        } else if (node instanceof StatementNode) {
            writer.print(";\n");
        }
    }

}
