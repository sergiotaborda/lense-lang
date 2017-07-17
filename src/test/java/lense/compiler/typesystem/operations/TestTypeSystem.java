package lense.compiler.typesystem.operations;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTypeSystem {

    @Test
    public void testBottomType() {
        System system = new AlphaSystem();
        
        assertTrue( "Nothing is not equal to it self ", system.areEqual(BottomType.instance(), BottomType.instance()));
    }
    
    @Test
    public void testBoundType() {
        System system = new AlphaSystem();
        
        Type A = BoundedType.within(TopType.instance(), BottomType.instance()).simplify(system);
        
        assertTrue(system.areEqual(A, BottomType.instance()));
    }
    
    @Test
    public void testInheritance() {
        System system = new AlphaSystem();
        
        Type I = new ConcreteType("I");
        Type K = new ConcreteType("K");
        
        Type A = new ConcreteType("A");
        Type B = new ConcreteType("B", A, I);
        Type C = new ConcreteType("C", B, K);
        
        assertSubType(system, B , A);
        assertSubType(system, B , I);

        assertSubType(system, C , A);
        assertSubType(system, C , B);
        assertSubType(system, C , I);
        assertSubType(system, C , K);
  
        assertNotSubType(system, I , C);
        assertNotSubType(system, B , C);
    }
    
    @Test
    public void testIntersection() {
        
        System system = new AlphaSystem();
        
        Type I = new ConcreteType("I");
        Type K = new ConcreteType("K");
        
        Type A = new ConcreteType("A");
        Type B = new ConcreteType("B", A);

        Type C = new ConcreteType("C");
        Type D = new ConcreteType("D", C, I, K );
        
        Type intersect = A.and(A).and(A);
        Type simplified = intersect.simplify(system);
        
        assertEquals( system , A, simplified);
        
        Type intersectWithBottom = A.and(BottomType.instance());
        simplified = intersectWithBottom.simplify(system);
        
        assertEquals( system , BottomType.instance(), simplified);
        
        Type intersectWithSubtype = A.and(B);
        simplified = intersectWithSubtype.simplify(system);
        
        assertEquals( system , B, simplified);
        
        Type intersectWithSubtypeAndOther = A.and(B).and(C);
        simplified = intersectWithSubtypeAndOther.simplify(system);
        
        assertEquals( system , B.and(C), simplified);
        
        ConcreteType F = new ConcreteType("F", A);
        F.setFinal(true);
        
        Type intersectWithFinalSubtype = A.and(F);
        simplified = intersectWithFinalSubtype.simplify(system);
        
        assertEquals( system , F, simplified);
       
        Type intersectWithFinalSubtypeAndOther = A.and(F).and(C);
        simplified = intersectWithFinalSubtypeAndOther.simplify(system);
        
        assertEquals( system , BottomType.instance(), simplified);

        assertSubType(system,  B.and(C),  A.and(C));
        assertSubType(system,  B.and(C),  C.and(A));
        assertSubType(system,  C.and(B),  A.and(C));
        assertSubType(system,  C.and(B),  C.and(A));
        
        assertSubType(system,  C.and(A),  C);
        assertSubType(system,  C.and(A),  A);
        
        assertSubType(system,  A ,  A);
        
        assertSubType(system,  D ,  I.and(K));

    }
    
    @Test
    public void testUnion() {
        
        System system = new AlphaSystem();
        
        Type A = new ConcreteType("A");
        Type B = new ConcreteType("B", A);
        Type C = new ConcreteType("C");
        
        Type union = A.or(A).or(A);
        Type simplified = union.simplify(system);
        
        assertEquals( system , A, simplified);
        
        Type U = A.or(C).simplify(system);
        Type V = B.or(C).simplify(system);
        
        assertSubType(system, V, U);
        
        Type W = C.or(B).simplify(system);
        
        assertSubType(system, W, U);
    }
    
    @Test
    public void testProduct() {
        
        System system = new AlphaSystem();
        
        Type A = new ConcreteType("A");
        Type B = new ConcreteType("B", A);
        Type C = new ConcreteType("C");
        
        Type product = A.times(A).times(A);
        Type simplified = product.simplify(system);
        
        assertEquals( system, product, simplified);
        
        Type U = A.times(C).simplify(system);
        Type V = B.times(C).simplify(system);
        
        assertTrue(V + " is not a sub type of " + U , system.isSubType(V, U));
    }

    @Test
    public void testProductDistribution() {
        
        System system = new AlphaSystem();
        
        Type A = new ConcreteType("A");
        Type B = new ConcreteType("B");
        Type C = new ConcreteType("C");
        Type D = new ConcreteType("D");
        
        Type X = A.or(B);
        
        assertEquals(system, new UnionType(A, B), X);
        
        Type Y = C.or(D);
        
        assertEquals(system, new UnionType(C, D), Y);
        
        Type simplified = X.times(Y).simplify(system);
        
        assertEquals(system,
                new UnionType(new ProductType(A, C) , new ProductType(A, D), new ProductType(B, C), new ProductType(B, D)), 
                simplified
        );
                
        Type Z = C.times(X).simplify(system);
        
        assertEquals(system,
                new UnionType(new ProductType(C, A) , new ProductType(C, B)), 
                Z
        );
        
        Type W = X.times(C).simplify(system);
        
        assertEquals(system,
                new UnionType(new ProductType(C, A) , new ProductType(C, B)), 
                W
        );
        
    }
    
    @Test
    public void testFunction() {
        
        System system = new AlphaSystem();
        
        
        Type S = new ConcreteType("S");
        Type R = new ConcreteType("R",S);
        Type A = new ConcreteType("A");
        Type B = new ConcreteType("B", A);
       
        Type f = new FunctionType(R, A);
        Type g = new FunctionType(S, B);
        
        assertSubType(system , f, g);
        assertNotSubType(system , g, f);
        
        Type r = new FunctionType(R, A);
        Type s = new FunctionType(S, A);
        
        assertSubType(system , r, s);
        assertNotSubType(system , s, r);
        
    }
    
    void assertEquals(System system, Type expected, Type obtained){
        assertTrue( expected + " is not equal to " + obtained, system.areEqual(expected, obtained));
    }
    
    void assertSubType(System system, Type a, Type b){
        assertTrue( a + " is not a sub type of " + b, system.isSubType(a, b));
    }
    
    void assertNotSubType(System system, Type a, Type b){
        assertFalse( a + " is a sub type of " + b, system.isSubType(a, b));
    }
    
}
