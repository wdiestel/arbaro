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

package net.sourceforge.arbaro.tree.impl;

import java.io.PrintWriter;
import java.lang.Math;
import java.util.Enumeration;

import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.params.LevelParams;
import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.tree.LeafCounter;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.StemCounter;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeTraversal;
import net.sourceforge.arbaro.feedback.Console;
import net.sourceforge.arbaro.feedback.Progress;

/**
 * A class for creation of threedimensional tree objects.
 * A tree has one or more trunks, with several levels of
 * branches all of which are instances of the Stem class.
 * <p>
 * See this class diagram for the parts of a Tree:
 * <p>
 * <img src="doc-files/Tree-1.png" />
 * <p>
 * 
 * @author Wolfram Diestel
 *
 */
class TreeImpl implements Tree {
	public Params params;
	int seed = 13;
	public int getSeed() { return seed; }
	
	Progress progress;
	
	long stemCount;
	long leafCount;
	public long getStemCount() { return stemCount; }
	public long getLeafCount() { return leafCount; }
	
	public void setStemCount(long cnt) { stemCount=cnt; }
	public void setLeafCount(long cnt) { leafCount=cnt; }

	// the trunks (one for trees, many for bushes)
	java.util.Vector trunks;
	double trunk_rotangle = 0;
	
	//Progress progress;
	
	Vector maxPoint;
	Vector minPoint;
	public Vector getMaxPoint() { return maxPoint; }
	public Vector getMinPoint() { return minPoint; }
	public double getHeight() { return maxPoint.getZ(); }
	public double getWidth() {
		return Math.sqrt(Math.max(
				 minPoint.getX()*minPoint.getX()
				+minPoint.getY()*minPoint.getY(),
				 maxPoint.getX()*maxPoint.getX()
				+maxPoint.getY()*maxPoint.getY())); 
	}
	
	/**
	 * Creates a new tree object 
	 */
	public TreeImpl(int seed, Params params) {
		this.params = params;
		this.seed = seed;
		trunks = new java.util.Vector();
		//newProgress();
	}
	
	/**
	 * Creates a new tree object copying the parameters
	 * from an other tree
	 * 
	 * @param other the other tree, from wich parameters are taken
	 */
	public TreeImpl(TreeImpl other) {
		params = new Params(other.params);
		trunks = new java.util.Vector();
	}
	
	public void clear() {
		trunks = new java.util.Vector();
		//newProgress();
	}
	
	/**
	 * Generates the tree. The following collaboration diagram
	 * shows the recursion trough the make process:
	 * <p>
	 * <img src="doc-files/Tree-2.png" />
	 * <p> 
	 * 
	 * @throws Exception
	 */
	
