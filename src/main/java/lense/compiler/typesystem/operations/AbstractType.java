package lense.compiler.typesystem.operations;

public abstract class AbstractType implements Type {

    
    public Type or (Type other) {
        if (other instanceof BottomType || other instanceof UnitType){
            return other.or(this);
        } else if (other instanceof FunctionType){
            return orFunctionType((FunctionType)other);
        }  else if (other instanceof ProductType){
            return orProductType((ProductType)other);
        } else if (other instanceof UnionType){
            return orUnionType((UnionType)other);
        } else if (other instanceof IntersectionType){
            return orIntersectionType((IntersectionType)other);
        } else if (other instanceof ConcreteType){
            return orConcreteType((ConcreteType)other);
        } else {
            throw new RuntimeException("Unrecognized type " + other.getClass().getName());
        }
    }
    

    protected abstract Type orFunctionType(FunctionType other);
    protected abstract Type orProductType(ProductType other);
    protected abstract Type orUnionType(UnionType other);
    protected abstract Type orIntersectionType(IntersectionType other);
    protected abstract Type orConcreteType(ConcreteType other);
    
    public Type and (Type other) {
        if (other instanceof BottomType || other instanceof UnitType){
            return other.and(this);
        } else if (other instanceof FunctionType){
            return andFunctionType((FunctionType)other);
        }  else if (other instanceof ProductType){
            return andProductType((ProductType)other);
        } else if (other instanceof UnionType){
            return andUnionType((UnionType)other);
        } else if (other instanceof IntersectionType){
            return andIntersectionType((IntersectionType)other);
        } else if (other instanceof ConcreteType){
            return andConcreteType((ConcreteType)other);
        } else {
            throw new RuntimeException("Unrecognized type " + other.getClass().getName());
        }
    }
    
    protected abstract Type andProductType(ProductType other);
    protected abstract Type andFunctionType(FunctionType other);
    protected abstract Type andUnionType(UnionType other);
    protected abstract Type andIntersectionType(IntersectionType other);
    protected abstract Type andConcreteType(ConcreteType other);
    
    public Type times (Type other) {
        if (other instanceof BottomType|| other instanceof UnitType){
            return other.times(this);
        } else if (other instanceof FunctionType){
            return timesFunctionType((FunctionType)other);
        }  else if (other instanceof ProductType){
            return timesProductType((ProductType)other);
        } else if (other instanceof UnionType){
            return timesUnionType((UnionType)other);
        } else if (other instanceof IntersectionType){
            return timesIntersectionType((IntersectionType)other);
        } else if (other instanceof ConcreteType){
            return timesConcreteType((ConcreteType)other);
        } else {
            throw new RuntimeException("Unrecognized type " + other.getClass().getName());
        }
    }
    
    protected abstract Type timesFunctionType(FunctionType other);
    protected abstract Type timesProductType(ProductType other);
    protected abstract Type timesUnionType(UnionType other);
    protected abstract Type timesIntersectionType(IntersectionType other);
    protected abstract Type timesConcreteType(ConcreteType other);
    

    

}
