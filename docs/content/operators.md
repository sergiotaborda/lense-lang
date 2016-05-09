title=Operators
date=2016-02-03
type=post
tags=lense, operator overloading
status=published
~~~~~~

# Operators

Lense supports operators and a special kind of operator overloading we named *Operator Interface*. Not all operators can be redefined some are intrinsic to the language.

## Intrinsic Operators

Intrinsic operators cannot be redefined and are specially handled by the compiler.

<table>
	<tr>
		<th>Operator</th>
		<th>Example</th>
		<th></th>
	</tr>
	<tr>
		<td> === </td>
		<td> a === b </td>
		<td> Determines if two objects have the same identity. Only objects of type <code>Identifiable</code> can use this operator </td>
	</tr>
	<tr>
		<td> !== </td>
		<td> a !== b </td>
		<td> Determines if two objects have *not* the same identity. Only objects of type <code>Identifiable</code> can use this operator </td>
	</tr>
	</tr>
		<tr>
		<td> :: </td>
		<td> Function &lt;Natural, Natural&gt; plus = Natural::plus; </td>
		<td> Allows for easy reflection of class members </td>
	</tr>
	<tr>
		<td> + (infix) </td>
		<td> +a</td>
		<td> Does nothing. An infix <code>+</code> signed is ignored after parsing. It does not turn a negative value into a positive value.</td>
	</tr>
	<tr>
		<td> && </td>
		<td> a && b</td>
		<td> *a* and *b* must be <code>Boolean</code>. Performs an AND logic operation on the operands but only is *a* is true. Otherwise simply return ``false`` </td>
	</tr>
	<tr>
		<td> || </td>
		<td> a || b</td>
		<td> *a* and *b* must be <code>Boolean</code>. Performs an OR logic operation on the operands but only is *a* is false. Otherwise simply return ``true`` </td>
	</tr>
		<tr>
		<td> ! </td>
		<td> !a</td>
		<td> *a* must be <code>Boolean</code>. Inverts the logic value of *a*. </td>
	</tr>
</table>

## Definable Operators

Lense supports the following overridable operators:

