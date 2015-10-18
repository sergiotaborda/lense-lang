/**
 * 
 */
package lense.compiler.crosscompile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.AstNodeProperty;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.RangeNode;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.crosscompile.java.ast.ArithmeticOperation;
import lense.compiler.crosscompile.java.ast.BlockNode;
import lense.compiler.crosscompile.java.ast.ComparisonNode;
import lense.compiler.crosscompile.java.ast.ExpressionNode;
import lense.compiler.crosscompile.java.ast.ForNode;
import lense.compiler.crosscompile.java.ast.PosExpression;
import lense.compiler.crosscompile.java.ast.VariableDeclarationNode;
import lense.compiler.crosscompile.java.ast.VariableReadNode;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;

/**
 * 
 */
public class Lense2JavaTransformer implements Function<AstNode, AstNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AstNode apply(AstNode snode) {
		
		if (snode == null){
			return null;
		}
		
		if (snode instanceof IdentifierNode){
			return snode;
		} else if (snode instanceof NumericValue){
			
			return new lense.compiler.crosscompile.java.ast.NumericValue(((NumericValue)snode).getValue());
		} else if (snode instanceof TypeParametersListNode){
			
			lense.compiler.crosscompile.java.ast.TypeParametersListNode jnode = new lense.compiler.crosscompile.java.ast.TypeParametersListNode();
			
			for(AstNode n : snode.getChildren()){
				jnode.add(apply(n));
			}
			
			return jnode;
		} else if (snode instanceof FieldOrPropertyAccessNode){
			FieldOrPropertyAccessNode p = (FieldOrPropertyAccessNode)snode;
			lense.compiler.crosscompile.java.ast.FieldAccessNode n = new lense.compiler.crosscompile.java.ast.FieldAccessNode();
			
			n.setName(p.getName());
			n.setTypeDefinition(n.getTypeDefinition());
			n.setPrimary(apply(p.getPrimary()));
			return n;
		} else if (snode instanceof ForEachNode){
		
			ForEachNode forloop = (ForEachNode)snode;
			if (forloop.getContainer() instanceof RangeNode){
				RangeNode range = (RangeNode) forloop.getContainer();
				ForNode jloop = new ForNode();
				
				jloop.setBlock( (BlockNode) apply(forloop.getBlock()));
				final VariableDeclarationNode init = (VariableDeclarationNode) apply(forloop.getVariableDeclarationNode());
				init.setInitializer((ExpressionNode) apply(range.getStart()));
				jloop.setVariableDeclarationNode( init);
				
				ComparisonNode conditional = new ComparisonNode(ComparisonNode.Operation.LessOrEqualTo);
				conditional.add(new VariableReadNode(forloop.getVariableDeclarationNode().getName()));
				conditional.add(apply(range.getEnd()));
				
				jloop.setConditional(conditional);
				
				PosExpression increment = new PosExpression(ArithmeticOperation.Increment);
				increment.add(new VariableReadNode(forloop.getVariableDeclarationNode().getName()));
				
				jloop.setConditional(increment);
				
				return jloop;
			} 
		} else if (snode instanceof RangeNode){
			return null;
		}
		
		
		try {
			
			Class type = Class.forName(snode.getClass().getName().replaceAll("lense.compiler.ast", "lense.compiler.crosscompile.java.ast"));
			
			Constructor c;
			try{
			    c = type.getConstructor();
			} catch (NoSuchMethodException e){
				c = type.getConstructors()[0];
			}
			
			AstNode jnode = (AstNode) c.newInstance(new Object[c.getParameters().length]) ;

			Map<String,AstNodeProperty> mapping = new HashMap<>();
			for(AstNodeProperty g : readProperties(jnode.getClass())){
				mapping.put(g.getName(), g);
			}
			
			for(AstNodeProperty f : readProperties(snode.getClass())){
				if (f.getName().equals("children") || f.getName().equals("parent")){
					continue;
				}
				AstNodeProperty g = mapping.get(f.getName());
				
				if (g != null){
					Object obj = f.get(snode);
					
					if (obj instanceof LenseAstNode){
						obj = TreeTransverser.transform((AstNode)obj, this);
					} else if (obj instanceof Enum){
						Class enumType = Class.forName(obj.getClass().getName().replaceAll("lense.compiler.ast", "lense.compiler.crosscompile.java.ast"));
						
						obj = Enum.valueOf(enumType, ((Enum) obj).name());
					} 
					try{
						g.set(jnode, obj);
					}catch (Exception e){
						throw e;
					}
				}
			
			}
			
			return jnode;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Set<AstNodeProperty> readProperties(Class type){
		
		Set<AstNodeProperty> f = new HashSet<AstNodeProperty>();
		
		collectProperties(type, f);
		return f;
	}

	private static void collectProperties(Class type, Set<AstNodeProperty> fields){
		
		Stream.concat(Stream.of(type.getMethods()), Stream.of(type.getDeclaredMethods()))
		.filter( m ->m.getName().startsWith("set") && !Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 1)
		.map(m -> {
			
			String name = m.getName().substring(3);
			String prefix =  Boolean.class.isAssignableFrom(m.getReturnType()) ||  Boolean.TYPE.isAssignableFrom(m.getReturnType()) ? "is" : "get";
			Method acessor;
			try {
				acessor = m.getDeclaringClass().getMethod(prefix + name, new Class[0]);
				return  new AstNodeProperty( name.substring(0,1).toLowerCase() + name.substring(1), acessor, m);
			} catch (Exception e) {
				return null;
			}
		}).filter( p -> p != null).collect(Collectors.toCollection(() -> fields));
		
		if (type.getSuperclass() != null){
			collectProperties(type.getSuperclass(), fields);
		}
	}
	
	

}