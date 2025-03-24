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

import net.sourceforge.arbaro.export.Console;
import net.sourceforge.arbaro.export.Progress;
import net.sourceforge.arbaro.tree.Tree;

/**
 * @author wolfram
 *
 */
public class ShieldedMeshGenerator implements MeshGenerator {
	private MeshGenerator meshGenerator;
	
	public boolean getUseQuads() {
		return meshGenerator.getUseQuads();
	}
	
	/**
	 * 
	 */
	public ShieldedMeshGenerator(MeshGenerator meshGenerator) {
		this.meshGenerator = meshGenerator;
	}

	protected void showException(Exception e) {
		Console.errorOutput("Error in mesh generator:");
		Console.printException(e);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.mesh.MeshGenerator#createLeafMesh(net.sourceforge.arbaro.tree.Tree, boolean)
	 */
	public LeafMesh createLeafMesh(Tree tree, boolean useQuads) {
		try {
			return meshGenerator.createLeafMesh(tree, useQuads);
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.mesh.MeshGenerator#createStemMesh(net.sourceforge.arbaro.tree.Tree, net.sourceforge.arbaro.export.Progress)
	 */
	public Mesh createStemMesh(Tree tree, Progress progress) {
		try {
			return meshGenerator.createStemMesh(tree, progress);
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.mesh.MeshGenerator#createStemMeshByLevel(net.sourceforge.arbaro.tree.Tree, net.sourceforge.arbaro.export.Progress)
	 */
	public Mesh createStemMeshByLevel(Tree tree, Progress progress) {
		try {
			return meshGenerator.createStemMeshByLevel(tree, progress);
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

}
