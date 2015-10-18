/**
 * 
 */
package lense.compiler.typesystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		return getInstance().getForName("lense.lang.Any").get();
	}

	public static TypeDefinition Nothing(){
		return getInstance().getForName("lense.lang.Nothing").get();
	}

	public static TypeDefinition None(){
		return getInstance().getForName("lense.lang.None").get();
	}

	public static TypeDefinition Maybe(){
		return getInstance().getForName("lense.lang.Maybe", 1).get();
	}

	public static TypeDefinition Progression() {
		return getInstance().getForName("lense.lang.Progression",1).get();
	}

	public static TypeDefinition Boolean() {
		return getInstance().getForName("lense.lang.Boolean").get();
	}

	public static TypeDefinition Void() {
		return getInstance().getForName("lense.lang.Void").get();
	}

	public static TypeDefinition Iterable() {
		return getInstance().getForName("lense.lang.Iterable",1).get();
	}

	public static TypeDefinition Exception() {
		return getInstance().getForName("lense.lang.Exception").get();
	}

	public static TypeDefinition Character() {
		return getInstance().getForName("lense.lang.Character").get();
	}

	public static TypeDefinition String() {
		return getInstance().getForName("lense.lang.String").get();
	}

	public static TypeDefinition Natural() {
		return getInstance().getForName("lense.lang.Natural").get();
	}

	public static TypeDefinition Decimal() {
		return getInstance().getForName("lense.lang.Decimal").get();
	}

	public static TypeDefinition Double() {
		return getInstance().getForName("lense.lang.Double").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Float() {
		return getInstance().getForName("lense.lang.Float").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Imaginary() {
		return getInstance().getForName("lense.lang.Imaginary").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Short() {
		return getInstance().getForName("lense.lang.Short").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Int() {
		return getInstance().getForName("lense.lang.Int").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Long() {
		return getInstance().getForName("lense.lang.Long").get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Function(int count) {
		return getInstance().getForName("lense.lang.Function", count).get();
	}

	/**
	 * @return
	 */
	public static TypeDefinition Whole() {
		return getInstance().getForName("lense.lang.Whole").get();
	}


	public LenseTypeSystem (){
		LenseTypeDefinition nothing = register(new LenseTypeDefinition("lense.lang.Nothing", Kind.Class, null));

		LenseTypeDefinition any = register(new LenseTypeDefinition("lense.lang.Any", Kind.Class, null));
	    register(new LenseTypeDefinition("lense.lang.Exception", Kind.Class, any));
	    
//	    SenseTypeDefinition function1 = register(new SenseTypeDefinition("lense.lang.Function", Kind.Class, any,
//				new GenericDefinition("R", Variance.Invariant, any, nothing)
//		));
//	    
		LenseTypeDefinition function2 = register(new LenseTypeDefinition("lense.lang.Function", Kind.Class, any,
				new GenericDefinition("R", Variance.Invariant, any, nothing),
				new GenericDefinition("T", Variance.Invariant, any, nothing)
		));
		
		LenseTypeDefinition function3 = register(new LenseTypeDefinition("lense.lang.Function", Kind.Class, any,
				new GenericDefinition("R", Variance.Invariant, any, nothing),
				new GenericDefinition("T", Variance.Invariant, any, nothing),
				new GenericDefinition("T", Variance.Invariant, any, nothing)
		));
		
		// TODO treat interfaces and traits as attached types
		LenseTypeDefinition iterable = register(new LenseTypeDefinition("lense.lang.Iterable", Kind.Interface, any, new GenericDefinition("T", Variance.Covariant, any,nothing))); 
		LenseTypeDefinition maybe = register(new LenseTypeDefinition("lense.lang.Maybe", Kind.Class, any, new GenericDefinition("T", Variance.Covariant, any,nothing)));  

		maybe.addMethod("map", specify(maybe, any), new MethodParameter(function2)); // TODO return must obey function return
		maybe.addConstructor(new MethodParameter(any));
		
		LenseTypeDefinition svoid = register(new LenseTypeDefinition("lense.lang.Void", Kind.Class, any));

		LenseTypeDefinition none = register(new LenseTypeDefinition("lense.lang.None", Kind.Class, specify(maybe, nothing), new GenericTypeParameter[0]));
		
		none.addField("None", none, Imutability.Imutable); // TODO this a Fake static field 
		
		register(new LenseTypeDefinition("lense.lang.Some", Kind.Class, maybe));

		LenseTypeDefinition sbool = register(new LenseTypeDefinition("lense.lang.Boolean", Kind.Class, any));
		LenseTypeDefinition character = register(new LenseTypeDefinition("lense.lang.Character", Kind.Class, any));
		register(new LenseTypeDefinition("lense.lang.Exception", Kind.Class, any));

		register(new LenseTypeDefinition("lense.lang.Progression", Kind.Class, iterable));
		LenseTypeDefinition sequence = register(new LenseTypeDefinition("lense.lang.Sequence", Kind.Class, iterable));
		LenseTypeDefinition array = register(new LenseTypeDefinition("lense.lang.Array", Kind.Class, sequence));

		LenseTypeDefinition number = register(new LenseTypeDefinition("lense.lang.Number", Kind.Class, any));
		LenseTypeDefinition whole = register(new LenseTypeDefinition("lense.lang.Whole", Kind.Class, number));
		LenseTypeDefinition natural =register(new LenseTypeDefinition("lense.lang.Natural", Kind.Class, whole));

		LenseTypeDefinition integer = register(new LenseTypeDefinition("lense.lang.Integer", Kind.Class, whole));
		LenseTypeDefinition sint = register(new LenseTypeDefinition("lense.lang.Int", Kind.Class, integer));
		LenseTypeDefinition slong =register(new LenseTypeDefinition("lense.lang.Long", Kind.Class, integer));
		LenseTypeDefinition sshort =register(new LenseTypeDefinition("lense.lang.Short", Kind.Class, integer));
		
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

		LenseTypeDefinition string = register(new LenseTypeDefinition("lense.lang.String", Kind.Class, specify(sequence, character), new GenericTypeParameter[0]));

		string.addMethod("toMaybe", specify(maybe, string));
		string.addMethod("get", character, new MethodParameter (natural));
		any.addMethod("toString", string);

		LenseTypeDefinition real = register(new LenseTypeDefinition("lense.lang.Real", Kind.Class, number));
		
		LenseTypeDefinition decimal = register(new LenseTypeDefinition("lense.lang.Decimal", Kind.Class, real));
		LenseTypeDefinition sdouble = register(new LenseTypeDefinition("lense.lang.Double", Kind.Class, decimal));
		LenseTypeDefinition sfloat = register(new LenseTypeDefinition("lense.lang.Float", Kind.Class, decimal));

		whole.addMethod("toDouble", sdouble);
		whole.addMethod("toFloat", sfloat);
		whole.addMethod("toDecimal", decimal);
		whole.addMethod("toReal", real);
		real.addMethod("toDouble", sdouble);
		real.addMethod("toFloat", sfloat);
		real.addMethod("toDecimal", decimal);
		
		LenseTypeDefinition img = register(new LenseTypeDefinition("lense.lang.Imaginary", Kind.Class, number));
		LenseTypeDefinition complex = register(new LenseTypeDefinition("lense.lang.Complex", Kind.Class, number));
		
		LenseTypeDefinition interval = register(new LenseTypeDefinition("lense.lang.Interval", Kind.Class, any, new GenericDefinition("T", Variance.Invariant, any, nothing))); // TODO use Comparable
		
		interval.addMethod("contains", sbool, new MethodParameter(any));
		
		whole.addMethod("plus", complex, new MethodParameter(img));
		whole.addMethod("minus", complex, new MethodParameter(img));
		

		
		LenseTypeDefinition math = register(new LenseTypeDefinition("lense.lang.Math", Kind.Class, any));
		
		math.addMethod("sin", sdouble, new MethodParameter(sdouble));
		
		LenseTypeDefinition console = register(new LenseTypeDefinition("lense.lang.Console", Kind.Class, any));
		console.addMethod("println", svoid, new MethodParameter(string));

	}

	private Map<TypeKey, LenseTypeDefinition> definitions = new HashMap<>();

	public LenseTypeDefinition register(LenseTypeDefinition definition){
		definitions.put(new TypeKey(definition.getGenericParameters(), definition.getName()), definition);
		return definition;
	}

	public Optional<LenseTypeDefinition> getForName(String name, GenericTypeParameter ... genericTypeParameters){
		
		if (name.endsWith("?")){
			name = name.substring(0, name.length()- 1);
			LenseTypeDefinition type = definitions.get(new TypeKey(Arrays.asList(genericTypeParameters), name));
			if (type == null){
				return Optional.empty();
			}
			
			return Optional.of(specify(Maybe(), type));
		} else {
			return Optional.ofNullable(definitions.get(new TypeKey(Arrays.asList(genericTypeParameters), name)));
		}
		
		
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

	public boolean isAssignableTo(GenericTypeParameter type, GenericTypeParameter target){
	    if (target.getVariance() == Variance.ContraVariant){
	    	isAssignableTo(target.getUpperbound(), type.getUpperbound());
		} else if (target.getVariance() == Variance.Covariant){
			return isAssignableTo(type.getUpperbound(), target.getUpperbound());
		}
		 
		return isAssignableTo(type.getUpperbound(), target.getUpperbound()) && isAssignableTo(type.getLowerBound(), target.getLowerBound()) ;
	}
	
	public boolean isAssignableTo(TypeDefinition type, TypeDefinition target){
		if (target.equals(Any())){
			return true;
		}
		if (type.equals(Nothing())){
			return true;
		}
		if (target.equals(Nothing())){
			return false;
		}
		if (type.equals(Any())){
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
			if (!type.getSuperDefinition().equals(Any()) && !type.getGenericParameters().isEmpty()){
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
	public LenseTypeDefinition specify(TypeDefinition definition, TypeDefinition ... genericParametersCapture) {

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
	public boolean isSignatureImplementedBy(MethodSignature signature, CallableMember m) {
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















}
