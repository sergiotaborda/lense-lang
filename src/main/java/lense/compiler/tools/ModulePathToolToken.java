package lense.compiler.tools;

public class ModulePathToolToken implements ToolToken {

    private final String value;

	public ModulePathToolToken(String value) {
       this.value = value;
    }

	
	public String toString() {
		return value;
	}
}
