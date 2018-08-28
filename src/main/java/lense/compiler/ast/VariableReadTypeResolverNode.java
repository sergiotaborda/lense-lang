package lense.compiler.ast;

public class VariableReadTypeResolverNode extends AbstractTypeResolverNode {

	
	private String variableName;

	public VariableReadTypeResolverNode (String variableName) {
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}


}
