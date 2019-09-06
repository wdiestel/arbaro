// #**************************************************************************
// #
// #    Copyright (C) 2008  Moritz Moeller
// #
// #    This program is free software; you can redistribute it and/or modify
// #    it under the terms of the GNU General Public License as published by
// #    the Free Software Foundation; either version 2 of the License, or
// #    (at your option) any later version.
// #
// #    This program is distributed in the hope that it will be useful,
// #    but WITHOUVector ANY WARRANTY; without even the implied warranty of
// #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// #    GNU General Public License for more details.
// #
// #    You should have received a copy of the GNU General Public License
// #    along with this program; if not, write to the Free Software
// #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// #
// #    Send comments and bug fixes to arbaro@virtualritz.com or
// #    diestel@steloj.de
// #
// #**************************************************************************/

package net.sourceforge.arbaro.transformation;

import java.lang.String;
import java.lang.Math;

import java.text.NumberFormat;
import net.sourceforge.arbaro.params.FloatFormat;
import net.sourceforge.arbaro.transformation.Vector;

/**
 * A bounding box class
 *
 * Based on ILM's Imath::Box C++ metaclass
 *
 * @author Moritz Moeller
 */
public final class Box {
	final static int X=0;
	final static int Y=1;
	final static int Z=2;

	private Vector min;
	private Vector max;

	public Box() {
		makeEmpty();
	}

	public Box(Vector point) {
		min = new Vector(point);
		max = new Vector(point);
	}

	public Box(Vector minV, Vector maxV) {
		min = new Vector(minV);
		max = new Vector(maxV);
	}

	public boolean equals(Box src) {
		return min.equals(src.min) && max.equals(src.max);
	}

	public void makeEmpty() {
	    min = new Vector(Double.MAX_VALUE);
		max = new Vector(Double.MIN_VALUE);
	}

	public void extendBy(Vector point) {
		for (int i=0; i<3; i++) {
			if ( point.get(i) < min.get(i) ) {
				min.set(i,point.get(i));
			}
			if ( point.get(i) > max.get(i) ) {
				max.set(i,point.get(i));
			}
		}
	}

	public void extendBy(Box box) {
		for (int i=0; i<3; i++) {
			if ( box.getMin().get(i) < min.get(i) ) {
				min.set(i, box.getMin().get(i));
			}
			if ( box.getMax().get(i) > max.get(i) ) {
				max.set(i, box.getMax().get(i));
			}
		}
	}

	public void extendBy(Vector point, double radius) {
		for (int i=0; i<3; i++) {
			if ( point.get(i)-radius < min.get(i) ) {
				min.set(i,point.get(i)-radius);
			}
			if ( point.get(i)+radius > max.get(i) ) {
				max.set(i,point.get(i)+radius);
			}
		}
	}

	public boolean intersects(Vector point) {
		for (int i=0; i<3; i++) {
			if (point.get(i) < min.get(i) || point.get(i) > max.get(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean intersects(Box box) {
		for (int i=0; i<3; i++) {
			if (box.getMax().get(i) < min.get(i) || box.getMin().get(i) > max.get(i)) {
				return false;
			}
		}
		return true;
	}

	public Vector size() {
		if (isEmpty()) {
			return new Vector();
		}
		return new Vector(max).sub(min);
	}

	public Vector center() {
		return new Vector(max).add(min).div(2);
	}

	boolean isEmpty() {
		for (int i=0; i<3; i++) {
			if (max.get(i) < min.get(i)) return true;
		}
		return false;
	}

	public boolean hasVolume() {
		for (int i=0; i<3; i++) {
			if (max.get(i) <= min.get(i)) return false;
		}
		return true;
	}

	public int majorAxis() {
		int major = 0;
		Vector s = size();

		for (int i=1; i<3; i++) {
			if ( s.get(i) > s.get(major) ) major = i;
		}

		return major;
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}
};








