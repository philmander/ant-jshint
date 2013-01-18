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
    public void createReport() {

        String pageHeader = "<div class=\"section\"><h2>JsHint details<a name=\"Details\"></a></h2>";

        String fileSection = "<div class=\"section\"><h3>a/path/to/a/file/aFile.js</h3>";

        String tableHeader = "<table border=\"0\" class=\"bodyTable\">" +
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

        String fileEnd = "</div>";

        String tableFooter = "</table>";

        String expectedHtml = pageHeader + fileSection + tableHeader + anError + fileEnd + tableFooter;

        JsHintReport dummyReport = new JsHintReport(1);
        JsHintResult dummyResult = new JsHintResult("a/path/to/a/file/aFile.js");
        JsHintError aDummyJsHintError = new JsHintError("A dummy reason", "An evidence", 24, 'k');
        dummyResult.addError(aDummyJsHintError);
        dummyReport.addResult(dummyResult);

        String htmlResult = new HtmlJsHintReporter(dummyReport).createReport();

        Assert.assertNotNull("The HTML result musn't be null.", htmlResult);
        Assert.assertEquals(expectedHtml.trim(), htmlResult.trim());
    }

    /**
     * Test method for createReport.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void createReport_withNullReport_throwsIllegalArgumentException() {
        new HtmlJsHintReporter(null).createReport();
    }

}
