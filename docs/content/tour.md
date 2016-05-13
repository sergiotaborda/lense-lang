title=Tour
date=2016-02-01
type=post
tags=tour, lense
status=published
~~~~~~

This a simple tour of the language. At this stage some feature are no decided upon, so sintax may change and features may be added or removed.
Features that may change are marked as *Under Consideration*.

# A basic Lense program (Under consideration)

~~~~brush: lense 
module application (1.0.0) {

	public Void run (){
		Console.println("Hello, world!");
	}
}
~~~~

Lense supports modules, and there must always exist a module in you application (you application *is* a module).
A module can be executable if it implements the ``run`` function. 

Each module must have a name and a version. In this case the name is "application" and the version is 1.0.0.
If you need to access to passed arguments (like in a command line application) you can read them from ``Runtime.Arguments``. This is a read only ``Sequence<String>`` containing the passed arguments.
If no arguments were passed or the application is running in an environment without access to arguments (like a web browser) the sequence is empty.

# Important concepts

Lense is base in some concepts that are always present.

+ 	Lense aims to be a universal language in the sense it can be executed on several target platforms. For these reason some features are only available for some platforms. For example, file system is not available for the JavaScript platform targeting browsers, since browser have no native access to file systems. On the other hand only a web application targeting JavaScript on a browser can call a DOM API and receive events and such.
For more on target platforms read the [plataforms guide](platforms.html)

+	Everything you can place in a variable is an object, and every object is an instance of a class. 
Even numbers and functions are objects. All classes inherit from the ``Any`` class. 

+	Modules, and not classes, are the units of deployment. All code compiled in Lense will produce a Module.
Modules can be organized in packages. Classes live in packages. Module are compiled depending on the target platform. The same module source code can produce several module archives, one for deployment in each platform. (Under revision)

+	Lense is strong typed and supports type inference. Specifying types in code allows the compiler, and other tools, to reason about your intent and is required at definition sites like classes, interfaces and methods but is optional at call site every-when the compiler could infer the type.

+	There is no explicit static scope. Hence, there is no ``static`` keyword. All members are objects that belong to objects. However, the ``object`` declaration allows to the definition of singleton objects that exist in a *static-like* context. You refer to these objects by their names as the name are unique. 
	
+	Identifiers can start with a letter,  followed by any combination of characters and digits.

# Reserved words and keywords

Lense, like all languages, reserves some words that cannot be used as identifiers. 
Some of these reserved works are keywords, e.i. they have special meaning to the compiler.

<table>
	<tr>
		<td>abstract</td>
		<td>as</td>
		<td>break</td>
		<td>case</td>
		<td>catch</td>
	</tr>
	<tr>
		<td>class</td>
		<td>continue</td>
		<td>default</td>
		<td>do</td>
		<td>else</td>
	</tr>
	<tr>
		<td>export</td>
		<td>extends</td>
		<td>finally</td>
		<td>for</td>
		<td>if</td>
	</tr>
	<tr>
		<td>import</td>
		<td>in</td>
		<td>inv</td>
		<td>module</td>
		<td>new</td>
	</tr>
	<tr>
		<td>null</td>
		<td>out</td>
		<td>package</td>
		<td>return</td>
		<td>super</td>
	</tr>
	<tr>
		<td>switch</td>
		<td>this</td>
		<td>throw</td>
		<td>try</td>
		<td>val</td>
	</tr>
	<tr>
		<td>var</td>
		<td>while</td>
		<td>true</td>
		<td>false</td>
		<td></td>
	</tr>
</table>

# Variables and Values

Creating variables in Lense is very similar to other languages.

~~~~brush: lense
var String name = "Alice";
~~~~

Variables always contain references to objects. The variable called *name* contains a reference to a ``String`` object with a value of "Alice".
The reference contained in a variable can be changed further down the code to another reference, like :

~~~~brush: lense
var name = "Alice";
name = "Beth";
~~~~

Lense prefers immutable references, so if you do not use the ``var`` keyword, Lense will assume the ``val`` keyword.

~~~~brush: lense
val String name = "Alice";
~~~~

or 

~~~~brush: lense
String name = "Alice"; // val is implicit.
~~~~

Immutable values cannot be changed, so trying to do so is a compilation error.

~~~~brush: lense
val String name = "Alice";
name = "Beth"; // Compilation Error 
~~~~

## Nullability

Variables (immutable or not) must be initialized with values. Lense does not allow uninitialized variables to be used. So, this code will fail:

~~~~brush: lense
String name;

Console.println(name); // Compilation error. Variable was not initialized.
~~~~

In addition, Lense as no concept of "null reference". References always exist.
This will also fail:

~~~~brush: lense
String name = null; // Compilation error. Lense does not recognize the null keyword (even though is a reserved word)

Console.println(name); 
~~~~

