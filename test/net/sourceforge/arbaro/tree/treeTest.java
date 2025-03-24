package net.sourceforge.arbaro.tree;


import junit.framework.TestCase;

import net.sourceforge.arbaro.tree.TreeGenerator;
import net.sourceforge.arbaro.params.*;
import net.sourceforge.arbaro.export.Progress;

import java.util.*;
import java.io.*;

public class treeTest extends TestCase {
	static final double prec=0.0000000001;
	
	static final String sassafrasCfg = 
		"species=sassafras\n"+
		"Shape=2\n"+
		"Levels=4\n"+
		"Scale=23\n"+
		"ScaleV=7\n"+
//		"ZScale=1\n"+
//		"ZScaleV=0\n"+
		"BaseSize=0.2\n"+
		"Ratio=0.02\n"+
		"RatioPower=1.3\n"+
		"Lobes=3\n"+
		"LobeDepth=0.05\n"+
		"Flare=0\n"+
		"Leaves=15\n"+
		"LeafShape=0\n"+
		"LeafScale=0.25\n"+
		"LeafScaleX=0.7\n"+
		"AttractionUp=0\n"+
		"LeafQuality=1\n"+
		"PruneRatio=0\n"+
		"0Scale=1\n"+
		"0ScaleV=0\n"+
		"0Length=1\n"+
		"0LengthV=0\n"+
		"0Taper=1.05\n"+
		"0BaseSplits=0\n"+
		"0SegSplits=0\n"+
		"0SplitAngle=20\n"+
		"0SplitAngleV=5\n"+
		"0CurveRes=15\n"+
		"0CurveBack=0\n"+
		"0Curve=0\n"+
		"0CurveV=60\n"+
		"1Length=0.4\n"+
		"1LengthV=0\n"+
		"1Taper=1\n"+
		"1DownAngle=90\n"+
		"1DownAngleV=-10\n"+
		"1Rotate=140\n"+
		"1RotateV=0\n"+
		"1Branches=15\n"+
		"1SplitAngle=20\n"+
		"1SplitAngle=0\n"+
		"1SegSplits=0\n"+
		"1CurveRes=5\n"+
		"1CurveBack=30\n"+
		"1Curve=-60\n"+
		"1CurveV=200\n"+
		"2Length=0.7\n"+
		"2LengthV=0\n"+
		"2Taper=1\n"+
		"2DownAngle=50\n"+
		"2DownAngleV=10\n"+
		"2Rotate=140\n"+
		"2RotateV=0\n"+
		"2Branches=20\n"+
		"2SplitAngle=0\n"+
		"2SplitAngle=0\n"+
		"2SegSplits=0\n"+
		"2CurveRes=8\n"+
		"2CurveBack=0\n"+
		"2Curve=-40\n"+
		"2CurveV=300\n"+
		"3Length=0.4\n"+
		"3LengthV=0\n"+
		"3Taper=1\n"+
		"3DownAngle=45\n"+
		"3DownAngleV=10\n"+
		"3Rotate=140\n"+
		"3RotateV=0\n"+
		"3Branches=30\n"+
		"3SplitAngle=0\n"+
		"3SplitAngleV=0\n"+
		"3SegSplits=0\n"+
		"3CurveRes=3\n"+
		"3CurveBack=0\n"+
		"3Curve=0\n"+
		"3CurveV=200\n";
	
	static Dictionary sassafrasParams;

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(treeTest.class);
	}
	
	public void setUp() {
		// fill sassafrasParams;
		sassafrasParams = new Hashtable();
		String[] strArray = sassafrasCfg.split("\n");
		for (int i=0; i<strArray.length; i++) {
			String[] key_val = strArray[i].split("=");
			sassafrasParams.put(key_val[0],key_val[1]);
		}
	}
	
	class ParamException extends RuntimeException{
		public ParamException(String msg) {
			super(msg);
		}
	};
	
	private void checkParam(String key, AbstractParam par) {
		String val = (String)sassafrasParams.get(key);
		if (par instanceof IntParam)
			assertEquals(new Integer(val).intValue(),((IntParam)par).intValue());
		else if (par instanceof FloatParam)
			assertEquals(new Double(val).doubleValue(),((FloatParam)par).doubleValue(),prec);
		else 
			assertEquals(val,par.getValue());			
//		if (! val.equals(par.getValue()))
//			throw new ParamException("Param "+key+": expected value "+
//					val+" but found "+par.getValue());
	}
	
	private void checkParams(Params params) {
		// compare with SassafrasParams
		Enumeration keys = sassafrasParams.keys();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			if (key.equals("species"))
				checkParam(key,params.getParam("Species"));
			else 
				checkParam(key,params.getParam(key));
		}
	}

	public void testTreeGenerator () {
		TreeGenerator generator = TreeGeneratorFactory.createTreeGenerator();
		
		// get params from String SassafrasCfg
		InputStream is = new ByteArrayInputStream(sassafrasCfg.getBytes());
		generator.readParamsFromCfg(is);
		
		checkParams(generator.getParams());
		
		// write Params as XML to a StringBuffer
		StringWriter xsw = new StringWriter();
		PrintWriter pw = new PrintWriter(xsw);
		generator.writeParamsToXML(pw);
		
		// clear params
		generator.clearParams();
		
		assertEquals(generator.getParam("Species"),"default");
		
		// read params again from there
		InputStream xis = new ByteArrayInputStream(xsw.toString().getBytes());
		generator.readParamsFromXML(xis);
		
		checkParams(generator.getParams());

		// make Tree with this params
		Tree tree = generator.makeTree(new Progress());
	}
	
}
