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

import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.params.*;
import net.sourceforge.arbaro.export.Console;

/**
 * A segment class, multiple segments form a stem.
 * 
 * @author Wolfram Diestel
 */
class SegmentImpl implements StemSection {
	
	Params par;
	public LevelParams lpar;
	public int index;
	Transformation transf;
	double rad1;
	public double rad2;
	double length;

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TraversableSegment#getLength()
	 */
	public double getLength() { return length; }
	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TraversableSegment#getTransformation()
	 */
	public Transformation getTransformation() { return transf; }

	StemImpl stem;
	
	// FIXME: use Enumeration instead of making this public
	java.util.Vector subsegments;
	
	public SegmentImpl(/*Params params, LevelParams lparams,*/ 
			StemImpl stm, int inx, Transformation trf, 
			double r1, double r2) {
		index = inx;
		transf = trf; 
		rad1 = r1;
		rad2 = r2;
		stem = stm;

		par = stem.par;
		lpar = stem.lpar;
		length = stem.segmentLength;
		
		// FIXME: rad1 and rad2 could be calculated only when output occurs (?)
		// or here in the constructor ?
		// FIXME: inialize subsegs with a better estimation of size
		subsegments = new java.util.Vector(10);
	}
	
	public void addSubsegment(SubsegmentImpl ss) {
		if (subsegments.size() > 0) {
			SubsegmentImpl p = ((SubsegmentImpl)subsegments.elementAt(subsegments.size()-1)); 
			p.next = ss;
			ss.prev = p; 
		}
		subsegments.add(ss);
	}
	

	void minMaxTest() {
		stem.minMaxTest(getUpperPosition());
		stem.minMaxTest(getLowerPosition());
	}
	
	/**
	 * Makes the segments from subsegments 
	 */
	
	public void make() {
		// FIXME: numbers for cnt should correspond to Smooth value
		// helical stem
		if (lpar.nCurveV<0) { 
			makeHelix(10);
		}
		
		// spherical end
		else if (lpar.nTaper > 1 && lpar.nTaper <=2 && isLastStemSegment()) {
			makeSphericalEnd(10);
		}
		
		// periodic tapering
		else if (lpar.nTaper>2) {
			makeSubsegments(20);
		}
		
		// trunk flare
		// FIXME: if nCurveRes[0] > 10 this division into several
		// subsegs should be extended over more then one segments?
		else if (lpar.level==0 && par.Flare!=0 && index==0) {
			
			if (Console.debug())
				stem.DBG("Segment.make() - flare");
			
			makeFlare(10);
			
		} else {
			makeSubsegments(1);
		}
		
		// FIXME: for helical stems maybe this test
		// should be made for all subsegments
		minMaxTest();
	}
	
	/**
	 * Creates susbsegments for the segment
	 * 
	 * @param cnt the number of subsegments
	 */
	
	private void makeSubsegments(int cnt) {
		Vector dir = getUpperPosition().sub(getLowerPosition());
		for (int i=1; i<cnt+1; i++) {
			double pos = i*length/cnt;
			// System.err.println("SUBSEG:stem_radius");
			double rad = stem.stemRadius(index*length + pos);
			// System.err.println("SUBSEG: pos: "+ pos+" rad: "+rad+" inx: "+index+" len: "+length);
			
			addSubsegment(new SubsegmentImpl(getLowerPosition().add(dir.mul(pos/length)),rad, pos, this));
		}
	}
	
	/**
	 * Make a subsegments for a segment with spherical end
	 * (last stem segment), subsegment lengths decrements near
	 * the end to get a smooth surface
	 * 
	 * @param cnt the number of subsegments
	 */
	
	private void makeSphericalEnd(int cnt) {
		Vector dir = getUpperPosition().sub(getLowerPosition());
		for (int i=1; i<cnt; i++) {
			double pos = length-length/Math.pow(2,i);
			double rad = stem.stemRadius(index*length + pos);
			//stem.DBG("FLARE: pos: %f, rad: %f\n"%(pos,rad))
			addSubsegment(new SubsegmentImpl(getLowerPosition().add(dir.mul(pos/length)),rad, pos, this));
		}
		addSubsegment(new SubsegmentImpl(getUpperPosition(),rad2,length, this));
	}
	
	/**
	 * Make subsegments for a segment with flare
	 * (first trunk segment). Subsegment lengths are decrementing
	 * near the base of teh segment to get a smooth surface
	 * 
	 * @param cnt the number of subsegments
	 */
	
	private void makeFlare(int cnt) {
		Vector dir = getUpperPosition().sub(getLowerPosition());
		//addSubsegment(new SubsegmentImpl(getLowerPosition(),rad1,0,this));
		for (int i=cnt-1; i>=0; i--) {
			double pos = length/Math.pow(2,i);
			double rad = stem.stemRadius(index*length+pos);
			//self.stem.DBG("FLARE: pos: %f, rad: %f\n"%(pos,rad))
			addSubsegment(new SubsegmentImpl(getLowerPosition().add(dir.mul(pos/length)),rad, pos,this));
		}
	}
	
	/**
	 * Make subsegments for a segment with helical curving.
	 * They curve around with 360° from base to top of the
	 * segment
	 * 
	 * @param cnt the number of subsegments, should be higher
	 *        when a smooth curve is needed.
	 */
	
