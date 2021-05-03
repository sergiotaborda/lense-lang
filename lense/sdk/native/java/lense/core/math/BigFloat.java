package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NotReplacedPlaceholderException;
import lense.core.lang.java.Placeholder;
import lense.core.lang.reflection.Type;

@Placeholder
public class BigFloat implements Float {

	@Constructor(paramsSignature = "lense.core.math.Whole")
	public static BigFloat valueOf(Whole n){
		throw new NotReplacedPlaceholderException();
	}

	@Constructor(paramsSignature = "lense.core.math.Float")
	public static BigFloat valueOf(Float n){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.math.Real")
	public static BigFloat valueOf(Real n){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.lang.String")
	public static BigFloat parse(String n){
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
	public boolean isNaN() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isNegativeZero() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isNegativeInfinity() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isPositiveInfinity() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isInfinity() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float abs() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float symmetric() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isOne() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isZero() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public boolean isWhole() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float floor() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float ceil() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float plus(Float other) {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float minus(Float other) {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float multiply(Float other) {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float divide(Float other) {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float raiseTo(Float other) {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float log() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float exp() {
		
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float invert() {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float raiseTo(Whole other) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float asFloat() {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float round() {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float remainder(Float other) {
		throw new NotReplacedPlaceholderException();
	}

	@Override
	public Float modulo(Float other) {
		throw new NotReplacedPlaceholderException();
	}
}
