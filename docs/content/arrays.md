title=Arrays
date=2016-05-11
type=post
tags=arrays, lense
status=published
~~~~~~

#Arrays

In other languages arrays are normally primitives (non-objects) or at least fundamental types (special types the compiler understands implicitly).
In Lense arrays are simply objects of the ``Array`` class that implements the ``EditableSequence`` interface that in turn implements the ``Sequence`` interface.
Not even sequences are the fundamental types, ``Iterable`` is.
This means there is no "new array[]" like sintax. Arrays are created like any other object.

~~~~brush: lense
	var Array<Boolean> flags = new Array<Boolean>(5);

    var Array<Natural> numbers = new Array<Natural>(5);

    var Array<Racional> fractions = new Array<Racional>(5);
~~~~

However arrays are a corner stone of several algorthms and specially of interoperability with native code. Lense leverages its [factory like constructors](constructors.html) and reified generics to provide the more specific/eficient implementation possible.
This means in reallity the Array type implementation is a native implementation. That native implementation will try to optimize space in memory using the native platform primitives and array types. Obviously this is only possible in some platforms.


#Indexing

In other languages because the array is a fundamental type it has special sintax indexing the array (read/ write data at a specifi array position).
Normally this index is an integer that ranges from positive to negative values, and the VM must validate the index is always positive or zero.
In Lense, all index are Natural. Natural ranges from zero onwards i nthe positive direction, so no index validation is necessary.
Lense also provide a special class member : the indexed property. An indexed proprty is like a common property but instead of a name has index parameters.
Index parameters can be of any tpye, and the Sequence interface leverages this kind of properties with Natural indexer to enable reading and writing from sequences, and specially from arrays.
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

#Inicialization

Arrays are objects that contain objects and because Lense does not have the concept of ``null`` all arrays must be inicialized correctly.
Normally you will use a List that allows you to start with zero elements in que collection and add the elements as you go.  When you use Array you need to supply the elements on creation time.
The easiest way to do this is with a literal

~~~~brush: lense
    var Array<Natural> numbers = [1, 2, 3 , 4 , 5];
~~~~

If you do not kown the values of the elements before hand but do not want to use a ``List`` ( or any ``ResizableSequence`` for that matter) you need to initialize the array with default values.

~~~~brush: lense
    // this code creates an array with 5 elements all equal to zero.
    var Array<Natural> numbers = new Array<Natural>(5, i -> 0);
~~~~

The Array constructor receives a lambda expression to inicialize each elements. The ``i`` parameter is the position in the array from 0 to the size array exclusivly.
Normally you will use a dummy constantvalue but you can inicialize the array with any function you want. For example, only even numbers:

~~~~brush: lense
    // this code creates an array with 5 elements corresponding to the first 5 even numbers.
    var Array<Natural> numbers = new Array<Natural>(5, i -> 2 * i);
~~~~

Alternativly you can create an array with an optional type. In this case initialization is not necessary, but you need to use a named constructor.

~~~~brush: lense
    // this code creates an array with 5 elements all equal to None.
    var Array<Natural?> numbers = new Array<Natural?>.ofNothing(5);
~~~~

A handy method is the empty constructor that creates an array with no elements at all. 

~~~~brush: lense
    // creates an array of non-optional elements with no elements in it
    var Array<Natural> numbers = new Array<Natural>.empty(); // equivalent to new Array<Natural>(0, i-> 0);

    // creates an array of optional elements with no elements in it
    var Array<Natural?> numbers = new Array<Natural?>.empty();  // equivalent to new Array<Natural?>.ofNothing(0);
~~~~