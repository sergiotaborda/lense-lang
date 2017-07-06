package lense.compiler.typesystem.operations;

import java.util.List;
import java.util.stream.Collectors;

public class UnionType extends CompositeType {


    protected UnionType(Type a, Type b) {
        super(a,b);
    }

    protected UnionType(UnionType a, UnionType b) {
        super(a.composition,b.composition);
    }
    
    protected UnionType(UnionType a, Type other) {
        super(a.composition, other);
    }
    
    private UnionType(List<Type> types) {
        super(types);
    }
    
    public UnionType(Type ... all) {
        super(all);
    }

    @Override
    public Type or(Type other) {
        if (other instanceof BottomType){
            return this;
        } else if (other instanceof UnionType){
            return new UnionType(this, ((UnionType) other));
        }
         
        return attach(other);
    }

    @Override
    public Type and(Type other) {
       return new IntersectionType(this, other);
    }

    @Override
    public Type times(Type other) {
        if (other instanceof UnionType){
            Type union = BottomType.instance();
            
            for (Type inner : this.composition){
                for (Type outter : ((UnionType)other).composition){
                    union = union.or(inner.times(outter));
                }    
            }
            
            return union;
        } else {
            return other.times(this);
        }
    }

    public Type attach(Type other) {
        return new UnionType(this, other);
    }

    @Override
    public Type simplify(System system) {
   
        List<Type> list = this.composition.stream().filter(t -> !system.areEqual(t, BottomType.instance()))
            .map(t -> new TypeWithEquals(t, system::areEqual))
            .distinct()
            .map(te -> te.getType())
            .collect(Collectors.toList());

        if (list.isEmpty()){
            return BottomType.instance();
        } else if (list.size() == 1){
            return list.get(0);
        }
        return system.reduce(new UnionType(list));
    }
    
    public String toString(){
        return this.composition.stream().map(t -> t.toString()).collect(Collectors.joining("|"));
    }
}
