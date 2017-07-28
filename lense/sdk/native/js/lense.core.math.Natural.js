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
	function upTo( other: Natural) : Progression<Natural>;
	
	function symmetric() {
		var result = new lense.core.math.Integer();
		result.value = -this.value;
		return result;
	}
}