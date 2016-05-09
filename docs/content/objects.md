title=Objects
date=2015-12-19
type=post
tags=tour, lense
status=published
~~~~~~

# Objects

In Lense all things are objects and all variables are references. This means, no primitives and no static members.
The Lense back-end compiler may try to use the native plataform's primitives as much as possible to enhance performance, but this is an otimization. Conceptually primitives do not exist.

Instead of static members, Lense has global objects.A global object is declared like:

~~~~brush: lense
public object System {

	
}

~~~~

and then imported normally like a type using the ``import`` statement:

~~~~brush: lense
import somepackage.System;

public class OtherClass {

	public void doIt() {
	     System.exit();
	}
}

~~~~

As you can see from this example, calling methods on the object is very much like a static call in other languages.

When declaring an object Lense will really create a single instance of the given anonymous class in the parent scope. Objects can implement interfaces and inherit from other classes but not from other objects. 

Objects can be nested in other types and other objects. In this case the object will have access to the private members of the surrounding type.
Nested objects calls must be prefixed with the type they are in:

~~~~brush: lense
public class OtherClass {

	public object SomeObject {
	     
	      public Void doItInTheObject {} 
	}
   
	public Void doItInTheOtherClass() {
	
	}
}

// called like 

OtherClass.SomeObject.doItInTheObject()

~~~~

 


