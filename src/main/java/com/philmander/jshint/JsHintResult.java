package com.philmander.jshint;

import java.util.ArrayList;
import java.util.List;

/**
 * Read only bean which wraps a JSHint result for a single file
 * @author Phil Mander
 *
 */
public class JsHintResult {
	
	private String file;
	
	private List<JsHintError> errors = new ArrayList<JsHintError>();

	public JsHintResult(String file) {
		this.file = file;
	}
	
	public String getFile() {
		return file;
	}
	
	public List<JsHintError> getErrors() {
		return errors;
	}

	public int getNumErrors() {
		return errors.size();
	}
	
	public void addError(JsHintError error) {
		errors.add(error);
	}
}
