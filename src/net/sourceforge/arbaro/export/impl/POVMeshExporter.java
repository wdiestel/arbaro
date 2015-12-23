//#**************************************************************************
//#
//#    Copyright (C) 2004-2006  Wolfram Diestel
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
//#    Send comments and bug fixes to diestel@steloj.de
//#
//#**************************************************************************/

package net.sourceforge.arbaro.export.impl;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.text.NumberFormat;

import net.sourceforge.arbaro.tree.DefaultTreeTraversal;
import net.sourceforge.arbaro.tree.Leaf;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.mesh.*;
import net.sourceforge.arbaro.transformation.FloatFormat;
import net.sourceforge.arbaro.transformation.Vector;
import net.sourceforge.arbaro.feedback.ProgramInfo;



class POVMeshLeafWriterBase extends DefaultTreeTraversal {
		LeafMesh leafMesh;
		ExporterBase exporter;
		long leafVertexOffset;
		PrintWriter w;
		long leavesProgressCount=0;
		Tree tree;
	
		static final NumberFormat fmt = FloatFormat.getInstance();

		/**
		 * 
		 */
		public POVMeshLeafWriterBase(ExporterBase exporter, LeafMesh leafMesh,
				long leafVertexOffset) {
			super();
			this.w = exporter.getWriter();
			this.exporter = exporter;
			this.leafMesh = leafMesh;
			this.leafVertexOffset = leafVertexOffset;
		}

		public boolean enterTree(Tree tree) {
			this.tree = tree;
			return true;
		}
		
		void writeVector(Vector v) {
			w.print("<"+fmt.format(v.getX())+","
			+fmt.format(v.getZ())+","
			+fmt.format(v.getY())+">");
		}
		
}

/**
 * @author wolfram
 *
 */
class POVMeshLeafFaceWriter extends POVMeshLeafWriterBase {
	
	public POVMeshLeafFaceWriter(ExporterBase exporter, LeafMesh leafMesh,
			long leafVertexOffset) {
		super(exporter,leafMesh,leafVertexOffset);
	}
	
	public boolean visitLeaf(Leaf leaf) {
		String indent = "    ";
		
		for (int i=0; i<leafMesh.getShapeFaceCount(); i++) {
			Face face = leafMesh.shapeFaceAt(i);
			w.print("<" + (leafVertexOffset+face.points[0]) + "," 
					+ (leafVertexOffset+face.points[1]) + "," 
					+ (leafVertexOffset+face.points[2]) + ">");
			if (i<leafMesh.getShapeFaceCount()-1) {
				w.print(",");
			}
			if (i % 6 == 4) {
				// new line
				w.println();
				w.print(indent + "          ");
			}
		}
		w.println();
		
		// increment face offset
		leafVertexOffset += leafMesh.getShapeVertexCount();
		
		exporter.incProgressCount(ExporterBase.LEAF_PROGRESS_STEP);
		
		return true;
	}

	

	
}


/**
 * @author wolfram
 *
 */
class POVMeshLeafNormalWriter extends POVMeshLeafWriterBase {

	/**
	 * @param pw
	 * @param leafMesh
	 * @param leafVertexOffset
	 */
	public POVMeshLeafNormalWriter(ExporterBase exporter, LeafMesh leafMesh,
			long leafVertexOffset) {
		super(exporter, leafMesh, leafVertexOffset);
		// TODO Auto-generated constructor stub
	}

	public boolean visitLeaf(Leaf leaf) {
		String indent = "    ";
		
//		try {
			for (int i=0; i<leafMesh.getShapeVertexCount(); i++) {
				writeVector(leaf.getTransformation().apply(leafMesh.shapeVertexAt(i).normal));
				
				if (i<leafMesh.getShapeVertexCount()-1) {
					w.print(",");
				}
				if (i % 3 == 2) {
					// new line
					w.println();
					w.print(indent+"          ");
				} 
			}
			
			exporter.incProgressCount(ExporterBase.LEAF_PROGRESS_STEP);

			throw new RuntimeException("Not implemented: if using normals for leaves use factor "+
			"3 instead of 2 in progress.beginPhase");
			
//		} catch (Exception e) {
//			throw new TraversalException(e.toString());
//		}

		//return true;

	}

}


