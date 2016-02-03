title=Monads
date=2016-02-03
type=post
tags=tour, lense
status=published
~~~~~~

#Monads

Lense supports the concept of Monad. A monad is a special container type that allows operations to occur on the contained type without explicitly requiring 
the boxing and unboxing of the contained instances. Monads are a special case of the Decorator pattern where all operations return a new object and transform the decorated type in some way.

Because all monads are containers, they rely on decorating a contained object. In Lense we do this simply by calling the monads constructor. 
You can also create [enhancements](enhancements.html) to facilitate some more common encapsulations.

The most common operation is to transform a monad object to another monad object of the same type operating in the contained elements. This is done by the map function.

#Iterable

Iterable is a monad that allows the iteration of elements in a given order. ``Sequence``s like ``Array``s and ``List``s implement ``Iterable``s . Also ``Association``s , ``Tuple``s and ``Progression`` are ``Iterable``s.
You can iterate over the elements of any ``Iterable<T>`` with the *for-each* directive and transform them in some way

~~~~brush: lense 
Sequence<String> cities = ["New York", "London", "Paris"];
List<Natural> lengths = new List<Natural>();

for (String city in cities){
	lengths.add(city.size());
}
~~~~

When you need to transform several containers or the transformations are complex this type of explicit iteration becomes cumbersome. 
Because all Iterables are monads , they have the ``map`` method that receives a lambda to operate over all elements. 

~~~~brush: lense 
Sequence<String> cities = ["New York", "London", "Paris"];

List<Natural> lengths =  cities.map( city -> city.size());
~~~~

This code simply applies a transformation to all elements of the original container (*cities*) and produces a new *Iterable<Natural>*. Them we assign that result to a List.
Because List has a conversion constructor based on Iterable the elements are copied to the list as if we had write

~~~~brush: lense 
Sequence<String> cities = ["New York", "London", "Paris"];

List<Natural> lengths =  new List<Natural>(cities.map( city -> city.size()));
~~~~

You can use several other methods like ``filter`` that allows you to exclude some values. 
For example, if we want to calculate the cubes of only odd numbers between 1 and 100 we can use a Progression and write 

~~~~brush: lense 
List<Natural> cubes =  1..100.filter(n -> n.isOdd()).map( n -> n ** 3));
~~~~

This a very simple, readable, code that means the same as 

~~~~brush: lense 
List<Natural> cubes = new List<Natural>();

for (Natural n in 1..100) {
	if (n.isOdd()) {
		lengths.add(n ** 3);
	}
}
~~~~

#Maybe

Maybe is a monad that allows us to manipulate possible absent values. ``Maybe<T>`` is an abstract type with only two subtypes : ``Some<T>`` and None. ``None`` has a single instance named ``none``. ``none`` represents the absence of value. 
Maybe is equivalent to the ``Optional`` type that exists in other languages. However, in Lense, Maybe is a fundamental type and its the only way you can handle the concept of "null" since Lense does not allow the traditional ``null`` reference value. 

~~~~brush: lense 
public Maybe<Natural> calculateNameLength(Maybe<String> name){
	return name.map(m -> m.size());
}
~~~~

Because Maybe is a fundamental type in Lense we can simplify this method with some shorter syntax:

~~~~brush: lense 
public Natural? calculateNameLength(String? name){
	return name.map(m -> m.size());
}
~~~~

This simple method returns the length of the given name and we can called it like:

~~~~brush: lense 
Natural?  x = calculateNameLength("London"); // x holds a Some<Natural> with a value 6 inside
Natural?  y = calculateNameLength(none); // y holds the instance of 'none'. 
~~~~

Note that the methods does not use any decision directive to handle the absence of value directly. This is handled by the ``map`` method it self.
If the original Maybe is really and instanceof of ``Some`` it has some value in it, that value is passed to the given lambda and a new ``Some`` is created having inside the calculated value.
If the original Maybe is really a ``none`` the lambda is not invoked and ``none`` is returned.

You can, if you want to, transform a Maybe<T> to a T by offering a default value, like this:

~~~~brush: lense 
Natural?  x =  ... // obtain in some way

Natural size = x.or(0);
~~~~

this means if *x* has a value, that value will assigned to *size*, otherwise 0 will be assigned.
