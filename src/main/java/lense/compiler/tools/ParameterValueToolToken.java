package lense.compiler.tools;

public final class ParameterValueToolToken implements ToolToken {

    private String value;

    public ParameterValueToolToken(String value) {
        this.value = value;
    }
    
    public String getValue(){
        return value;
    }

}
