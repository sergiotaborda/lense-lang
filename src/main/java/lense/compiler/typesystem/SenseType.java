package lense.compiler.typesystem;
///**
// * 
// */
//package lense.compiler.typesystem;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import lense.compiler.SenseTypeResolver;
//import lense.compiler.ast.Imutability;
//import compiler.typesystem.BoundedTypeParameter;
//import compiler.typesystem.BoxedClassBoundedTypeParameter;
//import compiler.typesystem.CapturableTypeParameter;
//import compiler.typesystem.ClassBoundedTypeParameter;
//import compiler.typesystem.ConcreteTypeParameter;
//import compiler.typesystem.Field;
//import compiler.typesystem.InherintedTypeParameter;
//import compiler.typesystem.Method;
//import compiler.typesystem.MethodDeclaringTypeParameter;
//import compiler.typesystem.MethodFreeBoxedTypeParameter;
//import compiler.typesystem.MethodFreeTypeParameter;
//import compiler.typesystem.MethodParameter;
//import compiler.typesystem.MethodReturn;
//import compiler.typesystem.MethodSignature;
//import compiler.typesystem.Property;
//import compiler.typesystem.Type;
//import compiler.typesystem.TypeVariable;
//import compiler.typesystem.Variance;
//
///**
// * 
// */
//public class SenseType implements Type {
//
//
//	public static final Type Nothing = new NothingType();
//	public static final SenseType Any = new SenseType("sense.Any", null);
//	public static final SenseType Exception = new SenseType("sense.Exception", Any);
//	public static final Type Void = new SenseType("sense.Void", Any);
//
//	public static final Type Function1 = new SenseType("sense.Function1", Any).withGenericParameter(Variance.Invariant,"R").withGenericParameter(Variance.Invariant,"T");
//	
//	
//	public static final SenseType Boolean = new SenseType("sense.Boolean", Any);
//	public static final SenseType Number = new SenseType("sense.Number", Any);
//	public static final SenseType Whole = new SenseType("sense.Whole", Number);
//	public static final SenseType Int = new SenseType("sense.Int", Whole);
//	public static final SenseType Natural = new SenseType("sense.Natural", Whole).addMethod("toInt", Int);
//	public static final SenseType Short = new SenseType("sense.Short", Whole).addMethod("toInt", Int);
//
//	
//	public static final SenseType Long = new SenseType("sense.Long",Whole);
//	
//	public static final SenseType Complex = new SenseType("sense.Complex", Number);
//	public static final SenseType Imaginary = new SenseType("sense.Imaginary", Complex);
//	
//	public static final SenseType Real = new SenseType("sense.Real", Complex);
//
//	public static final SenseType Decimal = new SenseType("sense.Decimal",Real);
//	
//	public static final SenseType FloatingPointReal = new SenseType("sense.FloatingPointReal", Real);
//	
//	public static final SenseType Double = new SenseType("sense.Double", FloatingPointReal);
//	public static final SenseType Float = new SenseType("sense.Float", FloatingPointReal).addMethod("toDouble", Double);
//	
//	public static final SenseType Rational = new SenseType("sense.Rational", Real);
//	public static final SenseType Character =  new SenseType("sense.Character", Any);
//	
//	public static final SenseType Iterable = new SenseType("sense.Iterable",Kind.Interface, Any).withGenericParameter(Variance.Covariant,"T");
//	public static final SenseType Sequence = new SenseType("sense.Sequence",Kind.Interface, Iterable)
//	.withInheritGenericParameter(Variance.Covariant, 0)
//	.addField("size", Natural, Imutability.Imutable);
//	
//	public static final SenseType String = new SenseType("sense.String", Sequence.of(Character));
//	
//	public static final SenseType Array = new SenseType("sense.Array", Sequence)
//	.withInheritGenericParameter(Variance.Invariant,0);
//	
//	public static final SenseType Progression =  new SenseType("sense.Progression",Kind.Interface, Sequence)
//	.withInheritGenericParameter(Variance.Covariant,0);
//
//	public static final SenseType Maybe = new SenseType("sense.Maybe", Any).withGenericParameter(Variance.Invariant,"T");
//	public static final SenseType Some = new SenseType("sense.Some", Maybe)
//	.withInheritGenericParameter(Variance.Covariant,0);	
//	
//	public static final SenseType None = new SenseType("sense.None", Maybe.of(Nothing));	
//	
//	public static final SenseType Interval = new SenseType("sense.Interval", Any).withGenericParameter(Variance.Invariant,"T");
//			
//
//	public static final SenseType Console = new SenseType("sense.Console", Any).addMethod("println", Void,new MethodParameter(Any, "obj"));
//	
//	static {
//		 Any.addMethod("toString", String);
//		 
//		 TypeVariable R = new CapturableTypeParameter("R");
//		 Method map = new Method("map", null );
//		 TypeVariable T = new MethodDeclaringTypeParameter(map, 0);
//		 BoxedClassBoundedTypeParameter boxed = new BoxedClassBoundedTypeParameter("mapper", Function1 , R , T);
//			
//		 
//		 map.setReturn(new MethodReturn(new BoxedClassBoundedTypeParameter("mapper", Maybe,  new MethodFreeBoxedTypeParameter(map, 0, 0))));
//		 map.getParameters().add(new MethodParameter(boxed, "it"));
//		 
//		 Maybe.addMethod(map);
//		 
//		 String.addMethod("get", String, new MethodParameter(Natural) );
//		 String.addMethod("toMaybe", Maybe.of(String));
//		 String.addMethod("size", Natural);
//		 
//		 Natural.addMethod("multiply", Int, new MethodParameter(Int) );
//		 Natural.addMethod("multiply", Long, new MethodParameter(Long) );
//		 Natural.addMethod("toLong", Long );
//		 Natural.addMethod("toDecimal", Decimal );
//		 Natural.addMethod("toReal", Real );
//		 Natural.addMethod("toFloat", Float );
//		 Natural.addMethod("negative", Int );
//		 Natural.addMethod("plus", Complex ,  new MethodParameter(Imaginary) );
//		 Natural.addMethod("minus", Complex ,  new MethodParameter(Imaginary) );
//		 
//		 Long.addMethod("remainder", Long, new MethodParameter(Int) );
//		 Double.addMethod("remainder", Double, new MethodParameter(Double) );
//		 
//		 Int.addMethod("plus", Long, new MethodParameter(Long) );
//		 
//		 Interval.addMethod("contains", Boolean, new MethodParameter( new ClassBoundedTypeParameter(Interval,0,Variance.ContraVariant), "candidade"));
//		 
//		 None.addField("None", None,Imutability.Imutable);
//		
//		 
//		 Sequence.addMethod("get" , new ClassBoundedTypeParameter(Sequence , 0, Variance.Covariant),  new MethodParameter(Natural));
//		 
//		 for(java.lang.reflect.Field f : SenseType.class.getFields()){
//			try {
//				SenseType t = (SenseType) f.get(null);
//				SenseTypeResolver.getInstance().registerType(t.getName(), t);
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			 
//		 }
//	}
//	
//	private String name;
//	private String simpleName;
//	private SenseType superType;
//	private List<Method> methods = new ArrayList<>();
//	private List<Field> fields = new ArrayList<>();
//	private List<Property> properties = new ArrayList<>();
//	private List<TypeVariable> parameters  = new ArrayList<>(0);
//	private Kind kind;
//
//	/**
//	 * Constructor.
//	 * @param string
//	 */
//	public SenseType(String name) {
//		this.name = name;
//		
//	}
//
//	/**
//	 * Constructor.
//	 * @param string2
//	 * @param number2
//	 */
//	public SenseType(String name, SenseType superType) {
//		this(name,  Kind.Class,superType);
//	}
//
//	public SenseType(String name, Kind kind, SenseType superType) {
//		this(name);
//		this.superType = superType;
//		this.kind = kind;
//		
//	}
//	
//	/**
//	 * Copy Constructor.
//	 * @param senseType
//	 */
//	protected SenseType(SenseType other) {
//		this(other.name,other.kind,other.superType);
//		
//		//this.methods = other.methods.stream().map(m -> m.replicate(this)).collect(Collectors.toList());
//		this.fields = other.fields.stream().map(m -> m.replicate(this)).collect(Collectors.toList());;
//		this.properties = other.properties.stream().map(m -> m.replicate(this)).collect(Collectors.toList());
//		this.parameters = other.parameters.stream().map(m -> m.replicate(this)).collect(Collectors.toList());
//	}
//	
//	/**
//	 * Constructor.
//	 * @param senseType
//	 * @param concreteTypes
//	 */
//	public SenseType(SenseType other, TypeVariable[] concreteTypes) {
//		
//		this.name = other.name;
//		this.kind = other.kind;
//		
//		if (other.superType != null){
//			this.superType = new SenseType((SenseType)other.superType, concreteTypes);
//		}
//	
//		
//		//this.methods = other.methods.stream().map(m -> m.replicate(this)).collect(Collectors.toList());
//		this.fields = other.fields.stream().map(m -> m.replicate(this)).collect(Collectors.toList());;
//		this.properties = other.properties.stream().map(m -> m.replicate(this)).collect(Collectors.toList());
//		
//		int i = 0;
//		for (TypeVariable tp : other.parameters){
//			tp = tp.replicate(this);
//			this.parameters.add(tp.setConcrete(concreteTypes[i]));	
//			i++;
//		}
//	
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 * 
//	 * Fixate the given type parameter index to the given type in the super class producing another classe.
//	 * Then, make the new super class the super type of this type.
//	 */
//	@Override
//	public void constraintSuperType(TypeVariable type, int superTypeParameterIndex) {
//		
//		if (this.superType!= null){
//			SenseType concrete = new SenseType(this.superType);
//			
//			TypeVariable t = concrete.getParameters().get(superTypeParameterIndex);
//			
//			concrete.getParameters().set(superTypeParameterIndex, t.setConcrete(type));
//			
//			
//			this.superType = concrete;
//		}
//	
//	}
//
//	
//
//	/**
//	 * @param superType2
//	 */
//	public void setSuperType(Type superType) {
//		this.superType = (SenseType)superType;
//	}
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public String getName(){
//		return name;
//	}
//
//
//	public boolean isGeneric(){
//		return !parameters.isEmpty();
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public TypeMatch matchAssignableTo (Type other){
//		if (this.equals(other)){
//			return TypeMatch.Exact;
//		} else if (this.isAssignableTo(other)){
//			return TypeMatch.UpCast;
//		} else if (this.isPromotableTo(other)){
//			return TypeMatch.Promote;
//		} else {
//			return TypeMatch.NoMatch;
//		}
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Type getSuperType() {
//		return superType;
//	}
//	
//	public SenseType addMethod(java.lang.String name,Type returningType, MethodParameter ... parameters) {
//		return addMethod(name, new ConcreteTypeParameter(returningType), parameters);
//		
//	}
//	/**
//	 * @param string2
//	 * @return
//	 */
//	public SenseType addMethod(java.lang.String name,TypeVariable returningType, MethodParameter ... parameters) {
//		
//		this.addMethod(new Method(name , new MethodReturn(returningType), parameters));
//		return this;
//	}
//	
//	public SenseType addMethod(Method method) {
//		
//		method.setDeclaringType(this);
//		this.methods.add(method);
//		return this;
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public SenseType of(Type ... parameterType){
//		if (!this.isGeneric()){
//			throw new IllegalStateException("Type " + this.getName() + " is not generic");
//		}
//		
//		if (this.parameters.size() != parameterType.length){
//			throw new IllegalStateException("Type " + this.getName() + " has " + this.parameters.size() + " generic types, not " +  parameterType.length);
//		}
//	
//		TypeVariable[] parameters = new ConcreteTypeParameter[parameterType.length];
//	
//		for (int i =0; i< parameters.length; i++){
//			parameters[i] = new ConcreteTypeParameter(parameterType[i]);
//		}
//		return  of(parameters);
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public SenseType of(TypeVariable... parameterType) {
//		if (!this.isGeneric()){
//			throw new IllegalStateException("Type " + this.getName() + " is not generic");
//		}
//		
//		if (this.parameters.size() != parameterType.length){
//			throw new IllegalStateException("Type " + this.getName() + " has " + this.parameters.size() + " generic types, not " +  parameterType.length);
//		}
//	
//		return new SenseType(this, parameterType);
//	}
//
//	
//	public SenseType withGenericParameter(Variance variance, String parameterTypeName){
//		SenseType dev = new SenseType(this);
//		dev.addParameter(variance, parameterTypeName, SenseType.Any, SenseType.Nothing);
//		return dev;
//	}
//	
//	public SenseType withInheritGenericParameter(Variance variance, int superParameterIndex){
//		SenseType dev = new SenseType(this);
//
//		dev.addParameter(new InherintedTypeParameter(dev.superType, superParameterIndex, variance));
//		return dev;
//	}
//	
//	public SenseType withDefineInheritGenericParameter(Variance variance, int superParameterIndex, Type type){
//		SenseType dev = new SenseType(this);
//		
//		SenseType s = new SenseType((SenseType)dev.superType);
//		
//		s.parameters.set(superParameterIndex, new ConcreteTypeParameter(type));
//		
//		dev.superType = s;
//
//		return dev;
//	}
//	
//	/**
//	 * @param g
//	 */
//	private void addParameter(Variance variance, String parameterTypeName, Type upperBound, Type lowerBound) {
//		addParameter(new BoundedTypeParameter(variance,parameterTypeName, upperBound, lowerBound ));
//	}
//	
//	private void addParameter(TypeVariable parameter) {
//		parameters.add(parameter);
//	}
//	
//	public String toString(){
//		if ( parameters.isEmpty() ){
//			return name;
//		}
//		
//		StringBuilder builder = new StringBuilder(name).append("<");
//		
//		for (TypeVariable p : parameters){
//			builder.append(p.getName().toString());
//			if(!p.getUpperbound().equals(SenseType.Any)){
//				builder.append(" extends ").append(p.getUpperbound().getSimpleName());
//			}
//			if(p.getLowerBound() != null && !p.getLowerBound().equals(SenseType.Nothing)){
//				builder.append(" super ").append(p.getLowerBound().getSimpleName());
//			}
//			builder.append(",");
//		}
//		builder.deleteCharAt(builder.length()- 1);
//		return builder.append(">").toString();
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Type or(Type other) {
//		return new UnionType(this, other);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public boolean isAssignableTo(Type type) {
//	
//		if (type.equals(Any)){
//			return true;
//		}
//		if (this.equals(Nothing)){
//			return true;
//		}
//		if (type.equals(Nothing)){
//			return false;
//		}
//		if (this.equals(Any)){
//			return false;
//		}
//		
//		
//		if (type instanceof SenseType){
//			SenseType other = (SenseType)type;
//			
//			if (this.name.equals( other.name) && other.parameters.size() == 0 ){
//				return true;
//			} else if( this.name.equals( other.name) 
//					&& this.parameters.size() == other.parameters.size()){
//				
//				for(int i =0; i < this.parameters.size();i++){
//					// TODO consider variance and lowerbound
//					if (!this.parameters.get(i).getUpperbound().isAssignableTo(other.parameters.get(i).getUpperbound())){
//						return false;
//					}
//				}
//				
//				return true;
//			}
//			
//			if (this.superType != null){
//				if (!superType.equals(Any) && this.isGeneric()){
//					Type[] types = new Type[this.parameters.size()];
//					for(int i = 0; i < types.length; i++){
//						types[i] = this.parameters.get(i).getUpperbound();
//					}
//					return this.superType.of(types).isAssignableTo(type);
//				} else {
//					return this.superType.isAssignableTo(type);
//				}
//			}
//		}
//		
//		
//		return false;
//	}
//
//
//	public int hashCode(){
//		return name.hashCode();
//	}
//	
//	public boolean equals(Object other){
//		return other instanceof SenseType && this.equals(((SenseType)other));
//	}
//	
//	public boolean equals(SenseType other){
//		if( this.name.equals( other.name) 
//				&& this.parameters.size() == other.parameters.size()){
//			
//			for(int i =0; i < this.parameters.size();i++){
//				if (!this.parameters.get(i).equals(other.parameters.get(i))){
//					return false;
//				}
//			}
//			return true;
//		}
//		return false;
//
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<Method> getDeclaredMethods(String name) {
//		return methods.stream().filter(m -> m.getName().equals(name)).collect(Collectors.toList());
//	}
//	 
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<Method> getAvailableMethods(String name) {
//		List<Method> list = getDeclaredMethods(name);
//		if (list.isEmpty() && superType != null && this != superType){
//			return superType.getAvailableMethods(name);
//		}
//		return list;
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<Field> getAvailableFields(java.lang.String fieldName) {
//		List<Field> list = getDeclaredFields(fieldName);
//		if (list.isEmpty() && superType != null && this != superType){
//			return superType.getAvailableFields(fieldName);
//		}
//		return list;
//	}
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<Field> getDeclaredFields(java.lang.String fieldName) {
//		return fields.stream().filter(m -> m.getName().equals(fieldName)).collect(Collectors.toList());
//	}
//	
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public boolean isPromotableTo(Type type) {
//		if (this == type || this.equals(type)){
//			return true;
//		} else if (this.isAssignableTo(type)){
//			return true;
//		}
//		return getAvailableMethods("to" + type.getSimpleName()).size() > 0;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public java.lang.String getSimpleName() {
//		if (simpleName == null){
//			String[] s = this.name.split("\\.");
//			simpleName = s[s.length - 1];
//		}
//		return simpleName;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<TypeVariable> getParameters(){
//		return parameters;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public boolean isPrimitive() {
//		return false;
//	}
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Kind getKind() {
//		return kind;
//	}
//
//	/**
//	 * @param kind2
//	 */
//	public void setKind(Kind kind) {
//		this.kind = kind;
//	}
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Optional<Field> getAppropriateField(java.lang.String name) {
//		List<Field> f = getAvailableFields(name);
//		return f.isEmpty() ? Optional.empty() : Optional.of(f.get(0));
//	}
//	/**
//	 * @param name2
//	 * @param type
//	 * @param imutabilityValue
//	 */
//	public SenseType addField(String name, Type type, Imutability imutabilityValue) {
//		fields.add(new Field(this, name, type, imutabilityValue));
//		return this;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<Property> getAvailableProperties(java.lang.String fieldName) {
//		List<Property> list = getDeclaredProperties(fieldName);
//		if (list.isEmpty() && superType != null && this != superType){
//			return superType.getAvailableProperties(fieldName);
//		}
//		return list;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<Property> getDeclaredProperties(java.lang.String name) {
//		return properties.stream().filter(m -> m.getName().equals(name)).collect(Collectors.toList());
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Optional<Property> getAppropriateProperty(java.lang.String name) {
//		List<Property> f = getAvailableProperties(name);
//		return f.isEmpty() ? Optional.empty() : Optional.of(f.get(0));
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Type bindMethodGenerics(MethodSignature signature) {
//		
//		Method m = this.getAppropriateMethod(signature).get();
//		
//		SenseType newType = new SenseType(this);
//		
//		newType.methods.remove(m);
//		
//		Method newMethod = new Method (m.getName(), null);
//		
//		if (m.getReturningType() instanceof MethodFreeTypeParameter){
//		//	newMethod.setReturn( new MethodReturn((MethodFreeTypeParameter)m.getReturningType().bindGenerics(signature)));
//		}
//		
//		for(MethodParameter mp : m.getParameters()){
//			TypeVariable tp = mp.getType();
//			
//			MethodParameter newParam = new MethodParameter(tp, mp.getName());
//			newMethod.getParameters().add(newParam);
//		}
//		
//		newType.addMethod(newMethod);
//		
//		return newType;
//	}
//
//
//
//
//
//
//}
