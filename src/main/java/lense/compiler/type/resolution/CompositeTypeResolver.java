/**
 * 
 */
package lense.compiler.type.resolution;

import java.util.LinkedList;
import java.util.List;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeResolver;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class CompositeTypeResolver implements TypeResolver {

	
	private List<TypeResolver> resolvers = new LinkedList<>();

	public CompositeTypeResolver (){}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition resolveTypeByName(TypeSearchParameters filter) {
		for(TypeResolver resolver : resolvers){
			TypeDefinition type = resolver.resolveTypeByName(filter);
			if (type != null){
				return type;
			}
		}
		return null;
	}

	/**
	 * @param resolver
	 */
	public void addTypeResolver(TypeResolver resolver) {
		 resolvers.add(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerType(TypeDefinition type) {
		for(TypeResolver resolver : resolvers){
			resolver.registerType(type);
		}
	}

}
