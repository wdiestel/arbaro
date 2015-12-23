/*
 * Created on 11.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.transformation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AllTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for net.sourceforge.arbaro.transformation");
		//$JUnit-BEGIN$
		suite.addTestSuite(MatrixTest.class);
		suite.addTestSuite(VectorTest.class);
		suite.addTestSuite(TransformationTest.class);
		//$JUnit-END$
		return suite;
	}
}
