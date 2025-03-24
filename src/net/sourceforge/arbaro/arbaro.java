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

package net.sourceforge.arbaro;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;

import net.sourceforge.arbaro.tree.*;
import net.sourceforge.arbaro.export.*;
import net.sourceforge.arbaro.params.Params;

/**
 * Main class for command line version of Arbaro
 */

public class arbaro {
	static int XMLinput = 0;
	static int CFGinput = 1;
	static int XMLoutput = 99;
	static void println(String s) { System.err.println(s); }
	static void println() { System.err.println(); }

	public static final String programName =
		"Arbaro 1.9.9b - creates trees objects for rendering from xml parameter files\n"+
		"(c) 2003-2012 by Wolfram Diestel <diestel@steloj.de> (GPL see file COPYING)\n"+
	        "RIB support added by Moritz Moeller\n";

	static void printProgramName() {
		println(programName);
		println();
	}

	// TODO need switch for adding uv-coordinates in output
	// switches should be more similar to the actual class
	// structure now, e.g. --exporter=OBJ (-x OBJ)
	static void usage () {
		println("syntax:");
		println("java -jar arbaro.jar [OPTIONS] <paramfile.xml> > <tree.inc>");
		println();
		println("options");
		println("    -h|--help            Show this helpscreen");
		println();
		println("    -q|--quiet           Only error messages are output to stderr ");
		println("                         no progress");
		println();
		println("    -qq|--reallyquiet    No messages are output to stderr");
		println();
		println("    -d|--debug           Much debugging ouput should be interesting");
		println("                         for developers only");
		println();
		println("    -o|--output <file>   Output tree code to this file instead of");
		println("                         stdout");
		println();
		println("    -s|--seed <seed>     Random seed for the tree, default is 13, but");
		println("                         you don't want all trees look the same as ");
		println("                         mine, so try something other seeds here; the");
		println("                         seed is part of the declaration string in");
		println("                         the renderer files");
		println();
		println("    -l|--levels <level>  1..Levels+1 -- calculates and ouputs only so");
		println("                         many levels; useful for fast testing of");
		println("                         parameter changes or to get a draft tree for");
		println("                         a first impression of a scene without all");
		println("                         the small stems and leaves");
		println();
		println("    -f|--format <format> Export format, this is one of: ");
		println("                         POV_CONES -- Povray primitives (cones and spheres)");
		println("                         POV_MESH  -- Povray mesh2 objects");
		println("                         RIB       -- RenderMan RIB archive");
		println("                         OBJ       -- Wavefront OBJ file");
		println("                         DXF       -- Autocad DXF file");
		println("                         The default is POV_CONES");
		println();
		println("    --uvleaves           For the export formats POV_MESH and OBJ:");
		println("                         Output uv coordinates for the leaves");
		println();
		println("    --uvstems            For the export formats POV_MESH, OBJ and RIB:");
		println("                         Output uv coordinates for the stems");
		println();
		println("    -s|--smooth <value>  0.0..1.0 -- for the export formats POV_MESH,");
		println("                         RIB, OBJ and DXF, the smooth value influences");
		println("                         how many vertices are used for every stem");
		println("                         section and for POV_MESH which levels normals");
		println("                         should be used to hide the triangle borders.");
		println();
		println("    -r|--treecfg         Input file is a simple Param=Value list. Needs");
		println("                         less typing for a new tree than writing XML");
		println("                         code");
		println();
		println("    -x|--xml             Output parameters as XML tree definition");
		println("                         instead of creating the tree and writing it");
		println("                         as a 3D file. Useful for converting a simple");
		println("                         simple parameter list to a XML file: ");
		println("                            java -jar arbaro_cmd.jar --treecfg \\");
		println("                                 --xml < mytree.cfg > mytree.xml");
		println();
		println("    -p|--scene <file>    Additionally output renderer scene to file");
		println();
		println("example:");
		println();
		println("    java -jar arbaro_cmd.jar quaking_aspen.xml > quaking_aspen.inc");
		println();
	}

	private static int getExportFormat(String format) throws InvalidExportFormatError {
		String[] formats = ExporterFactory.getShortExportFormats();
		for (int i=0; i<formats.length; i++) {
			if (formats[i].equals(format)) return i;
		}
		throw new InvalidExportFormatError("Invalid export format given.");
	}