/**
 * @author wolfram
 *
 */
class POVMeshLeafUVFaceWriter extends POVMeshLeafWriterBase {

	/**
	 * @param pw
	 * @param leafMesh
	 * @param leafVertexOffset
	 */
	public POVMeshLeafUVFaceWriter(ExporterBase exporter, LeafMesh leafMesh,
			long leafVertexOffset) {
		super(exporter, leafMesh, leafVertexOffset);
		// TODO Auto-generated constructor stub
	}

	public boolean visitLeaf(Leaf l) {
		String indent = "    ";
		
		for (int i=0; i<leafMesh.getShapeFaceCount(); i++) {
			Face face = leafMesh.shapeFaceAt(i);
			w.print("<" + (/*leafFaceOffset+*/face.points[0]) + "," 
					+ (/*leafFaceOffset+*/face.points[1]) + "," 
					+ (/*leafFaceOffset+*/face.points[2]) + ">");
			if (i<leafMesh.getShapeFaceCount()-1) {
				w.print(",");
			}
			if (i % 6 == 4) {
				// new line
				w.println();
				w.print(indent + "          ");
			}
		}
		w.println();
			
		// increment face offset
		//leafFaceOffset += leafMesh.getShapeVertexCount();
		
		exporter.incProgressCount(ExporterBase.LEAF_PROGRESS_STEP);
		
		return true;
	}

}


/**
 * @author wolfram
 *
 */
class POVMeshLeafVertexWriter extends POVMeshLeafWriterBase {

	/**
	 * @param pw
	 * @param leafMesh
	 * @param leafVertexOffset
	 */
	public POVMeshLeafVertexWriter(ExporterBase exporter, LeafMesh leafMesh,
			long leafVertexOffset) {
		super(exporter, leafMesh, leafVertexOffset);
		// TODO Auto-generated constructor stub
	}
	
	public boolean visitLeaf(Leaf l) {
		String indent = "    ";
	
		for (int i=0; i<leafMesh.getShapeVertexCount(); i++) {
			writeVector(l.getTransformation().apply(leafMesh.shapeVertexAt(i).point));
			
			if (i<leafMesh.getShapeVertexCount()-1) {
				w.print(",");
			}
			if (i % 3 == 2) {
				// new line
				w.println();
				w.print(indent+"          ");
			} 
		}
		
		exporter.incProgressCount(ExporterBase.LEAF_PROGRESS_STEP);
		
		return true;
	}


}



/**
 * Exports a tree mesh as Povray include file with mesh2 objects
 * 
 * @author wolfram
 *
 */
public class POVMeshExporter extends MeshExporter {
	Mesh mesh;
	LeafMesh leafMesh;
	Tree tree;
//	Progress progress;
	long leafVertexOffset;
	long stemsProgressCount=0;
	//long leavesProgressCount=0;
	
	boolean outputStemNormals=true;
	boolean outputLeafNormals=false;
	public boolean outputLeafUVs=true;
	public boolean outputStemUVs=true;
	
	String povrayDeclarationPrefix;
	
	static final NumberFormat fmt = FloatFormat.getInstance();
	
	public POVMeshExporter(Tree tree, MeshGenerator meshGenerator) {
		super(meshGenerator);
		this.tree = tree;
		this.povrayDeclarationPrefix =
			tree.getSpecies() + "_" + tree.getSeed() + "_";
	}
	
	public void doWrite() {
			// NumberFormat frm = FloatFormat.getInstance();
	//		progress = meshFactory.getProgress();
			
			// write tree definition as comment
			w.println("/*************** Tree made by: ******************");
			w.println();
			w.println(ProgramInfo.getInfo());
			w.println();
			tree.paramsToXML(w);
			w.println("************************************************/");
			
			// tree scale
			w.println("#declare " + povrayDeclarationPrefix + "height = " 
					+ fmt.format(tree.getHeight()) + ";");
			
			writeStems();
			writeLeaves();
			
			w.flush();
	}
	
