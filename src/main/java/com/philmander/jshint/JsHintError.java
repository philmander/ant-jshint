package com.philmander.jshint;

/**
 * Read only bean for 1 Jshint error
 * @author Phil Mander
 *
 */
public class JsHintError {

	private String reason;
	private String evidence;
	private String line;
	private int character;
	private String code;
	private String severity;



	public JsHintError(String reason, String evidence, String line, int character, String severity, String code) {
		super();
		this.reason = reason;
		this.evidence = evidence;
		this.line = line;
		this.character = character;
		this.severity = severity;
		this.code = code;
	}

	public String getReason() {
		return reason;
	}

	public String getEvidence() {
		return evidence;
	}

	public String getLine() {
		return line;
	}

	public int getCharacter() {
		return character;
	}

	public String getSeverity()
	{
		return severity;
	}

	public String getCode()
	{
		return code;
	}


}
