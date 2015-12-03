/**
 * 
 */
package lense.compiler.typesystem;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import lense.compiler.ast.QualifiedNameNode;
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
public class ProtoTypeDefinition implements TypeDefinition {

	private String name;
	private TypeKind kind;
	private List<GenericTypeParameter> genericTypeParameter;

	/**
	 * Constructor.
	 * @param name
	 */
	public ProtoTypeDefinition(String name, TypeKind kind, List<GenericTypeParameter> genericTypeParameter) {
		this.name = name;
		this.kind = kind;
		this.genericTypeParameter = genericTypeParameter;
	}
	
	public String toString(){
		return name;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSimpleName() {
		return new QualifiedNameNode(name).getLast().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeKind getKind() {
		return kind;
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
		return genericTypeParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Constructor> getConstructorByParameters(
			MethodParameter... parameters) {
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
	public Optional<Method> getMethodByPromotableSignature(MethodSignature signature) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

}
