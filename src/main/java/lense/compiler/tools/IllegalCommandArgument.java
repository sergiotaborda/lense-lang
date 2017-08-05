package lense.compiler.tools;

public class IllegalCommandArgument extends ToolException {


    private static final long serialVersionUID = 3201691720741360273L;


    public IllegalCommandArgument(String invalidArgumentValue) {
       super(invalidArgumentValue + " is not valid argument value in this position");
    }
}
