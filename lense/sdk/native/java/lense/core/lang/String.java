package lense.core.lang;

import lense.core.collections.Assortment;
import lense.core.collections.Iterator;
import lense.core.collections.NativeProgression;
import lense.core.collections.Progression;
import lense.core.collections.Sequence;
import lense.core.lang.java.Base;
import lense.core.lang.java.ConcatenatedString;
import lense.core.lang.java.JavaReifiedArguments;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.NativeString;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.lang.java.SubstringView;
import lense.core.lang.reflection.Type;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

@Signature("::lense.core.collections.Sequence<lense.core.lang.Character>&lense.core.lang.Concatenable<lense.core.lang.String,lense.core.lang.String,lense.core.lang.String>")
public abstract class String extends Base implements Sequence , CharSequence, Concatenable {


	private HashValue hash;
	
	@Override
	public HashValue hashValue() {
		
		if (hash == null) {
			var code = 0; 
			
			var iterator = this.getIterator();
			var index = 1;
			var length = NativeNumberFactory.naturalToPrimitiveInt(this.getSize());
			while(iterator.moveNext()) {
				code = iterator.current().hashCode() * (int)Math.pow(31, length - index);
				index++;
			}
			
			hash = HashValue.fromPrimitive(code);
		}
		
		return hash;
	}
	

	public abstract java.lang.String toString();
	
	@Override
	@Property(name = "size")
	@MethodSignature(returnSignature = "lense.core.math.Natural", paramsSignature = "", declaringType = "lense.core.collections.Sequence")
	public abstract Natural getSize();

	@Override
	public Iterator getIterator() {
		return getIndexes().getIterator().map(it ->  get((Natural)it));
	}
	@Override
	public abstract lense.core.lang.Character get(Natural index);

	@Override
	public Progression getIndexes(){
		return new NativeProgression(0, this.length() - 1);
	}
	
	@Override
	public String asString() {
		return this;
	}

	@Override
	public abstract boolean contains(Any other);
	
	@Override
	public boolean containsAll(Assortment other) {
		if (other.getEmpty()) {
			return true; // empty set is contained in any set
		}
		var iterator = other.getIterator();
		while(iterator.moveNext()) {
			if (!contains(iterator.current())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public abstract boolean getEmpty();


	@Override
	public boolean equalsTo(Any other) {
		if (this instanceof NativeString thisString && other instanceof NativeString nativeOther) {
			return thisString.equalsNative(nativeOther);
		} else if (other instanceof String that) {

			var thatSize = that.getSize();
			var thisSize = this.getSize();
			
			if (!thatSize.equals(thisSize)) {
				return false;
			} else if (thisSize.isZero()) {
				return true;
			}
	
			var a = this.getIterator();
			var b = that.getIterator();
			
			while(a.moveNext() && b.moveNext()) {
				if (!a.current().equals(b.current())) {
					return false;
				}
			}

		}
		return true;
	}
	
	public abstract String concat(String other);
	
	@PlatformSpecific
	public String concat(java.lang.String other) {
		return concat(NativeString.valueOfNative(other));
	}
	
	public String concat(Any other) {
		if (other == null){
			throw new IllegalArgumentException("argument cannot be null");
		}
		if (other.asString() == null){
			throw new IllegalArgumentException("asString cannot be null");
		}
		return concat(other.asString());
	}
   
	@Override
	public Type type() {
		 return NativeString.TYPE_RESOLVER.resolveType();
	}
	

	@Override
	public int length() {
		return NativeNumberFactory.toPrimitiveInt(getSize());
	}

	
	@Override
	public char charAt(int index) {
		return get(NativeNumberFactory.newNatural(index)).toPrimitiveChar();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO validate pre conditions
		return subString(NativeNumberFactory.newNatural(start), NativeNumberFactory.newNatural(end - start));
	}
	
	public String removeAt(Natural position) {
		if (position.isZero() || position.compareWith(this.getSize()).isGreater()) {
			return this;
		}
		return ConcatenatedString.newInstance(
			new SubstringView(this, Natural64.ZERO, position.predecessor()),
			this.subString(position.successor())
		);
	}
	
	@MethodSignature(returnSignature = "lense.core.lang.Maybe<lense.core.math.Natural>" , paramsSignature = "lense.core.lang.String")
	public Maybe indexOf(String candidate){
		if (this.isEmpty()) {
			if (candidate.isEmpty()) {
				return Some.constructor(JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), Natural64.ZERO);
			} else {
				return None.NONE;
			}
		}
		var fistChar = candidate.get(Natural64.ZERO);
		Natural pos = Natural64.ZERO;
		var iterator = this.getIterator();
		while(iterator.moveNext()) {
			if (iterator.current().equals(fistChar)) {
				if (new SubstringView(this,pos, candidate.getSize()).equalsTo(candidate)) {
					return Some.constructor(JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), pos);
				};
			}
			pos = pos.successor();
		}
		
		return None.NONE;
	}
	
	public String subString(Natural start, Natural length) {
		return new SubstringView(this, start, length);
	}
	
	public String subString(Natural start) {
		return new SubstringView(this, start, this.getSize().minus(start).abs());
	}
	
	public boolean starstWith(String other) {
		return indexOf(other).valueEqualsTo(Natural64.ZERO);
	}
	
	public boolean endsWith(String other ) {
		return indexOf(other).valueEqualsTo(this.getSize().minus(other.getSize()).abs());
	}
	
	@Override
	@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.String" , paramsSignature = "lense.core.lang.String,lense.core.lang.String" , override = true , satisfy = true, declaringType = "lense.core.lang.Concatenable")
	public  Any concatenate(Any a, Any b){
		return ((lense.core.lang.String)a).concat(((lense.core.lang.String)b));
	}
}
