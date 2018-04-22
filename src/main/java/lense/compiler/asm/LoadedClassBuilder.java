package lense.compiler.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

public class LoadedClassBuilder {

	private LenseUnitKind kind;
	private boolean plataformSpecific;
	private String name;
	private String superName;
	private String[] interfaces;
	private boolean isNative;
	private String signature;
	private final UpdatableTypeRepository typeContainer;
	
	Map<String, TypeMember> properties = new HashMap<String, TypeMember>();


	private List<ConstructorBuilder> constructors = new ArrayList<>();
	private List<MethodBuilder> methods = new ArrayList<>();
	
	public LoadedClassBuilder ( UpdatableTypeRepository typeContainer) {
		this.typeContainer = typeContainer;
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

		LenseTypeDefinition rdef = this.resolveTypByNameAndKind(name.replace('/', '.'), kind);

		if (!(rdef instanceof LoadedLenseTypeDefinition)) {
			return rdef;
		}
		LoadedLenseTypeDefinition def = (LoadedLenseTypeDefinition)rdef;
		
		def.setKind(kind);
		def.setPlataformSpecific(plataformSpecific);
		def.setNative(isNative);

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
		
		String[] parts = this.plataformSpecific ? new String[] {"","",""} : this.signature.split(":");

		if (parts.length == 0) {
			parts = new String[] { "", "", "" };
		} else if (parts.length  == 1) {
			parts = new String[] { parts[0], "", "" };
		} else if (parts.length  == 2) {
			parts = new String[] { parts[0], parts[1], "" };
		} 

		Map<String, Integer> maps = new HashMap<>();

		int pos = parts[0].indexOf('[');
		if (pos >= 0) {
			String[] variables = parts[0].substring(pos + 1, parts[0].lastIndexOf(']')).split(",");
			String[] names = new String[variables.length];

			List<TypeVariable> genericVariables = new ArrayList<>(variables.length);

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

				if (LenseTypeSystem.Any().getName().equals(upperBound)) {
					genericVariables.add(new RangeTypeVariable(symbol, variance, LenseTypeSystem.Any(),
							LenseTypeSystem.Nothing()));
				} else {
					genericVariables.add(new RangeTypeVariable(symbol, variance,
							resolveTypByNameAndKind(upperBound, null), LenseTypeSystem.Nothing()));
				}

			}

			if (!genericVariables.isEmpty() && !def.getGenericParameters().isEmpty()) {
				def = (LoadedLenseTypeDefinition) def.duplicate();
			}
			def.setGenericParameters(genericVariables);

		}

		String s = parts[1];
		if (s.length() > 0 && !def.getName().equals("lense.core.lang.Any")) {
			TypeDefinition sdef = parseSuperTypeSignature(s, maps, def);
			if (sdef != null) {
				def.setSuperTypeDefinition(sdef);
				
//			    if (this.genericParameters.size() < superType.getGenericParameters().size()) {
//		            this.genericParameters = new ArrayList<>(superType.getGenericParameters());
//		            this.genericParametersMapping = new HashMap<>(((LenseTypeDefinition)superType).genericParametersMapping);
//		        }
			}
		} else if (!def.getName().equals("lense.core.lang.Any")){
			TypeDefinition sdef = this.resolveTypeByNameWithVariables("lense.core.lang.Any", new ArrayList<>(0));
			def.setSuperTypeDefinition(sdef);
		}

		String[] g = parts[2].contains("&") ? parts[2].split("&") : new String[] { parts[2] };
		for (String ss : g) {
			Optional<LenseTypeDefinition> type = parseInterfaceSignature(ss, maps, def);

			if (type.isPresent()) {
				def.addInterface(type.get());
			}

		}


//		// super type
//		if (superName != null && !superName.startsWith("java/")) {
//			superName = superName.replace('/', '.');
//
//			superDef = resolveTypByNameAndKind(superName, LenseUnitKind.Class);
//			if (!superDef.isPlataformSpecific()) {
//				def.setSuperTypeDefinition(superDef);
//			} else {
//				def.setSuperTypeDefinition(any);
//			}
//
//		}
//
//		// interfaces
//		if (interfaces.length > 0) {
//			for (String f : interfaces) {
//				if (f != null && !f.startsWith("java/")) {
//					def.addInterface(resolveTypByNameAndKind(f.replace('/', '.'), LenseUnitKind.Interface));
//				}
//			}
//		}
//		
		for(ConstructorBuilder b : this.constructors) {
			b.buildAndAdd(def);
		}
		
		for(MethodBuilder b : this.methods) {
			b.buildAndAdd(def); // tODO method must correlate to generics 
		}
		
		return def;
	}

	LenseTypeDefinition resolveTypeByNameWithVariables(String name, List<TypeVariable> typeVar) {
		
		LenseTypeDefinition type = resolveTypByNameAndKind(name, null);
		
		if (type.getGenericParameters().size() < typeVar.size()) {
			
			if (!typeVar.isEmpty()) {

				LoadedLenseTypeDefinition n = new LoadedLenseTypeDefinition(((LoadedLenseTypeDefinition)type));
				
				n.setGenericParameters(typeVar);
				
				return n;
			}
		}
		
		return type;
	}
	
	LenseTypeDefinition resolveTypByNameAndKind(String name, lense.compiler.type.TypeKind kind) {
		if (name.equals("boolean")) {
			return (LenseTypeDefinition) LenseTypeSystem.Boolean();
		}


		Map<Integer, TypeDefinition> map = this.typeContainer.resolveTypesMap(name);
		if (map.isEmpty()) {
			LoadedLenseTypeDefinition type = new LoadedLenseTypeDefinition(name, kind, null);

			if (name.startsWith("lense.core.lang.java")) {
				type.setPlataformSpecific(true);
			}
			
			this.typeContainer.registerType(type, 0);

			return type;

		} else if (map.size() == 1) {
			return (LenseTypeDefinition) map.values().iterator().next();
			
		} else {
			throw new IllegalStateException("More than one type found");
		}
	}


	public Optional<LenseTypeDefinition> parseInterfaceSignature(String ss, Map<String, Integer> maps,
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
		
		LenseTypeDefinition type = resolveTypByNameAndKind(interfaceType, LenseUnitKind.Interface);

		if (genericPart.contains("<")) {

			return parseInterfaceSignature(genericPart, maps, parent).map(generic -> LenseTypeSystem.specify(type, generic));

		} else if (genericPart.length() > 0){
			String[] g = genericPart.contains(",") ? genericPart.split(",") : new String[] { genericPart };
			int i = 0;

			if (type instanceof LoadedLenseTypeDefinition) {
				LoadedLenseTypeDefinition loadedDef = (LoadedLenseTypeDefinition) type;

				if (loadedDef.getGenericParameters().isEmpty()) {
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
								LenseTypeDefinition t = resolveTypByNameAndKind(symbol, null);

								variables.add(new RangeTypeVariable(symbol,variance, t, t));
								// tv = new FixedTypeVariable();
								// variables.addGenericParameter("X" + Integer.toString(i), tv);
							} else {
								variables.add(new DeclaringTypeBoundedTypeVariable(parent, i, indexSymbol.get(),variance));
							}

						}
						i++;
					}

					loadedDef.setGenericParameters(variables);
				}

			}

		}
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
	
		LenseTypeDefinition type = resolveTypByNameAndKind(superTypeName, LenseUnitKind.Interface);
	
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

						LenseTypeDefinition t = resolveTypByNameAndKind(symbol, null);

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

	



}
