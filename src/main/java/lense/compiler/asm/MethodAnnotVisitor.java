package lense.compiler.asm;

import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.Method;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.AnnotationVisitor;

public class MethodAnnotVisitor extends MethodVisitor{

	private ByteCodeReader byteCodeReader;
	private Method method;
	private boolean isProperty;
	private boolean isIndexed;
	private boolean isSetter;
	private String propertyName;
	private String returnSignature;
	private String paramsSignature;

	public MethodAnnotVisitor(ByteCodeReader byteCodeReader, Method method) {
		super(ASM5);
		this.byteCodeReader = byteCodeReader;
		this.method = method;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Llense/core/lang/java/Native;")){
			method = null;
		} else if (desc.equals("Llense/core/lang/java/Property;")){
			isProperty = true;
		} 
		return new MAnnotationVisitor();

	}

	public void visitEnd() {

		if (method!= null){
			if (isProperty){
				if (returnSignature != null && returnSignature.length() > 0){
					if (!method.getReturningType().getTypeDefinition().getName().equals(returnSignature)){
						method.setReturn(new MethodReturn(new DeclaringTypeBoundedTypeVariable(method.getDeclaringType(), 0, returnSignature, lense.compiler.typesystem.Variance.Covariant)));
					}
					
				}
				byteCodeReader.addPropertyPart(method,isIndexed,propertyName, isSetter);
			} else {
				byteCodeReader.addMethod(method);
			}
		}
	}

	private class MAnnotationVisitor extends AnnotationVisitor {


		public MAnnotationVisitor() {
			super(ASM5);
		}

		public void visit(String name, Object value) {
			if (name.equals("indexed")){
				isIndexed = ((Boolean)value).booleanValue();
			} else if (name.equals("name")){
				propertyName = ((String)value);
			} else if (name.equals("setter")){
				isSetter = ((Boolean)value).booleanValue();
			} else if (name.equals("returnSignature")){
				returnSignature = ((String)value);
			} else if (name.equals("paramsSignature")){
				paramsSignature = ((String)value);
			}

		}

	}
}
