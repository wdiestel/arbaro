// #**************************************************************************
// #
// #    Copyright (C) 2008  Moritz Moeller
// #
// #    This program is free software; you can redistribute it and/or modify
// #    it under the terms of the GNU General Public License as published by
// #    the Free Software Foundation; either version 2 of the License, or
// #    (at your option) any later version.
// #
// #    This program is distributed in the hope that it will be useful,
// #    but WITHOUT ANY WARRANTY; without even the implied warranty of
// #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// #    GNU General Public License for more details.
// #
// #    You should have received a copy of the GNU General Public License
// #    along with this program; if not, write to the Free Software
// #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// #
// #    Send comments and bug fixes to arbaro@virtualritz.com or
// #    diestel@steloj.de
// #
// #**************************************************************************/

package net.sourceforge.arbaro.export;

import java.io.PrintWriter;
import java.lang.Double;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Calendar;

import net.sourceforge.arbaro.export.StringIndenter;
import net.sourceforge.arbaro.mesh.*;
import net.sourceforge.arbaro.params.*;
import net.sourceforge.arbaro.transformation.Box;
import net.sourceforge.arbaro.transformation.Vector;
import net.sourceforge.arbaro.tree.*;



class RIBLeafTraversal extends DefaultTreeTraversal {
	java.util.Vector leafNormalClusters = new java.util.Vector();
	java.util.Vector leafNormals = new java.util.Vector();
	java.util.Vector leafVertexClusters = new java.util.Vector();
	java.util.Vector leafVertices = new java.util.Vector();
	Tree tree;
	long maxLeaves = 1024;
	long counter = 0;

	/**
	 * Accumulates leaf vertices into a java.util.Vector
	 */
	public RIBLeafTraversal(Tree tree) {
		super();
		this.tree = tree;
	}

	public boolean visitLeaf(Leaf l) {

		Vector start = new Vector(0,0,tree.getLeafLength()*tree.getLeafStemLength());
		Vector end = new Vector(0,0,tree.getLeafLength()*(1+tree.getLeafStemLength()));
		Vector normal = new Vector( 0, 1, 0 );

		start = l.getTransformation().apply(start);
		end = l.getTransformation().apply(end);
		normal = l.getTransformation().apply(normal).normalize(); // normalize() should not be neccessary?

		leafVertices.addElement(start);
		leafVertices.addElement(end);
		leafNormals.addElement(normal);

		if( maxLeaves == ++counter ) {
			counter = 0;
			leafVertexClusters.addElement( leafVertices );
			leafVertices = new java.util.Vector();
			leafNormalClusters.addElement( leafNormals );
			leafNormals = new java.util.Vector();
		}

		return true;
	}

	public java.util.Vector getVertexClusters() {
		if(0<leafVertices.size()) {
			leafVertexClusters.addElement( leafVertices );
			leafVertices = null;
		}

		return leafVertexClusters;
	}

	public java.util.Vector getNormalClusters() {
		if(0<leafNormals.size()) {
			leafNormalClusters.addElement(leafNormals);
			leafNormals = null;
		}

		return leafNormalClusters;
	}
}


/**
 * Exports a tree mesh as RenderMan RIB file
 *
 */
final class RIBExporter extends MeshExporter {
	NumberFormat frm = FloatFormat.getInstance();
	Tree tree;
	Mesh mesh;
	java.util.Vector leafVertexClusters;
	java.util.Vector leafNormalClusters;
	String namePrefix;
	StringIndenter indent=new StringIndenter("  ");
	Box bounds = new Box();

	public boolean outputLeafUVs=true;
	public boolean outputStemUVs=true;

	boolean outputNormals = false;

	/**
	 * @param aTree
	 * @param pw
	 * @param p
	 */
	public RIBExporter(Tree tree, MeshGenerator meshGenerator) {
		super(meshGenerator);
		this.tree = tree;
		this.namePrefix = tree.getSpecies() + "_";
	}

