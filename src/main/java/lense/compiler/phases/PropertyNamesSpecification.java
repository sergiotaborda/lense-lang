package lense.compiler.phases;

public class PropertyNamesSpecification {

	public static String resolvePropertyName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public static String resolvePropertyInnerName (String propertyName){
		return "_" + propertyName;
	}
}
