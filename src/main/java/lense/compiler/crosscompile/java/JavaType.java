/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import compiler.typesystem.Constructor;
import compiler.typesystem.Field;
import compiler.typesystem.GenericTypeParameter;
import compiler.typesystem.Method;
import compiler.typesystem.MethodParameter;
import compiler.typesystem.MethodSignature;
import compiler.typesystem.Property;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeKind;
import compiler.typesystem.TypeMember;


/**
 * 
 */
public class JavaType implements TypeDefinition {

	public static final TypeDefinition Object = new JavaType(Object.class, null);
	public static final TypeDefinition NullType =  new JavaType(Void.class, null);
	public static final TypeDefinition Boolean = new JavaType(Boolean.class, null);
	public static final TypeDefinition String =  new JavaType(String.class, null);
	
	private Class<?> type;
	
	/**
	 * Constructor.
	 * @param type
	 * @param javaTypeResolver
	 */
	public JavaType(Class<?> type, JavaTypeResolver javaTypeResolver) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return type.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSimpleName() {
		return type.getSimpleName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeKind getKind() {
		if (type.isAnnotation()){
			return Kind.Annotation;
		} else if (type.isEnum()){
			return Kind.Enum;
		} else if (type.isInterface()){
			return Kind.Interface;
		} else {
			return Kind.Class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TypeMember> getMembers() {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getSuperDefinition() {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GenericTypeParameter> getGenericParameters() {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Field> getFieldByName(String name) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Property> getPropertyByName(String fieldName) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Method> getMethodsByName(String string) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Method> getMethodBySignature(MethodSignature signature) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Method> getMethodByPromotableSignature(
			MethodSignature signature) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Constructor> getConstructorByParameters(MethodParameter ... parameters) {
		throw new UnsupportedOperationException("Not implememented yet");
	}


}
