package lense.core.collections;

@lense.core.lang.java.Signature("[=T<lense.core.lang.Any]::lense.core.collections.EditableSequence<T>")
public   abstract interface ResizableSequence extends lense.core.collections.EditableSequence , lense.core.lang.Any{
	
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.Void" , paramsSignature = "_")
public  abstract void add(lense.core.lang.Any  element);	
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.Void" , paramsSignature = "_")
public  abstract void remove(lense.core.lang.Any  element);
}
