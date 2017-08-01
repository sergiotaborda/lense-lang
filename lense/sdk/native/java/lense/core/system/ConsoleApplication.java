package lense.core.system;

import lense.core.collections.Sequence;
import lense.core.io.Console;
import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Property;

public abstract class ConsoleApplication extends Application {

	private Sequence args;

	public abstract void onStart();

	@Property(name= "arguments")
	public Sequence getArguments (){
		return args;
	}
	
	public void setArguments (Sequence args){
		this.args = args;
	}
	
	@Property(name= "console")
	public Console getConsole () {
		return Console.constructor();
	}
	
	public boolean equalsTo(Any other){
		return this == other;
	}
	
	public HashValue hashValue(){
		return new HashValue(0);
	}
}