	public void doWrite()  {
		writeHeader();

		// need to run those here to find bounds
		buildLeafData();
		buildStemData();

		writeBounds();

		writeInfo();

		writeLooks();

		writeTree();

		writeTreeInstance();

		w.flush();
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

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		return sdf.format(cal.getTime());
	}

	private void writeHeader() {

		println("##RenderMan RIB-Structure 1.1");
		println("##Scene "+namePrefix+"tree");
		println("##Creator Arbarao 2.1 "+ System.getProperty("os.name") + " " + System.getProperty("os.arch"));
		println("##CreationDate "+now());
		println("##For "+System.getProperty("user.name"));
		println("##Shaders leaf stem");
		println("##CapabilitiesNeeded ShadingLanguage");
		println("##Shaders stem, leaf");
		println();
	}

	private void buildLeafData() {
		LeafMesh leafMesh = meshGenerator.createLeafMesh(tree,meshGenerator.getUseQuads());

		RIBLeafTraversal leafWalker = new RIBLeafTraversal(tree);
		tree.traverseTree(leafWalker);
		leafVertexClusters = leafWalker.getVertexClusters();
		leafNormalClusters = leafWalker.getNormalClusters();

		Enumeration vertexClusters = leafVertexClusters.elements();
		while (vertexClusters.hasMoreElements()) {

			Enumeration vertices = ((java.util.Vector)vertexClusters.nextElement()).elements();

			while (vertices.hasMoreElements()) {
				Vector vector = (Vector)vertices.nextElement();
				// bounding sphere expansion
				// should cover our ass for any leaf type added in the future :)
				bounds.extendBy(vector, tree.getLeafWidth());
			}
		}
	}

	private void buildStemData() {
		mesh = meshGenerator.createStemMeshByLevel(tree,progress);

		Enumeration vertices = mesh.allVertices(false);

		while (vertices.hasMoreElements()) {
			Vertex vertex = (Vertex)vertices.nextElement();
			bounds.extendBy(vertex.point);
		}
	}

	private void writeBounds() {

		String boundString =
			" " + frm.format(bounds.getMin().getX()) + " " + frm.format(bounds.getMax().getX()) +
			" " + frm.format(bounds.getMin().getZ()) + " " + frm.format(bounds.getMax().getZ()) +
			" " + frm.format(bounds.getMin().getY()) + " " + frm.format(bounds.getMax().getY());

		println("# Bounds" + boundString);
		println("Bound" + boundString);
		println();

	}

	private void writeInfo() {
		println("# Species "+tree.getSpecies());
		println("# Seed "+tree.getSeed());
		println();
	}

	private void writeLooks() {

		println("#");
		println("# Start of look archives");
		println("# {");
		println();

		println("ArchiveBegin \""+namePrefix+"leaf_look\"  # {");
		indent.increase();
			println("Sides 2");
			//println("IfBegin \"!defined(ArbaroRendering)\"");
			//indent.increase();
				println("Surface \"leaf\"");
			//indent.decrease();
			//println("IfEnd");
		indent.decrease();
		println("ArchiveEnd  # }");
		println();
		println("ArchiveBegin \""+namePrefix+"stem_look\"  # {");
		indent.increase();
			println("Sides 1");
			//println("IfBegin \"!defined(ArbaroRendering)\"");
			//indent.increase();
				println("Surface \"stem\"");
			//indent.decrease();
			//println("IfEnd");
		indent.decrease();
		println("ArchiveEnd  # }");
		println();

		println("# }");
		println("# End of look archives");
		println("#");
		println();

	}

	private void writePrimvars() {

		println("\"constant string species\" [ \""+tree.getSpecies()+"\" ]");
		println("\"constant int seed\" [ "+tree.getSeed()+" ]");

	}

