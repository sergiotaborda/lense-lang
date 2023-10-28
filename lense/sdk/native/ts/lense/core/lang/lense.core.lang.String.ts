
namepsace lense.core.lang {
	export class String {
	
	
		private constructor (private text : string){}
	
		public equalsTo( other: Any) : lense.core.lang.Boolean;
		
		public hashValue () : lense.core.lang.HashValue; 
		
		public asString () : lense.core.lang.String{
			return this;
		}
		public type() : lense.core.lang.reflection.Type;
	} 
}

