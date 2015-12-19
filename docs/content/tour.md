title=Tour
date=2015-12-12
type=post
tags=tour, lense
status=published
~~~~~~


# Hello, world !

This is our version of the quintessential Hello, world program.

~~~~brush: lense
	public class Hello extends ConsoleApplication {
	
		public run (){
			Console.print("Hello, world!");
		}
	}
~~~~

Prints

~~~~console
Hello, world!
~~~~

We define a classe Hello than extends the  ConsoleApplication class provided in the SDK. This makes the Hello class a executable entry point
The class can have any name. The entry method is run(). The VM will instanciate the class, set the Aguments property acording to the plataform parameters and execute the run method;
The method makes use of the print method in the Console object. The Console object is a SDK provided object that allows interaction with the console. Note the method does not declare any parameters or return type.
The return type is only needed when the compiler could not infer it. In this case the method run is defined in the ConsoleApplication class so the compiler can infer from the base declaration the return is Void.
Ther is no "void" reserved word. Void is a type like any other. 


The print method simply print the given String literal to the console. You can see a string is declared  enclosing the text in double quotes. String literals preserve line breaks and tabulation

~~~~brush: lense

public class Hello extends ConsoleApplication {

	public run () {
		Console.print("Hello,
						world!");
	}
}

~~~~

Prints

~~~~console
Hello,							
						world!  
~~~~

<h3>Escape Sequences</h3>
Strings suport unicode, and unicode characters can be embeded using the \{ } escape sequence that receives an Hexadecimal value. 
Also, Strings can be interpolated using he {{ }} escape sequence. Any expression can be use inside the interpolation sequence and its string representation will 
be printed (by calling the toString() method on the result of the expression).

~~~~brush: lense
public class Hello extends ConsoleApplication {

	public run () {
		Decimal pi = 3.1415; 
		Console.print("Hi!, the mathematical constant \{#03C0} is {{ pi }}  ");
	}
}

~~~~

Prints

Hi!, the mathematical constant &pi; is 3.1415

You can notice that #03C0 denotes an hexadecimal number.

## Nullability


Null referentes are not allowed , but the notion of an absent value is very usefull.

~~~~brush: lense
public class Hello extends ConsoleApplication {

	public run (){
		
		Console.print("Hi!, the first argument is {{ Arguments.first() }}.");
	}
}

~~~~

Prints

~~~~console
Hi!,  the first argument is null.
~~~~

"Arguments" is a read only property of ConsoleAplication that was set with the arguments passed in the console at the momento of running the application.
With can access the first element of the arguments sequence by invoking the first() method. The first argument in arguments might not be present, the arguments sequence may be empty. 
So in this case, in other languages, null would be returned and printed. lense does not have null references, but "null" is printed nontheless. How is that ?


Well, left look closer to the return of <code>first</code>

~~~~brush: lense
public class Hello extends ConsoleApplication {
	public run () {
		val String? argument = Arguments.first();
		Console.print("Hi!, the first argument is {{ argument }}.");
	}
}

~~~~

The type returned by the <code>first</code> method is a Maybe<String>. That why there is a ? after <code>String</code>. String is
the type of elements contained inside the <code>Arguments</code> sequence.
<br/> <code> Maybe<T></code> is a generic types class that allows for only two subclasses : <code>Some<T></code> and <code>None</code>.


~~~~brush: lense
public abstract class Maybe<T> {
	
