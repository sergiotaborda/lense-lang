package lense.compiler.repository;

import java.util.Optional;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public abstract class AbstractTypeRepositoryWithDependencies implements TypeRepositoryWithDependencies {

	private CompositeTypeRepository dependencies = new CompositeTypeRepository();
	

	@Override
	public final void addDependency(TypeRepository other) {
		dependencies.add(other);
	}

	protected Optional<TypeDefinition> resolveTypeFromDependencies(TypeSearchParameters filter){
		return dependencies.resolveType(filter);
	}
}
