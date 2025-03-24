// #**************************************************************************
// #
// #    Copyright (C) 2003-2006  Wolfram Diestel
// #
// #    This program is free software; you can redistribute it and/or modify
// #    it under the terms of the GNU General Public License as published by
// #    the Free Software Foundation; either version 2 of the License, or
// #    (at your option) any later version.
// #
// #    This program is distributed in the hope that it will be useful,
// #    but WITHOUT ANY WARRANTY; without even the implied warranty of
// #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// #    GNU General Public License for more details.
// #
// #    You should have received a copy of the GNU General Public License
// #    along with this program; if not, write to the Free Software
// #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// #
// #    Send comments and bug fixes to diestel@steloj.de
// #
// #**************************************************************************/


package net.sourceforge.arbaro.transformation;

import java.lang.String;
import java.lang.Math;

//import java.text.NumberFormat;
//import net.sourceforge.arbaro.params.FloatFormat;



/**
 * A transformation class - a matrix for rotations and a vector for translations
 * 
 * @author Wolfram Diestel
 */
public final class Transformation {
	public static final int X=0;
	public static final int Y=1;
	public static final int Z=2;
	public static final int T=3;
	
	Matrix matrix;
	Vector vector;
	
	public Transformation() {
		matrix = new Matrix();
		vector = new Vector();
	}
	
	public Transformation(Matrix m, Vector v) {
		matrix = m;
		vector = v;
	}
	
	public Matrix matrix() {
		return matrix;
	}
	
	public Vector vector() {
		return vector;
	}
	
	/**
	 * @param T1 the transformation to multiply with
	 * @return the product of two transformations, .i.e. the tranformation
	 * resulting of the two transformations applied one after the other
	 */
	public Transformation prod(Transformation T1) {
		return new Transformation(matrix.prod(T1.matrix()),
				matrix.prod(T1.vector()).add(vector));
	}
	
	/**
	 * Applies the transformation to a vector
	 * 
	 * @param v
	 * @return resulting vector
	 */
	public Vector apply(Vector v) {
		return matrix.prod(v).add(vector);
	}
	
	/**
	 * Returns the X-column of the rotation matrix. This
	 * is the projection on to the x-axis
	 * 
	 * @return X-column of the rotation matrix
	 */
	public Vector getX() {
		return matrix.col(X);
	}
	
	/**
	 * Returns the Y-column of the rotation matrix. This
	 * is the projection on to the y-axis
	 * 
	 * @return Y-column of the rotation matrix
	 */
	public Vector getY() {
		return matrix.col(Y);
	}
	
	/**
	 * Returns the Z-column of the rotation matrix. This
	 * is the projection on to the z-axis
	 * 
	 * @return Z-column of the rotation matrix
	 */
	public Vector getZ() {
		return matrix.col(Z);
	}
	
	/**
	 * Returns the translation vector of the transformation.
	 * (for convenience, same as vector()) 
	 * 
	 * @return translation vector of the transformation
	 */
	public Vector getT() { 
		return vector;
	}
	
	/*	
	 ostream& operator << (ostream &os, const Transformation &trf) {
	 os << "(X:" << trf[X] << "; Y:" << trf[Y] << "; Z:" << trf[Z] 
	 << "; T:" << trf[T] << ")";
	 return os;
	 }
	 */
	
	
	
	
	
	
	public String toString() {
		return "x: "+getX()+", y: "+getY()+", z: "+getZ()+", t: "+getT();
	}
	
	
	//    public String povray() {
	//	NumberFormat fmt = FloatFormat.getInstance();
	//	return "matrix <" + fmt.format(matrix.get(X,X)) + "," 
	//	    + fmt.format(matrix.get(X,Z)) + "," 
	//	    + fmt.format(matrix.get(X,Y)) + ","
	//	    + fmt.format(matrix.get(Z,X)) + "," 
	//	    + fmt.format(matrix.get(Z,Z)) + "," 
	//	    + fmt.format(matrix.get(Z,Y)) + ","
	//	    + fmt.format(matrix.get(Y,X)) + "," 
	//	    + fmt.format(matrix.get(Y,Z)) + "," 
	//	    + fmt.format(matrix.get(Y,Y)) + ","
	//	    + fmt.format(vector.getX())   + "," 
	//	    + fmt.format(vector.getZ())   + "," 
	//	    + fmt.format(vector.getY()) + ">";
	//    }
	
