package lense.compiler.repository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class EmptyTypeRepository implements UpdatableTypeRepository {

    @Override
    public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
        return Optional.empty();
    }

    @Override
    public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
        return type;
    }

    @Override
    public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
        return Collections.emptyMap();
    }

}
