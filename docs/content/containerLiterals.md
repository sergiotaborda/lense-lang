title=Container Literals
date=2015-12-19
type=post
tags=tour, lense
status=published
~~~~~~

# Container Literals

Lense recognizes literals for ``Sequence``s, ``Association``s and ``Tuple``s. As a rule, because Lense has type inference it must be possible to infer the type of literal from the syntax, so we need a diferent sintax for each container type. 

## Sequences

In C-familly languages an array is a fundamental type and can be created literaly using curly brakets *{* and *}*, but in more mathematical oriented languages such as Fortran an Julia common brakets are used. 

~~~~brush: java
var int[] array = new int[]{1, 2, 3};
~~~~

On a closer look we can understand the C-familly languages syntax like Java or C# as not having array literals *per se*, but initializers applyed to arrays. So the syntax ``{1 ,2 , 3}`` does not denote an array, but an initializer. Initializers do not have type and they are intrinsicly understood by the compiler.

What's appening in that java code is that an array is being constructed with ``new int[]`` and then initialized with the values 1, 2 and 3. The JVM will translate this two operations as a single operation, but conceptually there is a difference.

In C# the same concept is used to initialize both arrays and lists 

~~~~brush: c#
var int[] array = new int[]{1, 2, 3};
var List<int> list = new List(){1, 2, 3};
~~~~

In Lense the type of the container is fixed by the syntax and a [convertion constructor](constructors.html#convertion) is used to copy the values to another type if needed. So no construtor is called. A runtime object is created that depends on the underlying VM. The type inside the container is infered from the declared value types.


Lense uses common brakets to represent ``Sequence`` literals. Keep in mind arrays are not fundamental types em Lense, sequences are.An array is a special (mutable) subtype of sequence.

~~~~brush: lense
var Sequence<Natural> sequence = [1, 2, 3];
var Array<Natural> array = [1, 2, 3];
~~~~

The first line creates a sequence of elements 1, 2 and 3. The second line creates an array of the elements 1, 2 and 3 by first creating a sequence and them promoting it to an Array. In practice the compiler is free to optimize these constructions and not really call the convertion constructor on ``Array<T>``.

## Sets 

Currently there is no special syntax for sets. A set can be created by using a literal sequence and a construtor.

~~~~brush: lense
var Set<Natural> sequence = new HashSet([1, 2, 2, 3, 4, 3]);
~~~~

Or, you can use the ``toSet`` method in ``Iterable`` (super type of ``Sequence``):

~~~~brush: lense
var Set<Natural> sequence = [1, 2, 2, 3, 4, 3].toSet();
~~~~

The set will only contain the elements 1, 2, 3 and 4 once.


## Associations

Associations are used to maintain pairs of values related to each other. Normally in a *key to value* relationship. 

~~~~brush: lense
var Association<Natural,String> association = { 1:"First", 2:"Second", 3:"Third" };
var Map<Natural, String> map =  { 1:"First", 2:"Second", 3:"Third" };
~~~~

Like ``Sequence``, ``Association`` is a fundamental type in Lense, so it has its own literal type.  

The first line creates an association of elements. The second line creates a map of the same elements by first creating an association and them promoting it to a Map. In Lense, ``Map`` is a specific (mutable) implementation of ``Association`` and not an interface like in Java. Is equivalent to ``HashMap`` in Java.

## Tuples

Tuples are similar to sequences, but each index has its own type. Tuples also are fundamental types in Lense.
Structuraly a Tuple is node in a linked-list kind of structure. But Tuples are not Sequences, eventhought they are ``Iterable<Any>``
The syntax is similar to Sequences.

~~~~brush: lense
var Tuple<Natural , Tuple<String, Tuple< Boolean, Nothing >>> tuple = ( 1 , "2" , true );
var (Natural , String , Boolean) association = ( 1 : "2" : true );
~~~~

The first line creates a tuple of elements. The second line creates the same tuple but uses the tuple short type syntax for representing tuple types. The default form used in the first line can be very long and odd to write and read eventhought is the exact correct type of that tuple. 


## Matrix (Under Consideration)

It could be possible to define a specific sintax for a two dimentional``Matrix``. This is not currenly implemented.

~~~~brush: lense
var Matrix<Natural> matrix = [ 1 ,2 ,3 ; 4, 5, 6 ; 5 6 7];

// also can be writen as

var Matrix<Natural> matrix = [ 
	1 ,2 ,3 ;
 	4, 5, 6 ; 
 	5, 6, 7
];
~~~~

This would  make a lot of sense in order to facilitate using Lense in more math and/or science scenarios. 
An alternative is to write sequence of sequences literals an use convertion constructors :

~~~~brush: lense
var Matrix<Natural> matrix = [ [1 ,2 ,3 ], [4, 5, 6 ], [ 5 6 7]];

// also can be writen as

var Matrix<Natural> matrix = [ 
	[1 ,2 ,3],
 	[4, 5, 6],
 	[5, 6, 7]
];
~~~~

The support for matrixes is still under consideration and may not be implemented. 

## Records

Records are a special object that represents a non typed property bag. Records are not currently implemented.
A possible sintax would be similar to an association, but using = instead of ":". The diference is that on an association both sides of the ":" can be variables,
in a record only the right side can be a variable. The left side is interpreted as the name of a property. 

~~~~brush: lense
var Record address = {
	Street = "Baker Street",
	Number = "221B",
	City = "Lodon",
	Country = "England"
}

~~~~

Records are a fundamental type in Lense and can be used to initialize other types like so

~~~~brush: lense
var Address address = new Address {
	Street = "Baker Street",
	Number = "221B",
	City = "London",
	Country = "England"
}

~~~~

This a combination of a call to the [primary construtor](constructors.html) and a initialization of each property refered in the record literal. This is not a convertion but syntax sugar for:

~~~~brush: lense
var Address address = new Address();

address.Street = "Baker Street",
address.Number = "221B",
address.City = "Lodon",
address.Country = "England"

~~~~

This feature is similar to the initialization syntax in C# even thought in Lense we are using the conjuction of two concepts : constructors and records.

  