/*
 * Created on 12.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.tree.impl;

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
		TestSuite suite = new TestSuite("Test for net.sourcefore.arbaro.tree");
		//$JUnit-BEGIN$
		suite.addTestSuite(LeafTest.class);
		suite.addTestSuite(SegmentTest.class);
		suite.addTestSuite(StemTest.class);
		suite.addTestSuite(treeTest.class);
		//$JUnit-END$
		return suite;
	}
}
