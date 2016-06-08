package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.type.variable.TypeVariable;

public class ByteCodeReader extends ClassVisitor {

	LenseTypeDefinition def;

	public ByteCodeReader() {
		super(ASM5);
	}

	public TypeDefinition getType() {
		return def;
	}

	public void visit(int version, int access, String name,String signature, String superName, String[] interfaces) {
		lense.compiler.type.TypeKind kind = lense.compiler.type.LenseUnitKind.Class;
		if ((access & ACC_INTERFACE) !=0){
			kind = lense.compiler.type.LenseUnitKind.Interface;
		} else if ((access & ACC_ENUM) !=0){
			kind = lense.compiler.type.LenseUnitKind.Enum;
		} else if ((access & ACC_ANNOTATION) !=0){
			kind = lense.compiler.type.LenseUnitKind.Annotation;
		} 
		def = new LenseTypeDefinition(name.replace('/', '.'),kind,null);
	}

	public FieldVisitor visitField(int access, String name, String desc,String signature, Object value) {
		if ((access & ACC_STATIC) !=0){
			System.out.println(" STATIC " + desc + " " + name);
		} else {
			System.out.println(" " + desc + " " + name);
		}

		return null;
	}

	public MethodVisitor visitMethod(int access, String name,String desc, String signature, String[] exceptions) {
		if (name.startsWith("<")){
			return null;
		}
		try {
			if ((access & ACC_STATIC) !=0){
				// constructors
				List<ConstructorParameter> params = new ArrayList<ConstructorParameter>();
				
				int pos = desc.lastIndexOf(')');
		
				int a =1;
				while (a < pos){
					int s = a;
					if (desc.charAt(s) == 'L'){
						a = desc.indexOf(';',s) + 1;
						params.add(new ConstructorParameter( typeForName(desc.substring(s, a))));
					} else {
						return null;
					}
				}
				
				Constructor m = new  Constructor(name,params , false); // TODO read implicit
				return new ConstructorAnnotVisitor(this, m);
			} else {
				// instance methods
				int pos = desc.lastIndexOf(')');

				MethodReturn r = new MethodReturn(new FixedTypeVariable( typeForName(desc.substring(pos+1))));

				List<MethodParameter> params = new ArrayList<>();
				int a =1;
				while (a < pos){
					int s = a;
					if (desc.charAt(s) == 'L'){
						a = desc.indexOf(';',s) + 1;
						params.add(new MethodParameter( typeForName(desc.substring(s, a))));
					} else {
						return null;
					}
				}
				
				Method m = new Method(name,r,params);
				return new MethodAnnotVisitor(this, m);
			}
		} catch (IllegalArgumentException e){
			return null;
		}


	}

	private TypeDefinition typeForName(String name) {
		if ("V".equals(name)){
			return LenseTypeSystem.Void();
		} else if (name.startsWith("L")){
			return new LenseTypeDefinition(name.substring(1, name.length()-1).replace('/', '.'), null, null);
		} else {
			throw new IllegalArgumentException();
		}

	}

	public void addMethod(Method method) {
		def.addMethod(method);
	}

	public void addConstructor(Constructor method) {
		def.addConstructor(method);
	}

	private Map<String, TypeMember> properties = new HashMap<String, TypeMember>();
	
	public void addPropertyPart(Method method, boolean isIndexed, String propertyName, boolean isSetter) {
		if (isIndexed){
			String key = method.getParameters().toString();
			IndexerProperty member = (IndexerProperty)properties.get(key);
			if ( member == null){
				
				TypeVariable[] params = method.getParameters().stream().map( p -> p.getType()).collect(Collectors.toList()).toArray(new TypeVariable[0]);
				member = new IndexerProperty(method.getDeclaringType(), method.getReturningType(), false, false,params);
				properties.put(key, member);
				
				def.addIndexer(member);
			}
			if (!member.canRead() && !isSetter){
				member.setReadable(true);
			}
			if (!member.canWrite() && isSetter){
				member.setWritable(true);
			}

			
		} else {
			Property member = (Property)properties.get(propertyName);
			if ( member == null){
				member = new Property(method.getDeclaringType(), propertyName, method.getReturningType(), false, false);
				properties.put(propertyName, member);
				
				def.addProperty(member);
			}
			if (!member.canRead() && !isSetter){
				member.setReadable(true);
			}
			if (!member.canWrite() && isSetter){
				member.setWritable(true);
			}
			
		}
		
	}
	

}
