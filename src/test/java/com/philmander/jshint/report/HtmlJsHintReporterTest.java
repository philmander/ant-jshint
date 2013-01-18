/**
 * Created by: Nahuel Barrios <barrios.nahuel@gmail.com>.
 * On: 1/18/13 at 10:05 AMhs.
 */
package com.philmander.jshint.report;

import com.philmander.jshint.JsHintError;
import com.philmander.jshint.JsHintReport;
import com.philmander.jshint.JsHintResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test type for {@link HtmlJsHintReporter}.
 *
 * @author Nahuel Barrios <barrios.nahuel@gmail.com>.
 */
public class HtmlJsHintReporterTest {

    /**
     * Test method for createReport.
     */
    @Test
    public void createReport_with1FileReadAnd1Error_generatesHTMLReport() {

        String pageHeader = "<!DOCTYPE html>" +
                                    "<html>" +
                                    "<head>" +
                                    "<title>JSHint details</title>" +
                                    "</head>" +
                                    "<body>";

        String detailsSectionStart = "<div class=\"section\"><h2>JSHint details</h2>";

        String fileSection = "<div class=\"section\"><h3>a/path/to/a/file/aFile.js</h3>";

        String tableHeader = "<table border=\"1\">" +
                                     "<tr class=\"a\">" +
                                     "<th>Reason</th>" +
                                     "<th>Evidence</th>" +
                                     "<th>Line</th>" +
                                     "</tr>";

        String anError = "<tr>" +
                                 "<td>A dummy reason</td>" +
                                 "<td>An evidence</td>" +
                                 "<td>24</td>" +
                                 "</tr>";

        String fileEnd = "</table></div>";

        String detailsSectionEnd = "</div>";

        String pageFooter = "</body></html>";

        String expectedHtml = pageHeader + detailsSectionStart + fileSection + tableHeader + anError + fileEnd + detailsSectionEnd + pageFooter;

        JsHintReport dummyReport = new JsHintReport(1);
        JsHintResult dummyResult = new JsHintResult("a/path/to/a/file/aFile.js");
        JsHintError aDummyJsHintError = new JsHintError("A dummy reason", "An evidence", 24, 'k');
        dummyResult.addError(aDummyJsHintError);
        dummyReport.addResult(dummyResult);

        String htmlResult = new HtmlJsHintReporter(dummyReport).createReport();

        Assert.assertNotNull("The HTML result musn't be null.", htmlResult);
        Assert.assertEquals(expectedHtml, htmlResult);
    }

    /**
     * Test method for createReport.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void createReport_withNullReport_throwsIllegalArgumentException() {
        new HtmlJsHintReporter(null).createReport();
    }

    /**
     * Test method for createReport.
     */
    @Test
    public void createReport_with1FileReadAnd0Error_generatesHTMLReportWithNoErrorsDisplayed() {

        String pageHeader = "<!DOCTYPE html>" +
                                    "<html>" +
                                    "<head>" +
                                    "<title>JSHint details</title>" +
                                    "</head>" +
                                    "<body>";

        String detailsSectionStart = "<div class=\"section\"><h2>JSHint details</h2>";

        String noErrorsToDisplay = "<h3>There are no JSHint errors to display.</h3>";

        String detailsSectionEnd = "</div>";

        String pageFooter = "</body></html>";

        String expectedHtml = pageHeader + detailsSectionStart + noErrorsToDisplay + detailsSectionEnd + pageFooter;

        JsHintReport dummyReport = new JsHintReport(1);
        JsHintResult dummyResult = new JsHintResult("a/path/to/a/file/aFile.js");
        dummyReport.addResult(dummyResult);

        String htmlResult = new HtmlJsHintReporter(dummyReport).createReport();

        Assert.assertNotNull("The HTML result musn't be null.", htmlResult);
        Assert.assertEquals(expectedHtml, htmlResult);
    }

}
