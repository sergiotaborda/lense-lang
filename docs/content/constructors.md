title=Constructors
date=2015-12-14
type=post
tags=tour, lense
status=published
~~~~~~

# Constructors

In an Object Oriented language there is the inate need to instanciate objects. The instanciation occurs in two phases. First the memory needed to hold the object information is alocated and pointed at by a reference. Then this memory space is filled with initialization information. Only after these two phases are complete the object really exists and can be used by other objects.

The allocation phase can be manual like in C , Python and Swift or automatic like in C# or Java. Lense opts for an automatic allocation.

The inicialization part is more delicate because the object must always exist in a valid state. 
For simple objects whose state is simply a buch of properties that are inicialized with default values, this process is straitfoward. However when calculations or other computations are needed this presents a problem.

So there needs to be a contract between the machinary responsable for allocating and inicializing the object and the object class. Some functions must be runned by this machinary in order to garantee the object is in a valid state before it can be release in the wild.

This special function invoked by the machinary is called *Constructor*. Thus, the constructor is often an inicialization method and it does not realy constructs the object. Because the object already exists when the constructor is called (created in the previous phase of the instanciation process) the constructor has access to it to ensure the properties in the object are correclty set to valid values, however it should not call any polimorphic methods on the object.

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

Pay attention the construtor is vary similar to a method but as no return type and its name is the same as the class.

Inside the constructor, the ``this`` keyword refers to the already allocated object. So, at this point, the object already exists and it is of the class ``Fraction``.The constructor has no means to return an object of another class because the constructor has no return.

The constructor , being a special kind of method can throw exceptions , however it is considered bad practice to do so[needs references].

Constructors are usefull to garantee the object state is correctly inicialized but have some limitations, so in practice you would prefer to use a static factory method , like so:


~~~~brush: java
public class Fraction {

	private int numerator;
	private int denominator;
	
