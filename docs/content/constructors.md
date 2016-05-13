title=Constructors
date=2015-12-14
type=post
tags=tour, lense
status=published
~~~~~~

# Constructors

In an Object Oriented language there is the innate need to instantiate objects. The instantiation occurs in two phases. First the memory needed to hold the object information is allocated and pointed at by a reference. Then this memory space is filled with initialization information. Only after these two phases are complete the object really exists and can be used by other objects.

The allocation phase can be manual like in C , Python and Swift or automatic like in C# or Java. Lense opts for an automatic allocation.

The initialization part is more delicate because the object must always exist in a valid state. 
For simple objects whose state is simply a bunch of properties that are initialized with default values, this process is straightforward. However when calculations or other computations are needed this presents a problem.

So there needs to be a contract between the machinery responsible for allocating and initializing the object and the object class. Some functions must be runned by this machinery in order to guarantee the object is in a valid state before it can be release in the wild.

This special function invoked by the machinery is called *Constructor*. Thus, the constructor is often an initialization method and it does not really constructs the object. Because the object already exists when the constructor is called (created in the previous phase of the instantiation process) the constructor has access to it to ensure the properties in the object are correctly set to valid values, however it should not call any polymorphic methods on the object.

As an example, in Java, we would write something like:

~~~~brush: java
public class Fraction {

	private int numerator;
	private int denominator;
	
	public Fraction (int numerator, int denominator){
		if (denominator == 0){
			trows new IllegalArgumentException("Denominator cannot be zero");
		}
		this.numerator = numerator;
		this.denominator = denominator;
	}
}

// and can be used like 

Fraction third = new Fraction(1,3);
~~~~   

Pay attention the constructor is vary similar to a method but as no return type and its name is the same as the class.

Inside the constructor, the ``this`` keyword refers to the already allocated object. So, at this point, the object already exists and it is of the class ``Fraction``.The constructor has no means to return an object of another class because the constructor has no return.

The constructor , being a special kind of method can throw exceptions , however it is considered bad practice to do so[needs references].

Constructors are usefully to guarantee the object state is correctly initialized but have some limitations, so in practice you would prefer to use a static factory method , like so:


~~~~brush: java
public class Fraction {

	private int numerator;
	private int denominator;
	
	public static Fraction of(int numerator, int denominator){
		if (denominator == 0){
			throws new IllegalArgumentException("Denominator cannot be zero");
		}
		return new Fraction(numerator, denominator);
	}
	
	private Fraction (int numerator, int denominator){
		this.numerator = numerator;
		this.denominator = denominator;
	}
}

// but now the object is created like 

Fraction third = Fraction.of(1,3);
~~~~  

The validation of parameters is now moved to a method, so exceptions are allowed. The constructor is private so only the class can invoke it, the rest of the world would have to call the ``of`` static method.

The problem with static methods is that they are not inherited so, the static factory method pattern makes sense only for object not intended to be inherited from.
Nevertheless considered a good practice e several API nowadays use this technique to hide the constructor and provide a more fluent instantiation. 

A great thing about this pattern is that it encasuplates the use of ``new`` so the class designed can change the parameters of the constructor at will without interfering with the call site.

People often criticize java for being to verbose. The numerator and denominator names appear 6 times.

## Types of constructors

Other languages come up with new flavors of constructors to try to reduce the problems with constructors. However, because constructors are essentially linked with the concept of object instantiation and state validity they cannot be removed from the languages. Some type of constructor must exist.

<a name="primary"></a> 
### Primary Constructor

This is special kind of constructors that only initializes properties in the object. It is functionally equivalent to the private constructor in our second example above. Because it can only set properties, languages try to come up with special (shorter) syntax.

In Scala, Ceylon and Kotlin, for instance, the primary constructor parameters are declared immediately after the class name and extra code can be added in the class body without any other special delimiters:

~~~~brush: scala
public class Fraction (Integer numerator, Integer denominator) {
    // other code goes where
}
~~~~  

This constructors immediately inform the compiler there must be a *numerator* and a *denominator* field in the class and the values of the parameters should be directly assign to those fields. This really reduces the boilerplate but leaves the validation of state problem orphan. 

Scala resolves this by means of companion objects that have methods that act like a static factory methods calling the constructor only after validating the parameters are correct. Other special constructors are possible (called auxiliary constructors) but they action is limited. In scala constructors are pretty much meant only for field initialization, other computations are made in methods on objects.

In languages with primary constructors implemented like this trade one boilerplate for asymmetry: instead of having a delimited boilerplate inside a method like syntax, they provide a shorter syntax for the primary constructor making the other constructors syntax *ad hoc* and not symmetric, even to allow code in the class body and require the programmer to conform to special rules for execution of code inside the class body and inside the auxiliary constructors. They remove the boilerplate of having a constructors that sets the fields but have no dedicated place for "advanced" construction code and the class structure. If simple property bags in what you need this strategy really pays off, but it shows some cracks for more complex types that need to isolate construction a little better.