	public static void main (String [] args) throws Exception{
		//	try {

		boolean quiet = false;
		boolean reallyQuiet = false;
		boolean debug = false;
		boolean uvLeaves = false;
		boolean uvStems = false;
		int seed = 13;
		int levels = -1;
		int output=ExporterFactory.POV_MESH;
		double smooth=-1;
		int input = XMLinput;
		String input_file = null;
		String output_file = null;
		String scene_file = null;

		for (int i=0; i<args.length; i++) {

			if (args[i].equals("-d") || args[i].equals("--debug")) {
				debug = true;
			} else if (args[i].equals("-h") || args[i].equals("--help")) {
				printProgramName();
				usage();
				System.exit(0);
			} else if (args[i].equals("-q") || args[i].equals("--quiet")) {
				quiet = true;
			} else if (args[i].equals("-qq") || args[i].equals("--reallyquiet")) {
				reallyQuiet = true;
				quiet = true;
			} else if (args[i].equals("-o") || args[i].equals("--output")) {
				output_file = args[++i];
			} else if (args[i].equals("-s") || args[i].equals("--seed")) {
				seed = new Integer(args[++i]).intValue();
			} else if (args[i].equals("-l") || args[i].equals("--levels")) {
				levels = new Integer(args[++i]).intValue();
			} else if (args[i].equals("-f") || args[i].equals("--format")) {
				output = getExportFormat(args[++i]);
			} else if (args[i].equals("--uvleaves")) {
				uvLeaves = true; i++;
			} else if (args[i].equals("--uvstems")) {
				uvStems = true; i++;
			} else if (args[i].equals("-s") || args[i].equals("--smooth")) {
				smooth = new Double(args[++i]).doubleValue();
			} else if (args[i].equals("-x") || args[i].equals("--xml")) {
				output = XMLoutput;
			} else if (args[i].equals("-r") || args[i].equals("--treecfg")) {
				input = CFGinput;
			} else if (args[i].equals("-p") || args[i].equals("--scene")) {
				scene_file = args[++i];
			} else if (args[i].charAt(0) == '-') {
				printProgramName();
				usage();
				System.err.println("Invalid option "+args[i]+"!");
				System.exit(1);
			} else {
				// rest of args should be files
				// input_files = new String[] = ...
				input_file = args[i];
				break;
			}
		}


		//########## read params from XML file ################

		if (! quiet) {
			printProgramName();
		}

		if (debug)
			Console.setOutputLevel(Console.DEBUG);
		else if (reallyQuiet)
			Console.setOutputLevel(Console.REALLY_QUIET);
		else if (quiet)
			Console.setOutputLevel(Console.QUIET);
		else
			Console.setOutputLevel(Console.VERBOSE);

		TreeGenerator treeGenerator = TreeGeneratorFactory.createTreeGenerator();
		Exporter exporter;

		// put here or later?
		//if (smooth>=0) treeFactory.params.Smooth = smooth;

		InputStream in;
		if (input_file == null) {
			Console.verboseOutput("No tree definition file given.");
			Console.verboseOutput("Reading parameters from STDIN...");

			in = System.in;
		} else {
			Console.verboseOutput("Reading parameters from "
					+ input_file + "...");
			in = new FileInputStream(input_file);
		}

		// read parameters
		if (input == CFGinput) treeGenerator.readParamsFromCfg(in);
		else treeGenerator.readParamsFromXML(in);

		// FIXME: put here or earlier?
		if (smooth>=0) treeGenerator.setParam("Smooth",new Double(smooth).toString());

		PrintWriter out;
		if (output_file == null) {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		} else {
			out = new PrintWriter(new FileWriter(new File(output_file)));
		}

		if (output==XMLoutput) {
			// save parameters in XML file, don't create tree
			treeGenerator.writeParamsToXML(out);
		} else {
			treeGenerator.setSeed(seed);
			Progress progress = new Progress();
			Tree tree = treeGenerator.makeTree(progress);
			Params params = treeGenerator.getParams();
			params.stopLevel = levels;
			ExporterFactory.setExportFormat(output);
			ExporterFactory.setOutputStemUVs(uvStems);
			ExporterFactory.setOutputLeafUVs(uvLeaves);
			exporter = ExporterFactory.createExporter(tree);
			exporter.write(out,progress);

			if (scene_file != null) {
				if (! quiet) System.err.println("Writing renderer scene to "+scene_file+"...");
				PrintWriter scout = new PrintWriter(new FileWriter(new File(scene_file)));
				String image = scene_file+".tif";
				exporter = ExporterFactory.createSceneExporter(tree);
				ExporterFactory.setImageFilePath(image);
				exporter.write(scout,progress);
			}
		}
	}
};













