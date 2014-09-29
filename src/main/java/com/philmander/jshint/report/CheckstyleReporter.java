package com.philmander.jshint.report;

import org.apache.commons.lang3.StringEscapeUtils;

import com.philmander.jshint.JsHintError;
import com.philmander.jshint.JsHintReport;
import com.philmander.jshint.JsHintResult;

public class CheckstyleReporter implements JsHintReporter {

	private JsHintReport report;
	
	public CheckstyleReporter(JsHintReport report) {
		this.report = report;
	}

	@Override
    public String createReport() {
		StringBuilder output = new StringBuilder();

		output.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		output.append("<checkstyle version=\"5.7\">\n");

		for (JsHintResult result : report.getResults()) {

			output.append("    <file");
			output.append(attr("name", result.getFile()));
			output.append(">\n");
			for (JsHintError error : result.getErrors()) {
				output.append("        <error");
                output.append(attr("column", Integer.toString(error.getCharacter())));
				output.append(attr("line", Integer.toString(error.getLine())));
                output.append(attr("severity", "warning"));
                output.append(attr("message", error.getReason()));
				output.append(attr("source", "jshint"));
				output.append("/>\n");
			}
			output.append("    </file>\n");
		}
		output.append("</checkstyle>");

		return output.toString();
	}

	private String attr(String name, String value) {
		String attr = " " + name + "=\"" + StringEscapeUtils.escapeXml(value) + "\"";
		return attr;
	}

}