Dart goes another way.

~~~~brush : dart
class Fraction  {
	Integer numerator;
	Integer denominator;

   Fraction (this.numerator, this.denominator)
}

~~~~  

The syntax is different, more in line with the C syntax like Java, but the intent is the same: reducing boilerplate, but maintaining the tradiconal way constructors are represented.
However we are obliged to repeat the class name simply by the convention rule constructors are created this way. 
Traditionally the C family languages do not use a keyword for the constructor because it was introduces in the language after de initial design and so create a keyword could conflict with existing names in existing code. So the designed made it so the code for the construtor was not valid code in the previous versions.Them, by historic and similarity reason more modern languages simply copied the syntax like Java and Dart.
The "same name" rule is not always the case. Scala uses ``def this()`` and some languages use ``new`` in an attempt to not introduce a dedicated keyword. 

In Lense the primary constructor is written :

~~~~brush: lense 
public class Fraction {

   val Integer numerator;
   val Integer denominator;
   
   constructor (Integer numerator, Integer denominator);
}

// invoke like 

val Fraction third = new Fraction(1, 3);
~~~~

A constructor without a body means the parameters should be copied to the fields of the same name.

All final value fields (the ones with ``val``) must be initialized by all constructors, i.e. the compiler must be able to prove that all ``val`` fields have been set by the constructor. If this is not the case a compilation error will be raised.

There is no repetition of the class name and the keyword clearly states that the instruction is a constructor.
There is no boilerplate. The types on the parameters are needed since the private fields must not have the same types.

<a name="named"></a> 
## Named Constructors

All is fine when the class only needs a constructor. But more time than people would realize an object can be created by different forms. Design can argument this other forms should be handled by factory object and the class it self as only a set of parameters. While this can obviously accomplished is not practical. 

If we intend to have a ``Color`` type that can be created from RGB or HSL values the two algorithms are different and one or both require calculations before we can set the object private fields. On the other hand we need some practical way of distinguishing between them. Here the static method factory come in handy because it provides a name to the construction form. So in java we could write

~~~~brush: java 
Color a = Color.fromRGB(1.0 , 1.0 , 1.0);
Color b = Color.fromHSL(60 , 0.5 , 0.5); 
~~~~

However there is no ``new`` keyword being used. Dart provides the same sintax but using ``new``:

~~~~brush: dart 
Color a = new Color.fromRGB(1.0 , 1.0 , 1.0);
Color b = new Color.fromHSL(60 , 0.5 , 0.5); 
~~~~

In Dart you can provide named constructors like

~~~~brush: dart 
 class Color {

    Color.fromRGB(red, greee, blue){
         // code goes here  
    }
    
    Color.fromHSL(hue, saturation, lightness){
         // code goes here  
    }

}
~~~~

Its a little odd to have dots in the name of the constructor , but at least is consistent with the tradiconal constructor syntax. In Lense because we have the ``constructor`` key word we simply write the same as:

~~~~brush: lense 
class Color {

    constructor fromRGB(Rational red, Rational greee,Rational blue){
         // code goes here  
    }
    
    constructor fromHSL(Angle hue,Rational saturation,Rational lightness){
         // code goes here  
    }

}
~~~~

and invoke them in the same way 

~~~~brush: lense 
Color a = new Color.fromRGB(1.0 , 1.0 , 1.0);
Color b = new Color.fromHSL(60 , 0.5 , 0.5); 
~~~~

Note the similarity with the anonymous constructor invocation.

The named constructors must, at some point, directly or indirectly, invoke the primary constructor. So the final code should be something like

~~~~brush: lense 
public class Color {

	 val Natural rgb;
	 
	private constructor(Natural rgb);
	
    public constructor fromRGB(Rational red, Rational greee,Rational blue){
         	Natural rgb = red * 255;
			rgb = (rgb << 8) + green * 255;
			rgb = (rgb << 8) + blue * 255;
			return new Color(rgb);
    }
    
    public constructor fromHSL(Angle hue,Rational saturation,Rational lightness){
         // code goes here to caculate red, green and blue from the parameters , then call the fromRGB constructor
         Rational red = ...
         Rational green = ...
         Rational blue = ...
         return new Color.fromRGB(reg,green,blue);
    }

}
~~~~

Notice how the ``new`` keyword is used to call the other constructors. In fact constructors in Lense act as factories and can return any object that could be assigned to the class.

<a name="factory"></a> 
## Factory Constructor

