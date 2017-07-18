package lense.compiler.typesystem.labels;

public class TypeMember {

    private Type type;
    private String name;
    
    public TypeMember(Type type, String name) {
        super();
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    
}
