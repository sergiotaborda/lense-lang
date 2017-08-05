package lense.compiler.tools;

public class CommandToolToken implements ToolToken {

    private String name;

    public CommandToolToken(String name) {
       this.name = name;
    }

    public String getName() {
        return name;
    }

}
