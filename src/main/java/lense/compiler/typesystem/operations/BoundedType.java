package lense.compiler.typesystem.operations;

public class BoundedType implements Type{

    private Type top;
    private Type bottom;
    
    public static BoundedType withTop(Type top){
        return within(BottomType.instance(), top);
    }
    
    public static BoundedType withBottom(Type bottom){
        return within(bottom, TopType.instance());
    }
    
    public static BoundedType within(Type bottom, Type top){
        return new BoundedType(bottom, top);
    }
    
    private BoundedType(Type bottom, Type top){
        this.bottom = bottom;
        this.top = top;
    }
    
    @Override
    public Type or(Type other) {
        return new BoundedType(new UnionType(this.bottom, other), new UnionType(this.top, other));
    }

    @Override
    public Type and(Type other) {
        return new BoundedType(new IntersectionType(this.bottom, other), new IntersectionType(this.top, other));
    }

    @Override
    public Type times(Type other) {
       return new ProductType(this, other);
    }

    @Override
    public Type simplify(System system) {
        if (!system.isSubType(bottom, top)){
            // reversed bound
            return BottomType.instance();
        }
        return system.reduce(this);
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    public String toString(){
        return "[" + bottom.toString() + "," + top.toString() + "]"; 
    }
}
