package lense.compiler.utils;

public class Strings {

	
	public static String[] split(String text, String separator) {
		
		if (text.contains(separator)) {
			return text.split(separator);
		} else {
			return new String[] {text};
		}
	}
}
