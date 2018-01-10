package lense.compiler.asm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lense.compiler.crosscompile.PrimitiveTypeDefinition;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

public class MethodBuilder {

	LoadedClassBuilder loadedClassBuilder;
	MethodAsmInfo info;
	boolean isPlataformSpecific = false;
	boolean isProperty;
	boolean isIndexed;
	boolean isSetter;
	String propertyName;
	String returnSignature;
	String paramsSignature;

	public MethodBuilder(LoadedClassBuilder loadedClassBuilder, MethodAsmInfo info) {
		this.loadedClassBuilder  = loadedClassBuilder;
		this.info = info;

		loadedClassBuilder.addMethod(this);
	}

	public void buildAndAdd(LoadedLenseTypeDefinition def) {

		if (isPlataformSpecific) {
			return;
		}
		
		String desc = info.getDesc();
		int pos = desc.lastIndexOf(')');

		TypeDefinition rt = typeForName(def,desc.substring(pos + 1));
		
		if (rt instanceof PrimitiveTypeDefinition) {
			return;
		}
		
		MethodReturn r = new MethodReturn(new FixedTypeVariable(rt));

		List<MethodParameter> params = new LinkedList<>();
		int a = 1;
		while (a < pos) {
			int s = a;
			char type = desc.charAt(s);
			if (type == 'L') {
				a = desc.indexOf(';', s) + 1;
				params.add(new MethodParameter(typeForName(def,desc.substring(s, a))));
			} else if (type == 'Z') {
				params.add(new MethodParameter(LenseTypeSystem.Boolean()));
				a = a + 1;
			} else {
				return;
			}
		}

		Method m = new Method(info.getVisibility(), info.getName(), r, params);
		m.setDeclaringType(def);

		m.setAbstract(info.isAbstract());


		TypeDefinition returnTypeDefinitio = m.getReturningType().getTypeDefinition(); // TODO this definition is not propertly loaded with genric params
		;
		if (isProperty){
			if (returnSignature != null && returnSignature.length() > 0){
				m.setReturn(new MethodReturn(parseReturnSignature((LoadedLenseTypeDefinition) m.getDeclaringType(), returnSignature,returnTypeDefinitio)));
			}
			addPropertyPart(def, m,isIndexed,propertyName, isSetter);
		} else {
			def.addMethod(m);
		}


	}
	
	TypeDefinition typeForName(TypeDefinition declaringType, String name) {

		if ("V".equals(name)) {
			return LenseTypeSystem.Void();
		} else if (name.startsWith("Z")) {
			return LenseTypeSystem.Boolean();
		} else if (name.startsWith("L")) {

			String qualifiedName = name.substring(1, name.length() - 1).replace('/', '.');
			if (qualifiedName.equals(declaringType.getName())) {
				return declaringType;
			}
			return loadedClassBuilder.resolveTypByNameAndKind(qualifiedName, null);
		} else 	if ("I".equals(name)) {
			return new PrimitiveTypeDefinition("int");
		} else 	if ("C".equals(name)) {
			return new PrimitiveTypeDefinition("char");
		} else {
			throw new IllegalArgumentException(name + " is not a recognized type at " + declaringType.getName());
		}

	}

	private lense.compiler.type.variable.TypeVariable parseReturnSignature(LoadedLenseTypeDefinition declaringType, String returnSignature,TypeDefinition returnTypeDefinition ){
		
		Optional<Integer> symbolIndex = declaringType.getGenericParameterIndexBySymbol(returnSignature);

		if (symbolIndex.isPresent()){
			return new DeclaringTypeBoundedTypeVariable(declaringType, symbolIndex.get(), returnSignature, lense.compiler.typesystem.Variance.Covariant);

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

				 LenseTypeDefinition type = loadedClassBuilder.resolveTypByNameAndKind(paramType,null);
				 
				List<TypeVariable> variables = new ArrayList<>(genericsParams.length);

				for (int i=0; i < genericsParams.length; i++){
					symbolIndex = declaringType.getGenericParameterIndexBySymbol(genericsParams[i]);

					if (symbolIndex.isPresent()){
						variables.add(new GenericTypeBoundToDeclaringTypeVariable(type,declaringType, symbolIndex.get(), genericsParams[i], Variance.Covariant ));
					} else {
						variables.add(parseReturnSignature(declaringType, genericsParams[i],type));
					}
				}

				LenseTypeDefinition m =  loadedClassBuilder.resolveTypByNameWithVariables(paramType,variables);

				return new FixedTypeVariable(m);

			} else if (returnTypeDefinition.getGenericParameters().isEmpty()){
				return new FixedTypeVariable(returnTypeDefinition);
			} else {
				return new FixedTypeVariable(loadedClassBuilder.resolveTypByNameAndKind(returnSignature, null));


			}
		} 

	}

	void addPropertyPart(LoadedLenseTypeDefinition def, Method method, boolean isIndexed, String propertyName, boolean isSetter) {
		if (isIndexed) {
			String key = isSetter ? method.getParameters().subList(0, method.getParameters().size() - 1).toString()
					: method.getParameters().toString();
			IndexerProperty member = (IndexerProperty) loadedClassBuilder.properties.get(key);
			if (member == null) {

				int size = method.getParameters().size();
				if (isSetter) {
					size--;
				}
				TypeVariable[] params = method.getParameters().stream().map(p -> p.getType()).limit(size)
						.collect(Collectors.toList()).toArray(new TypeVariable[0]);

				TypeVariable indexType = isSetter ? method.getParameters().get(method.getParameters().size() - 1).getType() : method.getReturningType();

				member = new IndexerProperty(def, indexType, !isSetter, isSetter, params);
				loadedClassBuilder.properties.put(key, member);

				def.addIndexer(member);
			}
			if (!member.canRead() && !isSetter) {
				member.setReadable(true);
			}
			if (!member.canWrite() && isSetter) {
				member.setWritable(true);
			}

		} else {
			Property member = (Property) loadedClassBuilder.properties.get(propertyName);
			if (member == null) {
				member = new Property(method.getDeclaringType(), propertyName, method.getReturningType(), false, false);
				loadedClassBuilder.properties.put(propertyName, member);

				def.addProperty(member);
			}
			if (!member.canRead() && !isSetter) {
				member.setReadable(true);
			}
			if (!member.canWrite() && isSetter) {
				member.setWritable(true);
			}

		}

	}
}