	private void writeLeaves() {
		leafMesh = meshGenerator.createLeafMesh(tree,false /* don't use Quads */);

		int passes = 2; 
		if (outputLeafNormals) passes++;
		if (outputLeafUVs) passes=passes++;
		
		progress.beginPhase("Writing leaf mesh",tree.getLeafCount()*2);
		long leafCount = tree.getLeafCount();
		
		if (leafCount>0) {
			w.println("#declare " + povrayDeclarationPrefix + "leaves = mesh2 {");
			w.println("     vertex_vectors { "+leafMesh.getShapeVertexCount()*leafCount);
			//writeLeavesPoints();
			tree.traverseTree(new POVMeshLeafVertexWriter(this,leafMesh,leafVertexOffset));
			w.println("     }");

			if (outputLeafNormals) {
//			  w.println("     normal_vectors { "+mesh.getShapeVertexCount()*leafCount);
//			  trunk.povray_leaves_normals(w,mesh);
//			  w.println("     }");
			}
			
			// output uv vectors
			if (outputLeafUVs && leafMesh.isFlat()) {
				w.println("     uv_vectors {  " + leafMesh.getShapeVertexCount());
				for (int i=0; i<leafMesh.getShapeVertexCount(); i++) {
					writeUVVector(leafMesh.shapeUVAt(i));

					if (i<leafMesh.getShapeVertexCount()-1) {
						w.print(",");
					}
					if (i % 6 == 2) {
						// new line
						w.println();
						w.print("          ");
					} 
					w.println();
				}	
				w.println("    }");
			}
			
			
			leafVertexOffset=0;
			
			w.println("     face_indices { "+leafMesh.getShapeFaceCount()*leafCount);
			//writeLeavesFaces();
			tree.traverseTree(new POVMeshLeafFaceWriter(this,leafMesh,leafVertexOffset));
			w.println("     }");

			if (outputLeafUVs && leafMesh.isFlat()) {
				w.println("     uv_indices { "+leafMesh.getShapeFaceCount()*leafCount);
//				writeLeavesUVFaces();
				tree.traverseTree(new POVMeshLeafUVFaceWriter(this,leafMesh,leafVertexOffset));
				w.println("     }");
			}
			
			w.println("}");
		} else {
			// empty declaration
			w.println("#declare " + povrayDeclarationPrefix + "leaves = sphere {<0,0,0>,0}");		
		}
		
		progress.endPhase();
	}	
	
	private void writeStems() {
		String indent="  ";
		
		// FIXME: instead of outputStemNormals = true use separate boolean
		// for every level
		outputStemNormals = true;
		
		mesh = meshGenerator.createStemMesh(tree,progress);
		long vertex_cnt = mesh.vertexCount();
		long face_cnt = mesh.faceCount();
		long uv_cnt = mesh.uvCount();

		long elements = vertex_cnt+face_cnt;
		
//		int passes = 2; // vectors and faces
		if (outputStemNormals) elements += vertex_cnt; //passes++;
		if (outputStemUVs) elements += face_cnt; // passes=passes++; // for the faces
		progress.beginPhase("Writing stem mesh",elements/*mesh.size()*passes*/);
		
		w.println("#declare " + povrayDeclarationPrefix + "stems = "); 
		w.println(indent + "mesh2 {");
		
		
		// output section points
		w.println(indent+"  vertex_vectors { " + vertex_cnt);
		writeStemPoints(/*indent*/);
		w.println(indent + "  }");
		
		
		// output normals
		if (outputStemNormals) {
			w.println(indent + "  normal_vectors { " + vertex_cnt); 
			writeStemNormals(/*indent*/);
			w.println(indent+"  }");
		}
		
		// output uv vectors
		if (outputStemUVs) {
			w.println(indent + "  uv_vectors {  " + uv_cnt);
			writeStemUVs(/*indent*/);
			w.println();
			w.println(indent+"  }");
		}
		
		// output mesh triangles
		w.println(indent + "  face_indices { " + face_cnt);
		writeStemFaces(false/*,indent*/);
		w.println();
		w.println(indent + "  }");
		

		// output uv faces
		if (outputStemUVs) {
			/*offset = 0;*/
			w.println(indent + "  uv_indices {  " + face_cnt);
			writeStemFaces(true/*,indent*/);
			w.println();
			
			//				incStemsProgressCount();
			w.println(indent+"/* */  }");
		}	
	
	
		
		// use less memory
		// w.println(indent+"  hierarchy off");
		
		w.println(indent + "}");
		progress.endPhase();
		
		/*
		 if (debugmesh) try {
		 // draw normals as cones
		  w.println("union {");
		  for (int i=0; i<size(); i++) { 
		  MeshSection section = ((MeshSection)elementAt(i));
		  for (int j=0; i<section.size(); j++) {
		  w.println("  cone {" 
		  + section.point_at(j).povray()
		  + ",0.01," + (section.point_at(j).add( 
		  section.normal_at(j)).mul(0.2).povray()) 
		  + ",0}");
		  }
		  }
		  w.println("}");
		  } catch (Exception e) {}
		  */
		
	}
	
	
	private void writeStemPoints(/*String indent*/) {
		// w.println(indent + "  /* stem " + mp.getTreePosition() + "*/ ");

		int i = 0;
		for (Enumeration vertices = mesh.allVertices(false);
			vertices.hasMoreElements();) {
			
			Vertex vertex = (Vertex)vertices.nextElement();
			writeVector(vertex.point);
			if (vertices.hasMoreElements()) {
				w.print(",");
			}
			if (++i % 6 == 2) {
				// new line
				w.println();
			} 
			
			incProgressCount(ExporterBase.STEM_PROGRESS_STEP);
		}
		
		w.println();
	}	
	
