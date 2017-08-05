package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

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
					TypeDefinition typeDefinition = method.getReturningType().getTypeDefinition(); // TODO this definition is not propertly loaded with genric params
					if (typeDefinition.getName().equals(returnSignature)){
						int pos = returnSignature.indexOf('<');
						if (pos > 0) {
							String paramType = returnSignature.substring(0, pos);
							String generics = returnSignature.substring(pos + 1, returnSignature.length() - pos);
							String[] genericsParams;
							if (generics.indexOf(',') > 0){
								genericsParams = generics.split(",");
							} else {
								genericsParams = new String[]{generics};
							}
						
							TypeDefinition type = new LenseTypeDefinition(paramType, null, null);
							// TODO recursivlty define genric parameters
							for (int index =0; index < method.getDeclaringType().getGenericParameters().size(); index++){
								if (method.getDeclaringType().getGenericParameters().get(index).equals(genericsParams[0])){
									method.setReturn(new MethodReturn(new GenericTypeBoundToDeclaringTypeVariable(type, method.getDeclaringType(), index, genericsParams[0], Variance.Covariant )));
									break;
								}
							}

						} else if (typeDefinition.getGenericParameters().isEmpty()){
							method.setReturn(new MethodReturn(new DeclaringTypeBoundedTypeVariable(method.getDeclaringType(), 0, returnSignature, lense.compiler.typesystem.Variance.Covariant)));
						} else {
							DeclaringTypeBoundedTypeVariable t = new DeclaringTypeBoundedTypeVariable(method.getDeclaringType(), 0, returnSignature, lense.compiler.typesystem.Variance.Covariant);
							LenseTypeDefinition m = LenseTypeSystem.specify(typeDefinition, t);
							method.setReturn(new MethodReturn(new FixedTypeVariable(m)));
						}
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
