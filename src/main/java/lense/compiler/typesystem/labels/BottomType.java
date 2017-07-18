package lense.compiler.typesystem.labels;

public final class BottomType implements Type{

    private static final BottomType ME = new BottomType();
    
    public static BottomType instance() {
        return ME;
    }
    
    @Override
    public Type or(Type other) {
        return other;
    }

    @Override
    public Type and(Type other) {
        return this;
    }

    @Override
    public Type times(Type other) {
        return this;
    }

    @Override
    public Type simplify(System system) {
        return this;
    }
    
    public String toString(){
        return "Nothing";
    }

    @Override
    public boolean isFinal() {
        return true;
    }

}