	public val T value;
	public abstract val Boolean isPresent;
}

public selead class Some<T> extends Maybe<T> {
	
	public Some (T value){
	    this.value = value;
	}
	
	public override val Boolean isPresent {return true;}
}

public selead class None extends Maybe<Nothing> {
	
	object none extends None {
	
		public String toString (){
			return "null";
		}
	}

	private None (){}
	
	public override val Nothing value {
		throw new AbsentValueException();
	}
	
	public override val Boolean isPresent {return false;}
}

~~~~

If a value is present the class <code>Some<T></code> is instanciated with that value, if not, an instance of None is returned. 
However None has only one instance: the <code>Absent</code> object.
the <code>Absent</code> object itself overrides the <code>toString</code> method to return the word 'null'. Also 'null' is a reserved word in the language 
that can used to initialize to an absent value, like: 

~~~~brush: lense
	var String? name = null; // we don't kown the name yet.
	name = "The Name"; // now we kown.
~~~~

The usage is very similar to java and C# and others however is mandatory to declare the variable has maybe absent either explicitly declaring it as <code> Maybe<T></code> 
or using the ? sufix abreviation.
There is a lot of abreviations and syntax sugar so the transition from other languages is not that rought. We can explicitly write the same has: 

~~~~brush: lense
	var Maybe<String> name = Absent; // we don't kown the name yet.
	name = new Some("The Name"); // now we kown.
~~~~

However if we need to have a non absent value we can use the or method, or equivalently the (|) operator

~~~~brush: lense
public class Main {
	public Void main(Array<String> args){
		String? argument = args.first();
		String = argument | "world"; // the same as argument.or("world");
		Console.print("Hi!, the first argument is {{ argument }}.");
	}
}
~~~~

Prints

~~~~console
Hi!,  the first argument is world.
~~~~

Variables and Values
--------------------------

In any scope when can define a value

~~~~brush: lense
	val k = 3;
	val u: Int;
	u = 90;
	u = 80; // error!
~~~~

Values are imutable and cannot be changed after inicialized. All values need to be explicity inicialized before they can be read.
Once again the compiler will infer the type of the value from the initialization expression. If there is no inicialization, the declarion of the type is required.
A value can be made mutable by using the <code>var</code> annotation instead of <code>val</code>. An mutable value is called a <i>variable</i>.

~~~~brush: lense
	 var Int n = 1; // inicialize
     n = 2; // ok, n is a variable.
~~~~

## Functions

Functions allow for algorithms to be executed before returning a value. 
Normally this algorithms depend on parameters that the function declares explicitly.
  
~~~~brush: lense
	 doSomething() : Void { 
	 	Console.print("Doing something");
	 };
	 square (Int x) : Int { return x*x; }
~~~~

Funtions always return a value. <code>Void</code> is not a keyword is an actual type. <code>Void</code> only has one instance. All functions have an implicity return of the instance of ``Void`` at the end. This is correct unless the method return other type. You can explicitly write a return of a instance of ``Void``.

~~~~brush: lense
	 doSomething() : Void { 
	 	Console.print("Doing something");
	 	return; // implicitly return the instance of Void.
	 };
	 square (Int x) : Int { return x*x; }
~~~~

Functions are objects of type Callable:

~~~~brush: lense
	 Callable<Int, Int> f = x -> x*x;
	 Callable<Int, Int> g = x -> x*2;
	 
	 Callable<Int, Int> h = g.then(f); // the same as f(g(x)) - apply g first, them apply f
	 
	 Console.println(h(2)); // (2*2)*(2*2)
	 Console.println(h(3)); // (3*2)*(3*2)
~~~~

Prints

~~~~console
16						
36						
~~~~

If the internal function returns void, the secound applied function cannot have parameters.

~~~~brush: lense
     Callable<Int, Void> f = () -> 2;
	 Callable<Void, Int> g = x -> return; ;
	
	 
	 Callable<Void, Void> h = g.then(f); // the same as f(g(x)) - apply g first, them apply f., but g returns Void, so f must required Void as single argument.
	 
