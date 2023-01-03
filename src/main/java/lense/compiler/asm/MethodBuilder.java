package lense.compiler.asm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lense.compiler.crosscompile.PrimitiveTypeDefinition;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.utils.Strings;

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
	public boolean overloaded;
	public String declaringType;
	public String boundedTypes;
	public boolean isOverride;
	public boolean isConstructor;
	public boolean isStatic;
	public boolean isImplicit;
	public boolean isSatisfy;

	public MethodBuilder(LoadedClassBuilder loadedClassBuilder, MethodAsmInfo info) {
		this.loadedClassBuilder  = loadedClassBuilder;
		this.info = info;

		loadedClassBuilder.addMethod(this);
	}

	
	public ConstructorBuilder asConstructorBuilder() {
		var constructor = new ConstructorBuilder(loadedClassBuilder, info);
		
		constructor.isImplicit = isImplicit;
		constructor.paramsSignature = paramsSignature;
		
		return constructor;
	}
	
	
	private TypeVariable resolveRelativeType(LoadedLenseTypeDefinition def, String name) {
		
			
			int pos = name.indexOf('<');
			if (pos < 0) {
				if ("V".equals(name)) {
					return LenseTypeSystem.Void();
				} else if (name.startsWith("Z")) {
					return LenseTypeSystem.Boolean();
				} else if ("I".equals(name)) {
					return PrimitiveTypeDefinition.INT;
				} else 	if ("C".equals(name)) {
					return PrimitiveTypeDefinition.CHAR;
				} else  {
					String qualifiedName;
					if (name.startsWith("L")) {
					   qualifiedName = name.substring(1, name.length() - 1).replace('/', '.');
					   
					   if (declaringType != null && qualifiedName.equals(def.getName())) {
							return def;
					   }
					   
					   return loadedClassBuilder.resolveTypeByNameAndKind(qualifiedName, null);
					} else if( def.getKind().isEnhancement()) {
						return loadedClassBuilder.resolveTypeByNameAndKind(name, null);
					} else {
						Optional<Integer> index = def.getGenericParameterIndexBySymbol(name);

						if(index.isPresent()) {
							return new DeclaringTypeBoundedTypeVariable(def, index.get(),name,Variance.Covariant);
						} else {
							return loadedClassBuilder.resolveTypeByNameAndKind(name, null);
						}
					}
				}
			} else {

				String otherTypeName = name.substring(0,  pos);
				String[] parameterNames = Strings.split(name.substring(pos + 1 , name.indexOf('>', pos)) , ",");
				
				TypeVariable[] params = new TypeVariable[parameterNames.length];
				
				int i = 0;
				TypeDefinition type = null;
				for (String parameterName : parameterNames) {
					var index = def.getGenericParameterIndexBySymbol(parameterName);

					if(index.isPresent()) {
						params[i] = new DeclaringTypeBoundedTypeVariable(def, index.get(),parameterName,  Variance.Covariant);
					} else {
						params[i] = typeForName(def,parameterName);
					}
					i++;
				}
				
				type = loadedClassBuilder.resolveTypeByNameWithVariables(otherTypeName, Arrays.asList(params));
				
				return LenseTypeSystem.getInstance().specify(type, params);
			}
		
	}
	
	public void buildAndAdd(LoadedLenseTypeDefinition def) {

		if (isPlataformSpecific) {
			return;
		}

		String desc = info.getDesc();
		int pos = desc.lastIndexOf(')');

		TypeVariable rt;

		if (returnSignature == null || returnSignature.isEmpty()) {
			returnSignature = desc.substring(pos + 1);
		} 

		
		rt = resolveRelativeType(def, returnSignature);


		if (rt instanceof PrimitiveTypeDefinition) {
			return;
		}

		MethodReturn r = new MethodReturn(rt);

		List<MethodParameter> params = new LinkedList<>();
		String[] signatureParams = Strings.split(paramsSignature, ",");
								
		Set<String> boundedTypesParams = new HashSet<>(Arrays.asList(Strings.split(boundedTypes, ",")));
					
		int a = 1;
		while (a < pos) {
			int s = a;
			char type = desc.charAt(s);
			if (type == 'L') {
				a = desc.indexOf(';', s) + 1;
				
				String name = desc.substring(s, a);
				
				String sname = signatureParams.length > params.size() ?  signatureParams[params.size()]  : name;

				if (boundedTypesParams.contains(name)) {
					MethodParameter mp = new MethodParameter(RangeTypeVariable.allRange(sname, Variance.Invariant));
					mp.setMethodTypeBound(true);
					params.add(mp);
				} else {
					MethodParameter mp = new MethodParameter(resolveRelativeType(def,sname));
					params.add(mp);
				}
				
				
			} else if (type == 'Z') {
				params.add(new MethodParameter(LenseTypeSystem.Boolean()));
				a = a + 1;
			} else {
				return;
			}
		}

		if (loadedClassBuilder.getKind().isEnhancement()) {
			params.remove(0);
		}
		
		Method m = new Method(isProperty, info.getVisibility(), info.getName(), r, params);
	
		m.setAbstract(info.isAbstract());
		m.setDefault(info.isDefault());
		m.setOverride(isOverride);
		m.setSatisfy(isSatisfy);
		
		if (overloaded) {
			 m.setDeclaringType(typeForName(null, this.declaringType));
		} else {
			m.setDeclaringType(def);
		}

		//TypeVariable returnTypeDefinitio = m.getReturningType(); // TODO this definition is not propertly loaded with genric params

		if (isProperty){
//			if (returnSignature != null && returnSignature.length() > 0){
//				m.setReturn(new MethodReturn(parseReturnSignature((LoadedLenseTypeDefinition) m.getDeclaringType(), returnSignature,returnTypeDefinitio)));
//			}
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
		} else if ("I".equals(name)) {
			return PrimitiveTypeDefinition.INT;
		} else 	if ("C".equals(name)) {
			return PrimitiveTypeDefinition.CHAR;
		} else  {
			String qualifiedName;
			if (name.startsWith("L")) {
			   qualifiedName = name.substring(1, name.length() - 1).replace('/', '.');
			} else {
				qualifiedName = name;
			}
			
			if (declaringType != null && qualifiedName.equals(declaringType.getName())) {
				return declaringType;
			}
			return loadedClassBuilder.resolveTypeByNameAndKind(qualifiedName, null);
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
				
				member.setAbstract(method.isAbstract());
				member.setDefault(method.isDefault());
				member.setNative(method.isNative());
				member.setOverride(method.isOverride());
				member.setVisibility(method.getVisibility());
				
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
			
			member.setAbstract(method.isAbstract());
			member.setDefault(method.isDefault());
			member.setNative(method.isNative());
			member.setOverride(method.isOverride());
			member.setVisibility(method.getVisibility());
		
		}

	}

	
	public void addInfo(TypeDefinitionInfo info) {
		
		if (isPlataformSpecific) {
			return;
		}
		
		String desc = this.info.getDesc();
		int pos = desc.lastIndexOf(')');

		if (returnSignature == null || returnSignature.isEmpty()) {
			returnSignature = desc.substring(pos + 1);
		} 
		
		if(returnSignature.startsWith("L")) {
			LoadedClassBuilder.convertJavaType(Strings.join(Strings.split(returnSignature.substring(1,returnSignature.length()-1), "/"), "."))
			.ifPresent(n -> info.addImport(new TypeDefinitionInfo(n,null)));
		} else if(returnSignature.startsWith("Z")) {
			info.addImport(new TypeDefinitionInfo(LenseTypeSystem.Boolean().getName(), LenseTypeSystem.Boolean().getKind()) );
		} else if(returnSignature.startsWith("L")) {
			info.addImport(new TypeDefinitionInfo(LenseTypeSystem.Int64().getName(), LenseTypeSystem.Int64().getKind()) );
		} else if(returnSignature.startsWith("I")) {
			info.addImport(new TypeDefinitionInfo(LenseTypeSystem.Int32().getName(), LenseTypeSystem.Int32().getKind()) );
		}  else if(returnSignature.startsWith("V")) {
			info.addImport(new TypeDefinitionInfo(LenseTypeSystem.Void().getName(), LenseTypeSystem.Void().getKind()) );
		} else {
			
			Strings.parseGenerics(Strings.join(Strings.split(returnSignature.substring(0,returnSignature.length()), "/"), "."))
			.stream().map(s -> LoadedClassBuilder.convertJavaType(s)).forEach(op -> op.ifPresent(n -> info.addImport(new TypeDefinitionInfo(n,null))));

			
		}
	
		String[] signatureParams = Strings.split(paramsSignature, ",");
		List<MethodParameter> params = new LinkedList<>();
		Set<String> boundedTypesParams = new HashSet<>(Arrays.asList(Strings.split(boundedTypes, ",")));
		
		int a = 1;
		while (a < pos) {
			int s = a;
			char type = desc.charAt(s);
			if (type == 'L') {
				a = desc.indexOf(';', s) + 1;
				
				String name = desc.substring(s, a);
				
				String sname = signatureParams.length > params.size() ?  signatureParams[params.size()]  : name;

				if (boundedTypesParams.contains(name)) {
//					no-op
				} else if( sname.startsWith("L")){
					LoadedClassBuilder.convertJavaType(Strings.join(Strings.split(sname.substring(1,sname.length()-1), "/"), "."))
					.ifPresent(n -> info.addImport(new TypeDefinitionInfo(n,null)));
				} else {
					LoadedClassBuilder.convertJavaType(sname)
					.ifPresent(n -> info.addImport(new TypeDefinitionInfo(n,null)));
				}
				
				
			} else if (type == 'Z') {
				info.addImport(new TypeDefinitionInfo(LenseTypeSystem.Boolean().getName(), LenseTypeSystem.Boolean().getKind()) );
				a = a + 1;
			} else if (type == 'V') {
				info.addImport(new TypeDefinitionInfo(LenseTypeSystem.Void().getName(), LenseTypeSystem.Void().getKind()) );
				a = a + 1;
			} else {
				a = a + 1;
			}
		}
	}
	
	
}
