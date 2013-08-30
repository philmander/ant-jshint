package com.philmander.jshint.report;

import org.apache.commons.lang3.StringEscapeUtils;

import com.philmander.jshint.JsHintError;
import com.philmander.jshint.JsHintReport;
import com.philmander.jshint.JsHintResult;

/**
 * Make a report checkstyle compatible
 */
public class CheckstyleReporter extends XmlReporter
{
    /**
     * Ctor
     * @param  report The js hint report
     * @return        The xml string
     */
    public CheckstyleReporter(JsHintReport report)
    {
        super(report, "checkstyle");
    }

    /**
     * Override create report to make it more Checkstyle compatible
     *
     * @override
     * @return String A bunch of xml
     */
    public String createReport() {

        StringBuilder output = new StringBuilder();

        output.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        output.append("<" + this.rootElement + ">\n");

        for (JsHintResult result : this.report.getResults()) {

            output.append("    <file");
            output.append(attr("name", result.getFile()));
            output.append(">\n");
            for (JsHintError error : result.getErrors()) {
                output.append("        <error");
                output.append(attr("line",error.getLine()));
                output.append(attr("column", Integer.toString(error.getCharacter())));
                output.append(attr("message", error.getReason()));
                output.append(attr("severity", error.getSeverity()));
                output.append(attr("source", error.getCode()));
                output.append("/>\n");
            }
            output.append("    </file>\n");
        }
        output.append("</" + this.rootElement + ">");

        return output.toString();
    }
}