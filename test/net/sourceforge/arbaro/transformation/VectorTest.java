/*
 * Created on 10.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.transformation;

import junit.framework.TestCase;
import net.sourceforge.arbaro.transformation.Vector;

/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VectorTest extends TestCase {

	double prec=0.00001; // precision for comparing of doubles

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(VectorTest.class);
	}

	/*
	 * Class under test for void Vector()
	 */
	public void testVector() {
		Vector v = new Vector();
		assertEquals(v.getX(),0.0,prec);
		assertEquals(v.getY(),0.0,prec);
		assertEquals(v.getZ(),0.0,prec);
	}

	/*
	 * Class under test for void Vector(double, double, double)
	 */
	public void testVectordoubledoubledouble() {
		Vector v = new Vector(13.1,1700000.23,-0.002);
		assertEquals(v.getX(),13.1,prec);
		assertEquals(v.getY(),1700000.23,prec);
		assertEquals(v.getZ(),-0.002,prec);
	}

	public void testAbs() {
		Vector v1 = new Vector(1,2,3);
		Vector v2 = new Vector(-1,2,-3);
		assertEquals(v1.abs(),Math.sqrt(14),prec);
		assertEquals(v2.abs(),Math.sqrt(14),prec);
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		Vector v = new Vector(3,-4,5);
		assertEquals(v.toString(),"<3,-4,5>");

		Vector v1 = new Vector(3.0001,-4.46464646,5.999);
		assertEquals(v1.toString(),"<3.0001,-4.46465,5.999>");
	}

	public void testNormalize() {
		Vector v = new Vector(3,4,5);
		Vector n = v.normalize();
		assertEquals(n.abs(),1.0,prec);
		assertEquals(n.getX()/n.getY(),v.getX()/v.getY(),prec);
		assertEquals(n.getX()/n.getZ(),v.getX()/v.getZ(),prec);
		assertEquals(n.getZ()/n.getY(),v.getZ()/v.getY(),prec);
	}

	public void testGetX() {
		Vector v = new Vector(13.1,2,3);
		assertEquals(v.getX(),13.1,prec);
	}

	public void testGetY() {
		Vector v = new Vector(13.1,-2.444,3);
		assertEquals(v.getY(),-2.444,prec);
	}

	public void testGetZ() {
		Vector v = new Vector(13.1,2,3.333);
		assertEquals(v.getZ(),3.333,prec);
	}

	public void testMul() {
		Vector u = new Vector(-1,2,3);
		Vector v = u.mul(-2);
		assertEquals(v.getX(),2,prec);
		assertEquals(v.getY(),-4,prec);
		assertEquals(v.getZ(),-6,prec);
	}

	public void testProd() {
		Vector u = new Vector(-1,2,3);
		Vector v = new Vector(-2,2,2);
		double p = u.prod(v);
		assertEquals(p,12,prec);
	}

	public void testDiv() {
		Vector u = new Vector(-1,2,3);
		Vector v = u.div(-2);
		assertEquals(v.getX(),0.5,prec);
		assertEquals(v.getY(),-1,prec);
		assertEquals(v.getZ(),-1.5,prec);
	}

	public void testAdd() {
		Vector u = new Vector(-1,2,3);
		Vector v = new Vector(-2,2,2);
		Vector w = u.add(v);
		assertEquals(w.getX(),-3,prec);
		assertEquals(w.getY(),4,prec);
		assertEquals(w.getZ(),5,prec);
	}

	public void testSub() {
		Vector u = new Vector(-1,2,3);
		Vector v = new Vector(-2,2,2);
		Vector w = u.sub(v);
		assertEquals(w.getX(),1,prec);
		assertEquals(w.getY(),0,prec);
		assertEquals(w.getZ(),1,prec);		
	}

	public void testAtan2() {
		assertEquals(Vector.atan2(0,2),0,prec);
		assertEquals(Vector.atan2(2,0),90,prec);
		assertEquals(Vector.atan2(0,-2),180,prec);
		assertEquals(Vector.atan2(0.00000001,-2),180,prec);
		assertEquals(Vector.atan2(-0.00000001,-2),-180,prec);
		assertEquals(Vector.atan2(-2,0),-90,prec);
		assertEquals(Vector.atan2(2,2),45,prec);
		assertEquals(Vector.atan2(2,-2),135,prec);
		assertEquals(Vector.atan2(-2,2),-45,prec);
		assertEquals(Vector.atan2(-2,-2),-135,prec);
	}

	public void testSetMaxCoord() {
		Vector u = new Vector(-1,2,3);
		Vector v = new Vector(-2,2,2);
		u.setMaxCoord(v);
		assertEquals(u.getX(),-1,prec);
		assertEquals(u.getY(),2,prec);
		assertEquals(u.getZ(),3,prec);		
	}

	public void testSetMinCoord() {
		Vector u = new Vector(-1,2,3);
		Vector v = new Vector(-2,2,2);
		u.setMinCoord(v);
		assertEquals(u.getX(),-2,prec);
		assertEquals(u.getY(),2,prec);
		assertEquals(u.getZ(),2,prec);		
	}

}
