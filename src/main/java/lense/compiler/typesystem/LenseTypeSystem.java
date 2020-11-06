/**
 * 
 */
package lense.compiler.typesystem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import compiler.CompilationError;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;


/**
 * 
 */
public class LenseTypeSystem {

	private static LenseTypeSystem me = new LenseTypeSystem();

	public static LenseTypeSystem getInstance() {
		return me;
	}

	public static TypeDefinition Type() {
		return getInstance().getForName("lense.core.lang.Type").get();
	}

	public static TypeDefinition Any() {
		return getInstance().getForName("lense.core.lang.Any").get();
	}
	public static TypeDefinition ReifiedArguments() {
		return getInstance().getForName("lense.core.lang.reflection.ReifiedArguments").get();
	}
	
	public static TypeDefinition Nothing() {
		return getInstance().getForName("lense.core.lang.Nothing").get();
	}

	public static TypeDefinition Binary() {
		return getInstance().getForName("lense.core.lang.Binary").get();
	}

	public static TypeDefinition None() {
		return getInstance().getForName("lense.core.lang.None").get();
	}

	public static TypeDefinition Some() {
		return getInstance().getForName("lense.core.lang.Some").get();
	}

	public static TypeDefinition Maybe() {
		return getInstance().getForName("lense.core.lang.Maybe", 1).get();
	}

	public static TypeDefinition Progression() {
		return getInstance().getForName("lense.core.collections.Progression", 1).get();
	}

	// Fundamental
	public static TypeDefinition Boolean() {
		return getInstance().getForName("lense.core.lang.Boolean").get();
	}

	// Fundamental
	public static TypeDefinition Comparison() {
		return getInstance().getForName("lense.core.math.Comparison").get();
	}

	// Fundamental
	public static TypeDefinition Comparable() {
		return getInstance().getForName("lense.core.math.Comparable", 1).get();
	}

	// Fundamental
	public static TypeDefinition Void() {
		return getInstance().getForName("lense.core.lang.Void").get();
	}

	public static TypeDefinition Iterable() {
		return getInstance().getForName("lense.core.collections.Iterable", 1).get();
	}

