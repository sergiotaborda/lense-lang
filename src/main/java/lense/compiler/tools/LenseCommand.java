package lense.compiler.tools;

public enum LenseCommand {

	RUN,
	COMPILE,
	HELP,
	DOC;

    public enum Mode {
        JAVA,
        JAVA_SCRIPT
    }

    public enum Parameter {
        SOURCE,
        TARGET
    }

    
    public Mode  parseMode(String name) {
        switch (this){
        case COMPILE:
        case RUN:
            if (name.equalsIgnoreCase("java")){
                return Mode.JAVA;
            } else  if (name.equalsIgnoreCase("js") || name.equalsIgnoreCase("javascript")){
                return Mode.JAVA_SCRIPT;
            }
        default:
            throw new ToolException(name + " is not a recognized mode for " + this.name().toLowerCase());
        }
    }


    public Parameter parseParameter(String name) {
        if (name.equalsIgnoreCase("source")){
            return Parameter.SOURCE;
        } else if (name.equalsIgnoreCase("target")){
            return Parameter.TARGET;
        }
        throw new ToolException(name + " is not a recognized parameter for " + this.name().toLowerCase());
    }
	
}