	public void writeStemFaces(boolean uv/*, String indent*/) 
	throws MeshException {

		int j=0;
		for (Enumeration faces=mesh.allFaces(0,uv,-1 /* all levels */);
			faces.hasMoreElements();) {
			
			Face face = (Face)faces.nextElement();
			w.print("<" + face.points[0] + "," 
					+ face.points[1] + "," 
					+ face.points[2] + ">");
			if (faces.hasMoreElements()) w.print(",");
			
			if (j++ % 6 == 4) {
				// new line
				w.println();
				// w.print(indent + "          ");
			}

			incProgressCount(ExporterBase.STEM_PROGRESS_STEP);
				
		}
	}
	
	private void writeStemNormals(/*String indent*/) 
//	throws MeshException 
	{
		
		int i = 0;

//		try {
			for (Enumeration parts=mesh.elements(); 
				parts.hasMoreElements();) {
					((MeshPart)parts.nextElement()).setNormals(true /* check */);
			}
			// w.println(indent + "  /* stem " + mp.getTreePosition() + "*/ ");
			
			for (Enumeration vertices = mesh.allVertices(false);
			vertices.hasMoreElements();) {
				
				Vertex vertex = (Vertex)vertices.nextElement();
				writeVector(vertex.normal);
				if (vertices.hasMoreElements()) {
					w.print(",");
				}
				if (++i % 6 == 2) {
					// new line
					w.println();
				}

				incProgressCount(ExporterBase.MESH_PROGRESS_STEP);
				
			}
			
			w.println();
			
//		} catch (Exception e) {
//			// e.printStackTrace(System.err);
//			throw new MeshException("Error in MeshSection "+i+": "+e); //.getMessage());
//		}	    
	}
	

	
	private void writeStemUVs(/*MeshPart mp, String indent*/) 
	{
		// it is enough to create one
		// set of uv-Vectors for each stem level,
		// because all the stems of one level are
		// similar - only the base radius is different,
		// so there is a small irregularity at the base
		// of the uv-map, but stem base is hidden in the parent stem
		
		int j=0;
		for (Enumeration vertices=mesh.allVertices(true);
		vertices.hasMoreElements();) {
			
			UVVector vertex = (UVVector)vertices.nextElement();
			writeUVVector(vertex);
			if (vertices.hasMoreElements()) {
				w.print(",");
			}
			
			if (j++ % 6 == 2) {
				// new line
				w.println();
				//				w.print(indent+"          ");
			} 
		}
		
		
		w.println();
	}

	private void writeVector(Vector v) {
		w.print("<"+fmt.format(v.getX())+","
		+fmt.format(v.getZ())+","
		+fmt.format(v.getY())+">");
	}

	private void writeUVVector(UVVector uv) {
		// NumberFormat fmt = FloatFormat.getInstance();
		w.print("<"+fmt.format(uv.u)+","+fmt.format(uv.v)+">");
	}
	
}

