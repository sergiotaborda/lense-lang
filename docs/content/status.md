title=Status
date=2015-12-19
type=post
tags=tour, lense
status=published
~~~~~~

# Status

Lense is currently in the *Exploration Stage*. We are designing the language features and trying to understand how they interact together 
and providing an AST parser for the language itself. Compilation to targets (like java) is being taken into account on development but not
 activly persueed at the time. Feel free to contribute.

# Roadmap


In a given stage we focus on reaching the stage goals. This does not mean no work is done in other stages but this work is done 
to explore some decision consequences or ascertain future difficulties and can change once we move to that stage. 

1. Exploration Stage
2. Java Virtual Machine backend
3. JavaScript backend

## Exploration Stage

Decide on the core concepts, the supporting syntax and related keywords. At this stage the work is mainly de definition of a BNF grammar 
and testing the parser to obtain the Abstract Syntax Tree (AST) for the language. Work for this stage also focus on how to assure the core feature can be available in a multi-platform scenario. 
Also some exploratory work is made on the infrastructure necessary to transform the Lense AST to other intermediary representations and finally to bytecodes.

## Java Virtual Machine backend

At this stage the AST obtained in the previous stage is translated into to a Java Virtual Machine compatible intermediary representation and then that is transformed to bytecode and a .class file. Once the .class files are compiled, they are packed into a .jar file.

## JavaScript backend

At this stage the AST obtained in the previous stage is translate into a Javascript compatible AST, then that AST is transform to javascript code and stored in a .js file.