<table>
	<tr>
		<th>Operator</th>
		<th>Example</th>
		<th>Translates to</th>
		<th>Operator Interface</th>
		<th></th>
	</tr>
	<tr>
		<td> == </td>
		<td> a == b </td>
		<td> a.equals(b) </td>
		<td> Equatable </td>
		<td> Determines if two objects are equal. </td>
	</tr>
	<tr>
		<td> !=</td>
		<td> a != b </td>
		<td> !a.equals(b) </td>
		<td> Equatable </td>
		<td> Determines if two objects are *not* equal. </td>
	</tr>
	<tr>
		<td> + </td>
		<td> a + b </td>
		<td> a.plus(b) </td>
		<td> Summable<A,D,S> </td>
		<td> Sums two values and returns in a third value. The operand values are not changed in any way </td>
	</tr>
	<tr>
		<td> - </td>
		<td> a - b </td>
		<td> a.subtract(b) </td>
		<td> Subtractable<D,A,S> </td>
		<td> Substracts two values and returns in a third value. The operand values are not changed in any way </td>
	</tr>
	<tr>
		<td> * </td>
		<td> a * b </td>
		<td> a.multiply(b) </td>
		<td> Multiplyable<P,A,B> </td>
		<td> Multiplies the two values and returns in a third value. The operand values are not changed in any way </td>
	</tr>
	<tr>
		<td> ** </td>
		<td> a ** b </td>
		<td> a.raise(b) </td>
		<td> Powerable<P,B,E> </td>
		<td> Raises the first operand to the power of the second operand and returns the result in a third value. The operand values are not changed in any way </td>
	</tr>
	<tr>
		<td> / </td>
		<td> a / b </td>
		<td> a.divide(b) </td>
		<td> Dividable<Q,N,D> </td>
		<td> Divides the two values and returns in a third value. The operand values are not changed in any way. Note that Whole numbers implement Dividable<Rational, Whole,Whole> and
			Decimals implement Dividable<Decimals,Decimals,Decimals>
		</td>
	</tr>
	<tr>
		<td> % </td>
		<td> a % b </td>
		<td> a.remainder(b) </td>
		<td> Remainder<Q,N,D> </td>
		<td> Divides the two values and returns the remainder in a third value. The operand values are not changed in any way. </td>
	</tr>
	<tr>
		<td> - (infix) </td>
		<td> -a </td>
		<td> a.symetric() </td>
		<td> Symmetric<T> </td>
		<td> Returns the symmetric value. The Symmetric value of *x* is the same that can be calculated by *0 - x*</td>
	</tr>
	<tr>
		<td> ~ (infix) </td>
		<td> ~a </td>
		<td> a.negate() </td>
		<td> Binary<T> </td>
		<td> Returns a values equivalent to the original with bits reversed</td>
	</tr>
	<tr>
		<td> ++ (infix) </td>
		<td> ++a </td>
		<td> a.increment() </td>
		<td> Incrementable<T> </td>
		<td> Increments the value by one unit</td>
	</tr>
	<tr>
		<td> -- (infix) </td>
		<td> --a </td>
		<td> a.decrement() </td>
		<td> Decrementable<T> </td>
		<td> Decrements the value by one unit</td>
	</tr>
	<tr>
		<td> & </td>
		<td> a & b </td>
		<td> a.and(b) </td>
		<td> Injunctable<R,A,B> </td>
		<td> Injucts the two values and returns a third value. The operand values are not changed in any way. For binary forms, this implements a bitwise AND </td>
	</tr>
	<tr>
		<td> | </td>
		<td> a | b </td>
		<td> a.or(b) </td>
		<td> Dijunctable<R,A,B> </td>
		<td> Dijunsts the two values and returns a third value. The operand values are not changed in any way. For binary forms, this implements a bitwise OR </td>
	</tr>
	<tr>
		<td> ^ </td>
		<td> a ^ b </td>
		<td> a.xor(b) </td>
		<td> ExclusiveDijunctable<R,A,B> </td>
		<td> Exclusively dijunsts the two values and returns a third value. The operand values are not changed in any way. For binary forms, this implements a bitwise XOR </td>
	</tr>
	<tr>
		<td> <=> </td>
		<td> a <=> b </td>
		<td> a.compareTo(b) </td>
		<td> Comparable<T> </td>
		<td> Compared the order of the values of *a* and *b*. Returns <code>Comparison.Equals</code>, <code>Comparison.Greater</code> or <code>Comparison.Lesser</code> if , respectively, a = b, a > b and a < b.  The operand values are not changed in any way. </td>
	</tr>
	<tr>
		<td> > </td>
		<td> a > b </td>
		<td> a.compareTo(b) > 0 </td>
		<td> Comparable<T> </td>
		<td> Returns ``true`` if *a* is great than *b*, ``false`` otherwise. The operand values are not changed in any way.  </td>
	</tr>
	<tr>
		<td> >= </td>
		<td> a >= b </td>
		<td> a.compareTo(b) >= 0 </td>
		<td> Comparable<T> </td>
		<td> Returns ``true`` if *a* is great or equals to *b*, ``false`` otherwise. The operand values are not changed in any way.  </td>
	</tr>
	<tr>
		<td> < </td>
		<td> a < b </td>
		<td> a.compareTo(b) < 0 </td>
		<td> Comparable<T> </td>
		<td> Returns ``true`` if *a* is less than *b*, ``false`` otherwise. The operand values are not changed in any way.  </td>
	</tr>
	<tr>
		<td> <= </td>
		<td> a <= b </td>
		<td> a.compareTo(b) <= 0 </td>
		<td> Comparable<T> </td>
		<td> Returns ``true`` if *a* is less or equal to *b*, ``false`` otherwise. The operand values are not changed in any way.  </td>
	</tr>
	<tr>
		<td> .. </td>
		<td> a..b </td>
		<td> a.upTo(b)</td>
		<td> Progressable<T> </td>
		<td> Returns a Progression that starts at *a* and ends at *b*. The operand values are not changed in any way.  </td>
	</tr>
	<tr>
		<td> >> </td>
		<td> a >> b </td>
		<td> a.rightShiftBy(b)</td>
		<td> Binary<T> </td>
		<td> Returns a value equivalent to the original with bits moved to the right *b* times. The operand values are not changed in any way.  </td>
	</tr>
	<tr>
		<td> << </td>
		<td> a << b </td>
		<td> a.leftShiftBy(b)</td>
		<td> Binary<T> </td>
		<td> Returns a value equivalent to the original with bits moved to the left *b* times. The operand values are not changed in any way.  </td>
	</tr>
</table>

### A note on Increment and Decrement operators (Under consideration)

The infix ``--a`` and ``++b`` operators are transformed to calls into ``decrement`` and ``increment``. For example, this code:

~~~~brush:lense 
Integer a = 3;
Integer b = ++a;
~~~~

At the end of this code, both *a* and *b* are 4.
Is equivalent to

~~~~brush:lense 
Integer a = 3;
a = a.increment();
Integer b = a;
~~~~

As you can see the value in the variable is incremented implicitly as you would expect, however a new object is created and the reference is redirected to this new object. 

The suffix operators ``a--`` and ``a++`` are also transformed to calls into ``decrement`` and ``increment`` but in another sequence. For example,

~~~~brush:lense 
Integer a = 3;
Integer b = a++;
~~~~

At the end of this code, *b* is 3 and *a* is 4.
is translated internally to

~~~~brush:lense 
Integer a = 3;
Integer b = a;
a = a.increment();
~~~~