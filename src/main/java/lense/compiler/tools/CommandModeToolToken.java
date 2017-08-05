package lense.compiler.tools;

public class CommandModeToolToken implements ToolToken{

    private String name;

    public CommandModeToolToken(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
