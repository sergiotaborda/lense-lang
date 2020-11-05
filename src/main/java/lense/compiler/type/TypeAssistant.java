package lense.compiler.type;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeMatch;
import lense.compiler.typesystem.Visibility;

public interface TypeAssistant {

	public boolean areNomallyEquals(TypeDefinition a, TypeDefinition b);
	
	public boolean areNomallyEquals(TypeVariable a, TypeVariable b);


	boolean isAny(TypeDefinition type);

	boolean isMaybe(TypeVariable type);

	boolean isBoolean(TypeVariable type);

	boolean isNumber(TypeVariable type);
	
	boolean isNumber(TypeDefinition type);
	
	TypeVariable unionOf(TypeVariable typeVariable, TypeVariable typeVariable2);

	boolean isTuple(TypeVariable type, int tupleCount);

	TypeMatch isAssignableTo(TypeVariable typeVariable, TypeVariable maybe);

	boolean isNothing(TypeVariable typeVariable);

	boolean isPromotableTo(TypeVariable type, TypeVariable target);

    boolean isSignaturePromotableTo(MethodSignature from, MethodSignature to);
    
    TypeMatch isAssignableTo(TypeDefinition type, TypeDefinition target);
    
	boolean isMethodImplementedBy(Method reference, Method candidate);
	
    boolean isSignatureAssignableTo(MethodSignature from, MethodSignature to);
    
    <M extends CallableMember<M>> TypeMatch areSignatureParametersImplementedBy(
			List<CallableMemberMember<M>> signatureParameters, List<CallableMemberMember<M>> memberParameters);

	public TypeDefinition specify(TypeVariable definition, TypeVariable... genericParametersCapture);



	public TypeDefinition specify(TypeDefinition definition, List<TypeVariable> genericParameters);

	public TypeDefinition specify(TypeDefinition definition, TypeVariable... genericParameters);


	public List<Match<Constructor>> getConstructorByName(TypeDefinition type,String constructorName , ConstructorParameter... parameters);

    public List<Match<Constructor>> getConstructorByParameters(TypeDefinition type,ConstructorParameter... parameters);
    

    public List<Match<Constructor>> getConstructorByParameters(TypeDefinition type,Visibility visibility , ConstructorParameter... parameters);
	

	public Optional<Constructor> getConstructorByNameAndPromotableParameters(TypeDefinition type,String name , ConstructorParameter... parameters);


    public Optional<Constructor> getConstructorByPromotableParameters(TypeDefinition type,ConstructorParameter... parameters);

	public Optional<Constructor> getConstructorByImplicitAndPromotableParameters(TypeDefinition type,boolean implicit, ConstructorParameter... parameters);

    public Collection<Method> getMethodsByName(TypeDefinition type,String name);
    
    /**
     * used.
     * 
     * @param type
     * @param signature
     * @return
     */
    public Optional<Method> getDeclaredMethodBySignature(TypeDefinition type,MethodSignature signature);

    public Optional<Method> getMethodBySignature(TypeDefinition type,MethodSignature signature);


    public Optional<Method> getMethodByPromotableSignature(TypeDefinition type,MethodSignature signature);

    public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeDefinition type, TypeVariable[] params);



}
