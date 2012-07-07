package com.philmander.jshint.report;

import com.philmander.jshint.JsHintReport;

/**
 * Creates xml format reports with the 'jshint' root element
 * @author Phil Mander
 *
 */
public class XmlJsLintReporter extends XmlReporter {

	public XmlJsLintReporter(JsHintReport report) {

		super(report, "jslint");

	}
}
