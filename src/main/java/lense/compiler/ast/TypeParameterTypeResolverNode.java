package lense.compiler.ast;

public final class TypeParameterTypeResolverNode extends AbstractTypeResolverNode {

	private AbstractTypeResolverNode  other;
	private int index;

	public TypeParameterTypeResolverNode(AbstractTypeResolverNode other , int index) {
		this.other =  other;
		this.index = index;
	}

	public AbstractTypeResolverNode getOriginal() {
		return other;
	}
	
	public int getIndex() {
		return index;
	}
	
}
