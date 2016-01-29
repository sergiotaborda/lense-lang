/**
 * 
 */
package lense.compiler.crosscompile.java;

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
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.ImportDeclarationsListNode;
import lense.compiler.ast.LambdaExpressionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.NativeArrayInstanceCreation;
import lense.compiler.ast.NativeAssociationInstanceCreation;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.RangeNode;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.ast.VoidValue;
import lense.compiler.crosscompile.java.ast.ArgumentListNode;
import lense.compiler.crosscompile.java.ast.ArithmeticOperation;
import lense.compiler.crosscompile.java.ast.BlockNode;
import lense.compiler.crosscompile.java.ast.ClassInstanceCreation;
import lense.compiler.crosscompile.java.ast.ComparisonNode;
import lense.compiler.crosscompile.java.ast.ExpressionNode;
import lense.compiler.crosscompile.java.ast.ForNode;
import lense.compiler.crosscompile.java.ast.ImplementedInterfacesNode;
import lense.compiler.crosscompile.java.ast.NullValue;
import lense.compiler.crosscompile.java.ast.PosExpression;
import lense.compiler.crosscompile.java.ast.TypeNode;
import lense.compiler.crosscompile.java.ast.VariableDeclarationNode;
import lense.compiler.crosscompile.java.ast.VariableReadNode;
import lense.compiler.typesystem.Kind;
import lense.compiler.typesystem.LenseTypeSystem;

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
		
		if (snode == null || snode instanceof ModuleNode || snode instanceof ImportDeclarationsListNode){
			return null;
		}
		
		if (snode instanceof IdentifierNode){
			return snode;
		} else if (snode instanceof LambdaExpressionNode){
			return null;
		} else if (snode instanceof NumericValue){
			
			lense.compiler.crosscompile.java.ast.NumericValue n = new lense.compiler.crosscompile.java.ast.NumericValue(((NumericValue)snode).getValue());
			n.setValue(((NumericValue)snode).getValue(), ((NumericValue)snode).getTypeDefinition());
			return n;
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
		} else if (snode instanceof ClassTypeNode){
			ClassTypeNode type = (ClassTypeNode)snode;
			lense.compiler.crosscompile.java.ast.ClassType jtype = new lense.compiler.crosscompile.java.ast.ClassType();
			jtype.setName(type.getName());
			
			if (type.getKind() == Kind.Class){
				jtype.setKind(lense.compiler.crosscompile.java.Kind.Class);
			} else if (type.getKind() == Kind.Interface){
				jtype.setKind(lense.compiler.crosscompile.java.Kind.Interface);
			} else if (type.getKind() == Kind.Enum){
				jtype.setKind(lense.compiler.crosscompile.java.Kind.Enum);
			} else if (type.getKind() == Kind.Annotation){
				jtype.setKind(lense.compiler.crosscompile.java.Kind.Annotation);
			}			

			if (type.getSuperType() == null){
				// the any type it self
			} else if (type.getSuperType().getTypeDefinition().equals(LenseTypeSystem.Any())){
				TypeNode sp = new TypeNode("java.lang.Object");
				sp.setTypeDefinition(JavaType.Object);
				jtype.setSuperType(sp);
				jtype.setInterfaces(new ImplementedInterfacesNode());
				
				lense.compiler.crosscompile.java.ast.ClassType any = new lense.compiler.crosscompile.java.ast.ClassType(lense.compiler.crosscompile.java.Kind.Interface);
				any.setName(LenseTypeSystem.Any().getName());
				jtype.getInterfaces().add(any);
			} 
			
			//jtype.setBody((ClassBodyNode) TreeTransverser.transform((AstNode)type.getBody(), this));
			jtype.setInterfaces( (ImplementedInterfacesNode) TreeTransverser.transform((AstNode)type.getInterfaces(), this));
			jtype.setGenerics((lense.compiler.crosscompile.java.ast.TypeParametersListNode) TreeTransverser.transform((AstNode)type.getGenerics(), this));
			
			return jtype;
			
		} else if (snode instanceof NativeArrayInstanceCreation){
			NativeArrayInstanceCreation array = ((NativeArrayInstanceCreation) snode);
			
			ClassInstanceCreation ints = new ClassInstanceCreation();

			ints.setArguments( (ArgumentListNode) TreeTransverser.transform(array.getArguments(), this));
			ints.setTypeNode(new TypeNode("lense.core.collections.JavaNativeImutableSequence"));
			return ints;
		}else if (snode instanceof NativeAssociationInstanceCreation){
			NativeAssociationInstanceCreation array = ((NativeAssociationInstanceCreation) snode);
			
			ClassInstanceCreation ints = new ClassInstanceCreation();

			ints.setArguments( (ArgumentListNode) TreeTransverser.transform(array.getArguments(), this));
			ints.setTypeNode(new TypeNode("lense.core.collections.JavaNativeImutableAssociation"));
			return ints;
		}else if (snode instanceof LiteralTupleInstanceCreation){
			LiteralTupleInstanceCreation array = ((LiteralTupleInstanceCreation) snode);
			
			ClassInstanceCreation ints = new ClassInstanceCreation();

			ints.setArguments( (ArgumentListNode) TreeTransverser.transform(array.getArguments(), this));
			if (ints.getArguments().getChildren().size() == 1){
				ints.getArguments().add(new NullValue());
			}
			ints.setTypeNode(new TypeNode(array.getTypeNode().getName()));
			return ints;
		}else if (snode instanceof VoidValue){
			// TODO translate to static field call Void.Instance.
			return new NullValue();
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
