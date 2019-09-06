package net.sourceforge.arbaro.export;

final public class Console {
	static final public int REALLY_QUIET=0;
	static final public int QUIET=1;
	static final public int VERBOSE=2;
	static final public int DEBUG=3;

	static public char progrChr=' '; // show progress on Console
	
	static public int outputLevel;

	static public void setOutputLevel(int level) {
		outputLevel = level;
		if (outputLevel>= VERBOSE)
			progrChr = '.';
		else
			progrChr = ' ';
	}
	
	static synchronized public boolean debug() {
		return outputLevel>=DEBUG;
	}
	
	static synchronized public void progressChar() {
		if (outputLevel>=VERBOSE) {
			System.err.print(progrChr);
		}
	}
	
	static synchronized public void verboseOutput(String msg) {
		if (outputLevel>=VERBOSE) {
			System.err.println(msg);
		}
	}
	
	static synchronized public void debugOutput(String msg) {
		if (outputLevel>=DEBUG) {
			System.err.println(msg);
		}
	}

	static synchronized public void errorOutput(String msg) {
		if (outputLevel>REALLY_QUIET) {
			System.err.println(msg);
		}
	}
	
	static synchronized public void printException(Exception e) {
		if (outputLevel>REALLY_QUIET) {
			System.err.println(e);
			e.printStackTrace(System.err);
		}
	}

	static synchronized public void progressChar(char c) {
		if (outputLevel>=VERBOSE) {
			System.err.print(c);
		}
	}

	static public void setProgressChar(char consoleChar) {
		Console.progrChr= consoleChar;
	}

}
