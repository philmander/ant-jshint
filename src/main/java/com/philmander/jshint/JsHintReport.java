package com.philmander.jshint;

import java.util.ArrayList;
import java.util.List;

/**
 * Complete JsHint report results for a collection of files
 * @author Phil Mander
 *
 */
public class JsHintReport {

	private List<JsHintResult> results = new ArrayList<JsHintResult>();

	private int numFiles;
	
	public JsHintReport(int numFiles) {
		this.numFiles = numFiles;
	}
	
	public void addResult(JsHintResult result) {
		results.add(result);
	}
	
	public int getTotalErrors() {
		int errors = 0;
		for(JsHintResult result : results) {
			errors += result.getNumErrors();
		}
		return errors;
	}
	
	public int getNumFiles() {
		return numFiles;
	}

	public List<JsHintResult> getResults() {
		return results;
	}
}
