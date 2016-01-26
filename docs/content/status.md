title=Documentation
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

Work is focus on stage. In a given stage we focus on reaching the stage goals. This does not mean no work is done in other stages but this work is done 
to explore some decision consequences or assertain future dificulties and can change once we move to that stage. 

1. Exploration Stage
2. Target : Java Virtual Machine
3. Target : JavaScript

## Exploration Stage

Decide on the core conecpts, the suporting syntax and related keywords. At this stage the work is mainly de definition of a BNF grammar 
and testing the parser to obtain the Abstract Syntax Tree (AST) for the language.

## Target : Java Virtual Machine

At this stage the AST obtained in the previous stage and translate it to a  Java Virtual Machine compatible AST, then transform this AST to bytecode and finally produce a .class file.

## Target : JavaScript

At this stage the AST obtained in the previous stage and translate it to a Javascript compatible AST, then transform this AST to javascript code and produce a .js file.
