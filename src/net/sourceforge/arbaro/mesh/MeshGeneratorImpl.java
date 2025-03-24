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

package net.sourceforge.arbaro.mesh;

import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.export.Progress;
import net.sourceforge.arbaro.export.Console;

/**
 * @author wolfram
 *
 */
class MeshGeneratorImpl implements MeshGenerator {
//	Params params;
	public boolean useQuads;
	
	public boolean getUseQuads() { return useQuads; } 

	public MeshGeneratorImpl(boolean useQuads) {
		super();
		this.useQuads = useQuads;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.mesh.MeshGenerator#createStemMesh(net.sourceforge.arbaro.tree.Tree, net.sourceforge.arbaro.export.Progress)
	 */
	public Mesh createStemMesh(Tree tree, Progress progress) {
		progress.beginPhase("Creating mesh",tree.getStemCount());
		outputVertexInfo(tree);
		
		Mesh mesh = new Mesh(tree.getLevels());
/*		for (int t=0; t<trunks.size(); t++) {
			((Stem)trunks.elementAt(t)).addToMesh(mesh,true,useQuads);
		}
		getProgress().incProgress(trunks.size());
		*/
		MeshCreator meshCreator = new MeshCreator(/*params,*/ mesh, -1, useQuads, progress);
		tree.traverseTree(meshCreator);
		
		progress.endPhase();
		return mesh;
	}

	private void outputVertexInfo(Tree tree) {
		Console.verboseOutput("Output: mesh");
		for (int l=0; l<Math.min(tree.getLevels(),4); l++) {
			Console.verboseOutput("  Level " + l + ": "
					+ tree.getVertexInfo(l));
		}
	}
	
	// FIXME move to MeshFactory
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.mesh.MeshGenerator#createStemMeshByLevel(net.sourceforge.arbaro.tree.Tree, net.sourceforge.arbaro.export.Progress)
	 */
	public Mesh createStemMeshByLevel(Tree tree, Progress progress) {
		progress.beginPhase("Creating mesh",tree.getStemCount());
		outputVertexInfo(tree);

		Mesh mesh = new Mesh(tree.getLevels());
		
		for (int level=0; level < tree.getLevels(); level++) {
			MeshCreator meshCreator = new MeshCreator(mesh, level, useQuads, progress);
			tree.traverseTree(meshCreator);
		}
			
		progress.endPhase();
		return mesh;
	}
	
	// FIXME move to MeshFactory
	public LeafMesh createLeafMesh(Tree tree, boolean useQuads) {
		return new LeafMesh(tree.getLeafShape(),
				tree.getLeafLength(),tree.getLeafWidth(),
				tree.getLeafStemLength(),useQuads);
	}
	
}
