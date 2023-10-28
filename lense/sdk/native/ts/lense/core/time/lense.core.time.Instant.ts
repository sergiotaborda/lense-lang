
namepsace lense.core.time {
	export interface Instant {
	
		public equalsTo( other: Any) : lense.core.lang.Boolean;
		public hashValue () : lense.core.lang.HashValue; 
		public asString () : lense.core.lang.String;
		public type() : lense.core.lang.reflection.Type;
	} 
}

