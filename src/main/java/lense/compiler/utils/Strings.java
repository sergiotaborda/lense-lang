package lense.compiler.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Strings {

	
	public static String[] split(String text, String separator) {
		if (text == null) {
			return new String[0];
		}
		if (text.contains(separator)) {
			if (separator.equals(".")){
				return text.split("\\.");
			}
			return text.split(separator);
		} else {
			return new String[] {text};
		}
	}

	public static String cammelToPascalCase(String text) {
		 return text.substring(0, 1).toUpperCase() + text.substring(1);
	}
	
	public static String pascalToCammelCase(String text) {
		return text.substring(0, 1).toLowerCase() + text.substring(1);
	}

	public static String join(String[] name, String delimiter) {
		return Stream.of(name).collect(Collectors.joining(delimiter));
	}
	
	public static String join(Iterable<Object> names, String delimiter) {
		return StreamSupport.stream(names.spliterator(), false).map(obj -> obj.toString()).collect(Collectors.joining(delimiter));
	}

	public static String join(Stream<? extends Object> names, String delimiter) {
		return names.map(obj -> obj.toString()).collect(Collectors.joining(delimiter));
	}
}
