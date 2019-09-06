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

package net.sourceforge.arbaro.mesh;

import java.util.Enumeration;

import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.tree.*;
import net.sourceforge.arbaro.export.Progress;


/**
 * Creates a MeshPart for a Stem
 * 
 * @author wolfram
 *
 */
class MeshPartCreator {
	MeshPart meshPart;
	boolean useQuads;
	Stem stem;
	
	/**
	 * 
	 */
	public MeshPartCreator(Stem stem, /*Params params,*/ boolean useQuads) {
		super();
		this.useQuads = useQuads;
//		this.par = params;
//		this.lpar = par.getLevelParams(stem.getLevel());
		this.stem = stem;
	}

	
	public MeshPart createMeshPart(Progress progress) {
		meshPart = new MeshPart(stem, stem.isSmooth(), useQuads);
//try {
		double vLength = stem.getLength()+stem.getBaseRadius()+stem.getPeakRadius();
		//double vBase = + stem.stemRadius(0);
		
		// first section
		Enumeration sections = stem.sections();
		StemSection section = (StemSection)sections.nextElement();
		
		// first section - create lower meshpoints
		// one point at the stem origin, with normal in reverse z-direction
		createMidPoint(section,0);
		((MeshSection)meshPart.firstElement()).setNormalsToVector(section.getZ().mul(-1));
	
		while (true) {
			
			// create meshpoints at each section
			createSectionMeshpoints(section,
					/*vBase+*/section.getDistance()/vLength);
				//(/*vBase+*/segment.getIndex()*segment.getLength()+ss.getHeight())/vLength);

			if (! sections.hasMoreElements()) {
				// last section - close mesh with normal in z-direction
				
				if (section.getRadius()>0.000001) {
					createMidPoint(section,1);
				}
				
				//DBG System.err.println("LAST StemSegm, setting normals to Z-dir");
				((MeshSection)meshPart.lastElement()).setNormalsToVector(section.getZ());
			}

			// next section
			if (sections.hasMoreElements())
				section = (StemSection)sections.nextElement();
			else
				break;
		}
		
//} catch (Exception e) {
//	Console.errorOutput("Mesh creation error for stem: " + stem.getTreePosition());
//	throw new RuntimeException(e.getMessage());
//}
		
		if (meshPart.size()>0)
			return meshPart;
		else
			return null;
	}


	private void createMidPoint(StemSection sec, double vMap) {
		// create only one point in the middle of the sections
		MeshSection section = new MeshSection(1,vMap);
		Transformation trf = sec.getTransformation(); 
		section.addPoint(trf.apply(new Vector(0,0,0)),0.5);
		meshPart.addSection(section);
	}

	
	private void createSectionMeshpoints(StemSection sec, double vMap) {
		Vector[] points = sec.getSectionPoints();
		
		MeshSection section = new MeshSection(points.length,vMap);
			//stem.DBG("MESH+LOBES: lobes: %d, depth: %f\n"%(self.tree.Lobes, self.tree.LobeDepth))
			
		if (points.length == 1)
			section.addPoint(points[0],0.5);
		else { 
			for (int i=0; i<points.length; i++) {
				double angle = i*360.0/points.length;
				section.addPoint(points[i],angle/360.0);
			}
		}	
			//add section to the mesh part
		meshPart.addSection(section);
	}

}
