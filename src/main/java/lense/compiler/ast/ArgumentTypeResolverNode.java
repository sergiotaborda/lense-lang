package lense.compiler.ast;

public final class ArgumentTypeResolverNode extends AbstractTypeResolverNode {

	private ArgumentListItemNode arg;

	public ArgumentTypeResolverNode(ArgumentListItemNode arg) {
		this.arg = arg;
	}

	public ArgumentListItemNode getArgumentItem() {
		return arg;
	}

}
