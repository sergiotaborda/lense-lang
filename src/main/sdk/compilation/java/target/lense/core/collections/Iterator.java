package lense.core.collections;

@lense.core.lang.java.Signature("[+T<lense.core.lang.Any]::")
public   abstract interface Iterator{
	
@lense.core.lang.java.MethodSignature( returnSignature = "boolean" , paramsSignature = "")
public  abstract boolean moveNext();	
@lense.core.lang.java.MethodSignature( returnSignature = "T" , paramsSignature = "")
public  abstract lense.core.lang.Any current();
}
