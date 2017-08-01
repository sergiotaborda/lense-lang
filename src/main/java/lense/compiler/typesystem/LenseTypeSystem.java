/**
 * 
 */
package lense.compiler.typesystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import compiler.CompilationError;
import lense.compiler.type.CallableMember;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.CallableMemberSignature;
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.MethodSignature;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.UnionType;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class LenseTypeSystem{

	private static LenseTypeSystem me = new LenseTypeSystem();

	public static LenseTypeSystem getInstance(){
		return me;
	}

	public static TypeDefinition Any() {
		return getInstance().getForName("lense.core.lang.Any").get();
	}

	public static TypeDefinition Nothing(){
		return getInstance().getForName("lense.core.lang.Nothing").get();
	}

	public static TypeDefinition Binary() {
		return getInstance().getForName("lense.core.lang.Binary").get();
	}
	
	public static TypeDefinition None(){
		return getInstance().getForName("lense.core.lang.None").get();
	}
	
	public static TypeDefinition Some() {
		return getInstance().getForName("lense.core.lang.Some").get();
	}

	public static TypeDefinition Maybe(){
		return getInstance().getForName("lense.core.lang.Maybe", 1).get();
	}

	public static TypeDefinition Progression() {
		return getInstance().getForName("lense.core.collections.Progression",1).get();
	}

	// Fundamental
	public static TypeDefinition Boolean() {
		return getInstance().getForName("lense.core.lang.Boolean").get();
	}
	// Fundamental
	public static TypeDefinition Void() {
		return getInstance().getForName("lense.core.lang.Void").get();
	}

	public static TypeDefinition Iterable() {
		return getInstance().getForName("lense.core.collections.Iterable",1).get();
	}
	
	public static TypeDefinition Iterator() {
		return getInstance().getForName("lense.core.collections.Iterator",1).get();
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

	public static TypeDefinition Natural() {
		return getInstance().getForName("lense.core.math.Natural").get();
	}

	public static TypeDefinition Decimal() {
		return getInstance().getForName("lense.core.math.Decimal").get();
	}

	public static TypeDefinition Rational() {
		return getInstance().getForName("lense.core.math.Rational").get();
	}
	
	public static TypeDefinition Decimal64() {
		return getInstance().getForName("lense.core.math.Decimal64").get();
	}
	
	public static TypeDefinition Interval() {
		return getInstance().getForName("lense.core.math.Interval").get();
	}
	
	/**
	 * @return
	 */
	public static TypeDefinition Decimal32() {
		return getInstance().getForName("lense.core.math.Decimal32").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Imaginary() {
		return getInstance().getForName("lense.core.math.Imaginary").get();
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
	public static TypeDefinition Int() {
		return getInstance().getForName("lense.core.math.Int32").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Long() {
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
		if (!func.isPresent()){
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

	private LenseTypeSystem (){
		LenseTypeDefinition nothing = register(new FundamentalLenseTypeDefinition("lense.core.lang.Nothing", LenseUnitKind.Class, null));

		LenseTypeDefinition any = register(new FundamentalLenseTypeDefinition("lense.core.lang.Any", LenseUnitKind.Class, null));
	    register(new LenseTypeDefinition("lense.core.lang.Exception", LenseUnitKind.Class, any));
	    
//	    SenseTypeDefinition function1 = register(new SenseTypeDefinition("lense.core.lang.Function", Kind.Class, any,
//				new RangeTypeVariable("R", Variance.Invariant, any, nothing)
//		));
//	    
		LenseTypeDefinition function2 = register(new FundamentalLenseTypeDefinition("lense.core.lang.Function", LenseUnitKind.Interface, any,
				new RangeTypeVariable("R", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing)
		));
		
		LenseTypeDefinition function3 = register(new FundamentalLenseTypeDefinition("lense.core.lang.Function", LenseUnitKind.Interface, any,
				new RangeTypeVariable("R", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing)
		));
		
		register(new FundamentalLenseTypeDefinition("lense.core.lang.Binary", LenseUnitKind.Interface, any));

		LenseTypeDefinition sbool = register(new FundamentalLenseTypeDefinition("lense.core.lang.Boolean", LenseUnitKind.Class, any));
		
		sbool.addMethod("negate", sbool);
		
		register(new LenseTypeDefinition("lense.core.lang.Interval", LenseUnitKind.Class, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing))); 
		
		
		// TODO treat interfaces and traits as attached types
		LenseTypeDefinition iterable = register(new FundamentalLenseTypeDefinition("lense.core.collections.Iterable", LenseUnitKind.Interface, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing))); 
		LenseTypeDefinition iterator = register(new FundamentalLenseTypeDefinition("lense.core.collections.Iterator", LenseUnitKind.Interface, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing))); 
		
		iterable.addMethod("iterator", iterator); 
		
		iterator.addMethod("hasNext", sbool);
		iterator.addMethod("hasNext", any); 
		
		LenseTypeDefinition maybe = register(new FundamentalLenseTypeDefinition("lense.core.lang.Maybe", LenseUnitKind.Class, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing)));  

		maybe.addMethod("map", specify(maybe, any), new MethodParameter(function2)); // TODO return must obey function return
		maybe.addConstructor("",new ConstructorParameter(any));
		
		
		LenseTypeDefinition none = register(new FundamentalLenseTypeDefinition("lense.core.lang.None", LenseUnitKind.Class, specify(maybe, nothing), new IntervalTypeVariable[0]));
		
		none.addField("None", new FixedTypeVariable(none), Imutability.Imutable); // TODO this a Fake static field 
		
		register(new LenseTypeDefinition("lense.core.lang.Some", LenseUnitKind.Class, maybe));

	
		LenseTypeDefinition character = register(new FundamentalLenseTypeDefinition("lense.core.lang.Character", LenseUnitKind.Class, any));
		register(new LenseTypeDefinition("lense.core.lang.Exception", LenseUnitKind.Class, any));

		register(new LenseTypeDefinition("lense.core.collections.Progression", LenseUnitKind.Class, iterable));
		LenseTypeDefinition sequence = register(new FundamentalLenseTypeDefinition("lense.core.collections.Sequence", LenseUnitKind.Interface, iterable));
		LenseTypeDefinition array = register(new FundamentalLenseTypeDefinition("lense.core.collections.Array", LenseUnitKind.Class, sequence));
		
	
		RangeTypeVariable self = new RangeTypeVariable("T", Variance.Covariant, any,nothing);
		LenseTypeDefinition tuple =  register(new FundamentalLenseTypeDefinition("lense.core.collections.Tuple", LenseUnitKind.Class,any,
				new RangeTypeVariable("V", Variance.ContraVariant, any,nothing), 
				self
		));
		self.setUpperBound(new FixedTypeVariable(tuple));
		
		tuple.addMethod("tail", any); // TODO any -> T
		tuple.addMethod("head", any); // TODO any -> V
		
		LenseTypeDefinition svoid = register(new FundamentalLenseTypeDefinition("lense.core.lang.Void", LenseUnitKind.Class, specify(tuple, nothing, nothing), new IntervalTypeVariable[0]));

		
	
		
		LenseTypeDefinition keyValue = register(new FundamentalLenseTypeDefinition("lense.core.collections.KeyValuePair", LenseUnitKind.Interface,any,
				new RangeTypeVariable("K", Variance.ContraVariant, any,nothing), 
				new RangeTypeVariable("V", Variance.Covariant, any,nothing)
		));
		
		keyValue.addConstructor(true, "valueOf", 
				new ConstructorParameter(new DeclaringTypeBoundedTypeVariable(keyValue,0,"K", Variance.Invariant)),
				new ConstructorParameter(new DeclaringTypeBoundedTypeVariable(keyValue,1,"v", Variance.Invariant))
		);
		
		register(new LenseTypeDefinition("lense.core.collections.Association", LenseUnitKind.Class, specify(sequence,keyValue ) ,
				new RangeTypeVariable("K", Variance.ContraVariant, any,nothing), 
				new RangeTypeVariable("V", Variance.Covariant, any,nothing)
		));
		
		LenseTypeDefinition binary = register(new FundamentalLenseTypeDefinition("lense.core.lang.Binary", LenseUnitKind.Interface, any));
		
		LenseTypeDefinition number = register(new FundamentalLenseTypeDefinition("lense.core.math.Number", LenseUnitKind.Class, any));
		LenseTypeDefinition whole = register(new FundamentalLenseTypeDefinition("lense.core.math.Whole", LenseUnitKind.Class, number));
		LenseTypeDefinition natural =register(new FundamentalLenseTypeDefinition("lense.core.math.Natural", LenseUnitKind.Class, whole));

		tuple.addMethod("get", any, new MethodParameter(natural, "index"));

		sequence.addMethod("get", new MethodReturn( new DeclaringTypeBoundedTypeVariable(sequence, 0, "T", Variance.Covariant)) , new MethodParameter(natural, "index")); 
		array.addMethod("set", svoid  , new MethodParameter(natural, "index"), new MethodParameter(new DeclaringTypeBoundedTypeVariable(array,0,"T",Variance.Invariant), "value")); 
		
		LenseTypeDefinition integer = register(new FundamentalLenseTypeDefinition("lense.core.math.Integer", LenseUnitKind.Class, whole));
		LenseTypeDefinition sint = register(new FundamentalLenseTypeDefinition("lense.core.math.Int32", LenseUnitKind.Class, integer));
		LenseTypeDefinition slong =register(new FundamentalLenseTypeDefinition("lense.core.math.Int64", LenseUnitKind.Class, integer));
		LenseTypeDefinition sshort =register(new FundamentalLenseTypeDefinition("lense.core.math.Int16", LenseUnitKind.Class, integer));
		
		sint.addConstructor(true , "valueOf", new ConstructorParameter(natural));
		sint.addConstructor(true, "valueOf", new ConstructorParameter(whole));

		integer.addConstructor(true,"valueOf", new ConstructorParameter(natural));
		integer.addConstructor(true,"valueOf", new ConstructorParameter(whole));
		
		natural.addMethod("multiply", natural, new MethodParameter(natural));
		natural.addMethod("remainder", natural, new MethodParameter(natural));
		natural.addMethod("plus", natural, new MethodParameter(natural));
		
		natural.addMethod("multiply", integer, new MethodParameter(integer));
		natural.addMethod("remainder", integer, new MethodParameter(integer));
		natural.addMethod("plus", integer, new MethodParameter(integer));
		
		
		integer.addMethod("multiply", integer, new MethodParameter(integer));
		integer.addMethod("remainder", integer, new MethodParameter(integer));
		integer.addMethod("plus", integer, new MethodParameter(integer));
		
		natural.addMethod("negative", integer); // TODO could overflow
		//natural.addMethod("symmetric", integer); // TODO could overflow
		
		sequence.addProperty("size", natural, true, false);

	
		LenseTypeDefinition string = register(new FundamentalLenseTypeDefinition("lense.core.lang.String", LenseUnitKind.Class, any, new IntervalTypeVariable[0]));
		string.addInterface(specify(sequence, character));
	
		sint.addConstructor("parse", new ConstructorParameter(string));
		
		string.addMethod("toMaybe", specify(maybe, string));
		string.addMethod("get", character, new MethodParameter (natural));
		string.addMethod("plus", string, new MethodParameter (string));
		string.addMethod("plus", string, new MethodParameter (any));
		
		
		//any.addMethod("toString", string);

		LenseTypeDefinition real = register(new FundamentalLenseTypeDefinition("lense.core.lang.Real", LenseUnitKind.Class, number));
		
		LenseTypeDefinition decimal = register(new FundamentalLenseTypeDefinition("lense.core.math.Decimal", LenseUnitKind.Class, real));
		register(new FundamentalLenseTypeDefinition("lense.core.math.Decimal64", LenseUnitKind.Class, decimal));
		register(new FundamentalLenseTypeDefinition("lense.core.math.Decimal32", LenseUnitKind.Class, decimal));

		register(new FundamentalLenseTypeDefinition("lense.core.math.Rational", LenseUnitKind.Class, real));

		LenseTypeDefinition img = register(new FundamentalLenseTypeDefinition("lense.core.math.Imaginary", LenseUnitKind.Class, number));
		img.addMethod("real", real);
		
		LenseTypeDefinition complex = register(new FundamentalLenseTypeDefinition("lense.core.math.Complex", LenseUnitKind.Class, number));
		
		LenseTypeDefinition interval = register(new FundamentalLenseTypeDefinition("lense.core.math.Interval", LenseUnitKind.Class, any, new RangeTypeVariable("T", Variance.Invariant, any, nothing))); // TODO use Comparable
		
		interval.addMethod("contains", sbool, new MethodParameter(any));
		
		whole.addMethod("plus", complex, new MethodParameter(img));
		whole.addMethod("minus", complex, new MethodParameter(img));
		

		

//		LenseTypeDefinition console = register(new LenseTypeDefinition("lense.core.io.Console", LenseUnitKind.Object, any));
//		console.addMethod("println", svoid, new MethodParameter(string));
//		
		LenseTypeDefinition version = register(new FundamentalLenseTypeDefinition("lense.core.lang.Version", LenseUnitKind.Class, any));

		LenseTypeDefinition packagetype = register(new FundamentalLenseTypeDefinition("lense.core.lang.reflection.Package", LenseUnitKind.Interface, any));

		
		LenseTypeDefinition module = register(new LenseTypeDefinition("lense.core.lang.reflection.Module", LenseUnitKind.Interface, any));
		module.addMethod("getVersion",version);
		module.addMethod("getPackages",specify(sequence, packagetype));
		
		LenseTypeDefinition list = register(new FundamentalLenseTypeDefinition("lense.core.lang.List", LenseUnitKind.Interface, sequence));
		list.addMethod("add",svoid, new MethodParameter(any));

	}

	private Map<TypeKey, LenseTypeDefinition> definitions = new HashMap<>();

	public LenseTypeDefinition register(LenseTypeDefinition definition){
		definitions.put(new TypeKey(definition.getGenericParameters(), definition.getName()), definition);
		return definition;
	}

	public Optional<LenseTypeDefinition> getForName(String name, IntervalTypeVariable ... genericTypeParameters){
		return Optional.ofNullable(definitions.get(new TypeKey(Arrays.asList(genericTypeParameters), name)));
	}

	/**
	 * @param name
	 * @param i
	 * @return
	 */
	public Optional<LenseTypeDefinition> getForName(String name, int genericParametersCount) {
		
		boolean isMaybe = false;
		if (name.endsWith("?")){
			name = name.substring(0, name.length()- 1);
			isMaybe = true;
		}
		
		for(TypeKey key : definitions.keySet()){
			if (key.getName().equals(name) && key.getGenericTypeParameters().size() == genericParametersCount){
				
				if (isMaybe){
					return Optional.of(specify(Maybe(), definitions.get(key)));
				}
				return Optional.of(definitions.get(key));
			}
		}
		return Optional.empty();
	}

	public static boolean isAssignableTo(IntervalTypeVariable type, IntervalTypeVariable target){
	    if (target.getVariance() == Variance.ContraVariant){
	    	isAssignableTo(target.getUpperBound(), type.getUpperBound());
		} else if (target.getVariance() == Variance.Covariant){
			return isAssignableTo(type.getUpperBound(), target.getUpperBound());
		}
		 
		return isAssignableTo(type.getUpperBound(), target.getUpperBound()) && isAssignableTo(type.getLowerBound(), target.getLowerBound()) ;
	}
	
	public static boolean isAssignableTo(TypeVariable type, TypeVariable target){
		if (type.isSingleType()){
			if (target.isSingleType() ){
				return isAssignableTo (type.getTypeDefinition(), target.getTypeDefinition());
			} else {
				IntervalTypeVariable interval = (IntervalTypeVariable)target;
				// interval contains type ?
				return isAssignableTo(type, interval.getUpperBound()) && isAssignableTo(interval.getLowerBound(),type);
			}
		} else {
			IntervalTypeVariable interval = (IntervalTypeVariable)type;
			if (target instanceof FixedTypeVariable){
				return isAssignableTo(interval.getLowerBound(), interval.getUpperBound()) && isAssignableTo(interval.getUpperBound(), target);
			} else {
				IntervalTypeVariable other = (IntervalTypeVariable)target;
				return isAssignableTo(interval.getLowerBound(), other.getLowerBound()) && isAssignableTo(other.getUpperBound(), interval.getUpperBound());
			}
		}
	}
	
	public static boolean isAssignableTo(TypeDefinition type, TypeDefinition target){
		
		if (type == null || target == null){
			return false;
		}

		if (type == target){
			return true;
		}
		if (target.getName().equals("lense.core.lang.Any")){
			return true; // all types are assignable to Any
		}
		if (type.getName().equals("lense.core.lang.Nothing")){
			return true; // nothing is assignable to all types
		}
		if (target.getName().equals("lense.core.lang.Nothing")){
			return false; // only nothing is assignable to nothing
		}
		if (type.getName().equals("lense.core.lang.Any")){
			return false;
		}


		if (type.getName().equals( target.getName()) &&  type.getGenericParameters().size() == target.getGenericParameters().size() ){
			boolean assignable = true;
			for (int i = 0; i < type.getGenericParameters().size(); i++){
				if (!isAssignableTo(type.getGenericParameters().get(i), target.getGenericParameters().get(i))){
					assignable = false;
					break;
				}
			}
			if (assignable){
				return true;
			}
		} 
		
		// interface implementation
		for (TypeDefinition interfaceDefiniton : type.getInterfaces()) {		
			if ( isAssignableTo( interfaceDefiniton, target )){
				return true;
			}
		}

		// inheritance
		if (type.getSuperDefinition() != null){
			if (!type.getSuperDefinition().getName().equals("lense.core.lang.Any") && type.isGeneric()){
				IntervalTypeVariable[] types = new IntervalTypeVariable[type.getGenericParameters().size()];
				for(int i = 0; i < types.length; i++){
					types[i] = type.getGenericParameters().get(i);
				}
				return isAssignableTo(specify(type.getSuperDefinition(), types), target);
			} else {
				return isAssignableTo(type.getSuperDefinition(), target);
			}
		}
		
	

		return false;

		//return type.equals(Nothing()) || ( type.getName() == target.getName() && type.getGenericParameters().size() == target.getGenericParameters().size());
	}

	public static LenseTypeDefinition specify(TypeVariable definition, TypeVariable ... genericParametersCapture) {
		if (!(definition instanceof FixedTypeVariable)){
			throw new RuntimeException("Cannot specify a non fixed type variable");
		}

		return specify(definition.getTypeDefinition(), genericParametersCapture);
	}
	public static LenseTypeDefinition specify(TypeDefinition definition, TypeDefinition other) {
		return specify(definition, new FixedTypeVariable(other));
	}
	public static LenseTypeDefinition specify(TypeDefinition definition, TypeVariable ... genericParametersCapture) {
	
		IntervalTypeVariable[] defs = new IntervalTypeVariable[genericParametersCapture.length];
		for (int i =0; i < genericParametersCapture.length; i++){
			defs[i] = genericParametersCapture[i].toIntervalTypeVariable();
		}
		
		return specify(definition, defs);
	}
	

	/**
	 * @param progression
	 * @param finalType
	 * @return
	 */
	private static LenseTypeDefinition specify(TypeDefinition definition, TypeDefinition ... genericParametersCapture) {

		if (definition.getGenericParameters().size() != genericParametersCapture.length){
			throw new CompilationError("Wrong number of generic arguments for type " + definition + ". Expected " + definition.getGenericParameters().size() + " found " + genericParametersCapture.length);
		}
		IntervalTypeVariable[] genericParameters = new IntervalTypeVariable[definition.getGenericParameters().size()];

		for (int i =0; i < definition.getGenericParameters().size(); i++){
			IntervalTypeVariable gen = definition.getGenericParameters().get(i);
			if (gen.getLowerBound().equals(gen.getUpperBound())){
				throw new CompilationError("Cannot specify a non generic type");
			}
			genericParameters[i] = new RangeTypeVariable(gen.getSymbol(), gen.getVariance(),  genericParametersCapture[i],  genericParametersCapture[i]);
		}
		
		return specify(definition, genericParameters);
	}
	
	public static LenseTypeDefinition specify(TypeDefinition definition,IntervalTypeVariable ... genericParameters) {
		LenseTypeDefinition concrete = new LenseTypeDefinition(
				definition.getName(),
				definition.getKind(), 
				(LenseTypeDefinition)definition.getSuperDefinition(),
				genericParameters);

		concrete.addMembers(definition.getMembers().stream().map(m -> m.changeDeclaringType(concrete))); 
	
		for (TypeDefinition def : definition.getInterfaces()){
			
			if (def.isGeneric()){
				IntervalTypeVariable[] binded = new IntervalTypeVariable[def.getGenericParameters().size()];
				int i=0;
				for(IntervalTypeVariable p : def.getGenericParameters()){
					binded[i++] = p.changeBaseType(concrete).toIntervalTypeVariable();
				}
				TypeDefinition sp = specify(def, binded);
				
				concrete.addInterface(sp);
			} else {
				concrete.addInterface(def);
			}
			
		}
		
		return concrete;
	}

	public boolean isPromotableTo(TypeDefinition a, TypeDefinition b) {
		return isPromotableTo(new FixedTypeVariable(a), new FixedTypeVariable(b));
	}
	/**
	 * Indicates if the type at right position promotable to type at the left postision 
	 * @param left
	 * @param right
	 * @return
	 */
	public boolean isPromotableTo(TypeVariable a, TypeVariable b) {
		if (a == null || b == null){
			return false;
		}
		
		if (a == b ){
			return true;
		} else if (isAssignableTo(a, b)){
			return true;
//		} else if ( a.getTypeDefinition().equals(LenseTypeSystem.String()) && b.getTypeDefinition().equals(LenseTypeSystem.TextRepresentable())){
//			return true;
		} 
		
		if (b instanceof FixedTypeVariable){
			Optional<Constructor> op = b.getTypeDefinition().getConstructorByParameters(new ConstructorParameter(a));
			
			return op.filter(c -> c.isImplicit()).isPresent();
		}
		return false;
	}


	/**
	 * @param typeDefinition
	 * @param typeDefinition2
	 * @return
	 */
	public TypeVariable unionOf(TypeVariable a, TypeVariable b) {
		if (a ==b || a.equals(b)){
			return a;
		} else {
			return new FixedTypeVariable(new UnionType(a, b));
		}
	}

	/**
	 * @param constructorSignature
	 * @param m
	 * @return
	 */
	public <M extends CallableMember<M>> boolean isSignatureImplementedBy(CallableMemberSignature<M> signature, CallableMember<M> m) {
		final List<CallableMemberMember<M>> memberParameters = m.getParameters();
		final List<CallableMemberMember<M>> signatureParameters = signature.getParameters();
		
		return signature.getName().equals(m.getName()) && areSignatureParametersImplementedBy(signatureParameters,memberParameters);

	}

	public <M extends CallableMember<M>> boolean areSignatureParametersImplementedBy(List<CallableMemberMember<M>> signatureParameters, List<CallableMemberMember<M>> memberParameters) {

		if (signatureParameters.size() == memberParameters.size()){
			
			for (int i = 0; i < signatureParameters.size(); i++ ){
				if (!isAssignableTo(signatureParameters.get(i).getType(), memberParameters.get(i).getType())){
					return false;
				}
			}
			return true;
		}
		return false;
		
	}
	
	public boolean isSignatureAssignableTo(MethodSignature from , MethodSignature to){
		if ( from.getName().equals(to.getName()) && from.getParameters().size() == to.getParameters().size()){
			
			for (int i = 0; i < from.getParameters().size(); i++ ){
				if (!isAssignableTo(from.getParameters().get(i).getType(), to.getParameters().get(i).getType())){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	

	/**
	 * @param s
	 * @return
	 */
	public boolean isSignaturePromotableTo(MethodSignature from ,MethodSignature to) {
		if ( from.getName().equals(to.getName()) && from.getParameters().size() == to.getParameters().size()){
			
			for (int i = 0; i < from.getParameters().size(); i++ ){
				if (!isPromotableTo(from.getParameters().get(i).getType(), to.getParameters().get(i).getType())){
					return false;
				}
			}
			return true;
		}
		return false;
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
			if (pos <0 ){
				return s.getName();
			} else {
				return s.getName().substring(0, pos);
			}
			
			
		}).distinct().collect(Collectors.toSet());
	}

	public boolean isMethodImplementedBy(Method reference, Method candidate) {

		return reference.getName().equals(candidate.getName()) && areSignatureParametersImplementedBy(reference.getParameters(), candidate.getParameters());
	}

	public boolean areNomallyEquals(TypeDefinition a, TypeDefinition b) {
		return a.getName().equals(b.getName());
	}

	public static boolean isNumber(TypeDefinition maxType) {
		return maxType.getName().startsWith("lense.core.math") && ( 
				maxType.getName().endsWith("Natural") 
				|| maxType.getName().endsWith("Integer")
				|| maxType.getName().endsWith("Decimal") 
	); 
	}

	public boolean isTuple(TypeVariable type, int count) {
		return isTuple(type.getTypeDefinition(), count);
	}

	public boolean isTuple(TypeDefinition type, int count) {
		return type.getName().equals("lense.core.collections.Tuple") && countTupleParameters(type) == count;
	}
	
	private int countTupleParameters(TypeDefinition tuple){
		 TypeDefinition head = tuple.getGenericParameters().get(0).getTypeDefinition();
		 if (head.equals(LenseTypeSystem.Nothing())){
			 return 0;
		 }
		 TypeDefinition tail = tuple.getGenericParameters().get(1).getTypeDefinition();
		 int count = 1;
		 while (!tail.equals(LenseTypeSystem.Nothing())){
			 count++;
			 tail = tail.getGenericParameters().get(1).getTypeDefinition();
		 }
		 return count;
	}

































}
