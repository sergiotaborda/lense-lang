package noname;

class testeLiterals{
	
public void main(lense.core.collections.Tuple<V extends lense.core.lang.Natural,T extends lense.core.collections.Tuple<V extends lense.core.lang.String,T extends lense.core.collections.Tuple<V extends lense.core.lang.Int,T extends lense.core.collections.Tuple<V extends lense.core.lang.Boolean,T extends lense.core.lang.Nothing>>>>  params){
final lense.core.collections.Sequence array =  new lense.core.collections.JavaNativeImutableSequence (1,2,3);
final lense.core.collections.Sequence single =  new lense.core.collections.JavaNativeImutableSequence (1);
final lense.core.collections.Association map =  new lense.core.collections.JavaNativeImutableAssociation ( new lense.core.collections.Pair (1,2), new lense.core.collections.Pair (3,4), new lense.core.collections.Pair (5,6));
final lense.core.collections.Tuple tuple =  new lense.core.collections.Tuple (1, new lense.core.collections.Tuple ("2", new lense.core.collections.Tuple (3.negative(), new lense.core.collections.Tuple (false,null))));
final lense.core.collections.Tuple otherTuple =  new lense.core.collections.Tuple (1, new lense.core.collections.Tuple ("2", new lense.core.collections.Tuple (3.negative(), new lense.core.collections.Tuple (false,null))));
main(otherTuple);
final lense.core.lang.Natural n = tuple.head();
final lense.core.lang.String s = tuple.tail().head().toString();
final lense.core.lang.Integer i = tuple.tail().head();
final lense.core.lang.Boolean b = tuple.tail().tail().head();
final lense.core.lang.Int index = 2.toInt();
final lense.core.lang.Void voidTuple2 = null;
final lense.core.collections.Tuple voidTuple3 = null;
}

}
