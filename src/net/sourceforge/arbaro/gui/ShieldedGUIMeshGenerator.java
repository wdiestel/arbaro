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

package net.sourceforge.arbaro.gui;

import java.awt.Component;

import net.sourceforge.arbaro.mesh.MeshGenerator;
import net.sourceforge.arbaro.mesh.ShieldedMeshGenerator;

/**
 * @author wolfram
 *
 */
public class ShieldedGUIMeshGenerator extends ShieldedMeshGenerator {
	Component parent;

	/**
	 * @param meshGenerator
	 */
	public ShieldedGUIMeshGenerator(Component parent,
			MeshGenerator meshGenerator) {
		super(meshGenerator);
		this.parent = parent;
	}

	
	protected void showException(Exception e) {
		super.showException(e); // output to Console as well
		ShowException.msgBox(parent, "Mesh generation error", e);
	}
}