	private void writeTree() {

		println();

		long objCount = (tree.getStemCount()+tree.getLeafCount())*(outputNormals? 2 : 1);

		progress.beginPhase("Writing stem primitives",objCount);

		writeStemObject();

		progress.endPhase();

		println();

		progress.beginPhase("Writing leaves primitives",objCount);

		writeLeafObject();

		progress.endPhase();

	}

	private void writeTreeInstance() {

		println("#");
		println("# start geometry");
		println();

		println("AttributeBegin  # {");
		indent.increase();

		println("Attribute \"identifier\" \"name\" [ \""+namePrefix+"leaves\" ]");
		println("ReadArchive \""+namePrefix+"leaf_look\"");
		println("ObjectInstance \""+namePrefix+"leaf_gprims\"");

		indent.decrease();
		println("AttributeEnd  # }");

		println();
		println();

		println("AttributeBegin  # {");
		indent.increase();

		println("Attribute \"identifier\" \"name\" [ \""+namePrefix+"stems\" ]");
		println("ReadArchive \""+namePrefix+"stem_look\"");
		println("ObjectInstance \""+namePrefix+"stem_gprims\"");

		indent.decrease();
		println("AttributeEnd  # }");

		println();
		println("# end geometry");
		println("#");

	}

	private void writeLeafObject() {

		if (0<leafVertexClusters.size()) {

			println("#");
			println("# Start of leaves geometry");
			println("# {");
			println();

			println("ObjectBegin \""+namePrefix+"leaf_gprims\"  # {");
			indent.increase();

			Enumeration vertexClusters = leafVertexClusters.elements();
			Enumeration normalClusters = leafNormalClusters.elements();
			while (vertexClusters.hasMoreElements()) {

				java.util.Vector leafVertices = (java.util.Vector)vertexClusters.nextElement();

				println("Curves \"linear\"");
				indent.increase();

				print("[ ");

				int elementCounter = 0;

				for (int i=0; i < leafVertices.size()/2; i++) {
					if (17 < elementCounter++) {
						elementCounter = 1;
						println();
						print("  ");
					}

					w.print("2 ");
				}
				w.println("]");
				println("\"nonperiodic\"");

				print("\"P\" [ ");


				Enumeration vertices = leafVertices.elements();
				elementCounter = 0;

				while (vertices.hasMoreElements()) {
					if (3 < elementCounter++) {
						elementCounter = 1;
						println();
						print("  ");
					}
					writeVector((Vector)vertices.nextElement());
				}
				w.println("]");

				print("\"varying normal N\" [ ");

				Enumeration normals = ((java.util.Vector)normalClusters.nextElement()).elements();
				elementCounter = 0;

				while (normals.hasMoreElements()) {
					if (3 < elementCounter++) {
						elementCounter = 1;
						println();
						print("  ");
					}
					Vector normal=(Vector)normals.nextElement();
					writeVector(normal);
					writeVector(normal);
					//writeVector((Vector)normals.nextElement());
				}
				w.println("]");

				println("\"constantwidth\" [ "+tree.getLeafWidth()+" ]");

				writePrimvars();

				indent.decrease();
			}
			indent.decrease();
			println("ObjectEnd  # }");
			println();

			println("# }");
			println("# End of leaves geometry");
			println("#");
		}
	}