	public static Fraction of(int numerator, int denominator){
		if (denominator == 0){
			trows new IllegalArgumentException("Denominator cannot be zero");
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

The validation of parameters is now moved to a method, so exceptions are allowed. The construtor is private so only the class can invoke it, the rest of the world would have to call the ``of`` static method.

The problem with static methods is that they are not inherited so, the static factory method pattern makes sense only for object not intented to be inherited from.
Nerthelesse this is considered a good practive e several API nowadays use this tecnhique to hide the construtor and provide a more fluent instanciation. 

A great think about this pattern is thar encasuplates the use of ``new`` so the class designed can change the paramters of the construtor at will wihout interfering with the call site.

People often criticize java for being to verbose. The numerator and denominator names appear 6 times.

## Types of construtors

Other languages come up with new flavors of construtors to try to reduce the problems with constructors. However, because construtors are essencially linked with the concept of object instanciation and state validity they cannot be removed from the languages. Some type of construtor must exist.

### Primary Construtor

This is special kind of construtors that only inicializes properties in the object. It is funcionally equivalent to the private construtor in our second example above. Because it can only set properties, languages try to come up with special (shorter) syntax.

In Scala, Ceylon and Kotlin, for instance, the primary construtor parameters are declared imediatly after the class name and extra code can be added in the class body without any other special delimiters

~~~~brush: scala

public class Fraction (Integer numerator, Integer denominator) {
    // other code goes where
}

~~~~  

This constructors imediatly inform the compiler there must be a *numerator* and a *denominator* field in the class and the values of the parameters should be directly assign to those fields. This really reduces the boilerplate but leaves the validation of state problem orfan. 

Scala resolves this by means of companion objects that have methods that act like a static factory methods calling the construtor only after validating the parameters are correct. Other special construtors are possible (called auxiliar construtors) but they action is limited. In scala construtors are pretty much ment only for field inicialization, other computations are made in methods on objects.

In languages with primary constructors implemented like this trade boilerplates :instead of having a delimited boilerplate inside a method like syntax, they provide a shorter syntax for the primary construtor making the other constructors syntax *ad hoc* and not simetric, even to allow code in the class body and require the programaer to conform to special rules for execution of code inside the class body and inside the auxiliar construtors. They remove the boilerplate of having a construtors that sets the fields but have no dedicated place for "advanced" construction code and the class sructure. If simple property bags in what you need this strategy really pais of, but it shows some crack for more complex types that need to isolate construction a little better.

Dart goes another way.

~~~~brush : dart
class Fraction  {
	Integer numerator;
	Integer denominator;

    Fraction (this.numerator, this.denominator)
}

~~~~  

The syntax is different, more in line with the C syntax like Java, but the intent is the same: reducing boilerplate. 
However we are obliged to repeat the class name simply by the convention rule construtors are created this way. 
Tradicionally the C familly languages do not use a keyword for the construtor because it was introduces in the language after de initial design and so create a keyword could conflit with existing names in existing code. So the designed made it so the code for the construtor was not valid code in the previous versions.
Them, by historic and similary reason more modern languages simply copied the syntax like Java and Dart.
The "same name" rule is not always the case. Scala uses ``def this()`` and some languages use ``new`` in an attempt to not introduce a dedicated keyword. 

In Lense the Primary construtors is writen :

~~~~brush: lense 

public class Fraction {

   val Integer numerator;
   val Integer denominator;
   
   constructor (Integer numerator, Integer denominator);
}

// invoke like 

val Fraction third = new Fraction(1, 3);
~~~~

A construtor without a body means the parameters should be copied to the fields.

All final value fields (the ones with ``val``) must be inicialized by all construtors, i.e. the compiler must be able to prove that all ``val`` fields have been set by the constructor. If this is not the case a compilation error will be raised.

There is no repetion of the class name and the keyword cleary states that the instruction is a construtor.
There is no boilerplate. The types on the parameters are needed since the private fields must not have the same types.

## Named Constructors

All is fine when the class only needs a construtor. But more time than people whould realise an object can be created by diferent forms. Design can argument this other forms should be handled by factory object and the class it self as only a set of parameters. While this can obviously accomplished is not practical. 

If we intend to have a ``Color`` type that can be created from RGB or HSL values the two algortithms are diferent and one or both require calculations before we can set the object private fields. On the other hand we need some practical way of distinguishing between them. Here the static methdo factory come in handy because it provides a name to the construction form, So in java we could write

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

Its a little odd to have dots in the name of the constructor , but at least is consistent with the call site. In Lense because we have the ``constructor`` key word  we write the same as:

~~~~brush: lense 
class Color {

	 val Natural color;
	 
	 constructor(Natural color);
	
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

The named constructors must, at some point, directly or indirectly, invoke the primary constructor. So the final conde should be something like

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

## Factory Constructor

Constructors in Lense are real factories and can create and return any instance. This means constructors can control the number of instances being created and choose to create specific subtypes. For instances the ``Array`` constructor is :

~~~~brush: lense 
public class Array<T> {

        constructor (Natural size){
        	if (T is Int32){
        	    return new Int32Array(size);
        	} else if (T is Int64){
        	    return new Int64Array(size);
        	} else if (T is Byte){
        	    return new ByteArray(size);
        	} else {
        	    return new ObjectArray<T>(size);
        	}
        }
}
~~~~

The ``Natural`` constructor is equivalent to:

~~~~brush: lense 
public class Natural extends Whole {

	object cache {
		val values = new Array<Natural>();
	}

    constructor (Natural other){
       	if (other >= -2 && other < 100){
       		val cached = cache.values[other].or(other); 
       		cache[cached] = cached;
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
    	
    		val Natural? digit = char.toDigit();
    		
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

It uses and ``object`` to hold ["static like" data](objects.html). If the String is not valid the construtor throws an exception.
This is valid because a construtors is like a factory, however the compiler will only allow the ``throw`` clause on a named constructor.
 
## Convertion Constructor

A convertion construtor is used to obtain the state of the object from another object of a different type. For instance:

~~~~brush: lense 
Integer k = 23;
~~~~

Because all whole literals are understanded by the compiler as ``Natural``s,  23 is really a ``Natural``. On the other hand, because ``Natural``s are not ``Integer``s the assignment would not be valid. Before a compilation error is risen, the compiler tries to find an anonymous constructor in the class Integer that is marked as ``implicit`` and has a single parameter of type ``Natural``. 

~~~~brush: lense 

public class Integer extends Whole {

	implicit constructor Integer(Natural other){
		return new Integer(other.toString()); // this is not the real code, just and example.
	}
}

~~~~

If it exists, the compiler changes the assignment to:

~~~~brush: lense 
Integer k = new Integer(23);
~~~~

The ``implicit`` keyword is necessary because not every anonymous constructor with a single parameter is meant to be a convertion construtor. The ``Array<T>`` class (used above) has a construtor that receives a ``Natural`` to set the array size,but that without the implicit keyword would mean that:

~~~~brush: lense 
Array<Integer> array = 3;
~~~~

was really 

~~~~brush: lense 
Array<Integer> array = new Array<Integer>(3);
~~~~

The instruction would be trying to assign the number 3 to an array but the compiler would try to promote the value.
This would not be a very coerent form to create arrays because can be confused with:

~~~~brush: lense 
Array<Integer> array = [3];
~~~~
 
The programmer may have forgoten to surrond the value to be put in the array with brakets.  

Also, this other example could be made to be valid code using a convertion constructor:

~~~~brush: lense 
Uri address = "http://www.google.com"
~~~~

But this form is not recomemded because implicity constructor, as primary constructors, cannot throw exceptions. 
So a parse operation, that possibly could go wrong, is not suited to a convertion constructor.It is recomended that a construtor based on a string be a named construtor like ``parse(String)``. Named constructors can throw exceptions.

As we can see from the above examples, that the convertion construtor is a simple way to promote values of one class to another but only if it is garanteed that convertion will never fail.

As a limitation of convertion construtors the process only works if the class on the left side of the assignment accepts the instances of the class on the right side as valid argument. 

## Construtors Enhancement

If the original designer of the left side class did not added the convertion constructor for some other class we can add one latter by creating an [enhancement](enhancements.html), like so:

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

This is very powerfull feature of [enhancements](enhancements.html) and can easly be abused, so please design enhancements with care.   


