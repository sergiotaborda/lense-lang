package lense.compiler.tools;

public final class ParameterToolToken implements ToolToken {

    private final String name;

    public ParameterToolToken(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
