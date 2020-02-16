
namepsace lense.core.lang {
	export class HashValue {
	
	
		private constructor (private value : number){}
	
		public equalsTo( other: Any) : lense.core.lang.Boolean;
		
		public hashValue () : lense.core.lang.HashValue{
			return this;
		}
		
		public asString () : lense.core.lang.String{
			return "" + value;
		}
		
		public type() : lense.core.lang.reflection.Type;
	} 
}

