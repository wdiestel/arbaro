package net.sourceforge.arbaro.tree;

import net.sourceforge.arbaro.transformation.Transformation;

/**
 * The leaf interface for accessing a tree's leaf from outside
 * of the tree generator, e.g. for mesh generation and exporting 
 * @author wolfram
 *
 */

public interface Leaf {

	/**
	 * used with TreeTraversal interface
	 *  
	 * @param traversal
	 * @return when false stop travers tree at this level
	 */
	abstract boolean traverseTree(TreeTraversal traversal);

	/**
	 * @return the leaf's transformation matrix containing
	 * the position vector and the rotation matrix.
	 */
	public abstract Transformation getTransformation();

}