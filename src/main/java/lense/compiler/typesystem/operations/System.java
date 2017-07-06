package lense.compiler.typesystem.operations;

public interface System {

    /**
     * Is A a substype of B
     * @param A
     * @param B
     * @return
     */
    public boolean isSubType(Type a, Type b);
    
    public boolean areEqual(Type a, Type b);

    /**
     * Simplifies the representation of the type according to type system rules.
     *  
     * @param type
     * @return
     */
    public Type reduce(Type type);
}
