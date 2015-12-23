package net.sourceforge.arbaro.export;

public class InvalidExportFormatError extends Exception {
	private static final long serialVersionUID = 1L;
	
    public InvalidExportFormatError(String msg) {
	super(msg);
    }
};