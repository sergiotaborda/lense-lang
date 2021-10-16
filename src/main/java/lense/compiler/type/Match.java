package lense.compiler.type;

import lense.compiler.typesystem.TypeMatch;

public final class Match<T> implements Comparable<Match<T>> {


	public static <X> Match<X> of(X c, TypeMatch match) {
		return new Match<X>(c, match);
	}

	private T candidate;
	private TypeMatch match;
	
	public Match(T candidate, TypeMatch match) {
		this.candidate =candidate;
		this.match = match;
	}
	
	@Override
	public int compareTo(Match<T> other) {
		return this.getMatch().compareTo(other.getMatch());
	}

	public T getCandidate() {
		return candidate;
	}

	public TypeMatch getMatch() {
		return match;
	}
	
	public String toString() {
		return match.toString() + "[" + candidate +"]";
	}

}
