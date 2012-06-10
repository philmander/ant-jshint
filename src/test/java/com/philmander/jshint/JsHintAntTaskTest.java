package com.philmander.jshint;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

import com.philmander.jshint.JsHintAntTask;
import com.philmander.jshint.report.PlainJsHintReporter;

/**
 * @author Phil Mander
 *
 */
public class JsHintAntTaskTest extends BuildFileTest
{
    @Override
    protected void setUp() throws Exception
    {
        configureProject("src/test/resources/jshint.xml");
    }

    @Test
    public void testTask_includes() throws IOException
    {
    	try {
    		executeTarget("testIncludes");	
    	} catch(BuildException e) {
    		int expectedErrors = 8;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
    }
    
    @Test
    public void testTask_options() throws IOException {
    	try {
    		executeTarget("testOptions");	
    	} catch(BuildException e) {
    		int expectedErrors = 2;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
	@Test
	public void testTask_optionsFile() throws IOException {
    	try {
    		executeTarget("testOptionsFile");	
    	} catch(BuildException e) {
    		int expectedErrors = 4;
    		assertEquals(PlainJsHintReporter.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
	
	@Test
	public void testTask_customFile() throws IOException {	
    	try {
    		executeTarget("testCustomHintFile");	
    	} catch(BuildException e) {
    		fail("This test should not find any errors\n" + e.getMessage());
    	}
	}
	
	@Test
	public void testTask_report() throws IOException {
    	
    	File reportPlain = new File("target/temp/jshint-report.txt");
    	File reportXml = new File("target/temp/jshint-report.xml");
    	assertFalse(reportPlain.exists());
    	assertFalse(reportXml.exists());
    	try {
    		executeTarget("testReportFile");	
    	} catch(BuildException e) {
    		fail("This test should not find any errors\n" + e.getMessage());
    	}
    	assertTrue("Plain report does not exist", reportPlain.exists());
    	assertTrue("Plain report file is empty", reportPlain.length() > 0);
    	
    	assertTrue("XML report does not exist", reportXml.exists());
    	assertTrue("XML report file is empty", reportXml.length() > 0);   
    }
}