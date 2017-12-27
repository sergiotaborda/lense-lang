package lense.compiler.asm;

import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public class ByteCodeReader extends ClassVisitor {

	private final UpdatableTypeRepository typeContainer;
	private LenseTypeDefinition def;

	public ByteCodeReader(UpdatableTypeRepository typeContainer) {
		super(ASM5);
		this.typeContainer = typeContainer;
	}

	public LenseTypeDefinition getType() {
		return def;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Llense/core/lang/java/SingletonObject;")) {
			def.setKind(lense.compiler.type.LenseUnitKind.Object);
		} else if (desc.equals("Llense/core/lang/java/Signature;")) {
			return new SignatureAnnotationVisitor();
		} else if (desc.equals("Llense/core/lang/java/Native;")) {
			return new NativeAnnotationVisitor();
		} else if (desc.equals("Llense/core/lang/java/PlataformSpecific;")) {
			def.setPlataformSpecific(true);
		}
		return null;

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

	LenseTypeDefinition resolveTypByNameAndKind(String name, lense.compiler.type.TypeKind kind, int genericsCount) {

		TypeSearchParameters params = new TypeSearchParameters(name, genericsCount);

		Optional<LenseTypeDefinition> existingType = this.typeContainer.resolveType(params)
				.map(t -> (LenseTypeDefinition) t);
		if (!existingType.isPresent()) {
			TypeVariable[] generics = new TypeVariable[genericsCount];
			for (int i = 0; i < generics.length; i++) {
				generics[i] = new RangeTypeVariable(Optional.empty(), Variance.Invariant, LenseTypeSystem.Any(),
						LenseTypeSystem.Nothing());
			}
			LoadedLenseTypeDefinition type = new LoadedLenseTypeDefinition(name, kind, null, generics);

			this.typeContainer.registerType(type, genericsCount);

			return type;

		} else {
			return existingType.get();
		}
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		lense.compiler.type.TypeKind kind = lense.compiler.type.LenseUnitKind.Class;
		if ((access & ACC_INTERFACE) != 0) {
			kind = lense.compiler.type.LenseUnitKind.Interface;
		} else if ((access & ACC_ENUM) != 0) {
			kind = lense.compiler.type.LenseUnitKind.Enum;
		} else if ((access & ACC_ANNOTATION) != 0) {
			kind = lense.compiler.type.LenseUnitKind.Annotation;
		}

		def = this.resolveTypByNameAndKind(name.replace('/', '.'), kind);

		def.setKind((LenseUnitKind)kind);

		LenseTypeDefinition any = resolveTypByNameAndKind("lense.core.lang.Any", LenseUnitKind.Class);
		LenseTypeDefinition superDef = any;
		if (superName != null && !superName.startsWith("java/")) {
			superName = superName.replace('/', '.');

			superDef = resolveTypByNameAndKind(superName, LenseUnitKind.Class);
			if (!superDef.isPlataformSpecific()) {
				def.setSuperTypeDefinition(superDef);
			} else {
				def.setSuperTypeDefinition(any);
			}

		}

		if (interfaces.length > 0) {
			for (String f : interfaces) {
				if (f != null && !f.startsWith("java/")) {
					def.addInterface(resolveTypByNameAndKind(f.replace('/', '.'), LenseUnitKind.Interface));
				}
			}
		}
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		// if ((access & ACC_STATIC) !=0){
		// System.out.println(" STATIC " + desc + " " + name);
		// } else {
		// System.out.println(" " + desc + " " + name);
		// }

		return null;
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (name.startsWith("<")) {
			return null;
		}
		try {

			MethodAsmInfo info = new MethodAsmInfo(name, desc, signature, exceptions, readVisibility(access),readAbstract(access));


			if ((access & ACC_STATIC) != 0) {
				// constructors

				return new ConstructorAnnotVisitor(this, def, info);
			} else {
				// instance methods
				int pos = desc.lastIndexOf(')');

				MethodReturn r = new MethodReturn(new FixedTypeVariable(typeForName(desc.substring(pos + 1))));

				List<MethodParameter> params = new LinkedList<>();
				int a = 1;
				while (a < pos) {
					int s = a;
					char type = desc.charAt(s);
					if (type == 'L') {
						a = desc.indexOf(';', s) + 1;
						params.add(new MethodParameter(typeForName(desc.substring(s, a))));
					} else if (type == 'Z') {
						params.add(new MethodParameter(LenseTypeSystem.Boolean()));
						a = a + 1;
					} else {
						return null;
					}
				}

				Method m = new Method(readVisibility(access), name, r, params);
				m.setDeclaringType(def);

				m.setAbstract(readAbstract(access));

				return new MethodAnnotVisitor(this, m);
			}
		} catch (IllegalArgumentException e) {
			return null;
		}

	}

	private boolean readAbstract(int access) {
		return (access & Opcodes.ACC_ABSTRACT) == 1;
	}

	private Visibility readVisibility(int access) {
		if ((access & Opcodes.ACC_PUBLIC) == 1) {
			return Visibility.Public;
		} else if ((access & Opcodes.ACC_PRIVATE) == 1) {
			return Visibility.Private;
		} else if ((access & Opcodes.ACC_PROTECTED) == 1) {
			return Visibility.Protected;
		}

		return Visibility.Undefined;
	}

	TypeDefinition typeForName(String name) {

		if ("V".equals(name)) {
			return LenseTypeSystem.Void();
		} else if (name.startsWith("Z")) {
			return LenseTypeSystem.Boolean();
		} else if (name.startsWith("L")) {

			String qualifiedName = name.substring(1, name.length() - 1).replace('/', '.');
			if (qualifiedName.equals(def.getName())) {
				return def;
			}
			return resolveTypByNameAndKind(qualifiedName, null);
		} else {
			throw new IllegalArgumentException(name + " is not a recognized type at " + def.getName());
		}

	}

	public void addMethod(Method method) {
		def.addMethod(method);
	}


	private Map<String, TypeMember> properties = new HashMap<String, TypeMember>();

	public void addPropertyPart(Method method, boolean isIndexed, String propertyName, boolean isSetter) {
		if (isIndexed) {
			String key = isSetter ? method.getParameters().subList(0, method.getParameters().size() - 1).toString()
					: method.getParameters().toString();
			IndexerProperty member = (IndexerProperty) properties.get(key);
			if (member == null) {

				int size = method.getParameters().size();
				if (isSetter) {
					size--;
				}
				TypeVariable[] params = method.getParameters().stream().map(p -> p.getType()).limit(size)
						.collect(Collectors.toList()).toArray(new TypeVariable[0]);
				
				TypeVariable indexType = isSetter ? method.getParameters().get(method.getParameters().size() - 1).getType() : method.getReturningType();
				
				member = new IndexerProperty(def, indexType, !isSetter, isSetter, params);
				properties.put(key, member);

				def.addIndexer(member);
			}
			if (!member.canRead() && !isSetter) {
				member.setReadable(true);
			}
			if (!member.canWrite() && isSetter) {
				member.setWritable(true);
			}

		} else {
			Property member = (Property) properties.get(propertyName);
			if (member == null) {
				member = new Property(method.getDeclaringType(), propertyName, method.getReturningType(), false, false);
				properties.put(propertyName, member);

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

	private class NativeAnnotationVisitor extends AnnotationVisitor {

		public NativeAnnotationVisitor() {
			super(ASM5);
		}

		public void visit(String name, Object value) {

			if ("overridable".equals(name)) {
				def.setNative(!((Boolean) value).booleanValue());
			}
		}
	}

	private class SignatureAnnotationVisitor extends AnnotationVisitor {

		public SignatureAnnotationVisitor() {
			super(ASM5);
		}

		public void visit(String name, Object value) {

			if ("value".equals(name)) {
				String[] parts = ((String) value).split(":");

				if (parts.length < 3) {
					parts = new String[] { parts[0], "", "" };
				}

				Map<String, Integer> maps = new HashMap<>();

				int pos = parts[0].indexOf('[');
				if (pos >= 0) {
					String[] variables = parts[0].substring(pos + 1, parts[0].lastIndexOf(']')).split(",");
					String[] names = new String[variables.length];

					if (def instanceof LoadedLenseTypeDefinition) {
						LoadedLenseTypeDefinition loadedDef = (LoadedLenseTypeDefinition) def;

						
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

						if (!loadedDef.getGenericParameters().isEmpty()) {
							loadedDef = (LoadedLenseTypeDefinition) loadedDef.duplicate();
						}
						loadedDef.setGenericParameters(genericVariables);

						def = loadedDef;

					}
				}

				String s = parts[1];
				if (s.length() > 0) {
					parseSuperTypeSignature(s, maps, def);
				}

				String[] g = parts[2].contains("&") ? parts[2].split("&") : new String[] { parts[2] };
				for (String ss : g) {
					Optional<LenseTypeDefinition> type = parseInterfaceSignature(ss, maps, def);

					if (type.isPresent()) {
						def.addInterface(type.get());
					}

				}

			}
		}

		public Optional<LenseTypeDefinition> parseInterfaceSignature(String ss, Map<String, Integer> maps,
				LenseTypeDefinition parent) {
			int pos = ss.indexOf('<');
			if (pos < 0) {
				return Optional.empty();
			}
			String interfaceType = ss.substring(0, pos);
			LenseTypeDefinition type = resolveTypByNameAndKind(interfaceType, LenseUnitKind.Interface);
			String n = ss.substring(pos + 1, ss.lastIndexOf('>'));
			if (n.contains("<")) {

				return parseInterfaceSignature(n, maps, type).map(generic -> LenseTypeSystem.specify(type, generic));

			} else {
				String[] g = n.contains(",") ? n.split(",") : new String[] { n };
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

		public void parseSuperTypeSignature(String ss, Map<String, Integer> maps, TypeDefinition parent) {
			int pos = ss.indexOf('<');
			if (pos < 0) {
				return;
			}
			String interfaceType = ss.substring(0, pos);
			LenseTypeDefinition type = resolveTypByNameAndKind(interfaceType, LenseUnitKind.Interface);
			String n = ss.substring(pos + 1, ss.lastIndexOf('>'));
			if (n.contains("<")) {

				parseSuperTypeSignature(n, maps, type);

			} else {
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

					loadedDef.setGenericParameters(variables);
				}

			}
			def.setSuperTypeDefinition(type);

		}
	}
}
