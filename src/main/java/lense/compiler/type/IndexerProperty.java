/**
 * 
 */
package lense.compiler.type;

import java.util.stream.Stream;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class IndexerProperty  implements TypeMember {

	private TypeVariable type;
	private TypeDefinition declaringType;
	private boolean canRead;
	private boolean canWrite;
	private TypeVariable[] parameterTypes;
    private Visibility visibility;
    private boolean isAbstract;
	private boolean isDefault;
	private boolean isOverride;
	private boolean isNative;
	
	/**
	 * Constructor.
	 * @param typeAdapter
	 * @param name
	 * @param fromClass
	 */
	public IndexerProperty(TypeDefinition declaringType, TypeVariable type, boolean canRead, boolean canWrite , TypeVariable ... params ) {
		this.type = type;
		this.parameterTypes = params;
		this.declaringType = declaringType;
		this.canRead=  canRead;
		this.canWrite =canWrite;
		
		if (Stream.of(params).anyMatch(p -> p == null)){
		    throw new IllegalArgumentException("Parameter type cannot be null");
		}
	}

	public TypeVariable getReturningType() {
		return type;
	}

	public TypeVariable[] getIndexes() {
		return parameterTypes;
	}

	public TypeDefinition getDeclaringType() {
		return declaringType;
	}

	@Override
	public boolean isField() {
		return false;
	}

	@Override
	public boolean isProperty() {
		return false;
	}

	@Override
	public boolean isMethod() {
		return false;
	}

	@Override
	public boolean isConstructor() {
		return false;
	}

	@Override
	public TypeMember changeDeclaringType(TypeDefinition concrete) {
		TypeVariable t = this.type.changeBaseType(concrete);
		
		IndexerProperty p = new IndexerProperty(concrete, t , this.canRead, this.canWrite, this.parameterTypes);
		if (t instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)t).setDeclaringMember(p);
		}
		return p;
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder("[");
		
		for (TypeVariable p : parameterTypes){
			builder.append(p.getTypeDefinition().getName()).append(",");
		}
		builder.deleteCharAt(builder.length()-1);
		
		return builder.append("]").toString();
	}

	@Override
	public String toString(){
	    return this.getName();
	}
	
	@Override
	public boolean isIndexer() {
		return true;
	}

	public void setWritable(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public void setReadable(boolean canRead) {
		this.canRead = canRead;
	}

	public boolean canRead() {
		return canRead;
	}
	public boolean canWrite() {
		return canWrite;
	}
	
    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }

	public int hashCode(){
		return parameterTypes.length;
	}

	public boolean equals (Object other){
		return other instanceof IndexerProperty && equals((IndexerProperty)other);
	}
	
	private boolean equals (IndexerProperty other){
		if( this.parameterTypes.length != other.parameterTypes.length) {
			return false;
		}
		
		for (int i =0; i < this.parameterTypes.length; i++) {
			if (!this.parameterTypes[i].equals(other.parameterTypes[i])) {
				return false;
			}
		}
		
		return true;
	}

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @Override
	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
		
	}

	@Override
	public boolean isOverride() {
		return isOverride;
	}

	@Override
	public void setOverride(boolean isOverride) {
		this.isOverride = isOverride;
	}

	@Override
	public boolean isNative() {
		return isNative;
	}

	@Override
	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}
}
