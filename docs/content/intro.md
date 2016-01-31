title=Home
date=2015-12-12
type=post
tags=tour, lense
status=published
~~~~~~
# Lense

Lense is a strong typed, pure object oriented programming language that is - we hope - carefully crafted from merging what we think is and ideal aproach to a new modern programming language with a meticulous observation and analysis of other languages design goals and decisions (and mistakes, yes). Even with this analysis and comparation with the state of art, Lense does not aim to be an academic driven language even when it tries to follow a more scientific and logic approach to design decision making.

Lense goal is to be a simple language, easy to read and write that could be compiled to any object oriented platform. The first effort will focus on running Lense in the Java Virtual Machine (JVM). Other possible platforms are Javascript and CRL (.NET)

# Some features to expect 

Familiar syntax : Lense uses a c family style syntax. 

No primitives : all types are treated as objects in the heap. The compiler is free to optimize this to the plataform's primitives as it sees fit. The programmer can define immutable objects in order to hint to the compiler that is a good idea to optimize that class but this is not mandatory.

No nulls : all variables must have been initialized at some point. By default no object type includes the null value. The maybe monad is used when the "absent" value is required. The compiler can understand the maybe monad implicitly in order to free the programmer of writing a special syntax for possible absent value handling, this means... 

No strange null handling operators : because nulls cannot exist, the programmer does not need to inform the compiler about dereference safety and so operators like .? or :? are not needed. When interacting with other libraries written in the plataform's native language the compiler will wrap all call in a safe manner in order to maintain the code simple and do not stress the programmer with annoying details,

No symbolic noise : operator symbols are predefined and associated with specific interfaces so classes like numbers and strings can use operators.However defining you own operator symbol is not allowed in order to maintain the code simple to read. The use of interfaces to define operations follows an algebraic structure paradigm so the compiler can reason about the operations (example : altering the order of operations the enhance performance if the operation is commutative)

Reified Generics : The information of the generic type parameters can be reflected upon at runtime. 

Generics with variance : Types can have generic parameters and this parameters can declare their intended variance. 

String interpolation : variables can be easy inserted within a string using a simple syntax.

Support to Intervals literals.

Support to Progressions (Range) literals. 

Suport to Rational, Imaginary and Complex numbers.

Funcional programming support to some extend. Definitely Lambdas and types like Function<A,B>. 

No concept of "static' : All things are objects or exist within an object

Modules : ability to compile meta information in "module bundle" (think .jar or .dll) and their respective dependencies. This would allow for the runtime to determine the modules that are needed for a given module to run.

# Some features under consideration

Closures : are like lambdas but can capture and modify values in the calling scope. Very usefully we try to allow the user to implement its on control directives.

Constructors are like factory methods : A class is a factory and constructors really construct the object (not only initialize it). The new keyword can only be used in the constructor and means "create an auto-initialized" object and would be equivalent to a no parameters private constructor in other languages. So all calls to create new objects are calls to factory methods present in the "Class" object enforcing the "static factory method" design patter out-of the box. Being functions the constructors could be passed as lambdas to other functions.