However, Lense understands possible absent values if you use a ``Maybe<T>`` type. The ``Maybe<T>`` type is a [monad type](monads.html).


~~~~brush: lense
val String? name;   

Console.println(name); // prints "null";

name = "Alice";

Console.println(name); // prints "Alice";
~~~~

The ? sign after the ``String?`` type is a shorthand notation for ``Maybe<String>``. 

# Built-in types
The Lense language has special support for the following types:

+ Number
+ Binary
+ String
+ Boolean
+ Progression
+ Interval
+ Sequence (like Arrays and Lists)
+ Associations (like Maps)
+ Tuple

You can initialize an object of any of these special types using a [literal](containerLiterals.html). For example, 'this is a string' is a string literal, and ``true`` is a boolean literal.

Because every variable in Lense refers to an object, that is an instance of a class, you can usually also use constructors to initialize variables. 
Some of the built-in types have their own constructors. For example, you can use the *Map()* constructor to create a map, using code such as ``new Map()``.

## Numbers

Numbers are separated in specific algebraic structures that conform to the mathematical rules of the group of elements.
All numbers are descendent types of the ``Number`` class. Operations are defined for each type independently.
Lense supports Natural, Integer, Real,  Complex and Imaginary numbers. A byte is not a number in Lense.

Natural is used as an indexer for sequences. It is non-negative and has big as you need. Limits for the size of collections like arrays, lists and maps are only bound by their implementation. Using a Natural to index sequences removes the necessity to check for negative indexes and as Arrays always have a upper limit and always are constructed by [factory like constructors](constructors.html)
the implementation for each platform can accommodate different implementations according to maximum length demand.
For more information on how ``Natural`` relates to index of sequences, see how [Arrays](arrays.html) work in Lense.

Arithmetic operations are defined by [Operator Interfaces](operators.html) so you can implement you own versions of the common operators.

For more detail on number visit the dedicated [numbers](numbers.html) page

## Strings

A string in Lense is a Sequence of Character. Characters are UTF-16 code points. A string literal is just a text enclosed in double quotes.

~~~~brush: lense
val String greating = "Hello, world";
~~~~

You can interpolate values inside literal strings using ``{{`` and ``}}`` as delimiters.

~~~~brush: lense
val String name = "Alice";
val String greating = "Hello, {{ name }}";
~~~~

You can concatenate strings using the ``+`` operator.

~~~~brush: lense
val String name = "Alice";
val String greating = "Hello, " + name;
~~~~

String are mulit-line, so you can simply right

~~~~brush: lense
val String greating = "Hello, 
	wold";
~~~~

The line break , tab and spaces in the second line will be preserved.

If you need to use a Unicode a special character enclosing an hexadecimal natural value with ``\{`` and ``}`` delimiters.

~~~~brush: lense
val String define pi = "The value of \{#03C0} is the ratio between the circumference and the diameter of a circle"
~~~~

## Booleans

To represent boolean values, Lense has a type named ``Boolean``. Only two objects have type Boolean: the boolean literals ``true`` and ``false``, which are both compile-time constants.
Lense is strong types and only allows Boolean values and expressions where a Boolean is expected. For example, the following code will not compile:

~~~~brush: lense
val String name = "Alice";
if (name){  // Compilation error. Expected Boolean expression
	printName(name);
}
~~~~


# Collections

Lense offers a rich API to handle collections. All collections in Lense are [monads](monads.html).
All collections inherit from the ``Assortment`` class and are read-only and immutable by design. Mutable implementations exist.

## Sequence

Sequences are assortments that let you assign a ``Natural`` index to each element. The elements can be iterated in the order of their indexes.
Sequences are immutable and read-only. Sequences are fundamental in Lense and not Arrays as in other languages (like Java).
Lense provides a very familiar syntax for sequences:

~~~~brush: lense
val Sequence<String> cities = ["New York", "London", "Paris"];

val london = cities[1]; // access by a natural index
~~~~

### Arrays

In Lense arrays are editable sequences. This means you can chance the values in each position of the sequence but you cannot change the sequence's size.
Arrays in Lense are fixed in size. To add a new element to the array you need to create a new array. Also keep in mind arrays in Lense are objects of the ``Array<T>`` class and not primitive types like in Java.

~~~~brush: lense
val Array<String> cities = ["New York", "London", "Paris"];

val london = cities[1]; // access by a natural index

cities[1] = "São Paulo"; // position 1 now refers to "São Paulo" and not to "London" any more.
~~~~

Because of Lense's [conversion constructors](constructors.html) you can initialize an Array with a Sequence literal.

### Lists

Lists are Sequence that are both editable (like Arrays) and resizeable. This means you can add and remove elements from a list after the list is created.

~~~~brush: lense
val List<String> cities = ["New York", "London", "Paris"];

