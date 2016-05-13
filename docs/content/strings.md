title=Strings
date=2016-05-12
type=post
tags=strings, lense
status=published
~~~~~~

# Strings

A string in Lense is a sequence of characters and implements ``Sequence<Character>``. Characters are UTF-16 code points. A string literal is just a text enclosed in double quotes.

~~~~brush: lense
	val String greating = "Hello, world";
~~~~

String are mulit-line, so you can simply right

~~~~brush: lense
val String greating = "Hello, 
	wold";
~~~~

The line break , tab and spaces in the second line will be preserved.

## Special characters

If you need to use a Unicode special character, simply enclose its hexadecimal code as a natural  within ``\{`` and ``}`` delimiters.

~~~~brush: lense
	val String definitionOfPi = "The value of \{#03C0} is the ratio between the circumference and the diameter of a circle"
~~~~

Strings are [fundamental types](glossary.html#fundamental) in Lense and are mapped to the ``String`` class in the native platform.

## Concatenation

You can concatenate strings using the ``+`` operator.

~~~~brush: lense
	val String name = "Alice";
	val String greating = "Hello, " + name;
~~~~

## Interpolation

You can interpolate values inside literal strings using ``{{`` and ``}}`` as delimiters.

~~~~brush: lense
	val String name = "Alice";
	val String greating = "Hello, {{ name }}";
~~~~

You can interpolate any expression

~~~~brush: lense
	for (var i in 1..10){
		Console.println("The {{ i }}th even number is {{  (i-1) * 2 }}")
	}
~~~~

# Character 

Character corresponds to a UTF-16 code point. Literals are inclosed in single quotes like ``'a'``.A character is not a number (like in java) but you can use some operations with naturals.

~~~~brush: lense
	var a = 'a';
	
	var b = a++; //  'b' 
	var c = a + 2; // 'c'
~~~~