	public void make(Progress progress) {
		this.progress = progress;
		
		setupGenProgress();
		params.prepare(seed);
		maxPoint = new Vector(-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE);
		minPoint = new Vector(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		
		Console.verboseOutput("Tree species: " + params.Species + ", Seed: " + seed);
		Console.verboseOutput("making " + params.Species + "(" + seed + ") ");
		
		// create the trunk and all its stems and leaves
		Transformation transf = new Transformation();
		Transformation trf;
		double angle;
		double dist;
		LevelParams lpar = params.getLevelParams(0);
		for (int i=0; i<lpar.nBranches; i++) {
			trf = trunkDirection(transf,lpar);
			angle = lpar.var(360);
			dist = lpar.var(lpar.nBranchDist);
			trf = trf.translate(new Vector(dist*Math.sin(angle),
					dist*Math.cos(angle),0));
			StemImpl trunk = new StemImpl(this,null,0,trf,0);
			trunks.addElement(trunk);
			trunk.index=0;
			trunk.make();
		}
		
		
		// set leafCount and stemCount for the tree
		if (params.Leaves==0) setLeafCount(0);
		else {
			LeafCounter leafCounter = new LeafCounter();
			traverseTree(leafCounter);
			setLeafCount(leafCounter.getLeafCount());
		}
		StemCounter stemCounter = new StemCounter();
		traverseTree(stemCounter);
		setStemCount(stemCounter.getStemCount());
		
		// making finished
		Console.progressChar();
		progress.endPhase();
	}
	

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TraversableTree#traverseTree(net.sourceforge.arbaro.tree.TreeTraversal)
	 */
	
	Transformation trunkDirection(Transformation trf, LevelParams lpar) {
		
		// get rotation angle
		double rotangle;
		if (lpar.nRotate>=0) { // rotating trunk
			trunk_rotangle = (trunk_rotangle + lpar.nRotate+lpar.var(lpar.nRotateV)+360) % 360;
			rotangle = trunk_rotangle;
		} else { // alternating trunks
			if (Math.abs(trunk_rotangle) != 1) trunk_rotangle = 1;
			trunk_rotangle = -trunk_rotangle;
			rotangle = trunk_rotangle * (180+lpar.nRotate+lpar.var(lpar.nRotateV));
		}
		
		// get downangle
		double downangle;
		downangle = lpar.nDownAngle+lpar.var(lpar.nDownAngleV);
		
		return trf.rotxz(downangle,rotangle);
	}
	
	
	public boolean traverseTree(TreeTraversal traversal) {
	    if (traversal.enterTree(this))  // enter this tree?
        {
             Enumeration stems = trunks.elements();
             while (stems.hasMoreElements())
                if (! ((Stem)stems.nextElement()).traverseTree(traversal))
                        break;
        }

        return traversal.leaveTree(this);
	}

	public void minMaxTest(Vector pt) {
		maxPoint.setMaxCoord(pt);
		minPoint.setMinCoord(pt);
	}
	
	/*
	 void Tree::dump() const {
	 cout << "TREE:\n";
	 // trunk.dump();
	  }
	  */
	
	
	/**
	 * Writes out the parameters to an XML definition file
	 * 
	 * @param out The output stream
	 */
	
	public void paramsToXML(PrintWriter out)  {
		params.toXML(out);
	}
	
	/**
	 * Returns the species name of the tree
	 * 
	 * @return the species name
	 */
	public String getSpecies() {
		return params.getSpecies();
	}
	
	public int getLevels() {
		return params.Levels;
	}
	
	public String getLeafShape() {
		return params.LeafShape;
	}
	
	public double getLeafWidth() {
		return params.LeafScale*params.LeafScaleX/Math.sqrt(params.LeafQuality);
	}
	
	public double getLeafLength() {
		return params.LeafScale/Math.sqrt(params.LeafQuality);
	}
	
	public double getLeafStemLength() {
		return params.LeafStemLen;
	}
	
	public String getVertexInfo(int level) {
		return "vertices/section: " 
			+ params.getLevelParams(level).mesh_points + ", smooth: " 
			+ (params.smooth_mesh_level>=level? "yes" : "no");
	}

	
	/**
	 * Sets the maximum for the progress while generating the tree 
	 */
	
	public void setupGenProgress() {
		if (progress != null) {
			// max progress = trunks * trunk segments * (first level branches + 1) 
			long maxGenProgress = 
				params.getParam("0Branches").getIntValue()
				* params.getParam("0CurveRes").getIntValue()
				* (params.getParam("1Branches").getIntValue()+1);
			
			progress.beginPhase("Creating tree structure",maxGenProgress);
		}
	}
	
	/**
	 * Sets (i.e. calcs) the progress for the process of making the tree
	 * object.
	 */
	long genProgress;
	
	  
	 public synchronized void updateGenProgress() {
		try {
			// how much of 0Branches*0CurveRes*(1Branches+1) are created yet
			long sum = 0;
			for (int i=0; i<trunks.size(); i++) {
				StemImpl trunk = ((StemImpl)trunks.elementAt(i));
				if (trunk.substems != null) {
					sum += trunk.segments.size() * (trunk.substems.size()+1);
				} else {
					sum += trunk.segments.size();
				}
			}
			
			if (sum-genProgress > progress.getMaxProgress()/100) {
				genProgress = sum;
				progress.setProgress(genProgress);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	
};























