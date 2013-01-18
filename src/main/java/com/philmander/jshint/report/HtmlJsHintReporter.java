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
 * An implementation of the {@link JsHintReporter} interface that creates a HTML report similar to checkstyle or PMD.
 *
 * @author Nahuel Barrios <barrios.nahuel@gmail.com>.
 */
public class HtmlJsHintReporter implements JsHintReporter {

    /**
     * The JsHintReport used to create this HTML report.
     */
    private final JsHintReport jsHintReport;

    /**
     * Creates a HtmlJsHintReporter for a specific {@link JsHintReport}.
     *
     * @param jsHintReport
     *         The data used to generate the HTML. It musn't be {@code null}.
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

    /**
     * @see #td(String)
     */
    private String td(int line) {
        return td(String.valueOf(line));
    }

    /**
     * Creates a {@code td} HTML tag with the specified {@code aValue}.
     *
     * @param aValue
     *         The inner value for this {@code td} tag.
     *
     * @return A {@code td} HTML tag.
     */
    private String td(String aValue) {
        return "<td>" + StringEscapeUtils.escapeXml(aValue) + "</td>";
    }

}
