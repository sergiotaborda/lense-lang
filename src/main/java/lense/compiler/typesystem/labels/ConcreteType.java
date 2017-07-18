package lense.compiler.typesystem.labels;

import java.util.Arrays;
import java.util.List;

public class ConcreteType implements Type {

    
    private String name;
    private Type superType = TopType.instance();
    private boolean isfinal;
    private List<Type> interfaces;
    private Type derivation;
    
    public ConcreteType(String name){
        this.name = name;
    }
    
    public ConcreteType(String name, Type superType, Type ... interfaces) {
        this(name);
        this.superType = superType;
        this.interfaces = Arrays.asList(interfaces);
        this.derivation = superType.and(new IntersectionType(this.interfaces));
    }
    
    public Type getDerivationType(){
        return this.derivation;
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
        if (other instanceof UnionType){
            return new UnionType(this).times(other);
        }
        return new ProductType(this, other);
    }

    public String getName() {
        return name;
    }
    
    public Type getSuperType(){
        return superType;
    }
    
    public void setSuperType(Type type){
        this.superType = type;
    }
    
    @Override
    public Type simplify(System system) {
        return system.reduce(this);
    }

    public String toString(){
        return name;
    }
    
    @Override
    public boolean isFinal() {
        return isfinal;
    }
    
    public void setFinal(boolean isFinal){
        this.isfinal = isFinal;
    }
}
