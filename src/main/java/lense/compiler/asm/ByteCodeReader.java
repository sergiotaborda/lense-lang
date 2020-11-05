package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;

import java.util.Optional;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public class ByteCodeReader extends ClassVisitor {

	private final UpdatableTypeRepository typeContainer;
	private LoadedClassBuilder loadedClassBuilder;
	
	public ByteCodeReader(UpdatableTypeRepository typeContainer) {
		super(ASM5);
		this.typeContainer = typeContainer;
	}

	public LoadedClassBuilder getBuilder() {
		return loadedClassBuilder;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Llense/core/lang/java/SingletonObject;")) {
			loadedClassBuilder.setKind(lense.compiler.type.LenseUnitKind.Object);
		} else if (desc.equals("Llense/core/lang/java/Signature;")) {
			return new ClassSignatureAnnotationVisitor();
		} else if (desc.equals("Llense/core/lang/java/Native;")) {
			return new NativeAnnotationVisitor();
		} else if (desc.equals("Llense/core/lang/java/PlataformSpecific;")) {
			loadedClassBuilder.setPlataformSpecific(true);
		} else if (desc.equals("Llense/core/lang/java/PlataformSpecific;")) {
			loadedClassBuilder.setPlataformSpecific(true);
		} else if (desc.equals("Llense/core/lang/java/ValueClass;")) {
			loadedClassBuilder.setKind(LenseUnitKind.ValueClass);
		} else if (desc.equals("Llense/core/lang/java/EnhancementClass;")) {
			loadedClassBuilder.setKind(LenseUnitKind.Enhancement);
		}
		
		
		return null;

	}

	LenseTypeDefinition resolveTypeByNameAndKind(String name, lense.compiler.type.TypeKind kind, int genericsCount) {

		TypeSearchParameters params = new TypeSearchParameters(name, genericsCount);

		Optional<LenseTypeDefinition> existingType = this.typeContainer.resolveType(params)
				.map(t -> (LenseTypeDefinition) t);
		if (!existingType.isPresent()) {
			TypeVariable[] generics = new TypeVariable[genericsCount];
			for (int i = 0; i < generics.length; i++) {
				generics[i] = new RangeTypeVariable(Optional.empty(), Variance.Invariant, LenseTypeSystem.Any(),
						LenseTypeSystem.Nothing());
			}
			LoadedLenseTypeDefinition type = new LoadedLenseTypeDefinition(name, kind, null, generics);

			this.typeContainer.registerType(type, genericsCount);

			return type;

		} else {
			return existingType.get();
		}
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		
		loadedClassBuilder = new LoadedClassBuilder(typeContainer);
		
		loadedClassBuilder.setName(name);
		loadedClassBuilder.setSuperName(superName);
		loadedClassBuilder.setInterfaces(interfaces);
		
		lense.compiler.type.TypeKind kind = lense.compiler.type.LenseUnitKind.Class;
		if ((access & ACC_ANNOTATION) != 0) {
			kind = lense.compiler.type.LenseUnitKind.Annotation;
		} else if ((access & ACC_INTERFACE) != 0) {
			kind = lense.compiler.type.LenseUnitKind.Interface;
		} else if ((access & ACC_ENUM) != 0) {
			kind = lense.compiler.type.LenseUnitKind.Enum;
		} 

		loadedClassBuilder.setKind((LenseUnitKind)kind);
		
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		// if ((access & ACC_STATIC) !=0){
		// System.out.println(" STATIC " + desc + " " + name);
		// } else {
		// System.out.println(" " + desc + " " + name);
		// }

		return null;
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (name.startsWith("<")) {
			return null;
		}
		try {

			MethodAsmInfo info = new MethodAsmInfo(name, desc, signature, exceptions, readVisibility(access), readAbstract(access), readDefault(access));


//			if ((access & ACC_STATIC) != 0) {
//				// constructors
//				ConstructorBuilder builder = new ConstructorBuilder(loadedClassBuilder,info);
//				return new ConstructorAnnotVisitor(builder);
//			} else {
				// instance methods
				
				MethodBuilder builder = new MethodBuilder(loadedClassBuilder,info);
				
				builder.isStatic = (access & ACC_STATIC) != 0;
				
				return new MethodAnnotVisitor(builder);
//			}
		} catch (IllegalArgumentException e) {
			return null;
		}

	}

	private boolean readDefault(int access) {
		return (access & Opcodes.ACC_FINAL) == 0; // if final is not default
	}

	private boolean readAbstract(int access) {
		return (access & Opcodes.ACC_ABSTRACT) != 0;
	}

	private Visibility readVisibility(int access) {
		if ((access & Opcodes.ACC_PUBLIC) == 1) {
			return Visibility.Public;
		} else if ((access & Opcodes.ACC_PRIVATE) == 1) {
			return Visibility.Private;
		} else if ((access & Opcodes.ACC_PROTECTED) == 1) {
			return Visibility.Protected;
		}

		return Visibility.Undefined;
	}


	private class NativeAnnotationVisitor extends AnnotationVisitor {

		public NativeAnnotationVisitor() {
			super(ASM5);
		}

		public void visit(String name, Object value) {

			if ("overridable".equals(name)) {
				loadedClassBuilder.setNative(!((Boolean) value).booleanValue());
			}
		}
	}

	private class ClassSignatureAnnotationVisitor extends AnnotationVisitor {

		public ClassSignatureAnnotationVisitor() {
			super(ASM5);
		}

		public void visit(String name, Object value) {

			if ("value".equals(name)) {
				loadedClassBuilder.setSignature(value.toString());
			} else if ("caseValues".equals(name)) {
				loadedClassBuilder.setCaseValues(value.toString());
			} else if ("caseTypes".equals(name)) {
				loadedClassBuilder.setCaseTypes(value.toString());
			}
		}

	}
}
