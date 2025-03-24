//  #**************************************************************************
//  #
//  #    Copyright (C) 2003-2006  Wolfram Diestel
//  #
//  #    This program is free software; you can redistribute it and/or modify
//  #    it under the terms of the GNU General Public License as published by
//  #    the Free Software Foundation; either version 2 of the License, or
//  #    (at your option) any later version.
//  #
//  #    This program is distributed in the hope that it will be useful,
//  #    but WITHOUT ANY WARRANTY; without even the implied warranty of
//  #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  #    GNU General Public License for more details.
//  #
//  #    You should have received a copy of the GNU General Public License
//  #    along with this program; if not, write to the Free Software
//  #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//  #
//  #    Send comments and bug fixes to diestel@steloj.de
//  #
//  #**************************************************************************/

package net.sourceforge.arbaro.export;

import net.sourceforge.arbaro.mesh.MeshGenerator;
import net.sourceforge.arbaro.mesh.MeshGeneratorFactory;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.params.Params;


/**
 * @author wolfram
 *
 */
public class ExporterFactory {
	// Outputformats
	public final static int POV_MESH = 0;
	public final static int POV_CONES = 1;
	public final static int RIB = 2;
	public final static int DXF = 3;
	public final static int OBJ = 4;

	static int exportFormat = ExporterFactory.POV_MESH;
	static String outputPath = System.getProperty("user.dir")+System.getProperty("file.separator");
	static String imageFilePath = outputPath+System.getProperty("file.separator")+"tree";

	// TODO move render parameters to a special RenderCmd class?
	static int renderW = 400;
	static int renderH = 600;

	static boolean outputStemUVs = false;
	static boolean outputLeafUVs = false;

	final static String[] formats = { "Povray meshes","Povray primitives","RenderMan RIB","AutoCAD DXF","Wavefront OBJ" };
	final static String[] shortformats = { "POV_MESH","POV_CONES","RIB","DXF","OBJ" };


	/**
	 * Sets the output type for the Povray code
	 * (primitives like cones, spheres and discs or
	 * triangle meshes)
	 *
	 * @param output
	 */
	static public void setExportFormat(int output) {
		exportFormat = output;
	}

	static public int getExportFormat() {
		return exportFormat;
	}

	static public String getOutputPath() {
		return outputPath;
	}

	static public void setOutputPath(String p) {
		outputPath=p;
	}

	static public String getImageFilePath() {
		return imageFilePath;
	}

	static public void setImageFilePath(String p) {
		imageFilePath=p;
	}

	static public void setRenderW(int w) {
		renderW = w;
	}

	static public void setRenderH(int h) {
		renderH=h;
	}

	static public int getRenderH() {
		return renderH;
	}

	static public int getRenderW() {
		return renderW;
	}

	static public void setOutputStemUVs(boolean oUV) {
		outputStemUVs = oUV;
	}

	static public boolean getOutputStemUVs() {
		return outputStemUVs;
	}

	static public void setOutputLeafUVs(boolean oUV) {
		outputLeafUVs = oUV;
	}

	static public boolean getOutputLeafUVs() {
		return outputLeafUVs;
	}

	static public Exporter createExporter(Tree tree/*, Params params*/)
		throws InvalidExportFormatError {

		Exporter exporter = null;
		MeshGenerator meshGenerator;
		boolean useQuads = false;

		if (exportFormat == POV_CONES) {
			exporter = new POVConeExporter(tree/*,params*/);
		}
		else if (exportFormat == POV_MESH) {
			meshGenerator = MeshGeneratorFactory.createMeshGenerator(/*params,*/ useQuads);
			exporter = new POVMeshExporter(tree,meshGenerator);
			((POVMeshExporter)exporter).outputStemUVs = outputStemUVs;
			((POVMeshExporter)exporter).outputLeafUVs = outputLeafUVs;
		} else if (exportFormat == RIB) {
			useQuads = true;
			meshGenerator = MeshGeneratorFactory.createMeshGenerator(useQuads);
			exporter = new RIBExporter(tree,meshGenerator);
			((RIBExporter)exporter).outputStemUVs = outputStemUVs;
			((RIBExporter)exporter).outputLeafUVs = outputLeafUVs;
		} else if (exportFormat == DXF) {
			meshGenerator = MeshGeneratorFactory.createMeshGenerator(/*params,*/ useQuads);
			exporter = new DXFExporter(tree,meshGenerator);
		} else if (exportFormat == OBJ) {
			useQuads = true;
			meshGenerator = MeshGeneratorFactory.createMeshGenerator(/*params,*/ useQuads);
			exporter = new OBJExporter(tree,meshGenerator);
			((OBJExporter)exporter).outputStemUVs = outputStemUVs;
			((OBJExporter)exporter).outputLeafUVs = outputLeafUVs;
		} else {
			throw new InvalidExportFormatError("Invalid export format");
		}

		return exporter;
	}

	static public Exporter createSceneExporter(Tree tree /*, Params params*/) {

		if(exportFormat == RIB) {
			return new RIBSceneExporter(tree,/*params,*/renderW,renderH,outputPath,imageFilePath);
		} else {
			return new POVSceneExporter(tree,/*params,*/renderW,renderH);
		}
	}

	static public Exporter createShieldedExporter(Tree tree, Params params) throws InvalidExportFormatError
	{
		return new ShieldedExporter(createExporter(tree));
	}

	static public Exporter createShieldedSceneExporter(Tree tree /*,Params params*/) {
		return new ShieldedExporter(createSceneExporter(tree /*, params*/));
	}

	public static String[] getExportFormats() {
		return formats;
	}

	public static String[] getShortExportFormats() {
		return shortformats;
	}

}
