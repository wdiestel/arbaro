package net.sourceforge.arbaro.tree;

import net.sourceforge.arbaro.transformation.Transformation;
import net.sourceforge.arbaro.transformation.Vector;

/**
 * The stem interface used from outside the tree generator, e.g.
 * for mesh creation and exporting
 * 
 * @author wolfram
 *
 */
public interface Stem {

	/**
	 * 
	 * @return an enumeration of the stems sections
	 */
	public abstract java.util.Enumeration sections();
	
	/**
	 * 
	 * @return an section offset for clones, because uv-Coordinates should
	 * be at same coordinates for stems and theire clones
	 */
	public int getCloneSectionOffset();
	
	/**
	 * a vector with the smalles coordinates of the stem
	 */
	public abstract Vector getMinPoint();

	/**
	 * a vector with the heighest coordinates of the stem
	 */
	public abstract Vector getMaxPoint();

	/**
	 * The position of the stem in the tree. 0.1c2.3 means:
	 * fourth twig of the third clone of the second branch growing
	 * out of the first (only?) trunk 
	 * 
	 * @return The stem position in the tree as a string
	 */
	public abstract String getTreePosition();

	/**
	 * 
	 * @return the stem length
	 */
	public abstract double getLength();
	
	/**
	 * @return the radius at the stem base
	 */
	public abstract double getBaseRadius();

	/**
	 * @return the radius at the stem peak
	 */
	public abstract double getPeakRadius();
	
	/**
	 * @return the stem level, 0 means it is a trunk
	 */
	public abstract int getLevel();

	/**
	 * used with TreeTraversal interface
	 * 
	 * @param traversal
	 * @return when false stop traverse tree at this level
	 */
	abstract boolean traverseTree(TreeTraversal traversal);

	/**
	 * 
	 * @return the number leaves of the stem 
	 */
	public abstract long getLeafCount();

	/**
	 * 
	 * @return true, if this stem is a clone of another stem
	 */
	public abstract boolean isClone();
	
	/**
	 * 
	 * @return this stem should be smoothed, so output normals
	 * for Povray meshes
	 */
	public abstract boolean isSmooth();

	/**
	 * 
	 * @return the transformation of the stem, containing the position
	 * vector and the rotation matrix of the stem base 
	 */
	public Transformation getTransformation();

}