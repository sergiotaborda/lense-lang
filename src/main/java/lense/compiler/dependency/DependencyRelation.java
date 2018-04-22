/**
 * 
 */
package lense.compiler.dependency;

/**
 * 
 */
public class DependencyRelation {


	private DependencyRelationship relationship;

	public DependencyRelation(DependencyRelationship relationship) {
		super();
		this.relationship = relationship;
	}

	public String toString() {
		return relationship.toString();
	}


	public DependencyRelationship getRelationship() {
		return relationship;
	}


}
