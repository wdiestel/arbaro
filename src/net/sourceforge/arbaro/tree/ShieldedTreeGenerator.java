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

package net.sourceforge.arbaro.tree;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.TreeMap;

import net.sourceforge.arbaro.export.Progress;
import net.sourceforge.arbaro.params.AbstractParam;
import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.export.Console;

/**
 * A TreeGenerator facade handling exceptions in tree generation.
 * TreeGenerator method calls are delegated to a TreeGenerator object
 * given in the constructor. Exceptions are printed to the console.
 * 
 * @author wolfram
 *
 */
public class ShieldedTreeGenerator implements TreeGenerator {
	TreeGenerator treeGenerator;
	
	/**
	 * @param treeGenerator a TreeGenerator object without exception handling
	 */
	public ShieldedTreeGenerator(TreeGenerator treeGenerator) {
		this.treeGenerator = treeGenerator;
	}
	
	/**
	 * Print exceptions to the console using the Console class
	 * 
	 * @param e the Exception to print
	 */
	protected void showException(Exception e) {
		Console.errorOutput("Error in tree generator:");
		Console.printException(e);
	}

	/**
	 * See TreeGenerator interface
	 */
	public void clearParams() {
		try {
			treeGenerator.clearParams();
		} catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public AbstractParam getParam(String param) {
		try {
			return treeGenerator.getParam(param);
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public TreeMap getParamGroup(int level, String group) {
		try {
			return treeGenerator.getParamGroup(level,group);
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public Params getParams() {
		try {
			return treeGenerator.getParams();
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public int getSeed() {
		try {
			return treeGenerator.getSeed();
		} catch (Exception e) {
			showException(e);
			return 13;
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public Tree makeTree(Progress progress) {
		try {
			return treeGenerator.makeTree(progress);
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public void readParamsFromCfg(InputStream is) {
		try {
			treeGenerator.readParamsFromCfg(is);
		} catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public void readParamsFromXML(InputStream is) {
		try {
			treeGenerator.readParamsFromXML(is);
		} catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public void setParam(String param, String value) {
		try {
			treeGenerator.setParam(param,value);
		} catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public void setSeed(int seed) {
		try {
			treeGenerator.setSeed(seed);
		} catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * See TreeGenerator interface
	 */
	public void writeParamsToXML(PrintWriter out) {
		try {
			treeGenerator.writeParamsToXML(out);
		} catch (Exception e) {
			showException(e);
		}
	}
}
