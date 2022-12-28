package lense.sdk.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import lense.core.lang.Maybe;
import lense.core.lang.String;
import lense.core.lang.java.NativeString;
import lense.core.math.Natural64;

public class TestStringOperations {

	List<String> strings = List.of(NativeString.valueOfNative("The brown dog jumped over the lazy fox"),
			NativeString.valueOfNative("The brown dog").concat(NativeString.valueOfNative(" jumped over"))
					.concat(NativeString.valueOfNative(" the lazy fox")),
			NativeString.valueOfNative("The brown dog jumped over the lazy fox at night")
					.subString(Natural64.valueOfNative(0), Natural64.valueOfNative(38)),
			NativeString.EMPTY.concat(NativeString.valueOfNative("The brown dog jumped over the lazy fox"))
					.concat(NativeString.EMPTY));

	String fox = NativeString.valueOfNative("f").concat(NativeString.valueOfNative("ox"));
	String the = NativeString.valueOfNative("Th").concat(NativeString.valueOfNative("e"));

	@Test
	public void testConcat() {
		for (var a : strings) {
			for (var b : strings) {
				assertTrue(a.equals(b), a.getClass() + " is not equal to " + b.getClass());
			}
		}
	}

	@Test
	public void testContainsCharacter() {

		var j = lense.core.lang.Character.valueOfNative('j');

		for (var item : strings) {
			assertTrue(item.contains(j), item.getClass() + " does not contain " + j);
		}
	}

	@Test
	public void testIndexOf() {

		for (var item : strings) {
			Maybe possible = item.indexOf(fox);
			assertTrue(possible.isPresent(), item.getClass() + " does not have " + fox);
			assertEquals(Natural64.valueOfNative(35), possible.getValue(), item.getClass() + " does not have " + fox);
		}
	}

	@Test
	public void testEndsWith() {
		for (var item : strings) {
			assertTrue(item.endsWith(fox), item.getClass() + " does not end with " + fox);
		}
	}

	@Test
	public void testStartsWith() {
		for (var item : strings) {
			assertTrue(item.starstWith(the), item.getClass() + " does not starts with " + the);
		}
	}

	@Test
	public void testHashCode() {
		for (var a : strings) {
			for (var b : strings) {
				assertEquals(a.hashValue(), b.hashValue(), a.getClass() + " is not equal to " + b.getClass());
			}
		}
		
		assertEquals(NativeString.EMPTY.hashValue(), NativeString.valueOfNative("a").removeAt(Natural64.ZERO).hashValue(),"Emoty is not consistent");
	}

}
