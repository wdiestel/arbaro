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

package net.sourceforge.arbaro.export.impl;

import java.io.PrintWriter;

import net.sourceforge.arbaro.export.Exporter;
import net.sourceforge.arbaro.feedback.Console;
import net.sourceforge.arbaro.feedback.Progress;

/**
 * @author wolfram
 *
 */
abstract class ExporterBase implements Exporter {
	
	protected static final int LEAF_PROGRESS_STEP = 500;
	protected static final int STEM_PROGRESS_STEP = 100;
	protected static final int MESH_PROGRESS_STEP = 500;

	
	protected PrintWriter w;

	Progress progress;
	long progressCount = 0;
	

	public Progress getProgress() {
		return progress;
	}
	
	/*
	public void newProgress(boolean consoleProgress) {
		progress = new Progress();
		if (consoleProgress) 
			Console.setOutputLevel(Console.VERBOSE);
	}
	*/
	
	public PrintWriter getWriter() { return w; }

	public void write(PrintWriter w, Progress progress) {
		this.w=w;
		this.progress = progress;
		
		progress.beginPhase("writing tree code",-1);
		doWrite();
		progress.endPhase();
	}

	protected abstract void doWrite();

	protected void incProgressCount(int step) {
		if (progressCount++ % step == 0) {
			progress.incProgress(step);
			Console.progressChar();
		}
	}

}
