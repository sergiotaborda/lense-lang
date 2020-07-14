package lense.compiler.crosscompile.javascript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.FieldAccessNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.LiteralAssociationInstanceCreation;
import lense.compiler.ast.LiteralIntervalNode;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NoneValue;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ObjectReadNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PropertyDeclarationNode;
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
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.crosscompile.ErasurePointNode;
import lense.compiler.crosscompile.PrimitiveBox;
import lense.compiler.crosscompile.PrimitiveBooleanOperationsNode;
import lense.compiler.crosscompile.PrimitiveUnbox;
import lense.compiler.crosscompile.PrimitiveBooleanValue;
import lense.compiler.crosscompile.java.JavaSourceWriterVisitor;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;

public class Ecma5JavascriptWriterVisitor implements Visitor<AstNode> {

    private PrintWriter writer;
    private File nativeFolder;
	private String rootNameSpace;

    /**
     * Constructor.
     * @param file 
     * 
     * @param writer
     */
    public Ecma5JavascriptWriterVisitor(File nativeFolder, PrintWriter writer, String rootNameSpace) {
        this.writer = writer;
        this.rootNameSpace = rootNameSpace;
        this.nativeFolder = nativeFolder;
    }

    @Override
    public void startVisit() {

    }

    @Override
    public void endVisit() {
        writer.flush();
    }

    private static Set<String> reservedWords = new HashSet<>(Arrays.asList("var", "function"));

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
                
                writer.append("if (!(");
                
                TreeTransverser.transverse(node.getFirstChild(), this);
                
                writer.append(")){ throw lense.core.lang.AssertionException.constructor(); }").println();
                
