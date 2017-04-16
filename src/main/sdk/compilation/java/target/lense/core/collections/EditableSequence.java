package lense.core.collections;

@lense.core.lang.java.Signature("[=T<Optional.empty]::lense.core.collections.Sequence<T>")
public   abstract interface EditableSequence extends lense.core.collections.Sequence , lense.core.lang.Any{
	
@lense.core.lang.java.Property( indexed = true)
@lense.core.lang.java.MethodSignature( returnSignature = "T" , paramsSignature = "_")
public  abstract lense.core.lang.Any get(lense.core.math.Natural  index);	
@lense.core.lang.java.Property( indexed = true, setter = true)
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.Void" , paramsSignature = "_,_")
public  abstract void set(lense.core.math.Natural  index, lense.core.lang.Any  value);
}
