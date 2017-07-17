package lense.compiler.typesystem.labels;

import java.util.Arrays;

public class FunctionType implements Type{

    private Type returnType;
    private ProductType argumentType;
    
    public FunctionType(Type returnType){
        this.returnType = returnType;
        this.argumentType = UnitType.instance();
    }
    
    public FunctionType(Type returnType, Type ... arguments){
        this.returnType = returnType;
        this.argumentType = new ProductType(Arrays.asList(arguments));
    }
    
    public Type getReturn(){
        return returnType;
    }
    
    public Type getArgumentType(){
        return argumentType;
    }
    
    @Override
    public Type or(Type other) {
        if (other instanceof FunctionType){
            
        } else if (other instanceof FunctionType){
            FunctionType f = (FunctionType)other;
            if (f.returnType.equals(this.returnType)){
                return new FunctionType(this.returnType, this.argumentType.or(f.argumentType));
            }
        }
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
    public Type simplify(System system) {
        return this;
    }
    
    public String toString(){
        return this.argumentType.toString() + " -> " + this.returnType.toString();
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
