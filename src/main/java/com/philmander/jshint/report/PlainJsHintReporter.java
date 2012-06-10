package com.philmander.jshint.report;

import java.text.DateFormat;
import java.util.Date;

import com.philmander.jshint.JsHintError;
import com.philmander.jshint.JsHintReport;
import com.philmander.jshint.JsHintResult;

/**
 * Reports a JsHint result in a plain text format
 * @author Phil Mander
 *
 */
public class PlainJsHintReporter implements JsHintReporter {

	private JsHintReport report;

	public PlainJsHintReporter(JsHintReport report) {

		this.report = report;
	}

	public String createReport() {
		
		StringBuilder output = new StringBuilder();

		output.append("JSHint validation summary:\n");
		
		if (report.getTotalErrors() > 0) {
			output.append("----------------------------------------------------------------------\n");
			for(JsHintResult result : report.getResults()) { 
				output.append("\n");
				output.append(getFileFailureMessage((result.getFile()) + "\n"));
				for(JsHintError error : result.getErrors()) {
					output.append(getIssueMessage(error.getReason(), error.getEvidence(), error.getLine(), error.getCharacter()) + "\n");
				}
				
			}
			output.append("----------------------------------------------------------------------\n");
		}
		
		if (report.getTotalErrors()  > 0) {
			output.append(getFailureMessage(report.getTotalErrors()));
		} else {
			output.append(getSuccessMessage(report.getNumFiles()));
		}
		output.append("\n");
		output.append(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));

		return output.toString();
	}

	public static String getFileFailureMessage(String file) {
		return "JSHint validation failed for " + file;
	}

	public static String getIssueMessage(String reason, String evidence, int line, int character) {
		String msg = reason + " (line: " + line + ", character: " + character + ")\n > " + evidence;
		return msg;
	}

	public static String getFailureMessage(int numErrors) {
		String plural = numErrors > 1 ? "s" : "";
		String message = "JSHint found " + numErrors + " error" + plural + ".";
		return message;
	}

	public static String getSuccessMessage(int numFiles) {
		String message = numFiles + "  JS files passed JSHint code checks";
		return message;
	}
}