cities.remove(1); // removes element at index 1, "London" in this example
cities.add("São Paulo"); // add a new element at end of the list
~~~~

### Progression 

Lense supports Progressions. A Progression is a special sequence of elements that has a *start* and an *end* and know how to iterate elements from the start to the end.
A Progression is normally created from a Progressable. A Progressable defines an ``upTo`` method that returns a progression.

~~~~brush: lense
val Progression<Natural> range = 1.upTo(9);
~~~~

This constructs a progression from 1 inclusive to 9 inclusive. Lense also supports an operator called ``..`` that you can use instead of ``upTo``.

~~~~brush: lense
val Progression<Natural> range = 1..9;
~~~~

## Association

Associations are like sequences, but instead of attributing an Natural index to each element, you can attribute an object to each element. Lense also provides a familiar literal for Associations.

~~~~brush: lense
val Association<String, String> personsAndJobs = { "Alice": "CEO", "Bob":"CIO" , "Claude":"CFO" };
~~~~

Like Sequences, Associations are immutable and read-only.

### Maps 

``Map<K,V>`` is an implementation of ``ResizableAssociation<K,V>`` you can use to manipulate editable and resizeable associations.  In Lense, Map is not an interface but an implementation (similar to an HashMap in Java)

~~~~brush: lense
val Map<String, String> personsAndJobs = { "Alice": "CEO", "Bob":"CIO" , "Claude":"CFO" };

personsAndJobs.removeKey("Alice"); // removes the key and its value.
~~~~

## Tuples

Tuples are special collections. The are sequences of objects in the sense each element has an index. The difference is that each element can be of a different type with no relation to the other elements (has in a sequence all elements have to of the same class or inherit from it).

~~~~brush: lense
val (String, Natural , Boolean) personsAndJobs = ("Alice", 42 , true);
~~~~

Lense provides the abose short sintax to create tuple's types and values. the compiler will translate that notation to the following notation that you can also use.

~~~~brush: lense
val Tuple<String, Tuple<Natural , Tuple<Boolean, Nothing>>> personsAndJobs = ("Alice", 42 , true);
~~~~

If you are interested, you can read [more on container literals](containerLiterals.html). 

# Functions

Functions allow for algorithms to be  encapsulated. Normally this algorithms depend on parameters that the function declares explicitly and return a value.
  
~~~~brush: lense
	 public Void doSomething() { 
	 	Console.print("Did something");
	 }
	 
	 Natural square (Natural x) { 
		return x*x; 
	 }
~~~~

Functions always return a value. ``Void`` is not a keyword is an actual type. ``Void`` only has one instance denoted ``()`` (the empty tuple). All functions have an implicit return of the instance of ``Void`` at the end. This is correct unless the method return another type. 
You can explicitly write a return of the instance of ``Void``.

~~~~brush: lense
	 public Void doSomething()  { 
	 	Console.print("Did something");
	 	return; // implicitly return the instance of Void.
	 };
~~~~

~~~~brush: lense
	 public Void doSomething()  { 
	 	Console.print("Did something");
	 	return (); // explicit return the instance of Void.
	 };
~~~~

Functions are objects named *Function*. Really is a type for each number of parameters. So ``Function<R>`` is for a function with no parameters that returns a type ``R``. ``Function<R,T>`` if for a single parameter function.
``Function<R,T,U>`` is for a function of two parameters, and do on ...

~~~~brush: lense
	 Function<Int, Int> f = x -> x*x; 
	 Function<Int, Int, Int> g = (x,y) -> x*y;
	 
	 Console.println(f(3));  
	 Console.println(g(3,2));
~~~~

Prints

~~~~console
9					
6					
~~~~

When functions are defined in the context of a class we talk about methods. Methods are functions bound to an instance of a class.
Method can make calls to the ``this`` variable that implicitly represent the instance the function is bonded to.

## Transforming Methods into Functions (Under Consideration)

Using reflection, methods can be converter to functions that can be invoked if the instance object is passed explicitly 

~~~~brush: lense
	val Integer one = 1;

    val Integer minusOne = one.negative();

    // extract the underlying function
    val Function<Number,Number> negativeOf = one::negative(); // f has a parameter of type Number representing the bounded value of *negative*.

    val Integer alsoMinusOne = negativeOf(one);
	val Integer minusTwo = negativeOf(2);
~~~~

Note the use of the `::` operator to detach members from object instances. 
You can do the same using the class instead of the instance

~~~~brush: lense
    // extract the underlying function
    val Function<Number,Number> negativeOf = Integer::negative(); // f has a parameter of type Number representing the bounded value of *negative*.

    val Integer alsoMinusOne = negativeOf(one);
	val Integer minusTwo = negativeOf(2);
~~~~

# Operators

