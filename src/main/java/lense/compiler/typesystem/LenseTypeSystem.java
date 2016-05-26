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
import lense.compiler.ast.Imutability;
import lense.compiler.type.CallableMember;
import lense.compiler.type.Constructor;
import lense.compiler.type.Kind;
import lense.compiler.type.LenseTypeDefinition;
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

	public static TypeDefinition None(){
		return getInstance().getForName("lense.core.lang.None").get();
	}

	public static TypeDefinition Maybe(){
		return getInstance().getForName("lense.core.lang.Maybe", 1).get();
	}

	public static TypeDefinition Progression() {
		return getInstance().getForName("lense.core.collections.Progression",1).get();
	}

	public static TypeDefinition Boolean() {
		return getInstance().getForName("lense.core.lang.Boolean").get();
	}

	public static TypeDefinition Void() {
		return getInstance().getForName("lense.core.lang.Void").get();
	}

	public static TypeDefinition Iterable() {
		return getInstance().getForName("lense.core.lang.Iterable",1).get();
	}
	
	public static TypeDefinition Iterator() {
		return getInstance().getForName("lense.core.lang.Iterator",1).get();
	}
	
	/**
	 * @return
	 */
	public static TypeDefinition Sequence() {
		return getInstance().getForName("lense.core.collections.Sequence", 1).get();
	}

	public static TypeDefinition Exception() {
		return getInstance().getForName("lense.core.lang.Exception").get();
	}

	public static TypeDefinition Character() {
		return getInstance().getForName("lense.core.lang.Character").get();
	}

	public static TypeDefinition String() {
		return getInstance().getForName("lense.core.lang.String").get();
	}

	public static TypeDefinition Natural() {
		return getInstance().getForName("lense.core.math.Natural").get();
	}

	public static TypeDefinition Decimal() {
		return getInstance().getForName("lense.core.math.Decimal").get();
	}

	public static TypeDefinition Double() {
		return getInstance().getForName("lense.core.math.Double").get();
	}
	
	public static TypeDefinition Interval() {
		return getInstance().getForName("lense.core.math.Interval").get();
	}
	
	/**
	 * @return
	 */
	public static TypeDefinition Float() {
		return getInstance().getForName("lense.core.math.Float").get();
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
		LenseTypeDefinition nothing = register(new LenseTypeDefinition("lense.core.lang.Nothing", Kind.Class, null));

		LenseTypeDefinition any = register(new LenseTypeDefinition("lense.core.lang.Any", Kind.Class, null));
	    register(new LenseTypeDefinition("lense.core.lang.Exception", Kind.Class, any));
	    
//	    SenseTypeDefinition function1 = register(new SenseTypeDefinition("lense.core.lang.Function", Kind.Class, any,
//				new RangeTypeVariable("R", Variance.Invariant, any, nothing)
//		));
//	    
		LenseTypeDefinition function2 = register(new LenseTypeDefinition("lense.core.lang.Function", Kind.Interface, any,
				new RangeTypeVariable("R", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing)
		));
		
		LenseTypeDefinition function3 = register(new LenseTypeDefinition("lense.core.lang.Function", Kind.Interface, any,
				new RangeTypeVariable("R", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing),
				new RangeTypeVariable("T", Variance.Invariant, any, nothing)
		));
		
		LenseTypeDefinition sbool = register(new LenseTypeDefinition("lense.core.lang.Boolean", Kind.Class, any));
		
		sbool.addMethod("negate", sbool);
		
		register(new LenseTypeDefinition("lense.core.lang.Interval", Kind.Class, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing))); 
		
		
		// TODO treat interfaces and traits as attached types
		LenseTypeDefinition iterable = register(new LenseTypeDefinition("lense.core.lang.Iterable", Kind.Interface, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing))); 
		LenseTypeDefinition iterator = register(new LenseTypeDefinition("lense.core.lang.Iterator", Kind.Interface, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing))); 
		
		iterable.addMethod("iterator", iterator); 
		
		iterator.addMethod("hasNext", sbool);
		iterator.addMethod("hasNext", any); 
		
		LenseTypeDefinition maybe = register(new LenseTypeDefinition("lense.core.lang.Maybe", Kind.Class, any, new RangeTypeVariable("T", Variance.Covariant, any,nothing)));  

		maybe.addMethod("map", specify(maybe, any), new MethodParameter(function2)); // TODO return must obey function return
		maybe.addConstructor("",new MethodParameter(any));
		
		
		LenseTypeDefinition none = register(new LenseTypeDefinition("lense.core.lang.None", Kind.Class, specify(maybe, nothing), new IntervalTypeVariable[0]));
		
		none.addField("None", new FixedTypeVariable(none), Imutability.Imutable); // TODO this a Fake static field 
		
		register(new LenseTypeDefinition("lense.core.lang.Some", Kind.Class, maybe));

	
		LenseTypeDefinition character = register(new LenseTypeDefinition("lense.core.lang.Character", Kind.Class, any));
		register(new LenseTypeDefinition("lense.core.lang.Exception", Kind.Class, any));

		register(new LenseTypeDefinition("lense.core.collections.Progression", Kind.Class, iterable));
		LenseTypeDefinition sequence = register(new LenseTypeDefinition("lense.core.collections.Sequence", Kind.Interface, iterable));
		LenseTypeDefinition array = register(new LenseTypeDefinition("lense.core.collections.Array", Kind.Class, sequence));
		
	
		RangeTypeVariable self = new RangeTypeVariable("T", Variance.Covariant, any,nothing);
		LenseTypeDefinition tuple =  register(new LenseTypeDefinition("lense.core.collections.Tuple", Kind.Class,any,
				new RangeTypeVariable("V", Variance.ContraVariant, any,nothing), 
				self
		));
		self.setUpperBound(new FixedTypeVariable(tuple));
		
		tuple.addMethod("tail", any); // TODO any -> T
		tuple.addMethod("head", any); // TODO any -> V
		
		LenseTypeDefinition svoid = register(new LenseTypeDefinition("lense.core.lang.Void", Kind.Class, specify(tuple, nothing, nothing), new IntervalTypeVariable[0]));

		
		register(new LenseTypeDefinition("lense.core.collections.Association", Kind.Class,any,
				new RangeTypeVariable("K", Variance.ContraVariant, any,nothing), 
				new RangeTypeVariable("V", Variance.Covariant, any,nothing)
		));
		
		register(new LenseTypeDefinition("lense.core.collections.Pair", Kind.Interface,any,
				new RangeTypeVariable("K", Variance.ContraVariant, any,nothing), 
				new RangeTypeVariable("V", Variance.Covariant, any,nothing)
		));
		
		
		register(new LenseTypeDefinition("lense.core.io.Console", Kind.Class, any));
		register(new LenseTypeDefinition("lense.core.io.Console", Kind.Object, any));
		
		LenseTypeDefinition binary = register(new LenseTypeDefinition("lense.core.lang.Binary", Kind.Interface, any));
		
		LenseTypeDefinition number = register(new LenseTypeDefinition("lense.core.math.Number", Kind.Class, any));
		LenseTypeDefinition whole = register(new LenseTypeDefinition("lense.core.math.Whole", Kind.Class, number));
		LenseTypeDefinition natural =register(new LenseTypeDefinition("lense.core.math.Natural", Kind.Class, whole));

		tuple.addMethod("get", any, new MethodParameter(natural, "index"));

		sequence.addMethod("get", new MethodReturn( new DeclaringTypeBoundedTypeVariable(sequence, 0, Variance.Covariant)) , new MethodParameter(natural, "index")); 
		array.addMethod("set", svoid  , new MethodParameter(natural, "index"), new MethodParameter(new DeclaringTypeBoundedTypeVariable(array,0, Variance.Invariant), "value")); 
		
		LenseTypeDefinition integer = register(new LenseTypeDefinition("lense.core.math.Integer", Kind.Class, whole));
		LenseTypeDefinition sint = register(new LenseTypeDefinition("lense.core.math.Int32", Kind.Class, integer));
		LenseTypeDefinition slong =register(new LenseTypeDefinition("lense.core.math.Int64", Kind.Class, integer));
		LenseTypeDefinition sshort =register(new LenseTypeDefinition("lense.core.math.Int16", Kind.Class, integer));
		
		sint.addConstructor("valueOf", new MethodParameter(natural));
		sint.addConstructor("valueOf", new MethodParameter(whole));

		integer.addConstructor("valueOf", new MethodParameter(natural));
		integer.addConstructor("valueOf", new MethodParameter(whole));
		
		natural.addMethod("multiply", natural, new MethodParameter(natural));
		natural.addMethod("remainder", natural, new MethodParameter(natural));
		natural.addMethod("plus", natural, new MethodParameter(natural));
		
		natural.addMethod("multiply", integer, new MethodParameter(integer));
		natural.addMethod("remainder", integer, new MethodParameter(integer));
		natural.addMethod("plus", integer, new MethodParameter(integer));
		
		
		integer.addMethod("multiply", integer, new MethodParameter(integer));
		integer.addMethod("remainder", integer, new MethodParameter(integer));
		integer.addMethod("plus", integer, new MethodParameter(integer));
		
		natural.addMethod("negative", sint); // TODO could overflow
		
		sequence.addProperty("size", natural, true, false);

		LenseTypeDefinition string = register(new LenseTypeDefinition("lense.core.lang.String", Kind.Class, specify(sequence, character), new IntervalTypeVariable[0]));

		sint.addConstructor("parse", new MethodParameter(string));
		
		string.addMethod("toMaybe", specify(maybe, string));
		string.addMethod("get", character, new MethodParameter (natural));
		any.addMethod("toString", string);

		LenseTypeDefinition real = register(new LenseTypeDefinition("lense.core.lang.Real", Kind.Class, number));
		
		LenseTypeDefinition decimal = register(new LenseTypeDefinition("lense.core.math.Decimal", Kind.Class, real));
		LenseTypeDefinition sdouble = register(new LenseTypeDefinition("lense.core.math.Double", Kind.Class, decimal));
		LenseTypeDefinition sfloat = register(new LenseTypeDefinition("lense.core.math.Float", Kind.Class, decimal));

		whole.addMethod("toDouble", sdouble);
		whole.addMethod("toFloat", sfloat);
		whole.addMethod("toDecimal", decimal);
		whole.addMethod("toReal", real);
		real.addMethod("toDouble", sdouble);
		real.addMethod("toFloat", sfloat);
		real.addMethod("toDecimal", decimal);
		
		LenseTypeDefinition img = register(new LenseTypeDefinition("lense.core.math.Imaginary", Kind.Class, number));
		LenseTypeDefinition complex = register(new LenseTypeDefinition("lense.core.math.Complex", Kind.Class, number));
		
		LenseTypeDefinition interval = register(new LenseTypeDefinition("lense.core.math.Interval", Kind.Class, any, new RangeTypeVariable("T", Variance.Invariant, any, nothing))); // TODO use Comparable
		
		interval.addMethod("contains", sbool, new MethodParameter(any));
		
		whole.addMethod("plus", complex, new MethodParameter(img));
		whole.addMethod("minus", complex, new MethodParameter(img));
		

		
		LenseTypeDefinition math = register(new LenseTypeDefinition("lense.core.math.Math", Kind.Class, any));
		
		math.addMethod("sin", sdouble, new MethodParameter(sdouble));
		
		LenseTypeDefinition console = register(new LenseTypeDefinition("lense.core.io.Console", Kind.Class, any));
		console.addMethod("println", svoid, new MethodParameter(string));
		
		LenseTypeDefinition version = register(new LenseTypeDefinition("lense.core.lang.Version", Kind.Class, any));

		LenseTypeDefinition packagetype = register(new LenseTypeDefinition("lense.core.lang.reflection.Package", Kind.Interface, any));

		
		LenseTypeDefinition module = register(new LenseTypeDefinition("lense.core.lang.reflection.Module", Kind.Interface, any));
		module.addMethod("getVersion",version);
		module.addMethod("getPackages",specify(sequence, packagetype));
		
		LenseTypeDefinition list = register(new LenseTypeDefinition("lense.core.lang.List", Kind.Interface, sequence));
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
	    	isAssignableTo(target.getUpperbound(), type.getUpperbound());
		} else if (target.getVariance() == Variance.Covariant){
			return isAssignableTo(type.getUpperbound(), target.getUpperbound());
		}
		 
		return isAssignableTo(type.getUpperbound(), target.getUpperbound()) && isAssignableTo(type.getLowerBound(), target.getLowerBound()) ;
	}
	
	public static boolean isAssignableTo(TypeVariable type, TypeVariable target){
		if (type.isSingleType()){
			if (target.isSingleType() ){
				return isAssignableTo (type.getTypeDefinition(), target.getTypeDefinition());
			} else {
				IntervalTypeVariable interval = (IntervalTypeVariable)target;
				// interval contains type ?
				return isAssignableTo(type, interval.getUpperbound()) && isAssignableTo(interval.getLowerBound(),type);
			}
		} else {
			IntervalTypeVariable interval = (IntervalTypeVariable)type;
			if (target instanceof FixedTypeVariable){
				return isAssignableTo(interval.getLowerBound(), interval.getUpperbound()) && isAssignableTo(interval.getUpperbound(), target);
			} else {
				IntervalTypeVariable other = (IntervalTypeVariable)target;
				return isAssignableTo(interval.getLowerBound(), other.getLowerBound()) && isAssignableTo(other.getUpperbound(), interval.getUpperbound());
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
			if (gen.getLowerBound().equals(gen.getUpperbound())){
				throw new CompilationError("Cannot specify a non generic type");
			}
			genericParameters[i] = new RangeTypeVariable(gen.getName(), gen.getVariance(),  genericParametersCapture[i],  genericParametersCapture[i]);
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
	 * @param left
	 * @param right
	 * @return
	 */
	public boolean isPromotableTo(TypeVariable a, TypeVariable b) {
		if (a == null || b == null){
			return false;
		}
		
		if (a == b || a.equals(b)){
			return true;
		} else if (isAssignableTo(a, b)){
			return true;
		} 
		
		if (b instanceof FixedTypeVariable){
			Optional<Constructor> op = ((FixedTypeVariable)b).getTypeDefinition().getConstructorByParameters(new MethodParameter(b));
			
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
	public static boolean isSignatureImplementedBy(MethodSignature signature, CallableMember m) {
		final List<MethodParameter> memberParameters = m.getParameters();
		final List<MethodParameter> signatureParameters = signature.getParameters();
		
		return signature.getName().equals(m.getName()) && areSignatureParametersImplementedBy(signatureParameters,memberParameters);

	}

	public static boolean areSignatureParametersImplementedBy(List<MethodParameter> signatureParameters, List<MethodParameter> memberParameters) {

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



























}
