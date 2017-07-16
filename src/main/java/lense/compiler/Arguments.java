package lense.compiler;

public class Arguments {

	
	private LenseCommand command = LenseCommand.HELP;
	private String repositoryBase = "src/main/sdk/compilation/modules";
	private String source = "";
	
	public LenseCommand getCommand() {
		return command;
	}

	public void setCommand(LenseCommand command) {
		this.command = command;
	}

	public String getRepositoryBase() {
		return repositoryBase;
	}

	public void setRepositoryBase(String repositoryBase) {
		this.repositoryBase = repositoryBase;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
