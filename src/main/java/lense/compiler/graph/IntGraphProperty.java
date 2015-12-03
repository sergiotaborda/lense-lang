
/**
 * 
 */
package lense.compiler.graph;

/**
 * 
 */
public class IntGraphProperty extends GraphProperty {

	int value;
	
	/**
	 * Constructor.
	 * @param name
	 */
	public IntGraphProperty(String name) {
		super(name);
	}
	
	public IntGraphProperty(String name , int value) {
		super(name);
		this.value = value;
	}

	public int getValue(){
		return value;
	}
	
	public IntGraphProperty increment(){
		value++;
		return this;
	}
	
	public IntGraphProperty decrement(){
		value--;
		return this;
	}
	
	public boolean is(int value){
		return this.value == value;
	}
}
