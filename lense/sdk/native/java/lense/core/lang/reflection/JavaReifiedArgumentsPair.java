package lense.core.lang.reflection;

public final class JavaReifiedArgumentsPair {

	public Type type;
	public String name;

	public JavaReifiedArgumentsPair(String name, Type type) {
		this.name = name;
		this.type = type;
	}

}
