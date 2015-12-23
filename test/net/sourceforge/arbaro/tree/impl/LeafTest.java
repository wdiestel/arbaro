/*
 * Created on 11.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.tree.impl;

import junit.framework.TestCase;
import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.tree.impl.LeafImpl;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LeafTest extends TestCase {

	double prec=0.00001; // precision for comparing of doubles

	private void assertMatrixEquals(Matrix m1, Matrix m2) {
		for (int r=0; r<=2; r++) {
			for (int c=0;c<=2; c++) {
				assertEquals(m1.get(r,c),m2.get(r,c),prec);
			}
		}
	}
	
	private void assertVectorEquals(Vector v1, Vector v2) {
		assertEquals(v1.getX(),v2.getX(),prec);
		assertEquals(v1.getY(),v2.getY(),prec);
		assertEquals(v1.getZ(),v2.getZ(),prec);
	}
	
	private void assertTransformationEquals(Transformation T1, Transformation T2) {
		assertMatrixEquals(T1.matrix(),T2.matrix());
		assertVectorEquals(T1.vector(),T2.vector());
	}


	public static void main(String[] args) {
		junit.swingui.TestRunner.run(LeafTest.class);
	}
	
	public void testLeaf() {
		Params params = new Params();
		
		Transformation T[] = new Transformation[5];
		
		T[0] = new Transformation(); 
		T[1] = new Transformation().rotx(35).translate(new Vector(0.9,1.2,0));
		T[2] = new Transformation().rotz(120).translate(new Vector(0.9,1.2,0));
		T[3] = new Transformation().roty(156).translate(new Vector(0.9,1.2,0));
		T[4] = new Transformation().rotx(180).translate(new Vector(-0.9,1.2,0));
		
		LeafImpl leaf;
		for (int i=0; i<T.length; i++) {
			
			// no leaf bend
			params.LeafBend=0;
			leaf = new LeafImpl(T[i]);
			leaf.make(params);
			assertTransformationEquals(T[i],leaf.transf);
			
			// max leaf bend
			params.LeafBend=1.0;
			leaf = new LeafImpl(T[i]);
			leaf.make(params);
			
			Vector t = T[i].getT();
			Vector z = leaf.transf.getZ();

			// leaf shouldn't move, only rotate
			assertVectorEquals(t,leaf.transf.getT());

			// FIXME:
			// the algorithm not ensures for all transformations,
			// that the following conditions are true, sometimes
			// one get only a crude approximation, maybe a 
			// *statistical test* would be better here.
			// The goeal of leaf bending is to get more structure 
			// (nicer variations) in the "surface" of the tree,
			// not to reach a horizontal orientation of _all_ leaves.
			
			// z and t should be parallel now (scalar product 1 or -1)
			if (t.abs()>prec) 
				assertEquals("T["+i+"]",Math.abs(z.prod(t.normalize())),1,prec);
			
			// y should show up => z-component of z-axis is 0
			assertEquals("T["+i+"]",z.getZ(),0,prec);
		}
	}

}
