title=Container Literals
date=2015-12-19
type=post
tags=tour, lense
status=published
~~~~~~

# Container Literals

Lense recognizes literals for ``Sequence``s, ``Association``s and ``Tuple``s. As a rule, because Lense has type inference it must be possible to infer the type of literal from the syntax, so we need a diferent sintax for each container type. 

## Sequences

For sequences tipally in C-familly languages an array can be created using curly brakets *{* and *}*, but in more mathematical oriented languages such as Fortran an Julia common brakets are used.  C-familly languages like Java do not have array literals *per se*, they have array initializers:

~~~~brush: java
var int[] array = new int[]{1, 2, 3};
~~~~

What's appening is that an array is being constructed with ``new int[]`` and then initialized with the values 1, 2 and 3. The JVM will make this both operations as a single operation, but conceptually is a different thing.

In C# the same concept is used to initialize both arrays and lists 

~~~~brush: c#
var int[] array = new int[]{1, 2, 3};
var List<int> list = new List(){1, 2, 3};
~~~~

In Lense the type of the container is fixed by the syntax and a [convertion constructor](constructors.html#convertion) is used to copy the values to another type if needed. The type inside the container is infered from the declared value types. Also initializers are used a little different so there is no need to use the same sintax as other C-familly languages. Lense uses common brakets to represent sequences. Keep in mind arrays are not fundamental types em Lense, sequences are.

~~~~brush: lense
var Sequence<Natural> sequence = [1, 2, 3];
var Array<Natural> array = [1, 2, 3];
~~~~

The first line creates a sequence of elements 1, 2 and 3. The second line creates an array of the elements 1, 2 and 3 by first creating a sequence and them promoting it to an Array. 

## Sets 

Currently there is no special syntax for sets. A set can be created by promoting a literal sequence. 

~~~~brush: lense
var Set<Natural> sequence = [1, 2, 2, 3, 4, 3];
~~~~

This code creates a sequence of not unique values and promotes it to a Set. The set will only contain the elements 1, 2, 3 and 4 once.


## Associations

Associations are used to maitain pair or values related to each other. Normally in a key to value relationship. 

~~~~brush: lense
var Association<Natural,Natural> association = { 1:2, 2:4, 3:6 };
var Map<Natural, Natural> map = { 1:2, 2:4, 3:6 };
~~~~

Like ``Sequence``, ``Association`` is a fundamental type in Lense, so it has its own literal type.  

The first line creates an association of elements. The second line creates a map of the same elements by first creating an association and them promoting it to a Map. 

## Tuples

Tuples are similar to sequences, but each index has its own type. Tuples also are fundamental types in Lense.
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

This would  make a lot of sense in order to facilitate using Lense in more math and science scenarios. 
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

Records are a special object that represents a non typed property bag.
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

Records are fundamentaly a type in Lense and can be used to initialize other types like so

~~~~brush: lense
var Address address = new Address {
	Street = "Baker Street",
	Number = "221B",
	City = "Lodon",
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

  