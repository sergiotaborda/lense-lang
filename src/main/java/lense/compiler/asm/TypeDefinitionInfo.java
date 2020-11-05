package lense.compiler.asm;

import java.util.HashSet;
import java.util.Set;

import lense.compiler.dependency.Dependency;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeKind;
import lense.compiler.typesystem.LenseTypeSystem;

public class TypeDefinitionInfo implements Dependency{

	public TypeKind kind;
	public String name;
	public int genericCount;
	public String superName;
	public Set<TypeDefinitionInfo> imports = new HashSet<TypeDefinitionInfo>();
	public LoadedClassBuilder builder;
	
	public TypeDefinitionInfo(String name, TypeKind kind) {
		this.name = name;
		this.kind = kind;
	}

    @Override
	public int hashCode() {
		return name.hashCode();
	}
    
    @Override
    public boolean equals(Object other) {
    	return other instanceof TypeDefinitionInfo && ((TypeDefinitionInfo)other).name.equals(this.name);
    }
	
	@Override
	public String getDependencyIdentifier() {
		return name;
	}
	
	public void addImport(TypeDefinitionInfo info) {
		var name= info.name;
		if(!name.equals(this.name) && !name.equals(LenseTypeSystem.Any().getName())) {
			if(name.equals("lense.core.lang.AnyValue")) {
				kind = LenseUnitKind.ValueClass;
			} else {
				imports.add(info);
			}
			
		}
	}
	
	public String toString() {
		return name;
	}
}
