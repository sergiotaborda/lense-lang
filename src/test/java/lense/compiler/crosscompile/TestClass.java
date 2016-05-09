package lense.compiler.crosscompile;

public class TestClass {

	int field = 7;
	String g = "Hello";
	
	public int calculateIt (int x){
		
		int a = 2;
		
		a = 8;
		
		int b = -3;
		int c = this.field + b;
		
		this.field = c;
		
		int d = c * a + x;
		
		int u = calculateIt(a - c);
		
		System.out.print(g);
		
		Object xx = new Object();
		
	
		return u;
	}
}
