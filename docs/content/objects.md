title=Objects
date=2015-12-19
type=post
tags=tour, lense
status=published
~~~~~~


Lense does not have primitives (values that are not objects) and thus all values are objets , all objects decend from some class and all variables are references. 
The Lense back-end compiler may try to use the native plataform's primitives as much as possible to enhance performance, but this is an otimization. Conceptually primitives do not exist.
All being objetcs also means no static members exist.

# Classes

Classes are the main tool to define your own objects.
A class to represent fraction objects could be implemented like so:

~~~~brush: lense
public val class Fraction {
    
    val Integer numerator;
    val Integer denominator;

    private constructor (Integer numerator, Integer denominator);

    constructor valueOf (Decimal value){
        return new Fraction(value.toString());
    }

    constructor valueOf (String value){
         val Natural? pos = value.indexOf('.');
         if (pos.hasValue){
            val wholePart = new Integer.parse(value.subString(0,pos));
            val multiplier =  10 ** (value.length - pos); // 10 to the power of the number of decimal digits
            val decimalPart = new Integer.parse(value.subString(pos+1));     

            val numerator = wholePart * multiplier + decimalPart;

            return new Fraction(numerator, multiplier);
         } else {
            // whole number
            return new Fraction(new Integer.parse(value), 1);
         }
    }

    public Fraction multiply (Fraction other ){
          return new Fraction ( this.numerator * other.numerator, this.denominator * other.denominator);
    }

    public Fraction invert (){
        return new Fraction (this.denominator, this.numerator);
    }

    public Integer Numerator { get { return numerator; } } 

    public Integer Denominator { get { return denominator; } } 

}
~~~~

Classes in Lense support fields, properties, indeers, methods and constructors as you would expect.
Lense also support imutability declaration for classes with ``val class``.
Any type or member can also have generic type parameters.


~~~~brush: lense
public class Matrix<T> { 

    val Array<T> backingArray = new Array<T>();

    val Natural rowsCount;
    val Natural colsCount;

    constructor Matrix(Natural rowsCount, Natural colsCount){
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;

        backingArray = new Array<T>(rowsCount * colsCount);
    }

    public T [Natural row, Natural column] {
        get {
            return backingArray[row * ]
        }        
        set (value){

        }
    }

}
~~~~

# Objects

Instead of static members, Lense has object delcarations. An object declaration instructs the compiler to 
create a singleton object the exists in the package space.

~~~~brush: lense
public object Console {

	public Void println(String text){
         // implementation goes here ... 
    }


}
~~~~

Objects and then imported normally like a type using the ``import`` statement:

~~~~brush: lense
import somepackage.Console;

public class OtherClass {

	public void doIt() {
	     Console.println("Hello, world");
	}
}

~~~~

As you can see from this example, calling methods on the object is very much like a static call in other languages.

When declaring an object Lense will really create a single instance of the given anonymous class in the parent scope. Objects can implement interfaces and inherit from other classes but not from other objects. 

# Nested Objects (Under Consideration)

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

# Sum Types

In Lense is possible to define an abstract type that can only be implemented by a limited list of other types.
The classic example is the Node type. Normally we have a Branch node and a Leaf node. Tradicionally we would write:

~~~~brush: lense 
public abstract class Node <T> {

}

public class Brunch<T> extends Node<T>{
	constructor (Node<T> left , Node<T> right);
}
	
public class Leaf <T> extends Node<T>{
	constructor (T value);
}
~~~~ 

If we now need to visit a long and complex tree we need to normally do so validation of types. For example, in java would be:

~~~~brush: java
public <T> void colectLeafs(Node<T> node, List<T> leafs){

	if (node instanceof Branch){
	     Branch b = (Branch)node);
	     collectLeafs(b.left, leafs);
	     collectLeafs(b.right, leafs);
	} else if (node instanceof Leaf){
	    leafs.add(((Leaf)node).value);
	}
}
~~~~

We need to make a decision based on the type of the object, then cast it, then capture the properties and use them.
In Lense we could use a similar construction using the flow-cast mechanism:

~~~~brunch: lense 
public void colectLeafs<T>(Node<T> node, List<T> leafs){

	if (node is Branch<T>){
	     collectLeafs(node.left, leafs);
	     collectLeafs(node.right, leafs);
	} else if (node is Leaf<T>){
	    leafs.add(node.value);
	}
}
~~~~

However the compiler would be blind to the fact ``Node`` is a sum type. If we did not had a ``else if`` to test for leafs the compiler 
would not complain.  If we need the be sure all sub types are covered we can use the ``switch`` statement.

~~~~brush: lense
public <T> void colectLeafs(Node<T> node, List<T> leafs){

	switch(node) {
		case Branch (b){
			collectLeafs(b.left, leafs);
	      collectLeafs(b.right, leafs);
		} 
		case Leaf (leaf){
			leafs.add(leaf.value);
		} 
	}
}
~~~~

But for this to work as expect we need to informe the compiler the children types of ``Node`` are limited to ``Brunch`` and ``Leaf``:

~~~~brush: lense 
public abstract class Node<T> is Brunch<T> | Leaf<T> {

}
~~~~ 

# Interfaces

Interfaces are constract declarations with no implementation.
 
~~~~brush: lense
public interface Validator<in T> {
     public ValidatorResult validate( T candidate);
}
~~~~

Interfaces can extend other interfaces an are implemented by classes or objects.

~~~~brush: lense
public val class MailValidator implements Validator<String> {

     public ValidatorResult validate( String candidate) {
            var result = new ValidatorResult ();

            if (!candidate.indexOf('@').isPresent){
                   result.addReason("Invalid email");
            }

            return result;
     }
}
~~~~