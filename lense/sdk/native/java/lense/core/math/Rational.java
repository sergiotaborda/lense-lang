package lense.core.math;

public interface Rational extends Real {

	Integer getNumerator();

	Natural getDenominator();

	Rational invert();

}
