package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.Constructor;

public class ConstructorAnnotVisitor extends MethodVisitor{

	private ByteCodeReader byteCodeReader;
	private Constructor constructor;

	public ConstructorAnnotVisitor(ByteCodeReader byteCodeReader, Constructor constructor) {
		super(ASM5);
		this.byteCodeReader = byteCodeReader;
		this.constructor = constructor;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (!desc.equals("Llense/core/lang/java/Constructor;")){
			constructor = null;
		}
		return new CAnnotationVisitor();

	}

	public void visitEnd() {
		if (constructor!= null){
			byteCodeReader.addConstructor(constructor);
		}
	}
	
	
	private class CAnnotationVisitor extends AnnotationVisitor {

		public CAnnotationVisitor() {
			super(ASM5);
		}
		
		public void visit(String name, Object value) {
		       if (name.equals("isImplicit")){
		    	   constructor.setImplicit(((Boolean)value).booleanValue());
		       }
		}
		
	}
}
