/**
 * 
 */
package lense.compiler.type;

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
	private TypeVariable[] params;
    private Visibility visibility;
	 
	/**
	 * Constructor.
	 * @param typeAdapter
	 * @param name
	 * @param fromClass
	 */
	public IndexerProperty(TypeDefinition declaringType, TypeVariable type, boolean canRead, boolean canWrite , TypeVariable ... params ) {
		this.type = type;
		this.params = params;
		this.declaringType = declaringType;
		this.canRead=  canRead;
		this.canWrite =canWrite;
	}

	public TypeVariable getReturningType() {
		return type;
	}

	public TypeVariable[] getIndexes() {
		return params;
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
		
		IndexerProperty p = new IndexerProperty(concrete, t , this.canRead, this.canWrite, this.params);
		if (t instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)t).setDeclaringMember(p);
		}
		return p;
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder("[");
		
		for (TypeVariable p : params){
			builder.append(p.getTypeDefinition().getName()).append(",");
		}
		builder.deleteCharAt(builder.length()-1);
		
		return builder.append("]").toString();
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
		return params.length;
	}

	public boolean equals (Object other){
		return other instanceof IndexerProperty && equals((IndexerProperty)other);
	}
	
	private boolean equals (IndexerProperty other){
		if( this.params.length != other.params.length) {
			return false;
		}
		
		for (int i =0; i < this.params.length; i++) {
			if (!this.params[i].equals(other.params[i])) {
				return false;
			}
		}
		
		return true;
	}

}
