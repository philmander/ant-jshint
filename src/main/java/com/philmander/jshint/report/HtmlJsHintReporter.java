/**
 * Created by: Nahuel Barrios <barrios.nahuel@gmail.com>.
 * On: 1/18/13 at 10:04 AMhs.
 */
package com.philmander.jshint.report;

import java.util.List;

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

        String pageTitle = "JSHint details";

        String pageHeader = "<!DOCTYPE html>" +
                                    "<html>" +
                                    "<head>" +
                                    "<title>" + pageTitle + "</title>" +
                                    "</head>" +
                                    "<body>";

        String detailsSectionStart = "<div class=\"section\"><h2>" + pageTitle + "</h2>";


        StringBuilder errors = new StringBuilder();
        for (JsHintResult eachResult : jsHintReport.getResults()) {
            if (eachResult.getNumErrors() > 0) {
                addErrorsFromOneFile(errors, eachResult.getFile(), eachResult.getErrors());
            }
        }

        if (errors.length() == 0) {
            errors.append("<h3>There are no JSHint errors to display.</h3>");
        }

        String detailsSectiondEnd = "</div>";

        String pageFooter = "</body></html>";

        return pageHeader + detailsSectionStart + errors.toString() + detailsSectiondEnd + pageFooter;
    }

    /**
     * Creates the {@code div} section for showing errors from one file. It creates a {@code table} HTML element with the following columns:
     * Reason, Evidence, Line.
     *
     * @param errors
     *         The StringBuilder where to add the resulting HTML.
     * @param filePath
     *         The name and the path of the file.
     * @param errorsList
     *         A list of {@link JsHintError} with the errors to add.
     */
    private void addErrorsFromOneFile(StringBuilder errors, String filePath, List<JsHintError> errorsList) {
        errors.append("<div class=\"section\"><h3>").append(filePath).append("</h3>");


        errors.append("<table border=\"1\">" +
                              "<tr class=\"a\">" +
                              "<th>Reason</th>" +
                              "<th>Evidence</th>" +
                              "<th>Line</th>" +
                              "</tr>");

        for (JsHintError eachError : errorsList) {

            errors.append("<tr>");

            errors.append(td(eachError.getReason()))
                    .append(td(eachError.getEvidence()))
                    .append(td(eachError.getLine()));

            errors.append("</tr>");
        }

        errors.append("</table></div>");
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