	private void makeHelix(int cnt) {
		double angle = Math.abs(lpar.nCurveV)/180*Math.PI;
		// this is the radius of the helix
		double rad = Math.sqrt(1.0/(Math.cos(angle)*Math.cos(angle)) - 1)*length/Math.PI/2.0;
		
		if (Console.debug())
			stem.DBG("Segment.make_helix angle: "+angle+" len: "+length+" rad: "+rad);
		
		//self.stem.DBG("HELIX: rad: %f, len: %f\n" % (rad,len))
		for (int i=1; i<cnt+1; i++) {
			Vector pos = new Vector(rad*Math.cos(2*Math.PI*i/cnt)-rad,
					rad*Math.sin(2*Math.PI*i/cnt),
					i*length/cnt);
			//self.stem.DBG("HELIX: pos: %s\n" % (str(pos)))
			// this is the stem radius
			double srad = stem.stemRadius(index*length + i*length/cnt);
			addSubsegment(new SubsegmentImpl(transf.apply(pos), srad, i*length/cnt,this));
		}
	}
	
	/**
	 * Calcs the position of a substem in the segment given 
	 * a relativ position where in 0..1 - needed esp. for helical stems,
	 * because the substems doesn't grow from the axis of the segement
	 *
	 * @param trf the transformation of the substem
	 * @param where the offset, where the substem spreads out
	 * @return the new transformation of the substem (shifted from
	 *        the axis of the segment to the axis of the subsegment)
	 */
	
	public Transformation substemPosition(Transformation trf,double where) {
		if (lpar.nCurveV>=0) { // normal segment 
			return trf.translate(transf.getZ().mul(where*length));
		} else { // helix
			// get index of the subsegment
			int i = (int)(where*(subsegments.size()-1));
			// interpolate position
			Vector p1 = ((SubsegmentImpl)subsegments.elementAt(i)).pos;
			Vector p2 = ((SubsegmentImpl)subsegments.elementAt(i+1)).pos;
			Vector pos = p1.add(p2.sub(p1).mul(where - i/(subsegments.size()-1)));
			return trf.translate(pos.sub(getLowerPosition()));
		}
	}
	
	/**
	 * Position at the beginning of the segment
	 * 
	 * @return beginning point of the segment
	 */
	public Vector getLowerPosition() {
		// self.stem.DBG("segmenttr0: %s, t: %s\n"%(self.transf_pred,self.transf_pred.t()))
		return transf.getT();
	}
	
	public Vector getPosition() {
		// self.stem.DBG("segmenttr0: %s, t: %s\n"%(self.transf_pred,self.transf_pred.t()))
		return transf.getT();
	}
	
	/**
	 * Position of the end of the segment
	 * 
	 * @return end point of the segment
	 */
	public Vector getUpperPosition() {
		//self.stem.DBG("segmenttr1: %s, t: %s\n"%(self.transf,self.transf.t()))
		return transf.getT().add(transf.getZ().mul(length));
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getSubsegmentCount() {
		return subsegments.size();
	}
	
	public double getLowerRadius() {
		return rad1;
	}
	
	public double getRadius() {
		return rad1;
	}
	
	public double getDistance() {
		return index*length;
	}
	
	public Vector getZ() {
		return transf.getZ();
	}


	public double getUpperRadius() {
		return rad2;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.TraversableSegment#isLastStemSegment()
	 */
	public boolean isLastStemSegment() {
		// return index == lpar.nCurveRes-1;
		
		// use segmentCount, not segments.size, because clones
		// has less segments, but index starts from where the
		// clone grows out and ends with segmentCount
		return index == stem.segmentCount-1;
	}
	
	
	public Vector[] getSectionPoints() {
		int pt_cnt = lpar.mesh_points;
		Vector[] points;
		Transformation trf = getTransformation(); //segment.getTransformation().translate(pos.sub(segment.getLowerPosition()));
		double rad = this.rad1;
		
		// if radius = 0 create only one point
		if (rad<0.000001) {
			points = new Vector[1];
			points[0] = trf.apply(new Vector(0,0,0));
		} else { //create pt_cnt points
			points = new Vector[pt_cnt];
			//stem.DBG("MESH+LOBES: lobes: %d, depth: %f\n"%(self.tree.Lobes, self.tree.LobeDepth))
			
			for (int i=0; i<pt_cnt; i++) {
				double angle = i*360.0/pt_cnt;
				// for Lobes ensure that points are near lobes extrema, but not exactly there
				// otherwise there are to sharp corners at the extrema
				if (lpar.level==0 && par.Lobes != 0) {
					angle -= 10.0/par.Lobes;
				}
				
				// create some point on the unit circle
				Vector pt = new Vector(Math.cos(angle*Math.PI/180),Math.sin(angle*Math.PI/180),0);

				// scale it to stem radius
				if (lpar.level==0 && (par.Lobes != 0 || par._0ScaleV !=0)) {
					double rad1 = rad * (1 + 
							par.random.uniform(-par._0ScaleV,par._0ScaleV)/
							getSubsegmentCount());
					pt = pt.mul(rad1*(1.0+par.LobeDepth*Math.cos(par.Lobes*angle*Math.PI/180.0))); 
				} else {
					pt = pt.mul(rad); // faster - no radius calculations
				}
				// apply transformation to it
				// (for the first trunk segment transformation shouldn't be applied to
				// the lower meshpoints, otherwise there would be a gap between 
				// ground and trunk)
				// FIXME: for helical stems may be/may be not a random rotation 
				// should applied additionally?
				
				pt = trf.apply(pt);
				points[i] = pt;
			}
		}
		
		return points;
	}
};
























