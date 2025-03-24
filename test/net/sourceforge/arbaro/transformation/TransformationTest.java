/*
 * Created on 10.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.transformation;

import junit.framework.TestCase;

/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransformationTest extends TestCase {

	double prec=0.00001; // precision for comparing of doubles

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TransformationTest.class);
	}

	static Matrix aTestMatrix = new Matrix(1,2,3,4,5,6,7,8,9);
	
	static Vector aTestVector = new Vector(-1,-2,-3);
	
	static Transformation aTestTransformation = new Transformation(aTestMatrix,aTestVector);
	
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
		assertMatrixEquals(T1.matrix,T2.matrix);
		assertVectorEquals(T1.vector,T2.vector);
	}

	/*
	 * Class under test for void Transformation()
	 */
	public void testTransformation() {
		Transformation T=new Transformation();
		assertMatrixEquals(T.matrix(),new Matrix());
		assertVectorEquals(T.vector(),new Vector());
	}

	/*
	 * Class under test for void Transformation(Matrix, Vector)
	 */
	public void testTransformationMatrixVector() {
		Transformation T=new Transformation(aTestMatrix,aTestVector);
		assertMatrixEquals(T.matrix(),aTestMatrix);
		assertVectorEquals(T.vector(),aTestVector);
	}

	public void testMatrix() {
		Transformation T=new Transformation(aTestMatrix,aTestVector);
		assertMatrixEquals(T.matrix(),aTestMatrix);
	}

	public void testVector() {
		Transformation T=new Transformation(aTestMatrix,aTestVector);
		assertVectorEquals(T.vector(),aTestVector);
	}

	public void testProd() {
		Transformation P = aTestTransformation.prod(aTestTransformation);
		assertMatrixEquals(aTestMatrix.prod(aTestMatrix),P.matrix());
		assertVectorEquals(aTestVector.add(aTestMatrix.prod(aTestVector)),P.vector());

		// U x U = U
		Transformation U = new Transformation();
		assertTransformationEquals(U.prod(U),U);
		
		// does the product with inverse transformations 
		// result in the unit transformation...?
		// - this is tested in testInverse!
	}

	public void testApply() {
		Transformation T = new Transformation(aTestMatrix, aTestVector);
		assertVectorEquals(T.apply(new Vector()),T.vector());
		assertVectorEquals(T.apply(new Vector(1,1,1)),new Vector(5,13,21));
	}

	public void testGetX() {
		assertVectorEquals(new Transformation(aTestMatrix,aTestVector).getX(),aTestMatrix.col(0));
	}

	public void testGetY() {
		assertVectorEquals(new Transformation(aTestMatrix,aTestVector).getY(),aTestMatrix.col(1));
	}

	public void testGetZ() {
		assertVectorEquals(new Transformation(aTestMatrix,aTestVector).getZ(),aTestMatrix.col(2));
	}

	public void testGetT() {
		assertVectorEquals(new Transformation(aTestMatrix,aTestVector).getT(),aTestVector);
	}

	public void testRotz() {
		Transformation T = new Transformation().rotx(30).roty(-130).rotz(250).translate(aTestVector);
		// this test is not sufficient, add concrete example
		Transformation T1 = T.rotz(135).rotz(360-135);
		assertTransformationEquals(T,T1);
	}

	public void testRoty() {
		Transformation T = new Transformation().rotx(30).roty(-130).rotz(250).translate(aTestVector);
		// this test is not sufficient, add concrete example
		Transformation T1 = T.roty(135).roty(360-135);
		assertTransformationEquals(T,T1);
	}

	public void testRotx() {
		Transformation T = new Transformation().rotx(30).roty(-130).rotz(250).translate(aTestVector);
		// this test is not sufficient, add concrete example
		Transformation T1 = T.rotx(135).rotx(360-135);
		assertTransformationEquals(T,T1);
	}

	public void testRotxz() {
		Transformation T = new Transformation().rotx(30).roty(-130).rotz(250).translate(aTestVector);
		Transformation T1=T.rotz(261).rotx(-32);
		Transformation T2=T.rotxz(-32,261);
		assertTransformationEquals(T1,T2);
		assertVectorEquals(T2.vector(),aTestVector);
	}

	public void testRotaxisz() {
		fail("test not implemented");
	}

	public void testTranslate() {
		Transformation T = new Transformation(aTestMatrix,aTestVector).translate(aTestVector);
		assertMatrixEquals(T.matrix(),aTestMatrix);
		assertVectorEquals(T.vector(),aTestVector.mul(2));
	}

	public void testRotaxis() {
		Transformation T = new Transformation().rotx(30).roty(-130).rotz(250).translate(aTestVector);
		// this test is not sufficient, add concrete example
		Transformation T1 = T.rotaxis(135,aTestVector).rotaxis(360-135,aTestVector);
		assertTransformationEquals(T,T1);

	}

	public void testInverse() {
		Transformation U = new Transformation();
		assertTransformationEquals(U,U.inverse());
		
		// FIXME: use another more complex Transformation 
		// (maybe some rotation),
		// and see if the product with the inverse is the unit 
		// transformation
		Transformation T = new Transformation().rotx(30).roty(-130).rotz(250).translate(aTestVector);
		assertTransformationEquals(T.prod(T.inverse()),U);
	}

}
