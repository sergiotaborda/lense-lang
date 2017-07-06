package lense.compiler.typesystem.operations;

import java.util.List;
import java.util.stream.Collectors;

public class ProductType extends CompositeType {


    protected ProductType() {
        super();
    }
    
    protected ProductType(Type a, Type b) {
        super(a,b);
    }

    protected ProductType(ProductType a, Type other) {
        super(a.composition, other);
    }
    
    protected ProductType(Type ... all) {
        super(all);
    }
    
    protected ProductType(List<Type> all) {
        super(all);
    }
    
    protected ProductType(List<Type> a, List<Type> b) {
        super(a, b);
    }
    
    @Override
    public Type or(Type other) {
        if (other instanceof BottomType){
            return this;
        } else  if (other instanceof UnionType){
            return ((UnionType)other).attach(other);
        } 
        return new UnionType(this, other);
    }

    @Override
    public Type and(Type other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type times(Type other) {
        return attach(other);
    }

    @Override
    public Type attach(Type other) {
        return new ProductType(this, other);
    }

    @Override
    public Type simplify(System system) {
        return system.reduce(this);
    }

    public String toString(){
        return this.composition.stream().map(t -> t.toString()).collect(Collectors.joining("\u00D7"));
    }

  

}