	private void writeStemObject() {

		int vertexOffset = 0;
		int elementCounter = 0;

		println("#");
		println("# Start of stems geometry");
		println("# {");
		println();

		println("ObjectBegin \""+namePrefix+"stem_gprims\"  # {");
		indent.increase();

		for (int stemLevel = 0; stemLevel<tree.getLevels(); stemLevel++) {

			for (Enumeration parts=mesh.allParts(stemLevel);
			        parts.hasMoreElements();) {

				// => start a gprim
				if (outputNormals) {
					println("PointsPolygons");
				} else {
					// if we don't output normals, we output the mesh as a subdivision surface
					println("SubdivisionMesh \"catmull-clark\"");
				}

				indent.increase();

				MeshPart mp = (MeshPart)parts.nextElement();

				long highestVertexIndex = 0;
				{
					print("[ ");

					// output number of vertices per each face

					Enumeration faces=mp.allFaces(mesh,vertexOffset,false);
					elementCounter = 0;

					while (faces.hasMoreElements()) {
						if (17 < elementCounter++) {
							elementCounter = 1;
							println();
							print("  ");
						}

						Face face = (Face)faces.nextElement();
						w.print(face.points.length+" ");

						// TODO: add getHighestPointIndex() or similar function to mesh class
						// This code assumes that the highest vertex index is always the tip
						// of the mesh to add a corner tag but we can't rely on this as the
						// inner workings of the MeshPart class are opaque here and can change
						// any time
						highestVertexIndex++;
					}
					w.println("]");
				}


				// output face vertex indices
				{
					print("[ ");

					Enumeration faces=mp.allFaces(mesh,vertexOffset,false);
					elementCounter = 0;

					while (faces.hasMoreElements()) {
						if (3 < elementCounter++) {
							elementCounter = 1;
							println();
							print("  ");
						}

						Face face = (Face)faces.nextElement();
						//Face uvFace = (Face)uvFaces.nextElement();
						writeFaceVertexIndices(face);
					}
					w.println("]");
				}

				if (!outputNormals) {
					// output tags for subdivision surface
					// this will make sure the renderer interpolates any open mesh boundaries and the tips of the stems
					println("[ \"interpolateboundary\" \"facevaryinginterpolateboundary\" \"corner\" ] [ 1 0 1 0 1 1 ] [ 1 1 "+highestVertexIndex+" ] [ 10 ]");
				}

				// output vertex data
				{
					print("\"P\" [ ");

					Enumeration vertices = mp.allVertices(false);
					elementCounter = 0;

					while (vertices.hasMoreElements()) {
						if (3 < elementCounter++) {
							elementCounter = 1;
							println();
							print("  ");
						}

						Vertex vertex = (Vertex)vertices.nextElement();
						writeVector(vertex.point);
					}
					w.println("]");
				}

				if (outputNormals) {
					// output normal data (not needed for subdivision surfaces)
					print("\"N\" [ ");

					Enumeration vertices = mp.allVertices(false);
					elementCounter = 0;

					while (vertices.hasMoreElements()) {
						if (3 < elementCounter++) {
							elementCounter = 1;
							println();
							print("  ");
						}
						Vertex vertex = (Vertex)vertices.nextElement();
						writeVector(vertex.normal);
					}
					w.println("]");
				}

				// output UV data
				if (outputStemUVs) {
					print("\"facevarying float[2] st\" [ ");

					Enumeration vertices = mp.allVertices(true);
					elementCounter = 0;

					while (vertices.hasMoreElements()) {
						if (5 < elementCounter++) {
							elementCounter = 1;
							println();
							print("  ");
						}

						UVVector vertex = (UVVector)vertices.nextElement();
						writeUVPrimvar(vertex);
					}
					w.println("]");
				}

				writePrimvars();

				indent.decrease();

				println();
				//			offset += ((MeshPart)mesh.elementAt(i)).vertexCount();*/

				incProgressCount(AbstractExporter.MESH_PROGRESS_STEP);
			}
		}

		indent.decrease();
		println("ObjectEnd  # }");
		println();

		println("# }");
		println("# End of stems geometry");
		println("#");

	}



	private void writeVector(Vector v) {
		w.print(frm.format(v.getX())+" "
				+frm.format(v.getZ())+" "
				+frm.format(v.getY())+" ");
	}

	private void writeUVPrimvar(UVVector v) {
		w.print(frm.format(v.u)+" "
				+frm.format(v.v)+" ");
	}

	private void writeFaceVertexIndices(Face f) {
		for (int i=0; i<f.points.length; i++) {
			w.print(f.points[i]+" ");
		}
	}

}
