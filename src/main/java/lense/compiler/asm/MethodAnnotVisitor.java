package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.core.lang.java.PlatformSpecific;

public class MethodAnnotVisitor extends MethodVisitor{

	private static String PlatformSpecific = "L" + PlatformSpecific.class.getName() .replaceAll("\\.", "/")+ ";";
    
	private MethodBuilder builder;
	
    public MethodAnnotVisitor(MethodBuilder builder) {
    	  super(ASM5);
    	  this.builder = builder;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals( PlatformSpecific)){	
        	builder.isPlataformSpecific = true;
        } else if (desc.equals("Llense/core/lang/java/Property;")){
        	builder.isProperty = true;
        } else if (desc.equals("Ljava/lang/Override;")){
        	builder.isOverride = true;
        } 
        
        return new MAnnotationVisitor();

    }

    private class MAnnotationVisitor extends AnnotationVisitor {


        public MAnnotationVisitor() {
            super(ASM5);
        }

        public void visit(String name, Object value) {
            if (name.equals("indexed")){
            	builder.isIndexed = ((Boolean)value).booleanValue();
            } else if (name.equals("name")){
            	builder.propertyName = ((String)value);
            } else if (name.equals("setter")){
            	builder.isSetter = ((Boolean)value).booleanValue();
            } else if (name.equals("returnSignature")){
            	builder.returnSignature = ((String)value);
            } else if (name.equals("paramsSignature")){
            	builder.paramsSignature = ((String)value);
            } else if (name.equals("overloaded")){
            	builder.overloaded = ((Boolean)value).booleanValue();
            } else if (name.equals("declaringType")){
            	builder.declaringType = ((String)value);
            } else if (name.equals("boundedTypes")){
            	builder.boundedTypes = ((String)value);
            }
            
            

        }

    }
}
