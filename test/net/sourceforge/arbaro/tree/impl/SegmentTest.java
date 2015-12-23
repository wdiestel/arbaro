/*
 * Created on 11.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.arbaro.tree.impl;

import junit.framework.TestCase;
//import net.sourceforge.arbaro.tree.*;
import net.sourceforge.arbaro.params.*;
import net.sourceforge.arbaro.export.ExporterFactory;
import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeGenerator;
import net.sourceforge.arbaro.tree.TreeGeneratorFactory;
import net.sourceforge.arbaro.tree.impl.SegmentImpl;
import net.sourceforge.arbaro.tree.impl.StemImpl;
import net.sourceforge.arbaro.tree.impl.SubsegmentImpl;
import net.sourceforge.arbaro.tree.impl.TreeImpl;

/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SegmentTest extends TestCase {

	static Tree aTestTree = makeTestTree();
	static StemImpl aTestStem = makeTestStem();
	double prec=0.00001; // precision for comparing of doubles
	
	static StemImpl makeTestStem() {
		StemImpl stem=new StemImpl((TreeImpl)aTestTree,null,0,new Transformation(),0);
		// stem.make();
		return stem;
	}
	
	static Tree makeTestTree() {
		try {
			ParamManager params = new ParamManager();
			TreeGenerator treeGenerator = TreeGeneratorFactory.createTreeGenerator(params); //,null,false,false);
			treeGenerator.setSeed(13);
			Tree tree = treeGenerator.makeTree(null);
			return tree;
		} catch (Exception e) {
			fail(e.getMessage());
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(SegmentTest.class);
	}

	public void testSegment() {
		SegmentImpl segment = new SegmentImpl(
				aTestStem, 
				0, // first segment
				new Transformation(),
				0.3,0.2 // lower and upper radii
				); 
		assertTrue(segment != null);
	}

	public void testMake() {
		//Params params = new Params();
		//LevelParams lparams = params.levelParams[0];
		SegmentImpl segment = new SegmentImpl(
				aTestStem,
				0, // first segment
				new Transformation(),
				0.3,0.2 // lower and upper radii
				);
		
		// change params and test all the different
		// segments (normal, flare, helical)
		segment.make();
		
		// what are good test criteria?
		
		// 1) subsegments>=1
		assertTrue(segment.subsegments.size()>=1);
		// 2) first subsegment start at pos_from
		// 3) last subsegment end at pos_to
		// 4) characteristics of subsegments???
		//       radii depending on taper
		//       linearity depending on nCurveV <|> 0
		assertEquals(((SubsegmentImpl)segment.subsegments.elementAt(segment.subsegments.size()-1)).dist,
				segment.getLength(),prec);
	}

	public void testSubstemPosition() {
	}

	public void testPosFrom() {
	}

	public void testPosTo() {
	}

	public void testIsFirstStemSegment() {
	}

	public void testIsLastStemSegment() {
	}

	public void testAddToMeshpart() {
	}

}