	 Console.println(h(2)); // 2   
	 Console.println(h(3)); // 2
	 
~~~~

This is a complicated way to write the equivalent function:

~~~~brush: lense
     Callable<Int, Void> h = x -> {
     	return 2;
     }
~~~~

## Numbers

Numbers are separated in specific algebric strutures that conform to the matematical rules of the group of elements.
All numbers implement the <code>Number</code> class.

* Whole - numbers with no decimal part.
	- Natural - Represent elements from the mathematical **N** set, i.e. positive only whole values that include zero and range from zero to an arbitrary range limited only by available memory. 
	- Integer - Represent elements from the mathematical **Z** set, i.e. negative and positive whole values.
		*  Int16 - negative and positive whole values with range from -2<sup>16</sup> to  2<sup>16</sup>-1. 
		*  Int32 - negative and positive whole values with range from -2<sup>32</sup> to  2<sup>32</sup>-1. 
		*  Int64 - negative and positive whole values with range from -2<sup>64</sup> to  2<sup>64</sup>-1. 
		*  BigInt - negative and positive whole values with arbitrary range limited only by available memory
* Real - Represent elements from the mathematical **R** set.
	-  Rational - Represent elements from the mathematical **Q** set, i.e. rational numbers defined by a natural numerator and a natural denominator like 2/3 or -5/8. The denominator cannot be zero. 
	-  Decimal - Represent elements that have a fixed precision and so calculations may incur in loss of precision.
		*  Decimal32 - negative and positive decimal values that follow 32 bits IEEE 3744 conventions
		*  Decimal64 - negative and positive decimal values that follow 64 bits IEEE 3744 conventions
		*  BigDecimal - Representr elements in the **R** set incluing truncated version of irracional numbers.Negative and positive decimal values with arbitrary precision limited only by available memory.
* Imaginary -Represent elements from the mathematical **I** set. Numbers with pure imaginary parts of the form bi where ``i`` it the square root of -1.
	- ImaginaryOverReals<T extends Real>; - uses a Real type to store the numeric value
* Complex - Represent elements from the mathematical **C** set. Complex numbers are of the form ``a + bi`` where ``i`` it the square root of -1.
	-  ComplexOverReals<T extends Real>; - Use a Real to type to store a numeric value for the real part and a ImaginaryOverReals<T> for the imaginary part.


Whole number literals are always assumed Natural and transformed to other types as needed. This conversion may rise ``OverflowException`` as the Natural type was no max value being limited only by memory available (It's like a BigInt with no sign). Decimal values are always assumed as BigDecimal. BigDecimal constructor only acepts a string representation of the value this is because the BigDecimal representation must be exact.

~~~~brush: lense
	var Natural n = 1; // equivalent to Natural.valueOf("1")
	var Whole n = 1; // equivalent to Natural.valueOf("1")
	
	// literals are always assumed to be Natural and promoted when necessary
	var Int32 i = 1;  // equivalent to Int32.valueOf(Natural.valueOf("1"));
	var Int16 s = 1;  // equivalent to Int16.valueOf(Natural.valueOf("1"));
	var Int64 k = 1;  // equivalent to Int64.valueOf(Natural.valueOf("1"));
	var BigInt g = 1;  // equivalent to BigInt.valueOf(Natural.valueOf("1"));
	
	// If the target is Integer it's equivalent to having BigInt as target 
	var Integer all = 1;  // equivalent to BigInt.valueOf(Natural.valueOf("1"));
	
	// sufixes can be used to inform the compiler the corret type of the literal
	// for whole numbers only uppercase prefixes are allowed 
	var Int32 ii = 1T;  // equivalent to Int32.valueOf("1");
	var Int16 ss = 1S;  // equivalent to Int16.valueOf("1");
	var Int64 kk = 1L;  // equivalent to Int64.valueOf("1");
	var BigInt gg = 1G;  // equivalent to BigInt.valueOf("1");
	
	// Rationals are defined by the division of two whole positive values. 
	var Rational r = 2/3; // equivalent to Natural.valueOf("2").divide(Natural.valueOf("3"))
	var Rational q = -5/8; // equivalent to Natural.valueOf("5").negate().divide(Natural.valueOf("8"));
	var Rational q = -x/y; // equivalent to Natural.valueOf(x).negate().divide(Natural.valueOf(y));
	
	// In this case 1 is a Natural being promoted to a Decimal32.
	var Decimal32 f = 1; // equivalent to Decimal32.valueOf(Natural.valueOf("1"));
	
	// decimal values are always assumed to be BigDecimals
	var Decimal32 ff = 1.6; // equivalent to Decimal32.valueOf(BigDecimal.valueOf("1.6"));
	var Decimal64 d = 2.0; // equivalent to Decimal64.valueOf(BigDecimal.valueOf("2.0"));
	var BigDecimal m = 1.234567890E100; // equivalent to BigDecimal.valueOf("1.234567890E100");

	// prefixes can also be used to informe the compiler the corret type of the literal
	// for non whole numbers only lowercase prefixes are allowed 
	var Decimal32 fff = 1.6f; // equivalent to Decimal32.valueOf("1.6");
	var Decimal64 dd = 2.0d; // equivalent to Decimal64.valueOf("2.0");
	var BigDecimal mm = 1m; // equivalent to BigDecimal.valueOf("1");
	
	
	var Imaginary a = 2i; // equivalent to Imaginary.valueOf(Natural.valueOf("2"));
	var Imaginary b = 2.5i; // equivalent to Imaginary.valueOf(BigDecimal.valueOf("2.5"));
	
	var Imaginary error = 2; // does not compile because natural ca 

	var Complex = 5 + 2i; // equivalent to Natural.valueOf("5").plus(Imaginary.valueOf(Natural.valueOf("2")))
	var Complex = 3.9 + 0.2i; // equivalent to BigDecimal.valueOf("3.9").plus(Imaginary.valueOf(BigDecimal.valueOf("0.2"))
~~~~
 
In any representation you can use _ to logically separate digits in the value to help readability.

~~~~brush: lense
	var Integer = -1_000_000;
~~~~


### Literals Representations 

Numeral literals are assumed to be represented in decimal form (base 10) for all types.
For naturals is possible to use the hexadecimal (base 16) form.

~~~~brush: lense
	var Natural color = #A3C1_F100; // hexadecimal
~~~~

Some ``Integer`` implementations like Int32 and Int64 implement ``Binary``. This means this numbers   

Remeber whole literals are always assumed to be Natural values in decimal representation and promoted to other types when necessary.

## Byte and Binary


Lense includes the ``Binary`` imutable interface to represent any  value can be understanded as a sequence of bits. Each bit is represented as a Boolean value.

``Byte`` is a special class that implements ``Binary`` corresponding to a sequence of 8 bits. ``Byte`` is not a number and does not have an assigned numeric value. Also it as no arithemetic operations. It's primarily used for I/O operations.

``Int16`` , ``Int32`` and ``Int64`` also implement ``Binary`` corresponding to a sequence of 16, 32 and 64 bits respectivly. Because this values have a signed 
numeric value one of the bits (the left most bit) is reserved to determine the sign. The rest of the bits represent the value if the value is possitive (left most bit is zero), else represent the Two Complement representation of the value.

``BitArray`` is a mutable implementation of a ``Binary`` with variable bit size. 

Lense offers a literal representation for ``Binary``. Like in the decimal and hexadecimal representations we can use _ to separate digits.  
All binary literals are assumed to be ``BitArray``s of the given number of bits. It is not possible to have zero bits. 

~~~~brush: lense
	var Byte byte = $1111_0000; // equivalent to Byte.valueOf(BitArray.valueOf(1,1,1,1,0,0,0,0));
	var Int16 short = $1111_0000_1111_0000; // equivalent to Byte.valueOf(BitArray.valueOf(1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0));
	var BitArray flags = $1111_0000_0101_0110_0010_0001_0101_1001; // equivalent to BitArray.valueOf(1,1,1,1,0,0,0,0,0,1,0,1,0,1,1,0,0,0,1,0,0,0,0,1,0,1,0,1,1,0,0,1);
~~~~	

A ``Byte`` can be transformed explictitly to a ``Natural`` between 0 and 255 or to a Integer between -128 and 127.
There is not automatic promotion from ``Byte`` to any type of ``Number``.

~~~~brush: lense
	var Byte byte = $1111_0000; 
	var Natural n = byte.ToNatural(); // equivalent to 240;
	var Integer i = byte.ToInteger(); // -16
	
	var Natural error = byte; // illegal. Byte is not assignable to Natural.
~~~~

# Object Orientation
## Classes 

A class represents a structural template of an object and acts like a factory and protype ate the same time.  
You can define a class with the <code>class</code> keyword. Classes can named or anonymous.

~~~~brush: lense
/**A polar coordinate**/
public class Polar {

	private val Float angle; // imutable values
	private val Float radius; // imutable values
	
	// an initializer , aka constructor
	public Polar (Float angle, Float radius){
		this.angle = angle;
		this.radius = radius;
	}
	
	// some operations
    public Polar rotate(Float rotation) {
        return new Polar(this.angle+rotation, this.radius);
    }

    public  Polar scale(Float scale) { 
       return new Polar(angle, radius*scale);
	}

    public String toString () {
    	return "({{radius}},{{angle}})";
    } 

}
~~~~

The values angle and radius are imutable, so the class as a whole is imutable. When enforce this fact adding <code>val</code> to the class definition

~~~~brush: lense
/**An imutable polar coordinate**/
public val class Polar {

 ...
}
~~~~

This will inform the compiler the values in the class are not ment to change and any tentative to do so will rise a compiler error.
If all constructor parameters are intented to be imutable and private a simpler syntax can be used

~~~~brush: lense
/**An imutable, simplified, polar coordinate**/
public class Polar (Float angle, Float radius) {

	// the constructor is removed
	
	// some operations
    public Polar rotate(Float rotation) {
        return new Polar(this.angle+rotation, this.radius);
    }

    public  Polar scale(Float scale) { 
       return new Polar(angle, radius*scale);
	}

    public String toString () {
    	return "({{radius}},{{angle}})";
    } 

}
~~~~

A very common use of a class is to model Property Bag objects. Property Bags are intrisicly mutable objets and so is necessary something complitly diferent 

~~~~brush: lense
/**An Addres as an example of a propertybag**/
public class Address  {

	public var String street;
	public var String number;
	public var String city;
	public var String zipcode;
	
}
~~~~

Bacause those are public properties we can assign values to them, an read those values back, but because variables need to be inicialized before used
and all properties are of type <code>String</code> that inicialization cannot be absent. So that code will not compile. We need to write it like this:


~~~~brush: lense
/**An Addres as an example of a propertybag**/
public class Address  {

	public var String? street;
	public var String? number;
	public var String? city;
	public var String? zipcode;
	
}
~~~~

In the case the value can be absent the compiler will inicialize it like this , automaticly:

~~~~brush: lense
/**An Addres as an example of a propertybag**/
public class Address  {

	public var String? street = null;
	public var String? number= null;
	public var String? city= null;
	public var String? zipcode= null;
	
}
~~~~

Remember ``null`` is a reserved word that represent <code>None.none</code>.