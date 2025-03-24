/*
 * Created on 10.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.transformation;

import junit.framework.TestCase;
import net.sourceforge.arbaro.transformation.Matrix;

/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MatrixTest extends TestCase {

	double prec=0.00001; // precision for comparing of doubles
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(MatrixTest.class);
	}

	/*
	 * Class under test for void Matrix()
	 */
	public void testMatrix() {
		Matrix m = new Matrix();
		assertEquals(m.get(0,0),1,prec);
		assertEquals(m.get(0,1),0,prec);
		assertEquals(m.get(0,2),0,prec);
		assertEquals(m.get(1,0),0,prec);
		assertEquals(m.get(1,1),1,prec);
		assertEquals(m.get(1,2),0,prec);
		assertEquals(m.get(2,0),0,prec);
		assertEquals(m.get(2,1),0,prec);
		assertEquals(m.get(2,2),1,prec);
	}

	/*
	 * Class under test for void Matrix(double, double, double, double, double, double, double, double, double)
	 */
	public void testMatrixdoubledoubledoubledoubledoubledoubledoubledoubledouble() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9);
		assertEquals(m.get(0,0),1,prec);
		assertEquals(m.get(0,1),2,prec);
		assertEquals(m.get(0,2),3,prec);
		assertEquals(m.get(1,0),4,prec);
		assertEquals(m.get(1,1),5,prec);
		assertEquals(m.get(1,2),6,prec);
		assertEquals(m.get(2,0),7,prec);
		assertEquals(m.get(2,1),8,prec);
		assertEquals(m.get(2,2),9,prec);
	}

	public void testRow() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9);
		Vector u = m.row(0); 
		assertEquals(u.getX(),1,prec);
		assertEquals(u.getY(),2,prec);
		assertEquals(u.getZ(),3,prec);
		Vector v = m.row(1); 
		assertEquals(v.getX(),4,prec);
		assertEquals(v.getY(),5,prec);
		assertEquals(v.getZ(),6,prec);
		Vector w = m.row(2); 
		assertEquals(w.getX(),7,prec);
		assertEquals(w.getY(),8,prec);
		assertEquals(w.getZ(),9,prec);
	}

	public void testCol() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9);
		Vector u = m.col(0); 
		assertEquals(u.getX(),1,prec);
		assertEquals(u.getY(),4,prec);
		assertEquals(u.getZ(),7,prec);
		Vector v = m.col(1); 
		assertEquals(v.getX(),2,prec);
		assertEquals(v.getY(),5,prec);
		assertEquals(v.getZ(),8,prec);
		Vector w = m.col(2); 
		assertEquals(w.getX(),3,prec);
		assertEquals(w.getY(),6,prec);
		assertEquals(w.getZ(),9,prec);
	}

	public void testGet() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9);
		assertEquals(m.get(0,0),1,prec);
		assertEquals(m.get(0,1),2,prec);
		assertEquals(m.get(0,2),3,prec);
		assertEquals(m.get(1,0),4,prec);
		assertEquals(m.get(1,1),5,prec);
		assertEquals(m.get(1,2),6,prec);
		assertEquals(m.get(2,0),7,prec);
		assertEquals(m.get(2,1),8,prec);
		assertEquals(m.get(2,2),9,prec);
	}

	public void testSet() {
		Matrix m = new Matrix();
		m.set(0,0,-1);
		m.set(0,1,2);
		m.set(0,2,3);
		m.set(1,0,4);
		m.set(1,1,5);
		m.set(1,2,6);
		m.set(2,0,7);
		m.set(2,1,8);
		m.set(2,2,9);
		assertEquals(m.get(0,0),-1,prec);
		assertEquals(m.get(0,1),2,prec);
		assertEquals(m.get(0,2),3,prec);
		assertEquals(m.get(1,0),4,prec);
		assertEquals(m.get(1,1),5,prec);
		assertEquals(m.get(1,2),6,prec);
		assertEquals(m.get(2,0),7,prec);
		assertEquals(m.get(2,1),8,prec);
		assertEquals(m.get(2,2),9,prec);
		m.set(0,0,1);
		assertEquals(m.get(0,0),1,prec);
	}

	public void testTranspose() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9).transpose();
		assertEquals(m.get(0,0),1,prec);
		assertEquals(m.get(0,1),4,prec);
		assertEquals(m.get(0,2),7,prec);
		assertEquals(m.get(1,0),2,prec);
		assertEquals(m.get(1,1),5,prec);
		assertEquals(m.get(1,2),8,prec);
		assertEquals(m.get(2,0),3,prec);
		assertEquals(m.get(2,1),6,prec);
		assertEquals(m.get(2,2),9,prec);
	}

	public void testMul() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9).mul(2);
		assertEquals(m.get(0,0),2,prec);
		assertEquals(m.get(0,1),4,prec);
		assertEquals(m.get(0,2),6,prec);
		assertEquals(m.get(1,0),8,prec);
		assertEquals(m.get(1,1),10,prec);
		assertEquals(m.get(1,2),12,prec);
		assertEquals(m.get(2,0),14,prec);
		assertEquals(m.get(2,1),16,prec);
		assertEquals(m.get(2,2),18,prec);
	}

	/*
	 * Class under test for Matrix prod(Matrix)
	 */
	public void testProdMatrix() {
		Matrix n1 = new Matrix(1,2,3,4,5,6,7,8,9);
		Matrix n2 = new Matrix(1,2,3,4,5,6,7,8,9);
		Matrix m = n1.prod(n2);
		assertEquals(m.get(0,0),n1.row(0).prod(n2.col(0)),prec);
		assertEquals(m.get(0,1),n1.row(0).prod(n2.col(1)),prec);
		assertEquals(m.get(0,2),n1.row(0).prod(n2.col(2)),prec);
		assertEquals(m.get(1,0),n1.row(1).prod(n2.col(0)),prec);
		assertEquals(m.get(1,1),n1.row(1).prod(n2.col(1)),prec);
		assertEquals(m.get(1,2),n1.row(1).prod(n2.col(2)),prec);
		assertEquals(m.get(2,0),n1.row(2).prod(n2.col(0)),prec);
		assertEquals(m.get(2,1),n1.row(2).prod(n2.col(1)),prec);
		assertEquals(m.get(2,2),n1.row(2).prod(n2.col(2)),prec);
	}

	public void testAdd() {
		Matrix n1 = new Matrix(1,2,3,4,5,6,7,8,9);
		Matrix n2 = new Matrix(1,2,3,4,5,6,7,8,9);
		Matrix m = n1.add(n2);
		assertEquals(m.get(0,0),2,prec);
		assertEquals(m.get(0,1),4,prec);
		assertEquals(m.get(0,2),6,prec);
		assertEquals(m.get(1,0),8,prec);
		assertEquals(m.get(1,1),10,prec);
		assertEquals(m.get(1,2),12,prec);
		assertEquals(m.get(2,0),14,prec);
		assertEquals(m.get(2,1),16,prec);
		assertEquals(m.get(2,2),18,prec);
	}

	/*
	 * Class under test for Vector prod(Vector)
	 */
	public void testProdVector() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9);
		Vector v = new Vector(-1,1,2);
		Vector p = m.prod(v);
		assertEquals(p.getX(),7,prec);
		assertEquals(p.getY(),13,prec);
		assertEquals(p.getZ(),19,prec);
	}

	public void testDiv() {
		Matrix m = new Matrix(1,2,3,4,5,6,7,8,9).div(2);
		assertEquals(m.get(0,0),0.5,prec);
		assertEquals(m.get(0,1),1,prec);
		assertEquals(m.get(0,2),1.5,prec);
		assertEquals(m.get(1,0),2,prec);
		assertEquals(m.get(1,1),2.5,prec);
		assertEquals(m.get(1,2),3,prec);
		assertEquals(m.get(2,0),3.5,prec);
		assertEquals(m.get(2,1),4,prec);
		assertEquals(m.get(2,2),4.5,prec);
	}

	public void testSub() {
		Matrix n1 = new Matrix(1,2,3,4,5,6,7,8,9);
		Matrix n2 = new Matrix(-1,-2,-3,-4,-5,-6,-7,-8,-9);
		Matrix m = n1.sub(n2);
		assertEquals(m.get(0,0),2,prec);
		assertEquals(m.get(0,1),4,prec);
		assertEquals(m.get(0,2),6,prec);
		assertEquals(m.get(1,0),8,prec);
		assertEquals(m.get(1,1),10,prec);
		assertEquals(m.get(1,2),12,prec);
		assertEquals(m.get(2,0),14,prec);
		assertEquals(m.get(2,1),16,prec);
		assertEquals(m.get(2,2),18,prec);
	}

}
