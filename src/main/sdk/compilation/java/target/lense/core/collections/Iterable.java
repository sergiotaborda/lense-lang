package lense.core.collections;

@lense.core.lang.java.Signature("[+T<lense.core.lang.Any]::")
public   abstract interface Iterable{
	
@lense.core.lang.java.Property( name = "iterator")
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.collections.Iterator<lense.core.lang.Any>" , paramsSignature = "")
public  abstract lense.core.collections.Iterator getIterator();
}
