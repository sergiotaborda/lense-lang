package lense.compiler.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class Sequences {

	
	public static <A,B,C> void zipApply (Collection<A> a , Collection<B> b, BiConsumer<A,B> consumer) {
		
		if (a.isEmpty() || b.isEmpty() || a.size() != b.size()) {
			return;
		}
		
		Iterator<A> as = a.iterator();
		Iterator<B> bs = b.iterator();
		
		while (as.hasNext()) {
			consumer.accept(as.next(), bs.next());
		}
	}
	
	public static <A,B> boolean zipAny (Collection<A> a , Collection<B> b, BiPredicate<A,B> predicate) {
		if (a.isEmpty() || b.isEmpty() || a.size() != b.size()) {
			return false;
		}
		
		Iterator<A> as = a.iterator();
		Iterator<B> bs = b.iterator();
		
	
		while (as.hasNext()) {
			if (predicate.test(as.next(), bs.next())) {
				return true;
			}
		}
		
		return false;
	}
}
