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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

public class ByteCodeReader extends ClassVisitor {

	LenseTypeDefinition def;

	public ByteCodeReader() {
		super(ASM5);
	}

	public TypeDefinition getType() {
		return def;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Llense/core/lang/java/SingletonObject;")){
			def.setKind(lense.compiler.type.LenseUnitKind.Object);
		}
		if (desc.equals("Llense/core/lang/java/Signature;")){
			return new SignatureAnnotationVisitor();
		}
		if (desc.equals("Llense/core/lang/java/Native;")){
			return new NativeAnnotationVisitor();
		}
		return null;

	}
	public void visit(int version, int access, String name,String signature, String superName, String[] interfaces) {
		lense.compiler.type.TypeKind kind =  lense.compiler.type.LenseUnitKind.Class;
		if ((access & ACC_INTERFACE) !=0){
			kind = lense.compiler.type.LenseUnitKind.Interface;
		} else if ((access & ACC_ENUM) !=0){
			kind = lense.compiler.type.LenseUnitKind.Enum;
		} else if ((access & ACC_ANNOTATION) !=0){
			kind = lense.compiler.type.LenseUnitKind.Annotation;
		} 

		LenseTypeDefinition superDef = new LenseTypeDefinition("lense.core.lang.Any", LenseUnitKind.Class, null);
		if (superName != null && !superName.startsWith("java/")){
			superDef = new LenseTypeDefinition(superName.replace('/', '.'), LenseUnitKind.Class, null);
		}
		def = new LenseTypeDefinition(name.replace('/', '.'),kind,superDef);

		if (interfaces.length > 0){
			for(String f : interfaces){
				if (f != null && !f.startsWith("java/")){
					def.addInterface( new LenseTypeDefinition(f.replace('/', '.'),LenseUnitKind.Interface,null));
				}
			}
		}
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
					char type = desc.charAt(s);
					if (type == 'L'){
						a = desc.indexOf(';',s) + 1;
						params.add(new ConstructorParameter( typeForName(desc.substring(s, a))));
					} else if (type == 'Z'){
						params.add(new ConstructorParameter( typeForName(LenseTypeSystem.Boolean().toString())));
					} else {
						return null;
					}
				}

				Constructor m = new  Constructor(name,params , false); // TODO read implicit
				m.setDeclaringType(def);
				
				return new ConstructorAnnotVisitor(this, m);
			} else {
				// instance methods
				int pos = desc.lastIndexOf(')');

				MethodReturn r = new MethodReturn(new FixedTypeVariable( typeForName(desc.substring(pos+1))));

				List<MethodParameter> params = new ArrayList<>();
				int a =1;
				while (a < pos){
					int s = a;
					char type = desc.charAt(s);
					if ( type == 'L'){
						a = desc.indexOf(';',s) + 1;
						params.add(new MethodParameter( typeForName(desc.substring(s, a))));
					} else if (type == 'Z'){
						params.add(new MethodParameter( typeForName(LenseTypeSystem.Boolean().toString())));
					} else {
						return null;
					}
				}

				Method m = new Method(name,r,params);
				m.setDeclaringType(def);
				
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
				member = new IndexerProperty(def, method.getReturningType(), false, false,params);
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

	private class NativeAnnotationVisitor extends AnnotationVisitor {

		public NativeAnnotationVisitor() {
			super(ASM5);
		}
		
		public void visit(String name, Object value) {

			if ("overridable".equals(name)){
				def.setNative(!((Boolean)value).booleanValue());
			}
		}
	}
	private class SignatureAnnotationVisitor extends AnnotationVisitor {

		public SignatureAnnotationVisitor() {
			super(ASM5);
		}

		public void visit(String name, Object value) {

			if ("value".equals(name)){
				String[] parts = ((String)value).split(":");
				
				if (parts.length < 3){
					parts = new String[]{parts[0], "",""};
				}
				
				Map<String, Integer> maps = new HashMap<>();
				
				int pos = parts[0].indexOf('[');
				if (pos >=0){
					String[] variables = parts[0].substring(pos+1,  parts[0].lastIndexOf(']')).split(",");
					String[] names = new String[variables.length];
				
					for(int i =0; i < variables.length; i++){
						String s = variables[i];
						Variance variance = lense.compiler.typesystem.Variance.Invariant;
						if (s.startsWith("+")){
							variance = lense.compiler.typesystem.Variance.Covariant;
						} else if (s.startsWith("-")){
							variance = lense.compiler.typesystem.Variance.ContraVariant;
						}
						pos = s.indexOf('<');
						String symbol = s.substring(1, pos);
						String upperBound = s.substring(pos+1);

						names[i] = symbol;
						maps.put(symbol, i);
						if (upperBound.equals(LenseTypeSystem.Any().getName())){
							def.addGenericParameter(symbol, new RangeTypeVariable( symbol, variance, LenseTypeSystem.Any(), LenseTypeSystem.Nothing()));
						} else {
							def.addGenericParameter(symbol, new RangeTypeVariable( symbol, variance,new LenseTypeDefinition(upperBound, null, null) , LenseTypeSystem.Nothing()));
						}
					}
				}
			
				String s = parts[1];
				if (s.length()> 0){
					parseSuperTypeSignature(s, maps, def);
				}

				String[] g = parts[2].contains("&") ?  parts[2].split("&") : new String[]{parts[2]};
				for (String ss :g ){
					parseInterfaceSignature(ss, maps, def);
				}

			}
		}


		public void parseInterfaceSignature(String ss,Map<String, Integer> maps, TypeDefinition parent){
			int pos = ss.indexOf('<');
			if (pos <0){
				return;
			}
			String interfaceType = ss.substring(0, pos);
			LenseTypeDefinition type = new LenseTypeDefinition(interfaceType,LenseUnitKind.Interface, null);
			String n = ss.substring(pos+ 1 , ss.lastIndexOf('>'));
			if (n.contains("<")){

				parseInterfaceSignature(n, maps, type);

			} else {
				String[] g = n.contains(",") ?  n.split(",") : new String[]{n};
				int i= 0;
				for (String symbol : g){
					Integer index = maps.get(symbol);
					TypeVariable tv;
					if (index != null){
						// generic type
						tv = new DeclaringTypeBoundedTypeVariable(parent, index, symbol,lense.compiler.typesystem.Variance.Invariant);
						type.addGenericParameter(symbol, tv);
					} else {
						// hard bound
						tv = new FixedTypeVariable(new LenseTypeDefinition(symbol, null, null));
						type.addGenericParameter("X"+Integer.toString(i), tv);
					}		    	
					i++;
				}
			}
			def.addInterface(type);

		}
		
		public void parseSuperTypeSignature(String ss,Map<String, Integer> maps, TypeDefinition parent){
			int pos = ss.indexOf('<');
			if (pos <0){
				return;
			}
			String interfaceType = ss.substring(0, pos);
			LenseTypeDefinition type = new LenseTypeDefinition(interfaceType,LenseUnitKind.Interface, null);
			String n = ss.substring(pos+ 1 , ss.lastIndexOf('>'));
			if (n.contains("<")){

				parseSuperTypeSignature(n, maps, type);

			} else {
				String[] g = n.contains(",") ?  n.split(",") : new String[]{n};
				for (String symbol : g){
					Integer index = maps.get(symbol);
					TypeVariable tv;
					if (index != null){
						// generic type
						tv = new DeclaringTypeBoundedTypeVariable(parent, index, symbol, lense.compiler.typesystem.Variance.Invariant);
						type.addGenericParameter(symbol, tv);
					} else {
						// hard bound
						tv = new FixedTypeVariable(new LenseTypeDefinition(symbol, null, null));
						type.addGenericParameter("", tv);
					}		    		  
				}
			}
			def.setSuperTypeDefinition(type);

		}
	}
}
