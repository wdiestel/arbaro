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

package net.sourceforge.arbaro.gui;

import net.sourceforge.arbaro.params.IntParam;
import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.transformation.Vector;
import net.sourceforge.arbaro.tree.TreeGenerator;
import net.sourceforge.arbaro.tree.TreeGeneratorFactory;
import net.sourceforge.arbaro.tree.TreeTraversal;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.mesh.Mesh;
import net.sourceforge.arbaro.mesh.LeafMesh;
import net.sourceforge.arbaro.mesh.MeshGenerator;
import net.sourceforge.arbaro.mesh.MeshGeneratorFactory;
import net.sourceforge.arbaro.export.Progress;

import java.io.PrintWriter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


/**
 * A tree used to preview the edited tree, it draws
 * the stems and leaves with lines to Graphics context
 * and modifies the level and branching parameters to
 * calculate and draw only parts of the tree, reducing
 * calculation time as well.
 *
 * @author wdiestel
 *
 */
public final class PreviewTree implements Tree {
	// preview always shows this levels and
	// the previous levels stems
	int showLevel=1;
	Params originalParams;
	//Params params;
	Mesh mesh;
	LeafMesh leafMesh;
	MeshGenerator meshGenerator;
//	TreeGenerator treeGenerator;

	Tree tree;

	protected ChangeEvent changeEvent = null;
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * @param params tree parameters
	 */
	public PreviewTree(Params params/*, MeshGenerator meshGenerator,
			TreeGenerator treeGenerator*/) {
		this.originalParams=params;
//		this.meshGenerator = meshGenerator;
//		this.treeGenerator = treeGenerator;
		//this.params = new Params(params);
	}

	public Params getParams() { return originalParams; }

	public void setParams(Params params) { this.originalParams = params; }

	// delegate interface methods to the tree
	public boolean traverseTree(TreeTraversal traversal)
	{
		return tree.traverseTree(traversal);
	}

	public long getStemCount() { return tree.getStemCount(); }

	public long getLeafCount() { return tree.getLeafCount(); }

	public Vector getMaxPoint() { return tree.getMaxPoint(); }

	public Vector getMinPoint() { return tree.getMinPoint(); }

	public int getSeed() { return tree.getSeed(); }

	public double getHeight() { return tree.getHeight(); }

	public double getWidth() { return tree.getWidth(); }

	public void paramsToXML(PrintWriter w) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public String getSpecies() { return tree.getSpecies(); }

	public double getScale() { return tree.getScale(); }

	public int getLevels() { return tree.getLevels(); }

	public String getLeafShape() { return tree.getLeafShape(); }

	public double getLeafWidth() { return tree.getLeafWidth(); }

	public double getLeafLength() { return tree.getLeafLength(); }

	public double getLeafStemLength() { return tree.getLeafStemLength(); };

	public String getVertexInfo(int level) { return tree.getVertexInfo(level); };

	public void setShowLevel(int l) {
		int Levels = ((IntParam)(originalParams.getParam("Levels"))).intValue();
		if (l>Levels) showLevel=Levels;
		else showLevel=l;
	}

	public int getShowLevel() {
		return showLevel;
	}

	public void remake(boolean doFireStateChanged) {
			//clear();
			Params params = new Params(originalParams);
			params.preview=true;
//			previewTree = new Tree(originalTree);

			// manipulate params to avoid making the whole tree
			// FIXME: previewTree.Levels <= tree.Levels
			int Levels = ((IntParam)(originalParams.getParam("Levels"))).intValue();
			if (Levels>showLevel+1) {
				params.setParam("Levels",""+(showLevel+1));
				params.setParam("Leaves","0");
			}
			for (int i=0; i<showLevel; i++) {
				params.setParam(""+i+"Branches","1");
				// if (((FloatParam)previewTree.getParam(""+i+"DownAngleV")).doubleValue()>0)
				params.setParam(""+i+"DownAngleV","0");
			}

			Progress progress = new Progress();
			TreeGenerator treeGenerator = TreeGeneratorFactory.createShieldedTreeGenerator(params);
		    tree = treeGenerator.makeTree(progress);

		    MeshGenerator meshGenerator = MeshGeneratorFactory.createShieldedMeshGenerator(true); // useQuads
			mesh = meshGenerator.createStemMesh(tree,progress);
			leafMesh = meshGenerator.createLeafMesh(tree,true);

			if (doFireStateChanged)	fireStateChanged();
	}

	public Mesh getMesh() {
		return mesh;
	}

	public LeafMesh getLeafMesh() {
		return leafMesh;
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		Object [] listeners = listenerList.getListenerList();
		for (int i = listeners.length -2; i>=0; i-=2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
			}
		}
	}


}
