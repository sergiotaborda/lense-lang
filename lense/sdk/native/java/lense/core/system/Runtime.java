package lense.core.system;

import lense.core.collections.Association;
import lense.core.collections.EmptyAssociation;
import lense.core.lang.Maybe;
import lense.core.lang.Some;
import lense.core.lang.java.Base;
import lense.core.lang.java.Property;
import lense.core.lang.java.SingletonObject;
import lense.core.time.Clock;

@SingletonObject
public class Runtime extends Base {

	public static Runtime Runtime = new Runtime();
	
	
	public static Runtime constructor(){
		return Runtime;
	}
	
	@Property(name = "properties")
	public Association getProperties (){
		return new EmptyAssociation();
	}
	
	@Property(name = "clock")
	public Clock getClock() {
		return MachineClock.instance;
	}
	
	@Property(name = "environment")
	public Maybe/*<Enviroment>*/ getEnvironment(){
		return Some.constructor(null, new JavaEnvironment());
	}
}