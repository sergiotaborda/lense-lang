package lense.core.lang.reflection;

public   abstract interface Module{
	
@lense.core.lang.java.Property( name = "name")
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.String" , paramsSignature = "")
public  abstract lense.core.lang.String getName();	
@lense.core.lang.java.Property( name = "version")
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.Version" , paramsSignature = "")
public  abstract lense.core.lang.Version getVersion();	
@lense.core.lang.java.Property( name = "packages")
@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.collections.Sequence<lense.core.lang.reflection.Package>" , paramsSignature = "")
public  abstract lense.core.collections.Sequence getPackages();
}
