/**
 * 
 */
package lense.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lense.compiler.typesystem.LenseTypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.LenseTypeResolver;
import compiler.typesystem.TypeResolver;
import compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class LenseTypeResolver implements TypeResolver {

	
	private static LenseTypeResolver me = new LenseTypeResolver();
	private Map<TypeSearchParameters, LenseTypeDefinition> types = new HashMap<>();
	
	/**
	 * @return
	 */
	public static LenseTypeResolver getInstance() {
		return me;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LenseTypeDefinition resolveTypeByName(TypeSearchParameters filter) {
		String name = filter.getName();
		if (!name.contains(".")){
			name = "sense.lang." + name;
		}
		
		LenseTypeDefinition def = types.get(filter);
		if (def == null){
			Optional<LenseTypeDefinition> sdef = LenseTypeSystem.getInstance().getForName(name, filter.getGenericParametersCount());
			if (sdef.isPresent()){
				def=  sdef.get();
				types.put(filter, def);
				
			} else {
				return null;
			}
		}
		
		return def;
		
		
	}
	
//	public void registerType(String name , SenseTypeDefinition type){
//		types.put(name, type);
//	}


}
