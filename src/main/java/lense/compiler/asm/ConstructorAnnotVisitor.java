package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

public class ConstructorAnnotVisitor extends MethodVisitor{

	private ConstructorBuilder builder;

	public ConstructorAnnotVisitor(ConstructorBuilder builder) {
		super(ASM5);
		this.builder = builder;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return new CAnnotationVisitor();
	}

	
	private class CAnnotationVisitor extends AnnotationVisitor {

		public CAnnotationVisitor() {
			super(ASM5);
		}
		
		public void visit(String name, Object value) {
		       if (name.equals("isImplicit")){
		    	   builder.isImplicit = ((Boolean)value).booleanValue();
		       } else  if (name.equals("paramsSignature")){
		    	   builder.paramsSignature = value.toString();
		       }
		}
		
	}
}
