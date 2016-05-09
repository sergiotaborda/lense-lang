package lense.compiler.ast;

public class IndexerPropertyDeclarationNode extends PropertyDeclarationNode {

	private ParametersListNode indexes;

	public IndexerPropertyDeclarationNode(PropertyDeclarationNode other){
		this.setAcessor(other.getAcessor());
		this.setModifier(other.getModifier());
		this.setType(other.getType());
		this.setName(other.getName());
		this.setAnnotations(other.getAnnotations());
	}

	public boolean isIndexed(){
		return true;
	}
	
	public void setParameters(ParametersListNode indexes) {
		this.indexes =  indexes;
		this.add(indexes);
	}
	
	public ParametersListNode getIndexes(){
		return indexes;
	}
}
