package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NotReplacedPlaceholderException;
import lense.core.lang.java.Placeholder;
import lense.core.lang.reflection.Type;

@Placeholder
public class BigRational implements Rational {

	@Constructor(paramsSignature = "lense.core.math.Integer, lense.core.math.Integer", isImplicit = false)
	public static BigRational constructor(Integer n, Integer d){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.math.Integer", isImplicit = true)
	public static BigRational valueOf(Integer n){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.math.Whole", isImplicit = true)
	public static BigRational valueOf(Whole n){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "", isImplicit = false)
	public static BigRational zero(){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "", isImplicit = false)
	public static BigRational one(){
		throw new NotReplacedPlaceholderException();
	}
	
	@Override
	public Real abs() {
		 throw new NotReplacedPlaceholderException();
	}

	@Override
	public Integer asInteger() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real symmetric() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isZero() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isOne() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isWhole() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Integer floor() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Integer ceil() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real plus(Real other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real minus(Real other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real multiply(Real other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real divide(Real other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex plus(Imaginary other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex minus(Imaginary other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Imaginary multiply(Imaginary other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Imaginary divide(Imaginary other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real raiseTo(Real other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean equalsTo(Any other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public HashValue hashValue() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public String asString() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Type type() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Comparison compareWith(Any other) {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isNegative() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isPositive() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Integer sign() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Integer getNumerator() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Natural getDenominator() {

		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Rational invert() {

		throw new NotReplacedPlaceholderException();
	}

}
