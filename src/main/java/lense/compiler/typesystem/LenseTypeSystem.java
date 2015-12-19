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

import lense.compiler.ast.Imutability;

import compiler.CompilationError;
import compiler.typesystem.CallableMember;
import compiler.typesystem.GenericTypeParameter;
import compiler.typesystem.MethodParameter;
import compiler.typesystem.MethodSignature;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeSystem;
import compiler.typesystem.Variance;

/**
 * 
 */
public class LenseTypeSystem implements TypeSystem{

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
		return getInstance().getForName("lense.core.lang.Natural").get();
	}

	public static TypeDefinition Decimal() {
		return getInstance().getForName("lense.core.lang.Decimal").get();
	}

	public static TypeDefinition Double() {
		return getInstance().getForName("lense.core.lang.Double").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Float() {
		return getInstance().getForName("lense.core.lang.Float").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Imaginary() {
		return getInstance().getForName("lense.core.lang.Imaginary").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Short() {
		return getInstance().getForName("lense.core.lang.Short").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Int() {
		return getInstance().getForName("lense.core.lang.Int").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Long() {
		return getInstance().getForName("lense.core.lang.Long").get();
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
//				new GenericDefinition("R", Variance.Invariant, any, nothing)
//		));
//	    
		LenseTypeDefinition function2 = register(new LenseTypeDefinition("lense.core.lang.Function", Kind.Interface, any,
				new GenericDefinition("R", Variance.Invariant, any, nothing),
				new GenericDefinition("T", Variance.Invariant, any, nothing)
		));
		
		LenseTypeDefinition function3 = register(new LenseTypeDefinition("lense.core.lang.Function", Kind.Interface, any,
				new GenericDefinition("R", Variance.Invariant, any, nothing),
				new GenericDefinition("T", Variance.Invariant, any, nothing),
				new GenericDefinition("T", Variance.Invariant, any, nothing)
		));
		
		// TODO treat interfaces and traits as attached types
		LenseTypeDefinition iterable = register(new LenseTypeDefinition("lense.core.lang.Iterable", Kind.Interface, any, new GenericDefinition("T", Variance.Covariant, any,nothing))); 
		LenseTypeDefinition maybe = register(new LenseTypeDefinition("lense.core.lang.Maybe", Kind.Class, any, new GenericDefinition("T", Variance.Covariant, any,nothing)));  

		maybe.addMethod("map", specify(maybe, any), new MethodParameter(function2)); // TODO return must obey function return
		maybe.addConstructor(new MethodParameter(any));
		
		
		LenseTypeDefinition none = register(new LenseTypeDefinition("lense.core.lang.None", Kind.Class, specify(maybe, nothing), new GenericTypeParameter[0]));
		
		none.addField("None", none, Imutability.Imutable); // TODO this a Fake static field 
		
		register(new LenseTypeDefinition("lense.core.lang.Some", Kind.Class, maybe));

		LenseTypeDefinition sbool = register(new LenseTypeDefinition("lense.core.lang.Boolean", Kind.Class, any));
		LenseTypeDefinition character = register(new LenseTypeDefinition("lense.core.lang.Character", Kind.Class, any));
		register(new LenseTypeDefinition("lense.core.lang.Exception", Kind.Class, any));

		register(new LenseTypeDefinition("lense.core.collections.Progression", Kind.Class, iterable));
		LenseTypeDefinition sequence = register(new LenseTypeDefinition("lense.core.collections.Sequence", Kind.Interface, iterable));
		LenseTypeDefinition array = register(new LenseTypeDefinition("lense.core.collections.Array", Kind.Class, sequence));

		GenericDefinition self = new GenericDefinition("T", Variance.Covariant, any,nothing);
		LenseTypeDefinition tuple =  register(new LenseTypeDefinition("lense.core.collections.Tuple", Kind.Class,any,
				new GenericDefinition("V", Variance.ContraVariant, any,nothing), 
				self
		));
		self.setUpperBound(tuple);
		
		tuple.addMethod("tail", any); // TODO any -> T
		tuple.addMethod("head", any); // TODO any -> V
		
		LenseTypeDefinition svoid = register(new LenseTypeDefinition("lense.core.lang.Void", Kind.Class, specify(tuple, nothing, nothing), new GenericTypeParameter[0]));

	
		register(new LenseTypeDefinition("lense.core.collections.Association", Kind.Class,any,
				new GenericDefinition("K", Variance.ContraVariant, any,nothing), 
				new GenericDefinition("V", Variance.Covariant, any,nothing)
		));
		
		register(new LenseTypeDefinition("lense.core.collections.Pair", Kind.Interface,any,
				new GenericDefinition("K", Variance.ContraVariant, any,nothing), 
				new GenericDefinition("V", Variance.Covariant, any,nothing)
		));
		
		
		register(new LenseTypeDefinition("lense.core.io.Console", Kind.Class, any));
		register(new LenseTypeDefinition("lense.core.io.Console", Kind.Object, any));
		
		LenseTypeDefinition number = register(new LenseTypeDefinition("lense.core.lang.Number", Kind.Class, any));
		LenseTypeDefinition whole = register(new LenseTypeDefinition("lense.core.lang.Whole", Kind.Class, number));
		LenseTypeDefinition natural =register(new LenseTypeDefinition("lense.core.lang.Natural", Kind.Class, whole));

		tuple.addMethod("get", any, new MethodParameter(natural, "index"));
		sequence.addMethod("get", any , new MethodParameter(natural, "index")); // TODO covariant return
		
		LenseTypeDefinition integer = register(new LenseTypeDefinition("lense.core.lang.Integer", Kind.Class, whole));
		LenseTypeDefinition sint = register(new LenseTypeDefinition("lense.core.lang.Int", Kind.Class, integer));
		LenseTypeDefinition slong =register(new LenseTypeDefinition("lense.core.lang.Long", Kind.Class, integer));
		LenseTypeDefinition sshort =register(new LenseTypeDefinition("lense.core.lang.Short", Kind.Class, integer));
		
		whole.addMethod("toInt", sint);
		whole.addMethod("toLong", slong);
		whole.addMethod("toShort", sshort);
		
		integer.addMethod("toInt", sint);
		integer.addMethod("toLong", slong);
		integer.addMethod("toShort", sshort);
		
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
		
		sequence.addField("size", natural, Imutability.Mutable); // TODO should be Property

		LenseTypeDefinition string = register(new LenseTypeDefinition("lense.core.lang.String", Kind.Class, specify(sequence, character), new GenericTypeParameter[0]));

		string.addMethod("toMaybe", specify(maybe, string));
		string.addMethod("get", character, new MethodParameter (natural));
		any.addMethod("toString", string);

		LenseTypeDefinition real = register(new LenseTypeDefinition("lense.core.lang.Real", Kind.Class, number));
		
		LenseTypeDefinition decimal = register(new LenseTypeDefinition("lense.core.lang.Decimal", Kind.Class, real));
		LenseTypeDefinition sdouble = register(new LenseTypeDefinition("lense.core.lang.Double", Kind.Class, decimal));
		LenseTypeDefinition sfloat = register(new LenseTypeDefinition("lense.core.lang.Float", Kind.Class, decimal));

		whole.addMethod("toDouble", sdouble);
		whole.addMethod("toFloat", sfloat);
		whole.addMethod("toDecimal", decimal);
		whole.addMethod("toReal", real);
		real.addMethod("toDouble", sdouble);
		real.addMethod("toFloat", sfloat);
		real.addMethod("toDecimal", decimal);
		
		LenseTypeDefinition img = register(new LenseTypeDefinition("lense.core.lang.Imaginary", Kind.Class, number));
		LenseTypeDefinition complex = register(new LenseTypeDefinition("lense.core.lang.Complex", Kind.Class, number));
		
		LenseTypeDefinition interval = register(new LenseTypeDefinition("lense.core.lang.Interval", Kind.Class, any, new GenericDefinition("T", Variance.Invariant, any, nothing))); // TODO use Comparable
		
		interval.addMethod("contains", sbool, new MethodParameter(any));
		
		whole.addMethod("plus", complex, new MethodParameter(img));
		whole.addMethod("minus", complex, new MethodParameter(img));
		

		
		LenseTypeDefinition math = register(new LenseTypeDefinition("lense.core.lang.Math", Kind.Class, any));
		
		math.addMethod("sin", sdouble, new MethodParameter(sdouble));
		
		LenseTypeDefinition console = register(new LenseTypeDefinition("lense.core.io.Console", Kind.Class, any));
		console.addMethod("println", svoid, new MethodParameter(string));
		
		LenseTypeDefinition version = register(new LenseTypeDefinition("lense.core.lang.Version", Kind.Class, any));

		LenseTypeDefinition packagetype = register(new LenseTypeDefinition("lense.core.lang.reflection.Package", Kind.Interface, any));

		
		LenseTypeDefinition module = register(new LenseTypeDefinition("lense.core.lang.reflection.Module", Kind.Interface, any));
		module.addMethod("getVersion",version);
		module.addMethod("getPackages",specify(sequence, packagetype));
		
		LenseTypeDefinition list = register(new LenseTypeDefinition("lense.core.lang.List", Kind.Class, sequence));
		list.addMethod("add",svoid, new MethodParameter(any));

	}

	private Map<TypeKey, LenseTypeDefinition> definitions = new HashMap<>();

	public LenseTypeDefinition register(LenseTypeDefinition definition){
		definitions.put(new TypeKey(definition.getGenericParameters(), definition.getName()), definition);
		return definition;
	}

	public Optional<LenseTypeDefinition> getForName(String name, GenericTypeParameter ... genericTypeParameters){
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

	public static boolean isAssignableTo(GenericTypeParameter type, GenericTypeParameter target){
	    if (target.getVariance() == Variance.ContraVariant){
	    	isAssignableTo(target.getUpperbound(), type.getUpperbound());
		} else if (target.getVariance() == Variance.Covariant){
			return isAssignableTo(type.getUpperbound(), target.getUpperbound());
		}
		 
		return isAssignableTo(type.getUpperbound(), target.getUpperbound()) && isAssignableTo(type.getLowerBound(), target.getLowerBound()) ;
	}
	
	public static boolean isAssignableTo(TypeDefinition type, TypeDefinition target){
		if (type == null || target == null){
			return false;
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

		if (type.getSuperDefinition() != null){
			if (!type.getSuperDefinition().getName().equals("lense.core.lang.Any") && !type.getGenericParameters().isEmpty()){
				TypeDefinition[] types = new TypeDefinition[type.getGenericParameters().size()];
				for(int i = 0; i < types.length; i++){
					types[i] = type.getGenericParameters().get(i).getUpperbound();
				}
				return isAssignableTo(specify(type.getSuperDefinition(), types), target);
			} else {
				return isAssignableTo(type.getSuperDefinition(), target);
			}
		}

		return false;

		//return type.equals(Nothing()) || ( type.getName() == target.getName() && type.getGenericParameters().size() == target.getGenericParameters().size());
	}

	/**
	 * @param progression
	 * @param finalType
	 * @return
	 */
	public  static LenseTypeDefinition specify(TypeDefinition definition, TypeDefinition ... genericParametersCapture) {

		if (definition.getGenericParameters().size() != genericParametersCapture.length){
			throw new CompilationError("Wrong number of generic arguments for type " + definition + ". Expected " + definition.getGenericParameters().size() + " found " + genericParametersCapture.length);
		}
		GenericTypeParameter[] genericParameters = new GenericTypeParameter[definition.getGenericParameters().size()];

		for (int i =0; i < definition.getGenericParameters().size(); i++){
			GenericTypeParameter gen = definition.getGenericParameters().get(i);
			genericParameters[i] = new FixedGenericTypeParameter(gen.getName(), genericParametersCapture[i], gen.getVariance() ); 
		}

		LenseTypeDefinition concrete = new LenseTypeDefinition(
				definition.getName(),
				definition.getKind(), 
				(LenseTypeDefinition)definition.getSuperDefinition(),
				genericParameters);

		concrete.addMembers(definition.getMembers().stream().map(m -> m.changeDeclaringType(concrete))); 

		return concrete;
	}

	/**
	 * @param left
	 * @param right
	 * @return
	 */
	public boolean isPromotableTo(TypeDefinition a, TypeDefinition b) {
		if (a == null || b == null){
			return false;
		}
		
		if (a == b || a.equals(b)){
			return true;
		} else if (isAssignableTo(a, b)){
			return true;
		} else if (!a.getMethodsByName("to" + b.getSimpleName()).isEmpty()){
			return true;
		} 
		 
		return b.getConstructorByParameters(new MethodParameter(b)).isPresent();
	}

	/**
	 * @param typeDefinition
	 * @param typeDefinition2
	 * @return
	 */
	public TypeDefinition unionOf(TypeDefinition a, TypeDefinition b) {
		if (a ==b || a.equals(b)){
			return a;
		} else {
			return new UnionType(a, b);
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
		if (signature.getName().equals(m.getName()) &&  signatureParameters.size() == memberParameters.size()){
			
			for (int i = 0; i < signatureParameters.size(); i++ ){
				if (!isAssignableTo(signatureParameters.get(i).getType().getUpperbound(), memberParameters.get(i).getType().getUpperbound())){
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
				if (!isAssignableTo(from.getParameters().get(i).getType().getUpperbound(), to.getParameters().get(i).getType().getUpperbound())){
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
				if (!isPromotableTo(from.getParameters().get(i).getType().getUpperbound(), to.getParameters().get(i).getType().getUpperbound())){
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
	@Override
	public TypeDefinition MostUpperType() {
		return Any();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
