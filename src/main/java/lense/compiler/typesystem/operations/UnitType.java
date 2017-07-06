package lense.compiler.typesystem.operations;

public class UnitType extends ProductType {

    private static final UnitType ME = new UnitType();
    
    public static UnitType instance() {
        return ME;
    }
    
    @Override
    public Type or(Type other) {
        return new UnionType(this, other);
    }

    @Override
    public Type and(Type other) {
        return BottomType.instance();
    }

    @Override
    public Type times(Type other) {
        return other;
    }

    public String toString(){
        return "Unit";
    }

}