Constructors in Lense are real factories and can create and return any instance. This means constructors can control the number of instances being created and choose to create specific sub types. For instances the ``Array`` constructor is :

~~~~brush: lense 
public class Array<T> implement EditableSequence<T> {

        constructor filled(Natural size, T value){
        	if (T is Int32){
        	    return new Int32Array(size, value);
        	} else if (T is Int64){
        	    return new Int64Array(size, value);
        	} else if (T is Byte){
        	    return new ByteArray(size, value);
        	} else {
        	    return new ObjectArray<T>(size, value);
        	}
        }
		
		constructor ofAbsent<T?>(Natural size){
        	return new Array.filled<T?>(size, none);
        }
		
		// other methods
}
~~~~

The ``Natural`` constructor is equivalent to:

~~~~brush: lense 
public class Natural extends Whole {

	object cache {
		val values = new Array.absent<Natural>(10);
	}

    constructor (Natural value){
       	if (value >= 0 && value < cache.values.size -1){
       		val cached = cache.values[value].or(value); 
       		cache.values[cached] = cached;
       		return cached;
       	}
       	return other;
    }
    
    constructor parse (String value){
    	if (value.startsWith("-")){
    		throw new ParseException("Value cannot be negative");
    	}
    	if (value.contains(".")){
    		throw new ParseException("Value cannot be decimal");
    	}
    	
    	Natural power = 0;
    	Natural value = 0;
    	for(char in value.replaceAll("_",""){
    	
    		val digit = char.toDigit();
    		
    		if (digit == none){
    			throw new ParseException(char + "is not a digit.");
    		}
    		value += digit * 10**power;
    		
    		power++;
    	}
    	
    	return new Natural(value);
    }
}

~~~~

It uses and [``object``](objects.html#object) to hold the cache data. If the given ``String`` is not valid the constructor throws a ``ParseException``.
This is valid because a constructors is like a factory, however the compiler will only allow the ``throw`` clause on a named constructor.
 
<a name="conversion"></a> 
## Implicit Conversion Constructor

A conversion constructor is used to obtain the state of the object from another object of a different type. For instance:

~~~~brush: lense 
Integer k = 23;
~~~~

Because all whole literals are parser by the compiler as ``Natural``s,  23 is really a ``Natural``. On the other hand, because ``Natural``s are not an ``Integer``s the assignment would not be valid. Before a compilation error is risen, the compiler tries to find an constructor in the class Integer that is marked as ``implicit`` and has a single parameter of type ``Natural``. 

~~~~brush: lense 
public class Integer extends Whole {

	implicit constructor (Natural other){
		return new BigInt(other.toString()); // this is not the real code, just and example.
	}
}

~~~~

If it exists, the compiler changes the assignment to:

~~~~brush: lense 
Integer k = new Integer(23);
~~~~

The ``implicit`` keyword is necessary because not every constructor with a single parameter is meant to be a conversion constructor. 
The ``List<T>`` class (used above) has a constructor that receives a ``Natural`` to set the array size,but that, without the implicit keyword would mean that:

~~~~brush: lense 
List<Integer> list = 3;
~~~~

was really 

~~~~brush: lense 
List<Integer> list = new List<Integer>(3);
~~~~

The instruction would be (wrongly) trying to assign the number 3 to the list but the compiler would try to promote the value.
This would not be a very coherent form to create arrays because can be confused with:

~~~~brush: lense 
List<Integer> list = [3];
~~~~
 
The programmer may have forgotten to surround the value with brackets.  

Also, this other example could be made to be valid code using a conversion constructor:

~~~~brush: lense 
Uri address = "http://www.google.com"
~~~~

But this form is not recomemded because implicit constructors, as primary constructors, can not throw exceptions (under consideration). 
So a parse operation, that possibly could go wrong, is not suited to a conversion constructor. It is recomemded that a constructor based on a string be a named constructor like ``parse(String)``. Named constructors can throw exceptions.

As we can see from the above examples, that the conversion constructor is a simple way to promote values of one class to another but only if it is guaranteed that conversion will never fail.

As a limitation of conversion constructors the process only works if the class on the left side of the assignment accepts the instances of the class on the right side as valid argument. 

## Constructors Enhancement (Under Consideration)

If the original designer of the left side class did not added the conversion constructor for some other class we can add one latter by creating an [enhancement](enhancements.html), like so:

~~~~brush: lense 
public enhancement AddNaturalConvertionConstrutorToString extends String { // enhances String

	public implicit constructor (Natural n){
	       return n.toString();
	}
}
~~~~

With this enchamenent in scope we can write:

~~~~brush: lense 
String s = 8; // not supported without the enhancement
~~~~

This is very powerful feature of [enhancements](enhancements.html) and can easly be abused, so please design enhancements with care.   


