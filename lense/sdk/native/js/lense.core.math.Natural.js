lense.core.math.Natural = function (){



	var value = 0;

	function minus ( other) {
		var result = new lense.core.math.Integer();
		result.value = this.value - other.value;
		return result;
	};
	function plus ( other) {
		var result = new lense.core.math.Natural();
		result.value = this.value + other.value;
		return result;
	}
	//function multiply ( other: Natural) : Natural;
		
	//function multiply ( other: Integer) : Integer;
			
	function multiply ( other )  {
		if (other.value < 0) {
			var result = new lense.core.math.Integer();
			result.value = this.value * other.value;
			return result;
		} else {
			var result = new lense.core.math.Natural();
			result.value = this.value * other.value;
			return result;
		}
	}
	function upTo( other){ throw Error ("Not Implemented Yet")}
	
	function symmetric() {
		var result = new lense.core.math.Integer();
		result.value = -this.value;
		return result;
	}
}

lense.core.math.Natural.valueOfNative = function (number){
		var n = new lense.core.math.Natural();
		n.value = number;
		return n;
	}