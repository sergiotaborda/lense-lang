package lense.compiler.typesystem.operations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IntersectionType extends CompositeType{

    protected IntersectionType(IntersectionType intersectionType, Type other) {
        super(intersectionType.composition, other);
    }

    protected IntersectionType(IntersectionType a, IntersectionType b) {
        super(a.composition, b.composition);
    }
   
    protected IntersectionType(Type a, Type b) {
        super(a,b);
    }
    
    IntersectionType(Collection<Type> types) {
        super(types);
    }
    
    IntersectionType(Type ... types) {
        super(types);
    }


    @Override
    public Type or(Type other) {
        return new UnionType(this, other);
    }

    @Override
    public Type and(Type other) {
         return new IntersectionType(this, other);
    }

    @Override
    public Type times(Type other) {
       return new ProductType(this, other);
    }

    @Override
    public Type attach(Type other) {
       return new IntersectionType(this, other);
    }

    @Override
    public Type simplify(System system) {

        List<Type> uniqueTypes = new LinkedList<>();
        
       if (clean(this.composition, system, uniqueTypes )){
           return BottomType.instance();
       }
        
        List<Type> list = uniqueTypes.stream().filter(t -> !system.areEqual(t, TopType.instance()))
                .map(t -> new TypeWithEquals(t, system::areEqual))
                .distinct()
                .map(te -> te.getType())
                .collect(Collectors.toList());
                
        if (list.isEmpty()){
            return BottomType.instance();
        } else if (list.size() == 1){
            return list.get(0);
        }
        return system.reduce(new IntersectionType(list));
    }
    
    // return true if the result is a BottomType
    private boolean clean(List<Type> composition, System system, List<Type> cleanComposition) {
        
         for (Type t : composition){
             if (system.areEqual(t, BottomType.instance())){
                 return true;
             }
             
             if (t instanceof IntersectionType){
                 if (clean(((IntersectionType)t).composition , system, cleanComposition)){
                     return true;
                 }
             } else {
                 cleanComposition.add(t);
             }
             
         }
         
         return false;
    }

    public String toString(){
        return this.composition.stream().map(t -> t.toString()).collect(Collectors.joining("&"));
    }

}
