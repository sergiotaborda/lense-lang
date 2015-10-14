# lense-lang

Lense is a strong typed, pure object oriented programing language that is - we hope - carefully crafted from what we thing is and ideal aproach to a new modern programing language and a meticulous observation and analysis of other languages design goals and decisions. Even with this analysis and comparation with the state of art, Lense does not aim to be an academic driven language even when it tries to follow a more científic and logic approach to design decision making.

Lense goal is to be a simple language, easy to read and write that could be compiled to any object oriented plataform. The first efford will focus on running Lense in the Java Virtual Machine (JVM).

Lense is currently in the exploratory design stage, i.e. we are designing the language features and trying understand who they interact together.

# Some features to expect 

Familiar syntax : Lense uses a c/java style syntax with blocks of code definied within { }

No primitives : all types are treated as objects in the heap. The compiler is free to optimize this to the plataform's primitives as it sees fit. The programmer can define imutable objects in order to hint to the compiler that is a good ideia to optimise that class but this is not mandatory.

No nulls : all variables must have been inicialized at some point. By default no object type includes the null value. The maybe monad is used when the "absent" value is required. The compiler can understand the maybe monad implicitly in order to free the programmer of writing a special syntax for possible absent value handling, this means... 

No strange null handling operators : because nulls cannot exist, the programmer does not need to inform the compiler about dereference safty and so operators like .? or :? are not needed. When interacting with other libraries written in the plataform's native language the compiler will wrap all call in a safe manner in order to maintain the code simple and do not strees the programmer with anoying details,

No symbolic noise : operator symbols are predefined and associated with specific interfaces so classes like numbers and strings can use operators.However defining you own operator symbol is not allowed in order to mantain the code simple to read. The use of interfaces to define operations follows a algebric structure paradigm so the compilr can reason about the operations (example : altering the order of operations the enhance preformance if the operation is cumutative)

Reified Generics : The information of the generic type parameters can be reflected upon at runtime. 

Generics with variance : Types can have generic parameters and this paremeters can declare their intented variance. 

String interplation : variables can be easy inserted within a string using a simple sintax.

Support to Intervals literals.

Support to Proggression (Range) literals 

Suport to Imaginary and Complex numbers 

Funcional programing support to some extend. Definitly Lambdas.

No Static concept. All things exist in an object

Modules : hability to compile meta information in "module bundle" (think .jar or .dll) and their respective dependency. This would allow for the runtime to determine with modules are needed for a given module to run.

# Some features under consideration

Closures : are like lambdas but can capture and modify values in the calling scope.

Constructores like factory methods : A classe is a factory and constructors really construct the object (not only inicialize it). The new keyword can only be used in the constructor and means "create an auto-inicialized" object and would be equivalent to a no parameters private constructor in other languages. So all calls to create new objects are calls to factory methods present in the "Class" object inforcing the "static factory method" design patter out-of the box. Being functions the constructors could be passed as lambdas to other functions.