	public Transformation rotz(double angle) {
		// local rotation about z-axis
		double radAngle = angle*Math.PI/180;
		Matrix rm = new Matrix(Math.cos(radAngle),-Math.sin(radAngle),0,
				Math.sin(radAngle),Math.cos(radAngle),0,
				0,0,1);
		return new Transformation(matrix.prod(rm),vector);
	}
	
	public Transformation roty(double angle) {
		// local rotation about z-axis
		double radAngle = angle*Math.PI/180;
		Matrix rm = new Matrix(Math.cos(radAngle),0,-Math.sin(radAngle),
				0,1,0,
				Math.sin(radAngle),0,Math.cos(radAngle));
		return new Transformation(matrix.prod(rm),vector);
	}
	
	public Transformation rotx(double angle) {
		// local rotation about the x axis
		double radAngle = angle*Math.PI/180;
		Matrix rm = new Matrix(1,0,0,
				0,Math.cos(radAngle),-Math.sin(radAngle),
				0,Math.sin(radAngle),Math.cos(radAngle));
		return new Transformation(matrix.prod(rm),vector);
	}
	
	public Transformation rotxz(double delta, double rho) {
		// local rotation about the x and z axees - for the substems
		double radDelta = delta*Math.PI/180;
		double radRho = rho*Math.PI/180;
		double sir = Math.sin(radRho);
		double cor = Math.cos(radRho);
		double sid = Math.sin(radDelta);
		double cod = Math.cos(radDelta);
		
		Matrix rm = new Matrix(cor,-sir*cod,sir*sid,
				sir,cor*cod,-cor*sid,
				0,sid,cod);
		return new Transformation(matrix.prod(rm),vector);
	}
	
	public Transformation rotaxisz(double delta, double rho) {
		// local rotation away from the local z-axis 
		// about an angle delta using an axis given by rho 
		// - used for splitting and random rotations
		double radDelta = delta*Math.PI/180;
		double radRho = rho*Math.PI/180;
		
		double a = Math.cos(radRho);
		double b = Math.sin(radRho);
		double si = Math.sin(radDelta);
		double co = Math.cos(radDelta);
		
		Matrix rm = new Matrix((co+a*a*(1-co)),(b*a*(1-co)),(b*si),
				(a*b*(1-co)),(co+b*b*(1-co)),(-a*si),
				(-b*si),(a*si),(co));
		return new Transformation(matrix.prod(rm),vector);
	}
	
	public Transformation translate(Vector v) {
		return new Transformation(matrix,vector.add(v));
	}
	
	public Transformation rotaxis(double angle, Vector axis) {
		// rotation about an axis
		double radAngle = angle*Math.PI/180;
		Vector normAxis=axis.normalize();
		double a = normAxis.getX();
		double b = normAxis.getY();
		double c = normAxis.getZ();
		double si = Math.sin(radAngle);
		double co = Math.cos(radAngle);
		
		Matrix rm = new Matrix(
				(co+a*a*(1-co)),(-c*si+b*a*(1-co)),(b*si+c*a*(1-co)),
				(c*si+a*b*(1-co)),(co+b*b*(1-co)),(-a*si+c*b*(1-co)),
				(-b*si+a*c*(1-co)),(a*si+b*c*(1-co)),(co+c*c*(1-co)));
		return new Transformation(rm.prod(matrix),vector);
	}
	
	public Transformation inverse() {
		// get inverse transformation M+t -> M'-M'*t"
		Matrix T1 = matrix.transpose();
		return new Transformation(T1,T1.prod(vector.mul(-1)));
	}
};




































