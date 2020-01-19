package lense.core.lang.java;

public class NotReplacedPlaceholderException extends UnsupportedOperationException {


	private static final long serialVersionUID = 725902296525184934L;

	
	public NotReplacedPlaceholderException(){
		super("Native paceholder was not overriden");
	}

}
