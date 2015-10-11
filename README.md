# lense-lang

Lense is a strong typed, pure object oriented programing language that is - we hope - carefully crafted from minucious observation of the design goals and decisions followd by other languages. Nontheless, Lense does not aim to be an academic driven language even when it tries to follow a more científic and logic approch to design decision making.

Lense goal is to be a simple language, easy to read and write that could be compiled to any object oriented plataform. The first efford will focus on running Lense in the java virtual machine (JVM).

Lense is currently in the exploratory design stage.

# Some features to expect 

Familiar syntax : Lense uses a c/java style syntax with blocks of code definied within { }

No primitives : all types are treated as objects in the heap. The compiler is free to optimize this to the plataform primitives as it sees fit. The programmer can define imutable objects in order to hint the compiler that is a good ideia to optimise those classes but this is not mandatory.

No nulls : all variables must have been inicialized at some point. By default no object type includes the null value. The maybe monad is used when the "absent" value is required. The compiler can understand the maybe monad implicitly in order to free the programmer of writing a special syntax for possible absent value handling, this means... 

No strange null handling operators : because nulls cannot exist, the programmer does not need to inform the compiler about dereference safty and so operators like .? or :? are not needed. When interacting with other libraries written in the plataform's native language the compiler will wrap all call in a safe manner in order to maintain the code simple and do not strees the programmer with anoying details,

No symbolic noise : operator symbols are predefined and associated with specific interfaces so classes like numbers and strings can use operators.However defining you own operator symbol is not allowed in order to mantain the code simple to read. The use of interfaces to define operations follows a algebric structure paradigm so the compilr can reason about the operations (example : altering the order of operations the enhance preformance if the operation is cumutative)

Generics with variance : Types can have generic arguments and this arguments can declare their intented variance. 
