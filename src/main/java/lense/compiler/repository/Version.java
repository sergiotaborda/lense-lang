/**
 * 
 */
package lense.compiler.repository;

import java.util.Arrays;

/**
 * 
 */
public class Version implements Comparable<Version>{

	public static Version valueOf(String str){
		String[] parts = str.split("\\.");
		int[] vals = new int[parts.length];
		
		for (int i =0; i < parts.length; i++){
			vals[i] = Integer.parseInt(parts[i]);
		}
		
		return new Version(vals);
	}

	private int[] vals;
	
	/**
	 * Constructor.
	 * @param vals
	 */
	public Version(int ... vals) {
		this.vals= vals;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		for (int i : vals){
			builder.append(i).append(".");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	public int hashCode(){
		return vals[0];
	}
	
	public boolean equals(Object other){
		return other instanceof Version && Arrays.equals(((Version)other).vals, this.vals);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Version other) {
		for (int i= 0 ; i < vals.length; i++){
		   if (vals[i] > other.vals[i]){
			   return 1;
		   } else if (vals[i] < other.vals[i]){
			   return -1;
		   }
		}
		return 0;
	}

}
