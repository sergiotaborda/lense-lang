package lense.compiler.typesystem.labels;


public interface Type {

    public Type or (Type other);
    public Type and (Type other);
    public Type times (Type other);

    /**
     * Simplify the type internal representation according to relations between component types.
     * 
     * @param system
     * @return
     */
    public Type simplify(System system);
    
    public default boolean isSubTypeOf(Type other, System system){
        return system.isSubType(this, other);
    }
    
    public boolean isFinal();
}

