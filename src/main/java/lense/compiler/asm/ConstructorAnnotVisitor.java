package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Variance;

public class ConstructorAnnotVisitor extends MethodVisitor{

	private ByteCodeReader byteCodeReader;
	private MethodAsmInfo info;
	private LenseTypeDefinition def;
	private boolean isImplicit = false;
	private String paramsSignature = "";
	
	public ConstructorAnnotVisitor(ByteCodeReader byteCodeReader, LenseTypeDefinition def, MethodAsmInfo info) {
		super(ASM5);
		this.byteCodeReader = byteCodeReader;
		this.info = info;
		this.def = def;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (!desc.equals("Llense/core/lang/java/Constructor;")){
			info = null;
		}
		return new CAnnotationVisitor();

	}

	public TypeVariable parseTypeVariable(String name) {

		// is symbol 
		Optional<Integer> index = def.getGenericParameterIndexBySymbol(name);
		
		if (index.isPresent()) {
			 return new DeclaringTypeBoundedTypeVariable(def, index.get(), name, Variance.Invariant);
		} else {
			// is type
			TypeDefinition paramTypeDef;
		
			int pos = name.indexOf('<');
			
			if (pos >=0) {
				// is generic type
				paramTypeDef = byteCodeReader.resolveTypByNameAndKind(name.substring(0,pos), null);
				
				String generics = name.substring(pos+1, name.indexOf('>',pos));
				String[] genericsSignatures;
				if (generics.contains(",")) {
					genericsSignatures = generics.split(",");
				} else {
					genericsSignatures = new String[] {generics};
				}
				
				List<TypeVariable> variables = new ArrayList<>(paramTypeDef.getGenericParameters().size());

				for (String g : genericsSignatures) {
					variables.add(parseTypeVariable(g));
				}
				
				LoadedLenseTypeDefinition ld = new LoadedLenseTypeDefinition((LenseTypeDefinition)paramTypeDef);
				ld.setGenericParameters(variables);
				
				paramTypeDef = ld;
			} else {
				paramTypeDef = byteCodeReader.resolveTypByNameAndKind(name, null);
			}
			
			return new FixedTypeVariable(paramTypeDef);
		}
		
	}
	
	
	public void visitEnd() {
		
		if (info != null){
			
			List<ConstructorParameter> params = new LinkedList<ConstructorParameter>();

			
			if (paramsSignature.length() >0 ) {
				String[] signatures; 
				if (paramsSignature.contains(",")) {
					signatures = paramsSignature.split(",");
				} else {
					signatures = new String[] {paramsSignature};
				}
				
				for (String s : signatures) {
					params.add(new ConstructorParameter(parseTypeVariable(s.trim())));
				}
				
			}
			
			Constructor m = new Constructor(info.getName(), params, false, info.getVisibility()); 

			m.setAbstract(info.isAbstract());
			m.setImplicit(isImplicit);
			
			def.addConstructor(m);

		}
	}
	
	
	private class CAnnotationVisitor extends AnnotationVisitor {

		public CAnnotationVisitor() {
			super(ASM5);
		}
		
		public void visit(String name, Object value) {
		       if (name.equals("isImplicit")){
		    	   isImplicit = ((Boolean)value).booleanValue();
		       } else  if (name.equals("paramsSignature")){
		    	   paramsSignature = value.toString();
		       }
		}
		
	}
}
