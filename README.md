# Lense

Lense is a modern,strong typed, concise and safe pure object oriented programing languag. Lense is a crafted language in the sense it does not ignore other languages out there and its design is meant to capture the essence of the best features in each other language without being traped but those languages pitfalls.Lense does not aim to be an academic driven language even thought it tries to stand on state of the art and modern concepts. 

Lense goal is to be a simple language, easy to read and write that could be compiled to any object oriented plataform. The first efford will focus on running Lense in the Java Virtual Machine (JVM). A future effort will focus on JavaScript compability.

Lense is currently in the exploratory design stage. We are designing the language features and trying to understand how they interact together and providing an AST parser for the language itself. Compilation to targets (like java) is being taken into account on deveplment but not activly persueed at the time. Fee free to contribute.

## Some features 

This a small list of features we are aiming at.

###Syntax

Lense uses a c familly style syntax with some improments. there is no fallthought switch-case instruction. Lense's swicth case instruction isolates each case from the others. Fallthouhtg exists, but has to be explicitly declared by the user.

#### Object Oriented

Lense aims to be pure object oriented. Some features that exist in other languages where removed from the language. Some, like primitives, can be made use of if they exist in the target platform.

#### No primitives

All types are treated as objects in the heap. The compiler is free to optimize this to the plataform's primitives as it sees fit. The programmer can define imutable objects in order to hint to the compiler that is a good ideia to optimise that class of objects but this is not mandatory.

#### No nulls 

All variables must have been inicialized at some point. By default no object type includes the ``null`` value. The maybe monad is used when the "absent" value is required. The compiler can understand the maybe monad implicitly and some operators are provided.

#### No concept of "static' 

All things are objects or exist within an object. 

#### Modules 

Lense brings to the table the hability to compile meta information in "module bundle" (think .jar or .dll) and their respective dependencies (think maven or osgi). This would allow for the runtime to determine the modules that are needed for a given module to run.


#### Operators

Lense tries to reduce symbolic noise and so operator symbols are predefined and associated with specific methods in specific interfaces so classes like numbers and strings can use operators. However, contrary to other languages, defining you own operator symbol is not allowed in order to mantain the code simple to read and write.  The use of interfaces to define operations follows a algebric structure paradigm so the compiler can reason about the operations (example : altering the order of operations the enhance preformance if the operation is cumutative)

#### Constructors that really construct

A classe is a factory and constructors really construct the object (not only inicialize it). Constructors can be named and ae able to return instances of derived interfaces (like a static factory method would in other languages)

### Generics

Rhe languages undestands generic type parameters (generics).

#### Reified Generics
Generics information is reified , so the generic type parameters can be reflected upon at runtime. 

#### Generics with variance
Types can have generic parameters and these paremeters can declare their intented variance: co-variant, contra-variant and invariant. 

### Math support

#### Rational, Imaginary and Complex 
Lense can target math cenarios other languages don't by suporting Rational, Imaginary and Complex numbers and their respective literal representation.

#### Progressions

Lense suported the concept of Progression (think 'Range') with for-loop support and literals out of the box 

#### Intervals

Lense suported the concept of Interval out of the box an a set of literal forms for representing the diferent combinations.

### String interplation 
Variables can be easy inserted within a string using a simple sintax.


### Funcional programing support 

Lense includes functional support to some extend by means of lambda expressions with convertion to types like Function<A,B> and other Single Abstract Method (SAM) types. Lense also supports the Monad concept.



