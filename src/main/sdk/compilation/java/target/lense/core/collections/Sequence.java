package lense.core.collections;

@lense.core.lang.java.Signature("[+T<lense.core.lang.Any]::lense.core.collections.Assortment<T>")
public   abstract interface Sequence extends lense.core.collections.Assortment , lense.core.lang.Any{
	
@lense.core.lang.java.Property( indexed = true)
@lense.core.lang.java.MethodSignature( returnSignature = "T" , paramsSignature = "_")
public  abstract lense.core.lang.Any get(lense.core.math.Natural  index);	
@lense.core.lang.java.Property( name = "size")
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.math.Natural" , paramsSignature = "")
public  abstract lense.core.math.Natural getSize();	
@lense.core.lang.java.Property( name = "indexes")
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.collections.Progression<lense.core.math.Natural>" , paramsSignature = "")
public  abstract lense.core.collections.Progression getIndexes();
}
