package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NotReplacedPlaceholderException;
import lense.core.lang.java.Placeholder;
import lense.core.lang.reflection.Type;

@Placeholder
public class ComplexOverReal implements Complex {

	@Constructor(paramsSignature = "lense.core.math.Real, lense.core.math.Real")
	public static ComplexOverReal rectangular(Real r, Real img){
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isZero() {
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
	public Real getReal() {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real getImaginary() {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex plus(Complex other) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex minus(Complex other) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex multiply(Complex other) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex divide(Complex other) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex divide(Real denominator) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Complex conjugate() {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Real abs() {
		throw new NotReplacedPlaceholderException();
	}

}
