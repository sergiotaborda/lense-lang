package lense.compiler.ast;

public enum IntervalOperation {

	FromAToB, //  ...
	FromAToInf, //  ..*
	FromInfToB, //  *..
	FromAExludedToB, //  >..
	FromAToBExluded, //  ..<
	FromAExcludedToBExluded // >..<
}
