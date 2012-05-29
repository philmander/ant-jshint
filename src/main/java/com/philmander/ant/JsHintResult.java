package com.philmander.ant;

/**
 * Read only bean which wraps a JSHint result
 * @author Phil Mander
 *
 */
public class JsHintResult {
	
	private StringBuilder errorLog = null;
	
	private int numErrors = 0;

	public JsHintResult(StringBuilder errorLog, int numErrors) {
		
		this.errorLog = errorLog;
		this.numErrors = numErrors;
	}

	public StringBuilder getErrorLog() {
		return errorLog;
	}

	public int getNumErrors() {
		return numErrors;
	}
}