Lense does not support operator overloading but lets you use operators like ``+`` and ``*`` in your own classes. Each operator is defined in a interface.
For example , for the ``+`` operator is the ``Summable<A,D,S>`` interface. These special interfaces are called *Operator Interfaces* in the documentation.

Note that this interfaces do not define the result or the parameters must be of the same type. They as generic as you can get.

Another family of interfaces define algebraic structures. These structures enforce other rules (like the types all be the same) and provide properties for the underlying type.
Algebraic structures help model some more abstract algebra concepts like *Magma*, *Group* , *Ring*  or *Field*. 

The different algebraic structures are the reason not all number types have the same operations. Integer, for example, do not have division, and so cannot for a *Field*.

# Control Flow Statements

Lense control flow is pretty much what you would expect and are a costumed to see in other languages.

## if-then-else

The *if-then-else* decision statement is pretty much the same as in Java , C# and other languages.

~~~~brush: lense
if (condition){
   // do somthing
} else if (otherCondition){
   // do this other thing
} else {
   // do something else.
}
~~~~

The ``if`` clause demands a Boolean condition to be evaluated. The condition must be of type boolean. Any other type will throw a compilation error.
You can chain and nest *if-then-else* as much as you like.

## while-do 

The *while-do* repetition structure is also pretty much the same as in Java , C# and other languages.

~~~~brush: lense
while (condition){
	// repeat this if condition is true
}
~~~~

## for-each

A very common task in object oriented programming is iterating over a collection of elements. Lense provides the *for-each* structure to help in this very common task.

~~~~brush: lense
for (var element in collection){
	// repeat this code for each element 
}
~~~~

You can use *for-each* with any object that implements the ``Iterable`` interface.
You can use type of the variable instead of var. If you use ``var`` the type will be infered from the collection signature.

~~~~brush: lense
for (String element in collection){
	// repeat this code for each element 
}
~~~~

Lense does not have the traditional increment base *for* like it exist in Java or C#.

~~~~brush: java
for (int i = 1; i <= 9  ; i++){  // this exists in Java and C#, not in Lense
	// repeat for each i
}
~~~~

Instead you can use a Progression.
 
~~~~brush: lense
for (var i in 1..9){
	// repeat for each i
}
~~~~

# Exceptions

Lense supports throwing Exceptions. An Exception is a special object. When an Exception is thrown the execution of the code stops and the method returns.
You can that catch the exception with a *try-catch-finally* statement.

~~~~brush: lense
try {
  // do something that can throw an exception
  throw new ArithmenticException();
} catch (ArithmenticException e) {
  // do something is the exception occurred
} finally {
  // do something either if the exception occurred or not.
}
~~~~

Lense does not support checked exception like Java does. 

# Classes

Lense supports classes and class inheritance to define objects and relations betweeen them. 
Lense also supports interfaces, object delcarations and traits (under consideration).

All Lense types suport fields, properties , methods and constructors as members. 
Lense also supports overloading of methods and genric types.

More on types on the [types page](objects.html).

# Generics

Lense supports reified generics with variance control. 

~~~~brush: lense
public interface Sequence<out T> { }

public interface Validator<in T> { }

public interface List<T> { }
~~~~

Lense support co-variant types (out), contra-variant types (in) and invariant types (default) 

# Modules and visibility (Under Consideration)

Lense supports modules. Every application or library is packaged as a module. A modulue is similar to a jar file in java or a dll in .Net,
but contains a little more structure.

Modules can import types from other modules and can export they own types to other modules to use.

Visibility modifies like ``private``, ``public`` and ``protected`` have the same semantics an in java or C#, however there is no *default* level like in java. 
If visibility is not explicit , ``private`` is used. 

A litte diference is that a type being maked as ``public`` adicionaly means that any other module can used it and are exported by default. If you do not want to export a classe use the ``internal`` visibility modifier.

# Parallelism and Concurrency (Under Consideration)

Lense does not support creating an control of threads directly nor support the commom memory model.
Instead Lense provides an actor based API to handle concurrency and parallelism. Parallelism is also ofered by special APIs lik Parallelism is supported by APIs like ``Iterable.asParallel()``. 

# Reflection (Under Consideration)

Lense offer a reflection API based on the ``Type`` class. 

~~~~brush: lense
   val Type stringType = typeOf(String);
   val Type alsoStringType = "some String".getType(); 

   Console.println("String as {{stringType.Methods.size}} methods");
~~~~

# Comments

Lense supports inline comments with ``//`` and multi-line comments with ``/{`` and ``}/``

~~~~brush: lense
/{
    this is a multi-line comment

     /{
       Multi-line comments can be nested
     }/
}/
public class Client {

     // the following line uses a single line comment to inform the role of the field
      val Natural name; // the name of the client       
}
~~~~

As in other languages comments should be avoid by renaming your types and members with better names, but some times
you will need them to explain some complex algorithm.