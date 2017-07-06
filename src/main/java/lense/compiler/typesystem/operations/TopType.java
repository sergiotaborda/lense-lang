package lense.compiler.typesystem.operations;

public class TopType extends ConcreteType{

    private static final TopType ME = new TopType();
    
    public static TopType instance(){
        return ME;
    }
    
    public TopType() {
        super("Any");
    }

    public Type getSuperType(){
        return null;
    }
    
    public void setSuperType(Type type){
        throw new IllegalArgumentException("Cannot change the super type of the top type"); 
    }
}
