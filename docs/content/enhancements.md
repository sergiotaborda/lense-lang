title=Enhancements
date=2015-12-14
type=post
tags=tour, lense
status=published
~~~~~~

# Enhancements

Normally we extend a class by means of inheritance. Inheritance may not be a good solution in some cases.
Sometimes we need to extends the funcionally of a class without adding to the inheritance tree, e.g. when the class we are trying to extend is final. 
The standard pattern to do this is via a Decorator. A decorator holds a value of the original class and adds methods to it. 
Both class can share the same interface but normally that does not happen.  

Decorator is a solution for the problem and can be implemented in any object oriented language, however is not very practical.

Other languages like C# and Gosu, enable the use of Extention Methods. Extention Methods are static methods declared 
in a special way so they can be called as normal methods (after a dot). Being static methods this methods can also be 
called normally using the static method calling syntax. 

~~~~brush:csharp
var domain = 'some@e.mail.com".GetDomain(); // GetDomain is not a standard method of type String
Assert.AreEquals("e.mail.com", domain);
~~~~

Lense does not have static elements, so an extention method must be declared on an object and we apply the methods of that
object to the original instance of the class we want to enhance. 

When enhancing a given type we are in fact enhancing all subtypes aswell so polimorfism is at work. More generaly we
can enhance types that conform to some generic rules and thus generics support is needed for enhancement.

The difference between enhancements and extention methods is that we can add more that just normal methods. We can also
add constructors. 

The difference between enhancements and traits it that traits are a inheritance base mechanism where when doing the mixin a new type is created
while enhacements do not create a new type. They operate like static calls.

When compiling to he underlying platform the lense compiler does not really create an object so the methods can be called, the compiler is
free to optmize the calls using static semantics if they are possible in he platform. 

Enhancement implementations can only access public members of the enhanced type.

Interfaces cannot be fullfiled by adding enhancement methods.