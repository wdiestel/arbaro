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

package net.sourceforge.arbaro.export;

import java.io.PrintWriter;

/**
 * Exporter Facade with exception handling. Delegates all methods
 * of the Exporter interface to an exporter object given in then
 * constructor. Exceptions are printed to the console.
 */
public class ShieldedExporter implements Exporter {
    
	private Exporter exporter;
	
	public ShieldedExporter(Exporter exporter) {
		this.exporter = exporter;
	}

	/*
	 *  Output exception on the console
	 */
	protected void showException(Exception e) {
		Console.errorOutput("Export error:");
		Console.printException(e);
	}
	
	/**
	 * Delegates to exporter.getWriter and handles exceptions 
	 */
	public PrintWriter getWriter() {
		try {
			return exporter.getWriter();
		} catch (Exception e) {
			showException(e);
			return null;
		}
	}

	/**
	 * Delegates to exporter.write and handles exceptions 
	 */
	public void write(PrintWriter w, Progress progress)  {
		try {
			exporter.write(w,progress);
		} catch (Exception e) {
			showException(e);
		}
	}

}
