package net.sourceforge.arbaro.tree;

import net.sourceforge.arbaro.transformation.Vector;
import java.io.PrintWriter;

/**
 * The tree interface to access a tree from outside of this package
 * 
 * @author wolfram
 *
 */

public interface Tree {

	/**
	 * used with the TreeTraversal interface
	 * 
	 * @param traversal
	 * @return when false stop travers the tree
	 */
	abstract boolean traverseTree(TreeTraversal traversal);

	/**
	 * 
	 * @return the number of all stems of all levels of the tree
	 */
	public abstract long getStemCount();

	/**
	 * 
	 * @return the number of leaves of the tree
	 */
	public abstract long getLeafCount();

	/**
	 * 
	 * @return a vector with the highest coordinates of the tree.
	 * (Considering all stems of all levels)
	 */
	public abstract Vector getMaxPoint();

	/**
	 * 
	 * @return a vector with the lowest coordinates of the tree.
	 * (Considering all stems of all levels)
	 */
	public abstract Vector getMinPoint();

	/**
	 * 
	 * @return the seed of the tree. It is used for randomnization.
	 */
	public int getSeed();

	/**
	 * 
	 * @return the height of the tree (highest z-coordinate)
	 */
	public double getHeight();

	/**
	 * 
	 * @return the widht of the tree (highest value of sqrt(x*x+y*y))
	 */
	public double getWidth();
	
	/**
	 * Writes the trees parameters to a stream
	 * @param w
	 */
	public void paramsToXML(PrintWriter w);
	
	/**
	 * 
	 * @return the tree species name
	 */
	public String getSpecies();
	
	/**
	 *  
	 * @return the tree stem levels
	 */
	public int getLevels();
	
	/**
	 * 
	 * @return the leaf shape name
	 */
	public String getLeafShape();
	
	/**
	 * 
	 * @return the leaf width
	 */
	public double getLeafWidth();
	
	/**
	 * 
	 * @return the leaf length
	 */
	public double getLeafLength();
	
	/**
	 * 
	 * @return the virtual leaf stem length, i.e. the distance of
	 * the leaf from the stem center line
	 */
	public double getLeafStemLength();

	/**
	 * Use this for verbose output when generating a mesh or
	 * exporting a tree 
	 * 
	 * @param level
	 * @return an information string with the number of section
	 * points for this stem level and if smoothing should be used
	 */
	public String getVertexInfo(int level);
	
}