                return VisitorNext.Siblings;
            } else if (node instanceof StringValue) {
                writer.append("lense.core.lang.String.valueOfNative(").append("\"")
                        .append(((StringValue) node).getValue()).append("\")");
            } else if (node instanceof BooleanValue) {

                if (node instanceof PrimitiveBooleanValue) {
                    writer.print(((BooleanValue) node).getLiteralValue());
                } else {
                    writer.print(((BooleanValue) node).isValue() 
                            ? "lense.core.lang.Boolean.TRUE"
                            : "lense.core.lang.Boolean.FALSE");
                }

            } else if (node instanceof PrimitiveBooleanOperationsNode) {
                PrimitiveBooleanOperationsNode n = (PrimitiveBooleanOperationsNode) node;

                if (n.getOperation() == BooleanOperation.LogicNegate) {
                    writer.print("!");
                    TreeTransverser.transverse(n.getChildren().get(0), this);
                } else {
                    throw new UnsupportedOperationException();
                }
                return VisitorNext.Siblings;
            } else if (node instanceof PrimitiveUnbox) {

                writer.print("/* BOXING OUT */(");
                TreeTransverser.transverse(node.getChildren().get(0), this);
                writer.print(").toPrimitiveBoolean()");

                return VisitorNext.Siblings;
            } else if (node instanceof PrimitiveBox) {

                writer.print("/* BOXING IN */ lense.core.lang.Boolean.valueOfNative(");
                TreeTransverser.transverse(node.getChildren().get(0), this);
                writer.print(")");

                return VisitorNext.Siblings;
            } else if (node instanceof ErasurePointNode) {
                TypeVariable typeVariable = ((ErasurePointNode) node).getTypeVariable();
//                if (typeVariable != null) {
//                    writer.print("/* BOXING IN to " + typeVariable.getTypeDefinition().getName() + "*/");
//                } else {
//                    writer.print("/* BOXING IN to ?  */");
//                }

                TreeTransverser.transverse(node.getChildren().get(0), this);
                return VisitorNext.Siblings;

            } else if (node instanceof IdentifierNode) {
                String identifier = ((IdentifierNode) node).getName();
                writer.print(sanitize(identifier));
            } else if (node instanceof QualifiedNameNode) {
                writer.print(((QualifiedNameNode) node).getName());
            } else if (node instanceof NumericValue) {
                NumericValue n = (NumericValue) node;
                String name = n.getTypeVariable().getTypeDefinition().getName();
                if (name.equals("lense.core.lang.Binary")) {
                    writer.append(
                            "lense.core.lang.BitArray.constructor(lense.core.collections.Array.booleanArrayfromNativeNumberString(\"")
                            .append(node.toString()).append("\"))");
                } else if (name.equals("lense.core.math.Rational")) {
                    // TODO test bounds (number could be to big , should use
                    // string
                    int pos = n.getLiteralValue().indexOf('.');
                    if (pos >= 0) {
                        String intPart = n.getLiteralValue().substring(0, pos);
                        String decPart = n.getLiteralValue().substring(pos + 1);

                        int decPos = decPart.length();
                        StringBuilder builder = new StringBuilder("1");
                        for (int i = 0; i < decPos; i++) {
                            builder.append("0");
                        }
                        if (intPart.equals("0")) {
                            intPart = "";
                        }
                        writer.append(n.getTypeVariable().getTypeDefinition().getName()).append(".valueOfNative(")
                                .append(intPart).append(decPart).append(',').append(builder).append(")");

                    } else {
                        writer.append(n.getTypeVariable().getTypeDefinition().getName()).append(".valueOfNative(")
                                .append(n.toString()).append(")");
                    }
                } else {
                    // TODO test bounds (number could be to big , should use
                    // string

                    writer.append(n.getTypeVariable().getTypeDefinition().getName()).append(".valueOfNative(")
                            .append(n.toString()).append(")");
                }

            } else if (node instanceof lense.compiler.ast.LiteralSequenceInstanceCreation) {
                writer.append("lense.core.collections.Array.fromAnyArray(");

                Iterator<AstNode> it = ((lense.compiler.ast.LiteralSequenceInstanceCreation) node).getArguments()
                        .getChildren().iterator();
                while (it.hasNext()) {
                    TreeTransverser.transverse(it.next(), this);
                    if (it.hasNext()) {
                        writer.print(",");
                    }
                }

                writer.append(")");
                return VisitorNext.Siblings;
            } else if (node instanceof LiteralTupleInstanceCreation) {
                LiteralTupleInstanceCreation tuple = (LiteralTupleInstanceCreation) node;

                writer.append("lense.core.collections.Tuple.valueOf(");

                Iterator<AstNode> it = tuple.getArguments().getChildren().iterator();
                while (it.hasNext()) {
                    TreeTransverser.transverse(it.next(), this);
                    if (it.hasNext()) {
                        writer.print(",");
                    }
                }
                writer.append(")");
                return VisitorNext.Siblings;
            } else if (node instanceof lense.compiler.ast.LiteralAssociationInstanceCreation) {
                LiteralAssociationInstanceCreation tuple = (LiteralAssociationInstanceCreation) node;

                writer.append("lense.core.collections.Dictionary.fromKeyValueArray(");

                Iterator<AstNode> it = tuple.getArguments().getChildren().iterator();
                while (it.hasNext()) {
                    TreeTransverser.transverse(it.next(), this);
                    if (it.hasNext()) {
                        writer.print(",");
                    }
                }
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

                writer.append("lense.core.math.Interval.constructor(");

                if (interval.isStartInf()) {
                    writer.append("lense.core.lang.None.None");
                } else {
                    writer.append("lense.core.lang.Some.constructor(");
                    TreeTransverser.transverse(interval.getStart(), this);
                    writer.append(")");
                }

                writer.append(",");

                if (interval.isEndInf()) {
                    writer.append("lense.core.lang.None.None");
                } else {
                    writer.append("lense.core.lang.Some.constructor(");
                    TreeTransverser.transverse(interval.getEnd(), this);
                    writer.append(")");
                }

                writer.append(");");
                return VisitorNext.Siblings;
            } else if (node instanceof ClassTypeNode) {
                ClassTypeNode t = (ClassTypeNode) node;

                switch (t.getKind()) {

                case Class:
                case Object:
                    writeClass(t);
                    break;
                case Enum:
                    // TODO
                case Interface:
                case Annotation:
                    return VisitorNext.Siblings;
                }

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

                        writer.print(" catch (");

                        FormalParameterNode p = c.getExceptions();

                        TreeTransverser.transverse(p.getTypeNode(), this);

                        writer.append(" ").append(p.getName());
                        
                        writer.println(")");

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

                writer.append(n.getTypeVariable().getTypeDefinition().getName()).append(".").append(n.getObjectName());
            } else if (node instanceof ForEachNode) {
                ForEachNode f = (ForEachNode) node;

                writer.println();
                writer.append("var $it = (");
                TreeTransverser.transverse(f.getContainer(), this);
                writer.append(").getIterator();\n");
                writer.append("while ( $it.moveNext()) {\n");

                TreeTransverser.transverse(f.getVariableDeclarationNode(), this);

                writer.append("= ").append(" $it.current();\n");

                StringWriter w = new StringWriter();
                PrintWriter sp = new PrintWriter(w);
                JavaSourceWriterVisitor visitor = new JavaSourceWriterVisitor(sp);
                TreeTransverser.transverse(f.getBlock(), visitor);

                String str = w.toString();
                str = str.substring(str.indexOf('{') + 1, str.lastIndexOf("}"));
                writer.append(str);
                writer.append("}\n");
                // }

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
                writer.print(" switch (");
                TreeTransverser.transverse(n.getCandidate(), this);
                writer.println(") { ");

                for (AstNode a : n.getOptions().getChildren()) {
                    SwitchOption op = (SwitchOption) a;
                    if (op.isDefault()) {
                        writer.print(" default");
                    } else {
                        writer.print(" case ");
                        TreeTransverser.transverse(op.getValue(), this);
                        writer.println(":");
                    }

                    TreeTransverser.transverse(op.getActions(), this);
                    writer.println("break;");
                }

                writer.println("}");

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

                if (n.getAccess() != null) {
                    TreeTransverser.transverse(n.getAccess(), this);
                    writer.print(".");
                }

                writer.print(n.getCall().getName());
                writer.print("(");
                if (n.getCall().getArguments() != null) {
                    TreeTransverser.transverse(n.getCall().getArguments(), this);
                }

                writer.print(")");

                if (node.getParent() instanceof BlockNode) {
                    writer.println(";");
                }

                return VisitorNext.Siblings;
            } else if (node instanceof CastNode) {
                CastNode n = (CastNode) node;

                TreeTransverser.transverse(n.getInner(), this);
                
//                if (n.getType().getName().equals(LenseTypeSystem.Any().getName())) {
//
//                    TreeTransverser.transverse(n.getInner(), this);
//
//                } else {
//                    writer.append("((").append(n.getType().getName()).append(")");
//
//                    TreeTransverser.transverse(n.getInner(), this);
//
//                    writer.append(")");
//                }

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

                if (n.getConstructor().isImplicit()){
                    writer.print("new ");
                    
                    if (n.getTypeVariable() == null) {
                        writer.print(n.getTypeNode().getName());
                    } else {
                        writer.print(n.getTypeVariable().getTypeDefinition().getName());
                    }
                } else {
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
                }
                
                writer.print("(");
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
                        TreeTransverser.transverse(n, this);
                        writer.print(".toString())");
                    }

                }
                writer.print(".toString())");
                return VisitorNext.Siblings;
            } else if (node instanceof ArgumentListNode) {
                ArgumentListNode n = (ArgumentListNode) node;

                boolean first = true;
                for (AstNode a : n.getChildren()) {
                    ArgumentListItemNode item = (ArgumentListItemNode) a;
                    if (!first) {
                        writer.print(",");
                    } else {
                        first = false;
                    }
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


                writer.print(" var ");
                writer.print(sanitize(t.getName()));

                if (t.getInitializer() != null) {
                    writer.print(" = ");
                    TreeTransverser.transverse(t.getInitializer(), this);
                }

                if (t.getInfo() != null) {

                    if (t.getInfo().doesEscape()) {
                       // writer.print("/* doesEscape */");
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

                    writer.print(sanitize(n.getName()));

                    String propertyName = n.getName().substring(0, 1).toUpperCase() + n.getName().substring(1);
                    writer.append(".get").append(propertyName).append("(").append(")");

                }

                return VisitorNext.Siblings;

            } else if (node instanceof ConstructorDeclarationNode) {
                ConstructorDeclarationNode m = (ConstructorDeclarationNode) node;


                writer.print(" ");

                if (!m.isPrimary()) {
                    writer.println("\n ");
                    writer.print(" function ");
                    if (m.getName() == null) {
                        writer.print("constructor");
                    } else {
                        writer.print(sanitize(m.getName()));
                    }
                }

                writer.print("(");

                if (m.getParameters() != null) {
                    boolean isFirst = true;
                    for (AstNode n : m.getParameters().getChildren()) {
                        FormalParameterNode p = (FormalParameterNode) n;

                        if (isFirst) {
                            isFirst = false;
                        } else {
                            writer.print(", ");
                        }

                        writer.print(sanitize(p.getName()));

                    }
                }

                writer.print(")");

                TreeTransverser.transverse(m.getBlock(), this);
                
                writer.println("\n");
                return VisitorNext.Siblings;
            } else if (node instanceof MethodDeclarationNode) {

                MethodDeclarationNode m = (MethodDeclarationNode) node;

                writer.println("\t");

//                if (m.isProperty()) {
//                     writer.print("@lense.core.lang.java.Property(");
//                    if (m.isIndexer()) {
//                        writer.append(" indexed = true");
//                    } else {
//                        writer.append(" name = \"").append(m.getPropertyName()).append("\"");
//                    }
//                    if (m.isSetter()) {
//                        writer.append(", setter = ");
//                        writer.print(m.isSetter());
//                    }
//                    writer.println(")");
//                }


             
                
                writer.print(" function ");
                writer.print(m.getName());
                writer.print("(");

                if (m.getParameters() != null) {
                    boolean isFirst = true;
                    for (AstNode n : m.getParameters().getChildren()) {
                        FormalParameterNode p = (FormalParameterNode) n;

                        if (isFirst) {
                            isFirst = false;
                        } else {
                            writer.print(", ");
                        }

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

                writer.append("( typeof (");

                TreeTransverser.transverse(n.getExpression(), this);
                writer.append(") === \"").append(n.getTypeNode().getName());

                writer.append("\")");

                return VisitorNext.Siblings;
            } else if (node instanceof FieldDeclarationNode) {
                FieldDeclarationNode m = (FieldDeclarationNode) node;

                writer.print("\n");

                writer.print(" var ");
                writer.print(m.getName());

                if (!m.getInitializedOnConstructor() && m.getInitializer() != null) {
                    writer.print(" = ");
                    TreeTransverser.transverse(m.getInitializer(), this);
                }
                writer.print(";\n");
                return VisitorNext.Siblings;
            } else if (node instanceof PropertyDeclarationNode) {
                PropertyDeclarationNode m = (PropertyDeclarationNode) node;

                writer.print("\n");
                writer.print(" var ");
                writer.print(m.getName());

                if (m.getInitializer() != null) {
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

    private void writeClass(ClassTypeNode t) {

        if (t.isAbstract()){
            return;
        }
        if (t.isNative()){
            // TODO 
            
            File nativeFile = new File(nativeFolder, t.getFullname() + ".js");
            if (nativeFile.exists()){
                
                try (BufferedReader reader = new BufferedReader(new FileReader(nativeFile))){
                    String line;
                    while ((line = reader.readLine()) != null){
                        writer.println(line);
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            } else {
                writer.println("/* absent native " + t.getFullname() + "*/");
            }
            
            return;
        }

        writer.append(t.getFullname());
        
        writer.append(" = function ");

        // write primary constructor

        Collection<AstNode> constructors = t.getBody().findChilds(
                node -> node instanceof ConstructorDeclarationNode && ((ConstructorDeclarationNode) node).isPrimary());

        if (constructors.isEmpty()) {
            writer.append("()");
        } else {
            
            for (AstNode node : constructors) {
                ConstructorDeclarationNode ctr = (ConstructorDeclarationNode)node;
                if (ctr.isPrimary()){
                    TreeTransverser.transverse(node, this);
                    t.getBody().remove(node);
                }
            }
        }

        writer.println("{");
        
        for (AstNode node : constructors) {
            ConstructorDeclarationNode ctr = (ConstructorDeclarationNode)node;
            if (!ctr.isPrimary()){
                TreeTransverser.transverse(node, this);
                t.getBody().remove(node);
            }
        }

        TreeTransverser.transverse(t.getBody(), this);
        writer.println();
        writer.println("}");
    }

    private void writeType(TypeNode t) {
        if (t.getName().equals("lense.core.lang.Void")) {
            writer.print("void");

        } else if (t.getName().equals("boolean")) {
            writer.print("boolean");
        } else {
            final TypeVariable type = t.getTypeVariable();
            if (type == null) {
                TypeVariable upper = t.getTypeParameter().getUpperBound();

                writer.print(upper.getTypeDefinition().getName());

            } else if (type.isFixed() ) {

                TypeDefinition def = type.getTypeDefinition();
                writer.print(def.getName());
                writeGenerics(def);

            } else {
                TypeVariable interval = ((TypeVariable) type);
                writer.print(interval.getSymbol());
                if (!interval.getSymbol().equals(interval.getUpperBound().getSymbol())) {
                    writer.print(" extends ");
                    writer.print(interval.getUpperBound().getTypeDefinition().getName());
                }

            }

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
