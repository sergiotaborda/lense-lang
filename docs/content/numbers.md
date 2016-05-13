title=Numbers
date=2016-04-03
type=post
tags=tour, lense
status=published
~~~~~~

#Numbers

Numbers are separated in specific algebraic structures that conform to the mathematical rules of their group of elements.
All numbers are descendent types of the ``Number`` class. Operations are defined for each type independently.
Lense supports Complex and Imaginary numbers. Even thought we are aware the performance of these types may not be optimal, we understand that not supporting them would be a worst decision. 

* Whole - numbers with no decimal part.
	- Natural - Represents elements from the mathematical **&#8469;** set, i.e. positive only whole values that include zero and range from zero up to maximum value limited only by available memory
	- Integer - Represents elements from the mathematical **&#8484;** set, i.e. negative and positive whole values.
		*  Int16 - negative and positive whole values with range from -2<sup>15</sup> to  2<sup>15</sup>-1. 
		*  Int32 - negative and positive whole values with range from -2<sup>31</sup> to  2<sup>31</sup>-1. 
		*  Int64 - negative and positive whole values with range from -2<sup>63</sup> to  2<sup>63</sup>-1. 
		*  BigInt - negative and positive whole values with arbitrary range limited only by available memory
* Real - Represents elements from the mathematical **&#8477;** set.
	-  Rational - Represents elements from the mathematical **&#8474;** set, i.e. rational numbers defined by a natural numerator and a natural denominator like 2/3 or -5/8. The denominator cannot be zero. 
	-  Decimal - Represents elements that have a fixed precision and so calculations may incur in loss of precision.
		* FixedPrecisionDecimal - Represents Decimal elements with fixed precision:
			- Decimal32 - negative and positive decimal values that follow 32 bits IEEE 3744 conventions
			- Decimal64 - negative and positive decimal values that follow 64 bits IEEE 3744 conventions
		* ArbitraryPrecisionDecimal - Represents Decimal elements with arbitrary precision:
			- BigDecimal - Represents elements in the **&#8477;** set including truncated version of irrational numbers.Negative and positive decimal values with arbitrary precision limited only by available memory.
* Imaginary - Represents elements from the mathematical **&#120128;** set. Numbers with pure imaginary parts of the form ``bi`` where ``b`` is a ``Number`` and ``i`` is the square root of -1.
	- ImaginaryOverReals<T extends Real>; - uses a Real type to store the numeric value
* Complex - Represents elements from the mathematical **&#8450;** set. Complex numbers are of the form ``a + bi`` where ``i`` it the square root of -1.
	- ComplexOverReals<T extends Real>; - Use a Real to type to store a numeric value for the real part and a ImaginaryOverReals<T> for the imaginary part.

