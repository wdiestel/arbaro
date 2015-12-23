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

//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.util.Map;

import net.sourceforge.arbaro.feedback.Progress;
import net.sourceforge.arbaro.params.ParamManager;
import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeGenerator;

/**
 * @author wolfram
 *
 */
public class TreeGeneratorImpl implements TreeGenerator {
	ParamManager paramMgr;

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#makeTree(net.sourceforge.arbaro.export.Progress)
	 */
	public Tree makeTree(Progress progress) {
		Params params = paramMgr.getTreeParams();
		params.prepare(seed);
		TreeImpl tree = new TreeImpl(seed,params);
		tree.make(progress);
		
		return tree;
	}
	
	private int seed = 13;

//	public TreeGeneratorImpl() {
//		this.paramMgr = new ParamManager();
//	}
//	
	public TreeGeneratorImpl(ParamManager paramMgr) {
		this.paramMgr = paramMgr;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#setSeed(int)
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#getSeed()
	 */
	public int getSeed() {
		return seed;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#getParams()
	 */
	public ParamManager getParamManager() {
		return paramMgr;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#setParam(java.lang.String, java.lang.String)
	 */
//	public void setParam(String param, String value) {
//		params.setParam(param,value);
//	}
	
	// TODO: not used at the moment, may be the GUI
	// should get a TreeGenerator as a ParamContainer
	// and tree maker, and not work directly with Params
	// class
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#getParam(java.lang.String)
	 */
//	public AbstractParam getParam(String param) {
//		return params.getParam(param);
//	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#getParamGroup(int, java.lang.String)
	 */
	// TODO: not used at the moment, may be the GUI
	// should get a TreeGenerator as a ParamContainer
	// and tree maker, and not work directly with Params
	// class
//	public Map getParamGroup(int level, String group) {
//		return params.getParamGroup(level,group);
//	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#writeParamsToXML(java.io.PrintWriter)
	 */
	// TODO: not used at the moment, may be the GUI
	// should get a TreeGenerator as a ParamContainer
	// and tree maker, and not work directly with Params
	// class
//	public void writeParamsToXML(PrintWriter out) {
//		params.toXML(out);
//	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#clearParams()
	 */
//	public void clearParams() {
//		params.clearParams();
//	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#readParamsFromXML(java.io.InputStream)
	 */
//	public void readParamsFromXML(InputStream is) {
//		params.readFromXML(is);
//	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TreeGenerator#readParamsFromCfg(java.io.InputStream)
	 */
//	public void readParamsFromCfg(InputStream is) {
//		params.readFromCfg(is);
//	}

}
