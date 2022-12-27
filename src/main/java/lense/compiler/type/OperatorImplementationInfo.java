package lense.compiler.type;

import java.util.Optional;

public class OperatorImplementationInfo {
	
	private final TypeDefinition typeClassImplementation;
	private final Method method;
	private final String typeClassName;

	public OperatorImplementationInfo(
			String typeClassName,
			TypeDefinition typeClassImplementation,
			Method method
	) {
		this.typeClassName = typeClassName;
		this.typeClassImplementation = typeClassImplementation;
		this.method = method;
	}
	
	public String typeClassName() {
		return typeClassName;
	}
		
	public Optional<TypeDefinition> typeClassImplementation() {
		return Optional.ofNullable(this.typeClassImplementation);
	}
	
	public Optional<Method> method() {
		return Optional.ofNullable(this.method);
	}

}