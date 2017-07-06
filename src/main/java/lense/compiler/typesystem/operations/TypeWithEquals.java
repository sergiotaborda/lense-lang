package lense.compiler.typesystem.operations;

import java.util.function.BiPredicate;

public class TypeWithEquals {

    private Type type;
    private BiPredicate<Type, Type> rule;
    
    
    public TypeWithEquals(Type type, BiPredicate<Type, Type> rule) {
        super();
        this.type = type;
        this.rule = rule;
    }

    public int hashCode(){
        return 0;
    }
    
    public boolean equals(Object other){
        return other instanceof TypeWithEquals && equals((TypeWithEquals)other);
    }
    
    public boolean equals(TypeWithEquals other){
        return rule.test(this.type, other.type);
    }

    public Type getType() {
        return type;
    }
}
