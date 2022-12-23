package lense.core.lang;

import lense.core.collections.Assortment;
import lense.core.collections.Iterator;
import lense.core.collections.NativeProgression;
import lense.core.collections.Progression;
import lense.core.collections.Sequence;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.JavaReifiedArguments;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

@Signature("::lense.core.collections.Sequence<lense.core.lang.Character>&lense.core.lang.Concatenable<lense.core.lang.String,lense.core.lang.String,lense.core.lang.String>")
public class String extends Base implements Sequence , CharSequence, Concatenable {

	public static final String EMPTY = new String("");
	
	public static final TypeResolver TYPE_RESOLVER = TypeResolver.lazy(() -> Type.forClass(String.class));
	
    @Constructor(paramsSignature = "")
	public static String constructor(){
		return EMPTY;
	}
	
	@PlatformSpecific
	public static String valueOfNative(java.lang.String str){
		return new String(str);
	}
	
	private java.lang.String str;
	
	@PlatformSpecific
	private String(java.lang.String str){
		this.str = str;
	}
	
	@Override
	@Property(name = "size")
	@MethodSignature(returnSignature = "lense.core.math.Natural", paramsSignature = "", declaringType = "lense.core.collections.Sequence")
	public Natural getSize() {

		return Natural64.valueOfNative(str.length());
	}

	@Override
	public Iterator getIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public lense.core.lang.Character get(Natural index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Progression getIndexes() {
		return new NativeProgression(0, this.str.length());
	}

	@Override
	public String asString() {
		return this;
	}
	
	@Override
	public java.lang.String toString() {
		return str;
	}

	@Override
	public boolean contains(Any other) {
		if (other instanceof Character){
			return str.indexOf(((Character)other).toPrimitiveChar()) >= 0;
		}
		return false;
	}

	@Override
	public boolean containsAll(Assortment other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getEmpty() {
		return str.isEmpty();
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof String && ((String)other).str.equals(this.str);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(this.str.hashCode());
	}

	public String concat(String other){
		return new String(this.str + other.str);
	}
	
	@PlatformSpecific
	public String concat(java.lang.String other){
		return new String(this.str + other);
	}
	
	public String concat(Any other){
		if (other == null){
			throw new IllegalArgumentException("argument cannot be null");
		}
		if (other.asString() == null){
			throw new IllegalArgumentException("asString cannot be null");
		}
		return new String(this.str + other.asString().str);
	}

    @Override
    public Type type() {
        return TYPE_RESOLVER.resolveType();
    }

	
    @Override
	public int length() {
		return this.str.length();
	}

	@Override
	public char charAt(int index) {
		return this.str.charAt(index);
	}
	

	@Override
	public CharSequence subSequence(int start, int end) {
		return subSequence(start, end);
	}
	
	public String removeAt(Natural position) {
		return String.valueOfNative(this.str.substring(0, NativeNumberFactory.naturalToPrimitiveInt(position)) + 
		this.str.substring(NativeNumberFactory.naturalToPrimitiveInt(position) + 1));
	}
	
	@MethodSignature(returnSignature = "lense.core.lang.Maybe<lense.core.math.Natural>" , paramsSignature = "lense.core.lang.String")
	public Maybe indexOf(String candidate) {
		int pos = this.str.indexOf(candidate.str);
		
		if (pos < 0) {
			return None.constructor();
		}
		
		return Some.constructor(JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), Natural64.valueOfNative(pos));
				
	}
	
	public String subString(Natural start, Natural length) {
		return String.valueOfNative(this.str.substring(NativeNumberFactory.naturalToPrimitiveInt(start),NativeNumberFactory.naturalToPrimitiveInt(length)));
	}
	
	public String subString(Natural start) {
		
		return String.valueOfNative(this.str.substring(NativeNumberFactory.naturalToPrimitiveInt(start)));
	}
	
	public boolean starstWith( String other  ) {
		return str.startsWith(other.str);
	}
	
	public boolean endsWith(String other ) {
		return str.endsWith(other.str);
	}

	
	
	@Override
	@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.String" , paramsSignature = "lense.core.lang.String,lense.core.lang.String" , override = true , satisfy = true, declaringType = "lense.core.lang.Concatenable")
	public Any concatenate(Any a, Any b) {
		return ((lense.core.lang.String)a).concat(((lense.core.lang.String)b));
	}
}
