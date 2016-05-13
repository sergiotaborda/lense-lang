title=Arrays
date=2016-05-11
type=post
tags=arrays, lense
status=published
~~~~~~

#Arrays

In other languages arrays are normally [primitive types](glossary.html#primtiveTypes) or at least [fundamental types](glossary.html#fundamentalTypes). In Lense arrays are simply objects of the ``Array`` class. This class belongs to the Collections API and receive no special handling by the language or the compiler. The Collections API is a set of common object structures like hash table and linked list implemented in coherent framework.

This means you create arrays as you would any other class (i.e invoking the constructor). There is no ``new someArray[2]`` sintax for array definition. Arrays in Lense are the default implementation of the ``EditableSequence`` interface that behave like arrays in other languages. They have fixed length and values can be edited, but must be pre-initialized. 

#Inicialization

Because Lense does not have the concept of ``null`` all arrays must be inicialized correctly to a specified value. There are no default values. If you cannot determine the initialization values you will be better using a ``List``( or any other ``ResizableSequence`` like ``LinkedList``) that allows you to start with zero elements in que collection and add the elements as you go.  When you use ``Array`` you need to supply the elements at creation time. The easiest way to do this is with a literal:

~~~~brush: lense
    var Array<Natural> numbers = [1, 2, 3 , 4 , 5];
~~~~

Remember ``Array`` is not a fundamental type in Lense. That literal above really creates a ``Sequence`` object that is read-only by definition. The compiler then uses a [conversion constructor](constructors.html#conversion) to create the ``Array```from the ``Sequence``. If you wish to initilize all position in the array to the same value you can use a special [named constructor](constructors.html#named):

~~~~brush: lense
    // this code creates an array with 5 elements all equal to zero.
    var Array<Natural> numbers = new Array.filled<Natural>(5, 0);
~~~~

Here we will be writing the examples with no generic type inference, but normally you would write with less ceremony:

~~~~brush: lense
    var numbers = new Array.filled(5, 0);
~~~~

Its all the same.

If a constant is not a good option in your case you can, alternativly, use the overloaded vertion that receives a function.

~~~~brush: lense
    // this code creates an array with 5 elements corresponding to the first 5 even numbers.
    var Array<Natural> numbers = new Array.filled<Natural>(5, i -> 2 * i);
~~~~

This constructor receives a lambda expression to inicialize each elements. The ``i`` parameter (can be any variable name) is the position in the array from 0 to the size array exclusivly. In the example we inicialize the arrays with the first 5 even numbers.

# Optional values
Alternativly you can create an array with an optional type. This means each position of the array can have an absent value. You can create an array of optional type the same way you create it for any other type:

~~~~brush: lense
    // this code creates an array with 5 elements all equal to none.
    var Array<Natural?> numbers = new Array.filled<Natural?>(5, none);
~~~~

Remember ``none`` is the single value of type ``None`` that is equivalent to ``Maybe<Nothing>``. You can also use the shorter constructor:

~~~~brush: lense
    // this code creates an array with 5 elements all equal to none.
    var Array<Natural?> numbers = new Array.ofAbsent<Natural?>(5);
~~~~

# Empty Arrays

In practice is useful to be able to create an empty array. The empty constructor solves this problem creating an array with no elements at all. 

~~~~brush: lense
    // creates an array of non-optional elements with no elements in it
    var Array<Natural> numbers = new Array.empty<Natural>(); 

    // creates an array of optional elements with no elements in it
    var Array<Natural?> numbers = new Array.empty<Natural?>(); 
~~~~

In this case there is no diference if you use optional types as the arrays has no elements (its size is zero).

# Indexing

Arrays are specially useful because the values of at each position can be access by a second variable : the index. Normally this index is an integer.
In java , for example, you would write:

~~~~brush: java
    int[] array = new int[]{1,2,3};
	
	int x = array[1]; // x is 2
	
	array[2] = 6; // array position 2 is now 6 instead of 3.
~~~~

In java, and other languages, the array is a fundamental type and so the language and the compiler have special treatment for the index operator ``[]``. In Lense arrays are not fundamental, but the index operator is.
In reallity is not an operator is the way you use [indexed properties](properties.html#indexed). Indexer properties are a type of member of types that allow to read and write values based on indexes. 
In Lense all ``Sequence``s have an indexed property for reading values at a given position in the sequence, and for ``Array``s is also possible to write to that property. 

This means you can still use the ``array[i]`` sintax to read from and write to array positions.

~~~~brush: lense
public Void updateArray(Array<Natural> numbers) {
    numbers[0] = 1;
    numbers[1] = 2;
    numbers[2] = 3;
    numbers[3] = 2 * numbers[1];
    numbers[4] = numbers[1] + numbers[2];
}
~~~~

Addicionally indexes for all ``Sequences`` are ``Natural``s. Natural ranges from zero onwards in the positive direction, so no "negative index" validation is necessary.

# Interoperability

Even though arrays are not fundamental types in Lense, they probably are in the native platform where Lense is running. When calling native code the compiler will need to convert from and to native arrays. Plataform native arrays noramlly accept ``null`` as valid value, because we cannot accept this in Lense all interoperability is constructed with arrays of optional types.  Arrays them selfs can be objects in the native plaftform so then selfs can be ``null``even thought this is not a good practice, there is valid form to handle this automaticly.  In Java 8 and above types can be annotated with ``@NotNull`` and in this case would be possible to determine the type is really not optional.  This will better exploded and detailed when we discuss using types of the native platform inside Lense it self, a feature not yet designed ate [this stage](status.html).

On the other hand, when creating instances of arrays, the Lense will leverage native arrays to minimize space consumptidon and optimize speed. This is done by leveraging Lense's [factory like constructors](constructors.html#factory) and reified generics to provide the more specific/eficient implementation possible. Obviously this is only possible in some platforms.

