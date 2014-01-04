package com.philmander.jshint;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.junit.Ignore;
import org.junit.Test;

import com.philmander.jshint.report.PlainJsHintReporter;

/**
 * @author Phil Mander
 */
public class JsHintAntTaskTest extends BuildFileTest
{
    @Override
    protected void setUp() throws Exception {
        configureProject("src/test/resources/jshint.xml");
    }

    @Test
    public void testTask_includes() {
    	try {
    		executeTarget("testIncludes");	
    	} catch(BuildException e) {
    		int expectedErrors = 28;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
    }
    
    @Test
    public void testTask_options() {
    	try {
    		executeTarget("testOptions");	
    	} catch(BuildException e) {
    		int expectedErrors = 2;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
    
	@Test
	public void testTask_optionsFile() {
    	try {
    		executeTarget("testOptionsFile");	
    	} catch(BuildException e) {
    		int expectedErrors = 4;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
	
	@Test	
	public void testTask_optionsJsonFile() {
    	try {
    		executeTarget("testOptionsJsonFile");	
    	} catch(BuildException e) {
    		int expectedErrors = 4;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}

    @Test
    public void testTask_optionsJshintrcFile() {

        File path = this.getProject().getBaseDir();
        String jshintrc = ".jshintrc";

        File jshintrcFile = new File(path, "_" + jshintrc);
        jshintrcFile.renameTo(new File(path, jshintrc));
        try {
            executeTarget("testOptionsJshintrcFile");
        } catch(BuildException e) {
            int expectedErrors = 4;
            assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
        } finally {
            jshintrcFile = new File(path, jshintrc);
            jshintrcFile.renameTo(new File(path, "_" + jshintrc));
        }
    }
	
	@Test
	public void testTask_customFile() {	
    	try {
    		executeTarget("testCustomHintFile");	
    	} catch(BuildException e) {
    		fail("This test should not find any errors\n" + e.getMessage());
    	}
	}
	
	@Test
	public void testTask_globals() {	
    	try {
    		executeTarget("testGlobals");	
    	} catch(BuildException e) {
    		int expectedErrors = 2;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
	
	@Test
	public void testTask_globalsFile() {	
    	try {
    		executeTarget("testGlobalsFile");	
    	} catch(BuildException e) {
    		int expectedErrors = 2;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
	
	@Test
	public void testTask_report() {
    	
    	File reportPlain = new File("target/temp/jshint-report.txt");
    	File reportXml = new File("target/temp/jshint-report.xml");
    	File reportJsLintXml = new File("target/temp/jslint-report.xml");
    	assertFalse(reportPlain.exists());
    	assertFalse(reportXml.exists());
    	assertFalse(reportJsLintXml.exists());
    	try {
    		executeTarget("testReportFile");	
    	} catch(BuildException e) {
    		fail("This test should not find any errors\n" + e.getMessage());
    	}
    	assertTrue("Plain report does not exist", reportPlain.exists());
    	assertTrue("Plain report file is empty", reportPlain.length() > 0);
    	
    	assertTrue("XML report does not exist", reportXml.exists());
    	assertTrue("XML report file is empty", reportXml.length() > 0);
    	
    	assertTrue("XML JsLint report does not exist", reportJsLintXml.exists());
    	assertTrue("XML JsLint report file is empty", reportJsLintXml.length() > 0);   

    }
}