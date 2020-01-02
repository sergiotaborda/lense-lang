package lense.compiler.crosscompile;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.TypeDefinition;

public class SweepAndMarkVariablesVisitor implements Visitor<AstNode> {

	private final String primitiveAttribute;
	
	private final TypeDefinition type;
	private final PrimitiveTypeDefinition primitiveType;
	private Set<String> notAllowed = new HashSet<>();

	public SweepAndMarkVariablesVisitor(TypeDefinition type, PrimitiveTypeDefinition primitiveType) {
		this.type = type;
		this.primitiveType = primitiveType;

		
		notAllowed.add(ArithmeticOperation.Addition.equivalentMethod());
		notAllowed.add(ArithmeticOperation.Multiplication.equivalentMethod());
		notAllowed.add(ArithmeticOperation.Subtraction.equivalentMethod());
		notAllowed.add(ArithmeticOperation.Division.equivalentMethod());
		notAllowed.add(ArithmeticOperation.Power.equivalentMethod());
		
		this.primitiveAttribute = "primitive@" + type.getName();
		
	}


	public Optional<Boolean> isPrimitive(AstNode node) {
		return node.getProperty(primitiveAttribute,Boolean.class);
	}

	public Optional<Boolean> isPrimitive(VariableInfo varInfo) {
		return varInfo.getProperty(primitiveAttribute, Boolean.class);
	}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {

		if (node instanceof NumericValue) {
			NumericValue n = (NumericValue)node;
			
			if (n.getTypeVariable().equals(type)) {
				node.setProperty(primitiveAttribute,true);
			}
			
			
		} else if (node instanceof VariableDeclarationNode) {
			VariableDeclarationNode dec = (VariableDeclarationNode)node;

			ExpressionNode initializer = dec.getInitializer();
			VariableInfo varInfo = dec.getInfo();

			if (initializer != null && type.equals(initializer.getTypeVariable())){
				
				VariableRange limit = VariableRange.extractFrom(dec.getInitializer());

				limit.getMax().ifPresent(m -> varInfo.setMaximumValue(m));
				limit.getMin().ifPresent(m -> varInfo.setMininumValue(m));

				varInfo.setIncludeMaximum(limit.isIncludeMax());

				varInfo.setProperty(primitiveAttribute, initializer.getProperty(primitiveAttribute, Boolean.class).orElse(false));
			} 
		} else if (node instanceof VariableReadNode) {
			VariableReadNode varNode  = (VariableReadNode)node;
			
			if (varNode.getVariableInfo().getTypeVariable().equals(type)) {
				
				Optional<MethodInvocationNode> m = findParentMethodInvocationNode(varNode);
				
				if (m.isPresent()) {
					Boolean primitive = varNode.getVariableInfo().getProperty(primitiveAttribute, Boolean.class).orElse(true);
					
					if (primitive) {
						Boolean newPrimitive = m.map(it -> isPrimitiveErasurableMethod(varNode, it)).orElse(false);
						varNode.getVariableInfo().setProperty(primitiveAttribute, newPrimitive );
						
						Boolean methodPrimitive = m.get().getProperty(primitiveAttribute, Boolean.class).orElse(true);
						
						m.get().setProperty(primitiveAttribute, methodPrimitive && newPrimitive );
						
						
					} else {
						m.get().setProperty(primitiveAttribute, false );
						
					}
				}
			
			}
			
		} else if (!(node instanceof MethodInvocationNode)) {
			AstNode child = node.getFirstChild();
			
			if (child != null) {
				child.copyAttributesTo(node);
			}
		}
	}
	
	
	private Optional<MethodInvocationNode> findParentMethodInvocationNode(VariableReadNode node){
		
		AstNode n = node;
		while (n != n.getParent() && n.getParent() != null) {
			n = n.getParent();
			
			if (n instanceof MethodInvocationNode) {
				return Optional.of((MethodInvocationNode)n);
			} else if (n instanceof MethodDeclarationNode) {
				break;
			}
		}
		
		return Optional.empty();
	}
	
	private boolean isPrimitiveErasurableMethod(VariableReadNode node, MethodInvocationNode m) {
		
//		AstNode n = node;
//		while (n != m && n != n.getParent() && n.getParent() != null) {
//			n = n.getParent();
//			
//			if (n instanceof ErasurePointNode) {
//				ErasurePointNode err = (ErasurePointNode)n;
//				
//				return err.getBoxingDirection() == BoxingDirection.BOXING_IN;
//			}
//		}
//		
		
		return !this.notAllowed.contains(m.getCall().getName());
	}










}
