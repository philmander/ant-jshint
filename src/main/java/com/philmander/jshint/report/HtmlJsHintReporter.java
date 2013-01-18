/**
 * Created by: Nahuel Barrios <barrios.nahuel@gmail.com>.
 * On: 1/18/13 at 10:04 AMhs.
 */
package com.philmander.jshint.report;

import com.philmander.jshint.JsHintError;
import com.philmander.jshint.JsHintReport;
import com.philmander.jshint.JsHintResult;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * TODO : Javadoc for
 *
 * @author Nahuel Barrios <barrios.nahuel@gmail.com>.
 */
public class HtmlJsHintReporter implements JsHintReporter {

    /**
     * The JsHintReport used to create this HTML report.
     */
    private final JsHintReport jsHintReport;

    /**
     * TODO : Javadoc for Creates a HtmlJsHintReporter with a specific JsHintReport.
     *
     * @param jsHintReport
     */
    public HtmlJsHintReporter(JsHintReport jsHintReport) {
        this.jsHintReport = jsHintReport;
    }

    public String createReport() {

        if (jsHintReport == null) {
            throw new IllegalArgumentException("A JsHintReport is required to successfuly build a HTML report.");
        }

        String pageHeader = "<div class=\"section\"><h2>JsHint details<a name=\"Details\"></a></h2>";


        StringBuilder errors = new StringBuilder();
        for (JsHintResult eachResult : jsHintReport.getResults()) {

            errors.append("<div class=\"section\"><h3>" + eachResult.getFile() + "</h3>");


            errors.append("<table border=\"0\" class=\"bodyTable\">" +
                                  "<tr class=\"a\">" +
                                  "<th>Reason</th>" +
                                  "<th>Evidence</th>" +
                                  "<th>Line</th>" +
                                  "</tr>");

            for (JsHintError eachError : eachResult.getErrors()) {

                errors.append("<tr>");

                errors.append(td(eachError.getReason()))
                        .append(td(eachError.getEvidence()))
                        .append(td(eachError.getLine()));

                errors.append("</tr>");
            }

            errors.append("</div>");

        }

        String tableFooter = "</table>";


        return pageHeader + errors.toString() + tableFooter;
    }

    private String td(int line) {
        return td(String.valueOf(line));
    }

    private String td(String aValue) {
        return "<td>" + StringEscapeUtils.escapeXml(aValue) + "</td>";
    }

}
