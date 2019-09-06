//#**************************************************************************
//#
//#    Copyright (C) 2008  Moritz Moeller
//#
//#    This program is free software; you can redistribute it and/or modify
//#    it under the terms of the GNU General Public License as published by
//#    the Free Software Foundation; either version 2 of the License, or
//#    (at your option) any later version.
//#
//#    This program is distributed in the hope that it will be useful,
//#    but WITHOUT ANY WARRANTY; without even the implied warranty of
//#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//#    GNU General Public License for more details.
//#
//#    You should have received a copy of the GNU General Public License
//#    along with this program; if not, write to the Free Software
//#    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//#
//#    Send comments and bug fixes to arbaro@virtualritz.com or
//#    diestel@steloj.de
//#
//#**************************************************************************/

package net.sourceforge.arbaro.export;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.sourceforge.arbaro.params.FloatFormat;
import net.sourceforge.arbaro.tree.Tree;


/**
 * Creates a RenderMan scene file with the rendered tree
 * included.
 *
 */
class RIBSceneExporter extends AbstractExporter {
	Tree tree;
	int renderWidth;
	int renderHeight;
	String namePrefix;
	String archiveFile;
	String image;
	StringIndenter indent=new StringIndenter("  ");
	NumberFormat frm = FloatFormat.getInstance();

	/**
	 * @param tree
	 */
	public RIBSceneExporter(Tree tree,
			int renderWidth,
			int renderHeight,
			String archiveFile,
			String image) {
		this.tree = tree;
		this.renderWidth = renderWidth;
		this.renderHeight = renderHeight;
		this.namePrefix = tree.getSpecies() + "_" + tree.getSeed();
		this.archiveFile = archiveFile;
		this.image = image;
	}

	public void doWrite() {
		writeHeader();

		println("Option \"searchpath\" \"shader\" [ \""+windowsPathToUNC(System.getProperty("user.dir"))+"/renderman:@\" ]");
		File tmp = new File(archiveFile);
		println("Option \"searchpath\" \"archive\" [ \""+windowsPathToUNC(tmp.getParent())+":@\" ]");
		println();
		println("Format "+renderWidth+" "+renderHeight+" 1");
		println("ShadingRate 0.25");
		println("PixelSamples 6 6");
		println("PixelFilter \"catmull-rom\" 4 4");
		println();
		println("Display \""+namePrefix+"\" \"framebuffer\" \"rgb\"");
		println("Display \"+"+forwardPathSlashes(image)+"\" \"tiff\" \"rgba\"");
		indent.increase();
		println("\"float[4] quantize\" [ 0 65535 0 65535 ]");
		indent.decrease();
		println();
		println("Imager \"background\" \"background\" [ 0.95 0.95 0.9 ]");
		println();

		println("Projection \"orthographic\"");

		double height = tree.getHeight() * 1.3;
		double width = height * renderWidth / renderHeight;
		double halfWidth = width / 2;
		double halfHeight = height * 0.5;

		println("ScreenWindow "+frm.format(-halfWidth)+" "+frm.format(halfWidth)+" "+frm.format(-halfHeight)+" "+frm.format(halfHeight));
		println("Translate 0 "+frm.format(-0.45*height)+" "+frm.format(tree.getWidth()));
		println();
		println("WorldBegin  # {");
		indent.increase();
			println();
			println("LightSource \"shadowdistant\" \"key\" \"from\" [ 5000 5000 -3000 ] \"intensity\" [ 1.2 ] \"string shadowname\" [ \"raytrace\" ] \"float samples\" [ 1 ]");
			println("LightSource \"pointlight\" \"fill\" \"from\" [ -5000 2000 3000 ] \"intensity\" [ 0.5 ] \"float decay\" [ 0 ]");
			println();
			println("Rotate -90 0 1 0");
			println("Attribute \"visibility\" \"int transmission\" [ 1 ]");
			println("Attribute \"shade\" \"transmissionhitmode\" [ \"primitive\" ]");
			println("ReadArchive \"" + tmp.getName() + "\"");
			println();
		indent.decrease();
		println("WorldEnd  # }");

		w.flush();
	}

	private String windowsPathToUNC( String path ) {
		String fixedPath;
		if( System.getProperty("file.separator").equals("\\") ) {
			fixedPath = "//"+forwardPathSlashes(path.replace(":",""));
		} else {
			fixedPath = path;
		}
		return fixedPath;
	}

	private String forwardPathSlashes( String path ) {
		String fixedPath;
		if( System.getProperty("file.separator").equals("\\") ) {
			fixedPath = path.replace('\\','/');
		} else {
			fixedPath = path;
		}
		return fixedPath;
	}

	private static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		return sdf.format(cal.getTime());
	}

	private void writeHeader() {

		println("##RenderMan RIB-Structure 1.1");
		println("##Scene "+namePrefix+"render");
		println("##Creator Arbarao 2.1 "+ System.getProperty("os.name") + " " + System.getProperty("os.arch"));
		println("##CreationDate "+now());
		println("##For "+System.getProperty("user.name"));
		println("##Shaders leaf stem");
		println("##CapabilitiesNeeded ShadingLanguage, Ray Tracing");
		println();
	}

	private void print(String what) {
		if (0<what.length()) {
			w.print(indent.getIndent()+what);
		}
	}

	private void println(String what) {
		if (0<what.length()) {
			w.println(indent.getIndent()+what);
		} else {
			w.println();
		}
	}

	private void println() {
		w.println();
	}

}
