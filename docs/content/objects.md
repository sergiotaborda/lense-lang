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
            val multiplier =  10 ^ (value.length - pos); // 10 to the power of the number of decimal digits
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

# Interfaces

Interfaces are constract declarations with no implementation.

 
~~~~brush: lense
public interface Validator<in T> {

     public ValidatorResult validate( T candidate);
}
~~~~

Interfaces can extend other interfaces an are implemented by classes

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