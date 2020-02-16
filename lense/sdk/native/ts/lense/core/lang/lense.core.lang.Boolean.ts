
namepsace lense.core.lang {
	export class Boolean {
	
		private constructor (private value : boolean){}
	
		public equalsTo( other: Any) : lense.core.lang.Boolean;
		
		public hashValue () : lense.core.lang.HashValue; 
		
		public asString () : lense.core.lang.String{
			return this.value ? "true" : "false";
		}
		public type() : lense.core.lang.reflection.Type;
	} 
}

