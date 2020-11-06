package lense.compiler.asm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Variance;

public class ConstructorBuilder {

	private MethodAsmInfo info;
	private LoadedClassBuilder loadedClassBuilder;
	boolean isImplicit = false;
	String paramsSignature = "";

	public ConstructorBuilder(LoadedClassBuilder loadedClassBuilder, MethodAsmInfo info) {
		this.loadedClassBuilder = loadedClassBuilder;
		this.info = info;

		loadedClassBuilder.addConstructor(this);
	}


	public TypeVariable parseTypeVariable(LenseTypeDefinition declaringType , String name) {
		
		// is type
		int pos = name.indexOf('<');

		if (pos >=0) {
			// is generic type
			TypeDefinition paramTypeDef = loadedClassBuilder.resolveTypeByNameAndKind(name.substring(0,pos), null);

			if (paramTypeDef.getGenericParameters().isEmpty()) {

				String generics = name.substring(pos+1, name.lastIndexOf('>'));
				String[] genericsSignatures;
				if (generics.contains(",")) {
					genericsSignatures = generics.split(",");
				} else {
					genericsSignatures = new String[] {generics};
				}

				List<TypeVariable> variables = new ArrayList<>(paramTypeDef.getGenericParameters().size());

				for (String g : genericsSignatures) {
					variables.add(parseTypeVariable(declaringType, g));
				}

				((LoadedLenseTypeDefinition)paramTypeDef).setGenericParameters(variables);
			}

			return paramTypeDef;
		} else {
			// is symbol 
			Optional<Integer> index = declaringType.getGenericParameterIndexBySymbol(name);

			if (index.isPresent()) {
				return new DeclaringTypeBoundedTypeVariable(declaringType, index.get(), name, Variance.Invariant);
			} else {
				return loadedClassBuilder.resolveTypeByNameAndKind(name, null);
			}
		}
	}


	private String[] parsedParamsSignatures() {
		if (paramsSignature.length() >0 ) {
			if (paramsSignature.contains(",")) {
				return paramsSignature.split(",");
			} else {
				return new String[] {paramsSignature};
			}
		}
		return new String[0];

	}
	public void buildAndAdd(LoadedLenseTypeDefinition def) {

		List<ConstructorParameter> params = new LinkedList<ConstructorParameter>();


		for (String s : parsedParamsSignatures()) {
			params.add(new ConstructorParameter(parseTypeVariable(def,s.trim())));
		}

		Constructor m = new Constructor(info.getName(), params, false, info.getVisibility()); 

		m.setDeclaringType(def);
		m.setAbstract(info.isAbstract());
		m.setImplicit(isImplicit);

		def.addConstructor(m);
	}



	public void addInfo(TypeDefinitionInfo info) {
		for (String s : parsedParamsSignatures()) {
			LoadedClassBuilder.convertJavaType(s.trim())
			.ifPresent(n -> info.addImport(new TypeDefinitionInfo(n,null)));
		}
	}
}
