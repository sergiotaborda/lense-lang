package lense.compiler.typesystem.labels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CompositeType implements Type{



    List<Type> composition;

    public CompositeType(Type a, Type b) {
        composition = Arrays.asList(a,b);
    }
    
    public CompositeType(Type ... types) {
        composition = Arrays.asList(types);
    }
    
    public CompositeType( Collection<Type> types, Type b) {
        composition = new ArrayList<>(types.size() + 1);
        composition.addAll(types);
        composition.add(b);
    }
    
    public CompositeType( Collection<Type> types) {
        composition = new ArrayList<>(types);
    }

    public CompositeType(Collection<Type> a, Collection<Type> b) {
        composition = new ArrayList<>(a.size()+ b.size());
        composition.addAll(a);
        composition.addAll(b);
    }
    
    public abstract Type attach(Type other);
    

    @Override
    public final boolean isFinal() {
        return true;
    }
}
