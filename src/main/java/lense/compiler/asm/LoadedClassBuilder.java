package lense.compiler.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;
import lense.compiler.utils.Strings;

public class LoadedClassBuilder {

	private LenseUnitKind kind;
	private boolean plataformSpecific;
	private String name;
	private String superName;
	private String[] interfaces;
	private boolean isNative;
	private String signature;
	private String caseValues;
	private String caseTypes;
	private final UpdatableTypeRepository typeContainer;

	Map<String, TypeMember> properties = new HashMap<String, TypeMember>();


	private List<ConstructorBuilder> constructors = new ArrayList<>();
	private List<MethodBuilder> methods = new ArrayList<>();
	private Visibility visibility = Visibility.Undefined;

	public LoadedClassBuilder ( UpdatableTypeRepository typeContainer) {
		this.typeContainer = typeContainer;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public void addConstructor(ConstructorBuilder builder) {
		constructors.add(builder);
	}

	public void addMethod(MethodBuilder builder) {
		methods.add(builder);
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKind(LenseUnitKind kind) {
		this.kind = kind;
	}
	
	public LenseUnitKind getKind() {
		return kind;
	}

	public void setPlataformSpecific(boolean plataformSpecific) {
		this.plataformSpecific = plataformSpecific;
	}

	public void setSuperName(String superName) {
		this.superName = superName;
	}

	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}


	public LenseTypeDefinition build() {

		String[] classNames = Strings.split(name,"/");

		if (kind.isObject()) {
			classNames[classNames.length - 1] = Strings.pascalToCammelCase(classNames[classNames.length - 1]);
		}
		
		String[] parts = this.plataformSpecific || this.signature == null ? new String[] {"","",""} : this.signature.split(":");

		if (parts.length == 0) {
			parts = new String[] { "", "", "" };
		} else if (parts.length  == 1) {
			parts = new String[] { parts[0], "", "" };
		} else if (parts.length  == 2) {
			parts = new String[] { parts[0], parts[1], "" };
		} 
		
		Map<String, Integer> maps = new HashMap<>();

		List<TypeVariable> genericVariables;

		int genericCount = 0;
		int pos = parts[0].indexOf('[');
		if (pos >= 0) {
			String[] variables = parts[0].substring(pos + 1, parts[0].lastIndexOf(']')).split(",");
			String[] names = new String[variables.length];
			
			genericCount = variables.length;
			
			genericVariables = new ArrayList<>(variables.length);

			for (int i = 0; i < variables.length; i++) {
				String s = variables[i];

				pos = s.indexOf('<');
				String symbol = s.substring(1, pos);
				String upperBound = s.substring(pos + 1);

				Variance variance = lense.compiler.typesystem.Variance.Invariant;
				if (symbol.charAt(0) == '+') {
					variance = lense.compiler.typesystem.Variance.Covariant;
					symbol = symbol.substring(1);
				} else if (symbol.charAt(0) == '-') {
					variance = lense.compiler.typesystem.Variance.ContraVariant;
					symbol = symbol.substring(1);
				} else if (symbol.charAt(0) == '=') {
					symbol = symbol.substring(1);
				}

				names[i] = symbol;
				maps.put(symbol, i);

				if (upperBound.equals("lense.core.lang.Any")) {
					genericVariables.add(new RangeTypeVariable(symbol, variance, LenseTypeSystem.Any(),
							LenseTypeSystem.Nothing()));
				} else {
					genericVariables.add(new RangeTypeVariable(symbol, variance,
							resolveTypeByNameAndKind(upperBound, null), LenseTypeSystem.Nothing()));
				}

			}

//			if (!genericVariables.isEmpty() && !def.getGenericParameters().isEmpty()) {
//				def = (LoadedLenseTypeDefinition) def.duplicate();
//			}
	

		} else {
			genericVariables = new ArrayList<>(0);
		}
		
		var fullName = Strings.join(classNames, ".");
		if (fullName.equals("boolean")) {
			return (LenseTypeDefinition) LenseTypeSystem.Boolean();
		}

		LenseTypeDefinition rdef;
		Map<Integer, TypeDefinition> map = this.typeContainer.resolveTypesMap(fullName);
		if (map.isEmpty()) {
			LoadedLenseTypeDefinition ldef = new LoadedLenseTypeDefinition(fullName, kind, null);
			ldef.setGenericParameters(genericVariables);
		
			rdef = ldef;
			
			this.typeContainer.registerType(rdef, genericCount);

		} else if (map.size() == 1) {
			var found =  map.values().iterator().next();
			
			if(found instanceof LenseTypeDefinition) {
				rdef =  (LenseTypeDefinition)found;
			} else if(found instanceof ProxyTypeDefinition) {
				
				LoadedLenseTypeDefinition ldef = new LoadedLenseTypeDefinition(fullName, kind, null);
				ldef.setGenericParameters(genericVariables);
			
				rdef = ldef;
				
				this.typeContainer.registerType(rdef, genericCount);
				
				((ProxyTypeDefinition) found).setOriginal(ldef);
			} else {
				throw new IllegalStateException("Connot process found type");
			}
			
		} else {
			throw new IllegalStateException("More than one type found");
		}
		

		if (!(rdef instanceof LoadedLenseTypeDefinition)) {
			return rdef;
		} else if (genericVariables.size() > 0){
			((LoadedLenseTypeDefinition)rdef).forceSetGenericParameters(genericVariables);
		}
		LoadedLenseTypeDefinition def = (LoadedLenseTypeDefinition)rdef;

		def.setVisibility(visibility);
		def.setKind(kind);
		def.setPlataformSpecific(plataformSpecific || fullName.startsWith("lense.core.lang.java"));
		def.setNative(isNative);



		def.setCaseValues(Stream.of(Strings.split(this.caseValues, ",")).map( c -> this.resolveTypeByNameAndKind(c, LenseUnitKind.Object)).collect(Collectors.toList()));
		def.setCaseTypes(Stream.of(Strings.split(this.caseTypes, ",")).map( c -> this.resolveTypeByNameAndKind(c, LenseUnitKind.Class)).collect(Collectors.toList()));

		def.setAlgebric(!def.getAllCases().isEmpty());

		// parse signature for types
		if (this.signature == null) {
			if (superName.equals("java/lang/Object") || superName.equals("lense/core/lang/java/Base") || superName.equals("java/lang/RuntimeException")) {
				superName = "lense.core.lang.Any";
			}

			this.signature  = ":" + superName.replace('/', '.') + ":";

			if (interfaces.length > 0) {
				for (int i =0; i< interfaces.length; i++) {
					if (i != 0) {
						this.signature += "&";
					}
					this.signature += interfaces[i].replace('/', '.');
				}
			}
		}

	

	

		String s = parts[1];
		if (s.length() > 0 && !def.getName().equals("lense.core.lang.Any")) {
			TypeDefinition sdef = parseSuperTypeSignature(s, maps, def);
			if (sdef != null && !def.getName().equals(sdef.getName()) ) {
				def.setSuperTypeDefinition(sdef);
			} else {
				sdef = this.resolveTypeByNameWithVariables("lense.core.lang.Any", new ArrayList<>(0));
				def.setSuperTypeDefinition(sdef);
			}
		} else if (!def.getName().equals("lense.core.lang.Any")){
			TypeDefinition sdef = this.resolveTypeByNameWithVariables("lense.core.lang.Any", new ArrayList<>(0));
			def.setSuperTypeDefinition(sdef);
		}

		String[] g = parts[2].contains("&") ? parts[2].split("&") : new String[] { parts[2] };
		for (String ss : g) {
			Optional<TypeDefinition> type = parseInterfaceSignature(ss, maps, def);

			if (type.isPresent()) {
				if (type.get().getName().equals("lense.core.lang.Immutable")) {
					def.setExplicitlyImmutable(true);
				}
				if (type.get().getName().equals("lense.core.lang.AnyValue")) {
					def.setExplicitlyImmutable(true);
					def.setKind(LenseUnitKind.ValueClass);
				}
				def.addInterface(type.get());
				
			}

		}
		
		for(MethodBuilder b : this.methods) {
			if(b.isConstructor) {
				b.asConstructorBuilder().buildAndAdd(def);
			} else {
				b.buildAndAdd(def); // tODO method must correlate to generics 
			}
		}

//		for(ConstructorBuilder b : this.constructors) {
//			b.buildAndAdd(def);
//		}

		

		def.setLoaded(true);
		return def;
	}

	TypeDefinition resolveTypeByNameWithVariables(String name, List<TypeVariable> typeVar) {

		TypeDefinition type = resolveTypeByNameAndKind(name, null);

		if (!typeVar.isEmpty() && type.getGenericParameters().size() < typeVar.size()) {

			LoadedLenseTypeDefinition n = new LoadedLenseTypeDefinition(((LenseTypeDefinition)type));

			n.setGenericParameters(typeVar);

			return n;
		}

		return type;
	}

	TypeDefinition resolveTypeByNameAndKind(String name, lense.compiler.type.TypeKind kind) {
		
	
		if (name.equals("boolean")) {
			return (LenseTypeDefinition) LenseTypeSystem.Boolean();
		}

		Map<Integer, TypeDefinition> map = this.typeContainer.resolveTypesMap(name);
		if (map.isEmpty()) {
			LoadedLenseTypeDefinition type = new LoadedLenseTypeDefinition(name, kind, null);

			if (name.startsWith("lense.core.lang.java")) {
				type.setPlataformSpecific(true);
			}

//			this.typeContainer.registerType(type, genericCount);

			return type;

		} else if (map.size() == 1) {
			return  map.values().iterator().next();

		} else {
			throw new IllegalStateException("More than one type found");
		}
	}


	public Optional<TypeDefinition> parseInterfaceSignature(String ss, Map<String, Integer> maps,
			LenseTypeDefinition parent) {

		if (ss.trim().isEmpty()) {
			return Optional.empty();
		}

		int pos = ss.indexOf('<');
		String interfaceType = ss;
		String genericPart = "";
		if (pos >= 0) {
			interfaceType = ss.substring(0, pos);
			genericPart  = ss.substring(pos + 1, ss.lastIndexOf('>'));
		}

		var type = resolveTypeByNameAndKind(interfaceType, LenseUnitKind.Interface);

		if (genericPart.contains("<")) {

			return parseInterfaceSignature(genericPart, maps, parent).map(generic ->  {
				
				
				return LenseTypeSystem.getInstance().specify(type, generic); 
				
			});

		} else if (genericPart.length() > 0){
			String[] g = genericPart.contains(",") ? genericPart.split(",") : new String[] { genericPart };
			int i = 0;

			if (type instanceof LoadedLenseTypeDefinition) {
				LoadedLenseTypeDefinition loadedDef = (LoadedLenseTypeDefinition) type;


				List<TypeVariable> variables = new ArrayList<>(g.length);

				for (String symbol : g) {
					Integer index = maps.get(symbol);

					lense.compiler.typesystem.Variance variance = lense.compiler.typesystem.Variance.Invariant;

					if (symbol.charAt(0) == '-') {
						variance = Variance.ContraVariant;
						symbol = symbol.substring(1);
					} else if (symbol.charAt(0) == '+') {
						variance = Variance.Covariant;
						symbol = symbol.substring(1);
					}

					if (index != null) {
						// generic type
						variables.add(new DeclaringTypeBoundedTypeVariable(parent, index, symbol,variance));

					} else {
						// hard bound
						Optional<String> indexSymbol = parent.getGenericParameterSymbolByIndex(i);
						if (!indexSymbol.isPresent()) {
							
							if (name.replace('/', '.').equals(parent.getName())) {
								variables.add(new RangeTypeVariable(symbol,variance, parent,parent));
							} else {
								var t = resolveTypeByNameAndKind(symbol, null);

								variables.add(new RangeTypeVariable(symbol,variance, t, t));
							}
							
						} else {
							variables.add(new DeclaringTypeBoundedTypeVariable(parent, i, indexSymbol.get(),variance));
						}

					}
					i++;
				}



				if (loadedDef.getGenericParameters().isEmpty()) {
					loadedDef.setGenericParameters(variables);
				} else {
					// specify
					return Optional.of(LenseTypeSystem.getInstance().specify(loadedDef, variables));
				}

			}

		}
		
		this.typeContainer.registerType(type, type.getGenericParameters().size());
		return Optional.of(type);

	}

	public TypeDefinition parseSuperTypeSignature(String ss, Map<String, Integer> maps, TypeDefinition parent) {
		int pos = ss.indexOf('<');
		String superTypeName = ss;
		String n = "";
		if (pos >= 0) {
			superTypeName = ss.substring(0, pos);
			n  = ss.substring(pos + 1, ss.lastIndexOf('>'));
		}

		var type = resolveTypeByNameAndKind(superTypeName, LenseUnitKind.Interface);

		if (n.contains("<")) {

			parseSuperTypeSignature(n, maps, type);

		} else if (n.length() > 0){
			String[] g = n.contains(",") ? n.split(",") : new String[] { n };

			if (type instanceof LoadedLenseTypeDefinition) {
				LoadedLenseTypeDefinition loadedDef = (LoadedLenseTypeDefinition) type;
				List<TypeVariable> variables = new ArrayList<>(g.length);

				for (String symbol : g) {
					Integer index = maps.get(symbol);

					lense.compiler.typesystem.Variance variance = lense.compiler.typesystem.Variance.Invariant;

					if (symbol.charAt(0) == '-') {
						variance = Variance.ContraVariant;
						symbol = symbol.substring(1);
					} else if (symbol.charAt(0) == '+') {
						variance = Variance.Covariant;
						symbol = symbol.substring(1);
					} else if (symbol.charAt(0) == '=') {
						symbol = symbol.substring(1);
					}

					if (index != null) {
						// generic type
						variables.add(new DeclaringTypeBoundedTypeVariable(parent, index, symbol,variance));
					} else {
						// hard bound

						var t = resolveTypeByNameAndKind(symbol, null);

						variables.add(
								new RangeTypeVariable(symbol, variance, t, t));
					}
				}

				if (!loadedDef.getGenericParameters().isEmpty()) {
					type = loadedDef.specify(variables);
				} else {
					loadedDef.setGenericParameters(variables);
				}


			}

		}
		return type;

	}

	public String getCaseTypes() {
		return caseTypes;
	}

	public void setCaseTypes(String caseTypes) {
		this.caseTypes = caseTypes;
	}

	public String getCaseValues() {
		return caseValues;
	}

	public void setCaseValues(String caseValues) {
		this.caseValues = caseValues;
	}

	
	
	public TypeDefinitionInfo info() {
		
		String[] classNames = Strings.split(name,"/");

		if (kind.isObject()) {
			classNames[classNames.length - 1] = Strings.pascalToCammelCase(classNames[classNames.length - 1]);
		}
		
		var info = new TypeDefinitionInfo(Strings.join(classNames, "."), kind);
	
		String[] parts = this.plataformSpecific || this.signature == null ? new String[] {"","",""} : this.signature.split(":");

		if (parts.length == 0) {
			parts = new String[] { "", "", "" };
		} else if (parts.length  == 1) {
			parts = new String[] { parts[0], "", "" };
		} else if (parts.length  == 2) {
			parts = new String[] { parts[0], parts[1], "" };
		} 
		
		info.genericCount = 0;
		
		int pos = parts[0].indexOf('[');
		if (pos >= 0) {
			String[] variables = parts[0].substring(pos + 1, parts[0].lastIndexOf(']')).split(",");

			info.genericCount = variables.length;
			
		}
		
		if (superName.equals("java/lang/Object") || superName.equals("lense/core/lang/java/Base") || superName.equals("java/lang/RuntimeException")) {
			superName = "lense.core.lang.Any";
		} else {
			superName = Strings.join(Strings.split(name,"/"), ".");
		}
		
		info.superName = superName;
		convertJavaType(superName).ifPresent(it -> info.addImport(new TypeDefinitionInfo(it, LenseUnitKind.Class)));
		
//		for(var name : Strings.split(this.caseValues, ",")) {
//			convertJavaType(name).ifPresent(it -> info.addImport(new TypeDefinitionInfo(it, LenseUnitKind.Object)));
//		}
//		
//		for(var name : Strings.split(this.caseTypes, ",")) {
//			convertJavaType(name).ifPresent(it -> info.addImport(new TypeDefinitionInfo(it, LenseUnitKind.Class)));
//		}
	
		String[] g = parts[2].contains("&") ? parts[2].split("&") : new String[] { parts[2] };
		boolean isFirst =  true;
		for (String ss : g) {
			while(ss.contains("<")) {
				int start = ss.indexOf("<");
				int end = ss.lastIndexOf(">");
				if(start>=0) {
					final boolean s = isFirst;
					convertJavaType(ss.substring(0,start)).ifPresent(it ->  info.addImport(new TypeDefinitionInfo(it, s ? LenseUnitKind.Interface : null)));
					ss = ss.substring(start+1, end);
					isFirst =false;
				}
			}
			final boolean s = isFirst;
			convertJavaType(ss).ifPresent(it ->  info.addImport(new TypeDefinitionInfo(it, s ? LenseUnitKind.Interface : null)));
		}
		
		for( var method : this.methods) {
			method.addInfo(info);
		}
		
		
		for( var c : this.constructors) {
			c.addInfo(info);
		}

		return info;
	}



	static Optional<String> convertJavaType(String type) {
		if(type.startsWith("java")) {
			if(type.equals("java.lang.Integer")) {
				return Optional.of(LenseTypeSystem.Int32().getName());
			} else if(type.equals("java.lang.Long")) {
				return Optional.of(LenseTypeSystem.Int64().getName());
			} else if(type.equals("java.lang.Boolean")) {
				return Optional.of(LenseTypeSystem.Boolean().getName());
			} else {
				return Optional.empty();
			} 	
		} else if (type.indexOf('.')  < 0) {
			return Optional.empty();
		}  else if (type.indexOf('$')  >= 0) {
			return Optional.empty();
		}else if (type.indexOf(',')  >= 0) {
			return Optional.empty();
		}
		
		return Optional.of(type);
	}




}