Type ``Natural`` is used as an indexer for ``Sequence``s. Limits to collections like arrays, lists and maps are only bound by limit of Natural which in turn is limited only by available memory.
Using a Natural to index sequences removes the necessity to check for negative indexes and because ``Arrays`` always have a upper limit and always are constructed by [factory like constructors](constructors.html#factory) the implementation for each platform can accommodate different implementations according to maximum length demand.

For more information on how Natural relates to index of sequences, see how [Arrays](arrays.html) work in Lense.
For more information on arithmetic operations  more on Lense [operators](operators.html).


## Number Literals 

For ``Whole`` number literals are always assumed ``Natural`` and in base ten representation. The natural values are transformed to other types as needed. 
This conversion may rise an ``OverflowException`` as a ``Natural`` can exceed the maximum values of other types. For ``Decimal`` values, literals are always assumed to be instance of ``BigDecimal``. ``BigDecimal`` constructor only accepts a string representation of the value as the BigDecimal literal representation must be exact.

If you need to define the type of the literal explicitly you can use specific sufixes, upper case letters for whole numbers and lowercase letters to decimal numbers.

~~~~brush: lense
	var Natural n = 1; // equivalent to Natural.valueOf("1")
	
	// literals are always assumed to be Natural and promoted when necessary
	var Int32 i = 1;  // equivalent to Int32.valueOf(Natural.valueOf("1"));
	var Int16 s = 1;  // equivalent to Int16.valueOf(Natural.valueOf("1"));
	var Int64 k = 1;  // equivalent to Int64.valueOf(Natural.valueOf("1"));
	var BigInt g = 1;  // equivalent to BigInt.valueOf(Natural.valueOf("1"));
	
	// If the target type is Whole or Integer, the literal it's equivalent to having BigInt as target 
	var Whole n = 1; // equivalent to BigInt.valueOf(Natural.valueOf("1"));
	var Integer all = 1;  // equivalent to BigInt.valueOf(Natural.valueOf("1"));
	
	// sufixes can be used to inform the compiler the correct type of the literal
	// for whole numbers only uppercase prefixes are allowed 
	var Int32 ii = 1Z;  // equivalent to Int32.valueOf("1");
	var Int16 ss = 1S;  // equivalent to Int16.valueOf("1");
	var Int64 kk = 1L;  // equivalent to Int64.valueOf("1");
	var BigInt gg = 1G;  // equivalent to BigInt.valueOf("1");
	
	// Rationals are defined by the division of two whole positive values. 
	var Rational r = 2/3; // equivalent to Natural.valueOf("2").divide(Natural.valueOf("3"))
	var Rational q = -5/8; // equivalent to Natural.valueOf("5").negate().divide(Natural.valueOf("8"));
	var Rational q = -x/y; // equivalent to Natural.valueOf(x).negate().divide(Natural.valueOf(y));
	
	// In this case 1 is a Natural being promoted to a Decimal32.
	var Decimal32 f = 1; // equivalent to Decimal32.valueOf(Natural.valueOf("1"));
	
	// decimal values are always assumed to be BigDecimals
	var Decimal32 ff = 1.6; // equivalent to Decimal32.valueOf(BigDecimal.valueOf("1.6"));
	var Decimal64 d = 2.0; // equivalent to Decimal64.valueOf(BigDecimal.valueOf("2.0"));
	var BigDecimal m = 1.234567890E100; // equivalent to BigDecimal.valueOf("1.234567890E100");

	// prefixes can also be used to inform the compiler the correct type of the literal
	// for non whole numbers only lower-case prefixes are allowed 
	var Decimal32 fff = 1.6f; // equivalent to Decimal32.valueOf("1.6");
	var Decimal64 dd = 2.0d; // equivalent to Decimal64.valueOf("2.0");
	var BigDecimal mm = 1m; // equivalent to BigDecimal.valueOf("1");
	
	
	var Imaginary a = 2i; // equivalent to Imaginary.valueOf(Natural.valueOf("2"));
	var Imaginary b = 2.5i; // equivalent to Imaginary.valueOf(BigDecimal.valueOf("2.5"));
	
	var Imaginary error = 2; // does not compile because a Natural can not be converted to an Imaginary number

	var Complex = 5 + 2i; // equivalent to Natural.valueOf("5").plus(Imaginary.valueOf(Natural.valueOf("2")))
	var Complex = 3.9 + 0.2i; // equivalent to BigDecimal.valueOf("3.9").plus(Imaginary.valueOf(BigDecimal.valueOf("0.2"))
~~~~

In any representation you can use _ to logically separate digits in the value to help readability.

~~~~brush: lense
	var Integer -1000000;
	// or
	var Integer = -1_000_000;
~~~~

### Other Bases for Literal Representations 

Numeral literals are assumed to be represented in decimal form (base 10) for all types. For naturals it is also possible to use the hexadecimal (base 16) form.

The hexadecimal form begins with a ``#`` symbol followed by a valid hexadecimal digit: 1, 2, 3, 4, 5, 6, 7, 8, A , B, C, D , E , F. You can also use _ to separate digits like in base ten representation.

~~~~brush: lense
	var Natural color = #A3_C1; // hexadecimal
~~~~

## Binary and Bytes

Lense supports the ``Binary`` immutable interface to represent any value that can be understood as a sequence of bits. ``Binary`` does not,necessarily, represent a number. ``BitArray`` is the default, mutable, implementation of ``Binary`` present in the SDK API. ``BitArray`` supports a variable size of bits.

``Byte`` is a special class that implements ``Binary`` corresponding to a fixed length sequence of 8 bits. It's primarily used for I/O operations. ``Byte`` is not a number, does not have an assigned numeric value and there is no automatic promotion from ``Byte`` to any type of ``Number``. Also it has no arithmetic operations. However, a ``Byte`` can be transformed explicitly to a ``Natural`` between 0 and 255 or to a ``Int32`` between -128 and 127 by means of the ``toNatural()`` and ``toInteger()`` functions.

~~~~brush: lense
	var Byte byte = $1111_0000; 
	var Natural n = byte.toNatural(); // equivalent to 240;
	var Int32 i = byte.toInteger(); // equivalent to -16
	
	var Natural error = byte; // illegal. Byte is not assignable to Natural.
~~~~

``Int16`` , ``Int32`` and ``Int64`` also implement ``Binary`` corresponding to a fixed length sequence of 16, 32 and 64 bits respectively. Because this values have a signed numeric value, one of the bits is reserved to determine the sign. The rest of the bits represent the value if the value is positive, else represent the Two Complement representation of the (then negative) value.

### Literal Representation

The literal begins with a ``$`` sign flowed by a sequence of ones (to represent ``true``) and zeros (to represent ``false``). The ``_`` symbol can be used, as in number literals, to separate digit logically.

All binary literals are assumed to be instances of ``BitArray``s of the given number of bits. It is not possible to have a zero bits sequence. 

~~~~brush: lense
	var Byte byte = $1111_0000; // equivalent to Byte.valueOf(BitArray.valueOf(true,true,true,true,false,false,false,false));
	var Int16 short = $1111_0000_1111_0000; // equivalent to Int16.valueOf(BitArray.valueOf(true,true,true,true,false,false,false,false,true,true,true,true,false,false,false,false));
	var BitArray flags = $1111_0000_0101_0110_0010_0001_0101_1001; // equivalent to BitArray.valueOf(true,true,true,true,false,false,false,false,true,false,tru,false,true,true,false,false,false,false,false,false,false,false,true,false,true,false,true,true,false,false,true);
~~~~	

*Note the equivalent expressions are conceptual, in practice the compiler uses more suitable constructors for each case.*
