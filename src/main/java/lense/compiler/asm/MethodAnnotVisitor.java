package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import java.util.Optional;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.TypeVariable;
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
        if (desc.equals("Llense/core/lang/java/PlataformSpecific;")){
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
                    method.setReturn(new MethodReturn(parseReturnSignature(returnSignature)));
                }
                byteCodeReader.addPropertyPart(method,isIndexed,propertyName, isSetter);
            } else {
                byteCodeReader.addMethod(method);
            }
        }
    }

    private lense.compiler.type.variable.TypeVariable parseReturnSignature(String returnSignature){
        TypeDefinition typeDefinition = method.getReturningType().getTypeDefinition(); // TODO this definition is not propertly loaded with genric params

        LenseTypeDefinition declaringType = (LenseTypeDefinition) method.getDeclaringType();

        Optional<Integer> symbolIndex = declaringType.getGenericParameterIndexBySymbol(returnSignature);

        if (symbolIndex.isPresent()){
            return new DeclaringTypeBoundedTypeVariable(method.getDeclaringType(), symbolIndex.get(), returnSignature, lense.compiler.typesystem.Variance.Covariant);

        } else {
            int pos = returnSignature.indexOf('<');
            if (pos > 0) {
                String paramType = returnSignature.substring(0, pos);
                String generics = returnSignature.substring(pos + 1, returnSignature.indexOf('>', pos+ 1));
                String[] genericsParams;
                if (generics.indexOf(',') > 0){
                    genericsParams = generics.split(",");
                } else {
                    genericsParams = new String[]{generics};
                }

                LenseTypeDefinition type =  byteCodeReader.resolveTypByNameAndKind(paramType, null, genericsParams.length);

                TypeVariable[] variables = new TypeVariable[genericsParams.length];

                for (int i=0; i < genericsParams.length; i++){
                    symbolIndex = declaringType.getGenericParameterIndexBySymbol(genericsParams[i]);

                    if (symbolIndex.isPresent()){
                        variables[i] = new GenericTypeBoundToDeclaringTypeVariable(type, method.getDeclaringType(), symbolIndex.get(), genericsParams[i], Variance.Covariant );
                    } else {
                        variables[i] = parseReturnSignature(genericsParams[i]);
                    }
                }

                LenseTypeDefinition m = LenseTypeSystem.specify(type, variables);

                return new FixedTypeVariable(m);

            } else if (typeDefinition.getGenericParameters().isEmpty()){
                return new FixedTypeVariable(typeDefinition);
            } else {
                return new FixedTypeVariable(byteCodeReader.resolveTypByNameAndKind(returnSignature, null));


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
