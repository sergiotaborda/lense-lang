package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;

@Signature("[=T<lense.core.lang.Any]::")
public abstract class Maybe extends Base implements Any{

    
    @PlatformSpecific
    public Maybe() {}{}
	
	public abstract boolean isPresent();
	public abstract boolean isAbsent();
	
	@Property(name = "value")
	public abstract Any getValue();
	
	public abstract Maybe map(Function transformer);

	public abstract boolean is(Any content);
	

}
