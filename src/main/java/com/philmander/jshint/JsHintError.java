package com.philmander.jshint;

/**
 * Read only bean for 1 Jshint error
 * @author Phil Mander
 *
 */
public class JsHintError {

	private String reason;
	
	private String evidence;
			
	private int line;
	
	private int character;

	public JsHintError(String reason, String evidence, int line, int character) {
		super();
		this.reason = reason;
		this.evidence = evidence;
		this.line = line;
		this.character = character;
	}

	public String getReason() {
		return reason;
	}

	public String getEvidence() {
		return evidence;
	}

	public int getLine() {
		return line;
	}

	public int getCharacter() {
		return character;
	}
	
	
}