	public static TypeDefinition Iterator() {
		return getInstance().getForName("lense.core.collections.Iterator", 1).get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Sequence() {
		return getInstance().getForName("lense.core.collections.Sequence", 1).get();
	}

	public static TypeDefinition KeyValuePair() {
		return getInstance().getForName("lense.core.collections.KeyValuePair", 2).get();
	}

	public static TypeDefinition Association() {
		return getInstance().getForName("lense.core.collections.Association", 2).get();
	}

	public static TypeDefinition Exception() {
		return getInstance().getForName("lense.core.lang.Exception").get();
	}

	public static TypeDefinition Character() {
		return getInstance().getForName("lense.core.lang.Character").get();
	}

	// Fundamental
	public static TypeDefinition String() {
		return getInstance().getForName("lense.core.lang.String").get();
	}

	public static TypeDefinition Number() {
		return getInstance().getForName("lense.core.math.Number").get();
	}
	
	public static TypeDefinition Natural() {
		return getInstance().getForName("lense.core.math.Natural").get();
	}

	public static TypeDefinition Float() {
		return getInstance().getForName("lense.core.math.Float").get();
	}

	public static TypeDefinition Rational() {
		return getInstance().getForName("lense.core.math.Rational").get();
	}

	public static TypeDefinition Float64() {
		return getInstance().getForName("lense.core.math.Float64").get();
	}

	public static TypeDefinition Interval() {
		return getInstance().getForName("lense.core.math.Interval").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Float32() {
		return getInstance().getForName("lense.core.math.Float32").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Imaginary() {
		return getInstance().getForName("lense.core.math.Imaginary").get();
	}
	
	public static TypeDefinition Complex() {
		return getInstance().getForName("lense.core.math.Complex").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Short() {
		return getInstance().getForName("lense.core.math.Int16").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Int32() {
		return getInstance().getForName("lense.core.math.Int32").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Int64() {
		return getInstance().getForName("lense.core.math.Int64").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Tuple() {
		return getInstance().getForName("lense.core.collections.Tuple", 2).get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Function(int count) {
		Optional<LenseTypeDefinition> func = getInstance().getForName("lense.core.lang.Function", count);
		if (!func.isPresent()) {
			throw new CompilationError("No function of " + count + " parameters exist");
		}
		return func.get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Whole() {
		return getInstance().getForName("lense.core.lang.Whole").get();
	}

	private LenseTypeSystem() {

		LenseTypeDefinition any = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.Any", LenseUnitKind.Class, null));

		LenseTypeDefinition nothing = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.Nothing", LenseUnitKind.Class, null));

		register(new FundamentalLenseTypeDefinition("lense.core.math.Comparison", LenseUnitKind.Class, null));

		register(new FundamentalLenseTypeDefinition("lense.core.math.Comparable", LenseUnitKind.Interface, any,
				new RangeTypeVariable("T", Variance.Covariant, any, nothing)));

		register(new FundamentalLenseTypeDefinition("lense.core.lang.Exception", LenseUnitKind.Class, any));

		// SenseTypeDefinition function1 = register(new
		// SenseTypeDefinition("lense.core.lang.Function", Kind.Class, any,
		// new RangeTypeVariable("R", Variance.Invariant, any, nothing)
		// ));
		//
		LenseTypeDefinition function2 = register(new FundamentalLenseTypeDefinition("lense.core.lang.Function",
				LenseUnitKind.Interface, any, new RangeTypeVariable("R", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing)));

		register(new FundamentalLenseTypeDefinition("lense.core.lang.Function",
				LenseUnitKind.Interface, any, new RangeTypeVariable("R", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing)));

		register(new FundamentalLenseTypeDefinition("lense.core.lang.Binary", LenseUnitKind.Interface, any));

		LenseTypeDefinition sbool = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.Boolean", LenseUnitKind.Class, any));
		sbool.setFinal(true);
		sbool.addMethod("negate", sbool);

		register(new FundamentalLenseTypeDefinition("lense.core.lang.Interval", LenseUnitKind.Class, any,
				new RangeTypeVariable("T", Variance.Covariant, any, nothing)));

		// TODO treat interfaces and traits as attached types
		LenseTypeDefinition iterable = register(new FundamentalLenseTypeDefinition("lense.core.collections.Iterable",
				LenseUnitKind.Interface, any, new RangeTypeVariable("T", Variance.Covariant, any, nothing)));
		LenseTypeDefinition iterator = register(new FundamentalLenseTypeDefinition("lense.core.collections.Iterator",
				LenseUnitKind.Interface, any, new RangeTypeVariable("T", Variance.Covariant, any, nothing)));

		iterable.addProperty("iterator", iterator, true, false);

		iterator.addMethod("moveNext", sbool).setAbstract(true);
		iterator.addMethod("current", any ).setAbstract(true);

		LenseTypeDefinition maybe = register(new FundamentalLenseTypeDefinition("lense.core.lang.Maybe",
				LenseUnitKind.Class, any, new RangeTypeVariable("T", Variance.Covariant, any, nothing)));

		maybe.addMethod("map", specify(maybe, any), new MethodParameter(function2, "transform")); // TODO
		// return
		// must
		// obey
		// function
		// return

		LenseTypeDefinition none = register(new FundamentalLenseTypeDefinition("lense.core.lang.None",
				LenseUnitKind.Class, specify(maybe, nothing), new TypeVariable[0]));

		none.addField("None", none, Imutability.Imutable); // TODO
		// this
		// a
		// Fake
		// static
		// field

		register(new FundamentalLenseTypeDefinition("lense.core.lang.Some", LenseUnitKind.Class, maybe));

		LenseTypeDefinition character = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.Character", LenseUnitKind.Class, any));
		register(new FundamentalLenseTypeDefinition("lense.core.lang.Exception", LenseUnitKind.Class, any));

		register(new FundamentalLenseTypeDefinition("lense.core.collections.Progression", LenseUnitKind.Class, iterable));
		
		LenseTypeDefinition assortment = register(new FundamentalLenseTypeDefinition("lense.core.collections.Assortment", LenseUnitKind.Interface, iterable));
		
		LenseTypeDefinition sequence = register(new FundamentalLenseTypeDefinition("lense.core.collections.Sequence", LenseUnitKind.Interface, assortment));
		
		LenseTypeDefinition array = register(
				new FundamentalLenseTypeDefinition("lense.core.collections.Array", LenseUnitKind.Class, sequence));

		RangeTypeVariable self = new RangeTypeVariable("T", Variance.Covariant, any, nothing);
		LenseTypeDefinition tuple = register(new FundamentalLenseTypeDefinition("lense.core.collections.Tuple",
				LenseUnitKind.Class, any, new RangeTypeVariable("V", Variance.ContraVariant, any, nothing), self));
		self.setUpperBound(tuple);

		tuple.addMethod("tail", any); // TODO any -> T
		tuple.addMethod("head", any); // TODO any -> V

		LenseTypeDefinition svoid = register(new FundamentalLenseTypeDefinition("lense.core.lang.Void",
				LenseUnitKind.Class, specify(tuple, nothing, nothing), new TypeVariable[0]));

		LenseTypeDefinition keyValue = register(
				new FundamentalLenseTypeDefinition("lense.core.collections.KeyValuePair", LenseUnitKind.Interface, any,
						new RangeTypeVariable("K", Variance.ContraVariant, any, nothing),
						new RangeTypeVariable("V", Variance.Covariant, any, nothing)));

		keyValue.addConstructor(true, "valueOf",
				new ConstructorParameter(new DeclaringTypeBoundedTypeVariable(keyValue, 0, "K", Variance.Invariant)),
				new ConstructorParameter(new DeclaringTypeBoundedTypeVariable(keyValue, 1, "v", Variance.Invariant)));

		LenseTypeDefinition association = register(new FundamentalLenseTypeDefinition("lense.core.collections.Association", LenseUnitKind.Class,
				null, new RangeTypeVariable("K", Variance.ContraVariant, any, nothing),
				new RangeTypeVariable("V", Variance.Covariant, any, nothing)));

		association.addInterface(specify(assortment, keyValue));
		
		register(
				new FundamentalLenseTypeDefinition("lense.core.lang.Binary", LenseUnitKind.Interface, any));

		LenseTypeDefinition number = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Number", LenseUnitKind.Interface, any));
		LenseTypeDefinition whole = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Whole", LenseUnitKind.Interface, number));
		LenseTypeDefinition natural = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Natural", LenseUnitKind.Interface, whole));

		tuple.addMethod("get", any, new MethodParameter(natural, "index"));

		sequence.addIndexer(new DeclaringTypeBoundedTypeVariable(sequence, 0, "T", Variance.Covariant), true, true,
				new TypeVariable[] { natural });

		// array.addMethod("set", svoid , new MethodParameter(natural, "index"),
		// new MethodParameter(new
		// DeclaringTypeBoundedTypeVariable(array,0,"T",Variance.Invariant),
		// "value"));

		array.addIndexer(new DeclaringTypeBoundedTypeVariable(array, 0, "T", Variance.Invariant), true, true,
				new TypeVariable[] { natural });

		LenseTypeDefinition integer = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Integer", LenseUnitKind.Interface, whole));
		LenseTypeDefinition sint = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Int32", LenseUnitKind.ValueClass, integer));
		register(
				new FundamentalLenseTypeDefinition("lense.core.math.Int64", LenseUnitKind.ValueClass, integer));
		register(
				new FundamentalLenseTypeDefinition("lense.core.math.Int16", LenseUnitKind.ValueClass, integer));

		//sint.addConstructor(true, "valueOf", new ConstructorParameter(whole));

		//integer.addConstructor(true, "valueOf", new ConstructorParameter(natural));
		//integer.addConstructor(true, "valueOf", new ConstructorParameter(whole));

		natural.addMethod("multiply", natural, new MethodParameter(natural));
		natural.addMethod("remainder", natural, new MethodParameter(natural));
		natural.addMethod("plus", natural, new MethodParameter(natural));

		natural.addMethod("multiply", integer, new MethodParameter(integer));
		natural.addMethod("remainder", integer, new MethodParameter(integer));
		natural.addMethod("plus", integer, new MethodParameter(integer));

		integer.addMethod("multiply", integer, new MethodParameter(integer));
		integer.addMethod("remainder", integer, new MethodParameter(integer));
		integer.addMethod("plus", integer, new MethodParameter(integer));

		natural.addMethod("symmetric", integer);

		sequence.addProperty("size", natural, true, false);

		LenseTypeDefinition string = register(new FundamentalLenseTypeDefinition("lense.core.lang.String",
				LenseUnitKind.Class, any, new TypeVariable[0]));
		string.addInterface(specify(sequence, character));

		// sint.addConstructor("parse", new ConstructorParameter(string));

		string.addMethod("get", character, new MethodParameter(natural));
		string.addMethod("concat", string, new MethodParameter(string));
		string.addMethod("concat", string, new MethodParameter(any));

		LenseTypeDefinition type = register(new FundamentalLenseTypeDefinition("lense.core.lang.reflection.Type", LenseUnitKind.Class, any));

		LenseTypeDefinition hashValue = register(new FundamentalLenseTypeDefinition("lense.core.lang.HashValue", LenseUnitKind.Class, any));

		hashValue.addMethod("concat", hashValue, new MethodParameter(hashValue, "other"));
		
		any.addMethod("asString", string).setDefault(true);
		any.addMethod("hashValue", hashValue).setDefault(true);
		any.addMethod("equalsTo", sbool, new MethodParameter(any,"other")).setDefault(true);
		any.addMethod("type", type).setDefault(false);
		
		

		LenseTypeDefinition real = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Real", LenseUnitKind.Interface, number));

		LenseTypeDefinition decimal = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Float", LenseUnitKind.Interface, real));
		register(new FundamentalLenseTypeDefinition("lense.core.math.Float64", LenseUnitKind.ValueClass, decimal));
		register(new FundamentalLenseTypeDefinition("lense.core.math.Float32", LenseUnitKind.ValueClass, decimal));

		register(new FundamentalLenseTypeDefinition("lense.core.math.Rational", LenseUnitKind.ValueClass, real));

		LenseTypeDefinition img = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Imaginary", LenseUnitKind.ValueClass, number));
		img.addMethod("real", real);

		LenseTypeDefinition complex = register(
				new FundamentalLenseTypeDefinition("lense.core.math.Complex", LenseUnitKind.ValueClass, number));

		LenseTypeDefinition interval = register(new FundamentalLenseTypeDefinition("lense.core.math.Interval",
				LenseUnitKind.Class, any, new RangeTypeVariable("T", Variance.Invariant, any, nothing))); // TODO
		// use
		// Comparable

		interval.addMethod("contains", sbool, new MethodParameter(any));

		whole.addMethod("plus", complex, new MethodParameter(img));
		whole.addMethod("minus", complex, new MethodParameter(img));

		// LenseTypeDefinition console = register(new
		// LenseTypeDefinition("lense.core.io.Console", LenseUnitKind.Object,
		// any));
		// console.addMethod("println", svoid, new MethodParameter(string));
		//
		LenseTypeDefinition version = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.Version", LenseUnitKind.ValueClass, any));

		LenseTypeDefinition packagetype = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.reflection.Package", LenseUnitKind.Interface, any));

		LenseTypeDefinition module = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.reflection.Module", LenseUnitKind.Interface, any));
		module.addMethod("getVersion", version);
		module.addMethod("getPackages", specify(sequence, packagetype));

		LenseTypeDefinition list = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.List", LenseUnitKind.Interface, sequence));
		list.addMethod("add", svoid, new MethodParameter(any));

		LenseTypeDefinition refArgs = register(
				new FundamentalLenseTypeDefinition("lense.core.lang.reflection.ReifiedArguments", LenseUnitKind.Interface, any));
		
		refArgs.addMethod("typeAt", type, new MethodParameter(natural, "index"));
		refArgs.addMethod("fromIndex", refArgs, new MethodParameter(natural, "index"));
		
		
	}

	private Map<TypeKey, LenseTypeDefinition> definitions = new HashMap<>();

	public LenseTypeDefinition register(LenseTypeDefinition definition) {
		definitions.put(new TypeKey(definition.getGenericParameters(), definition.getName()), definition);
		return definition;
	}

	public Optional<LenseTypeDefinition> getForName(String name, TypeVariable... genericTypeParameters) {
		return Optional.ofNullable(definitions.get(new TypeKey(Arrays.asList(genericTypeParameters), name)));
	}

	/**
	 * @param name
	 * @param i
	 * @return
	 */
	public Optional<LenseTypeDefinition> getForName(String name, int genericParametersCount) {

		boolean isMaybe = false;
		if (name.endsWith("?")) {
			name = name.substring(0, name.length() - 1);
			isMaybe = true;
		}

		for (TypeKey key : definitions.keySet()) {
			if (key.getName().equals(name) && key.getGenericTypeParameters().size() == genericParametersCount) {

				if (isMaybe) {
					return Optional.of(specify(Maybe(), definitions.get(key)));
				}
				return Optional.ofNullable(definitions.get(key));
			}
		}
		return Optional.empty();
	}



	/**
	 * {@inheritDoc}
	 */
	public TypeDefinition MostUpperType() {
		return Any();
	}

	/**
	 * {@inheritDoc}
	 */
	public TypeDefinition MostLowerType() {
		return Nothing();
	}

	/**
	 * @return
	 */
	public Set<String> packageNames() {
		return definitions.keySet().stream().map(s -> {

			int pos = s.getName().lastIndexOf('.');
			if (pos < 0) {
				return s.getName();
			} else {
				return s.getName().substring(0, pos);
			}

		}).distinct().collect(Collectors.toSet());
	}




	public boolean isTuple(TypeVariable type, int count) {
		return isTuple(type.getTypeDefinition(), count);
	}

	public boolean isTuple(TypeDefinition type, int count) {
		return type.getName().equals("lense.core.collections.Tuple") && countTupleParameters(type) == count;
	}

	private int countTupleParameters(TypeDefinition tuple) {
		TypeDefinition head = tuple.getGenericParameters().get(0).getTypeDefinition();
		if (head.equals(LenseTypeSystem.Nothing())) {
			return 0;
		}
		TypeDefinition tail = tuple.getGenericParameters().get(1).getTypeDefinition();
		int count = 1;
		while (!tail.equals(LenseTypeSystem.Nothing())) {
			count++;
			tail = tail.getGenericParameters().get(1).getTypeDefinition();
		}
		return count;
	}

	public Collection<LenseTypeDefinition> getAll() {
		return Collections.unmodifiableCollection(this.definitions.values());
	}

	public boolean isAny(TypeDefinition type) {
		return type.getName().equals(Any().getName());
	}
	
	public boolean isAny(TypeVariable type) {
	    return type.isFixed() && type.getTypeDefinition().getName().equals(Any().getName());
	}
	    
	
	public boolean isNothing(TypeVariable type) {
	        return type.getTypeDefinition().getName().equals(Nothing().getName());
	}


	public boolean isBoolean(TypeVariable type) {
		return type.getTypeDefinition().getName().equals(Boolean().getName());
	}


	public boolean isMaybe(TypeDefinition type) {

		return type.getName().equals("lense.core.lang.None") || type.getName().equals("lense.core.lang.Some") || type.getName().equals("lense.core.lang.Maybe");
	}

	public boolean isMaybe(TypeVariable typeVar) {
		if (typeVar.isSingleType()) {
			TypeDefinition type = typeVar.getTypeDefinition();
			return type.getName().equals("lense.core.lang.None") || type.getName().equals("lense.core.lang.Some") || type.getName().equals("lense.core.lang.Maybe");
		}
		return false;
	}

    public boolean isVoid(TypeDefinition type) {
        return type.getName().equals(Void().getName());
    }

    

	public LenseTypeDefinition specify(TypeVariable definition, TypeVariable... genericParametersCapture) {
		if (!definition.isFixed()) {
			throw new RuntimeException("Cannot specify a non fixed type variable");
		}

		return specify(definition.getTypeDefinition(), genericParametersCapture);
	}



	public LenseTypeDefinition specify(TypeDefinition definition, List<TypeVariable> genericParameters) {

		return ((LenseTypeDefinition)definition).specify(genericParameters);
	}

	public LenseTypeDefinition specify(TypeDefinition definition, TypeVariable... genericParameters) {
		if (definition == null){
			throw new IllegalArgumentException("Definition type is required");
		}
		return ((LenseTypeDefinition)definition).specify(Arrays.asList(genericParameters));
	}



	private LenseTypeDefinition specify(TypeDefinition definition, TypeDefinition... genericParametersCapture) {

		if (definition.getGenericParameters().size() != genericParametersCapture.length) {
			throw new CompilationError("Wrong number of generic arguments for type " + definition + ". Expected "
					+ definition.getGenericParameters().size() + " found " + genericParametersCapture.length);
		}
		TypeVariable[] genericParameters = new TypeVariable[definition.getGenericParameters().size()];

		for (int i = 0; i < definition.getGenericParameters().size(); i++) {
			TypeVariable gen = definition.getGenericParameters().get(i);
			if (gen.getLowerBound().equals(gen.getUpperBound())) {
				throw new CompilationError("Cannot specify a non generic type");
			}
			genericParameters[i] = new RangeTypeVariable(gen.getSymbol(), gen.getVariance(),
					genericParametersCapture[i], genericParametersCapture[i]);
		}

		return specify(definition, genericParameters);
	}

	
	

}
