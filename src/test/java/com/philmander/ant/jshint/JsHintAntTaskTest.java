package com.philmander.ant.jshint;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

import com.philmander.ant.jshint.JsHintAntTask;

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
    public void testTask() throws IOException
    {
    	try {
    		executeTarget("testIncludes");	
    	} catch(BuildException e) {
    		int expectedErrors = 8;
    		assertEquals(JsHintAntTask.getFailureMessage(expectedErrors), e.getMessage());
    	}
    	
    	try {
    		executeTarget("testOptions");	
    	} catch(BuildException e) {
    		int expectedErrors = 1;
    		assertEquals(JsHintAntTask.getFailureMessage(expectedErrors), e.getMessage());
    	}
    	
    	try {
    		executeTarget("testOptionsFile");	
    	} catch(BuildException e) {
    		int expectedErrors = 4;
    		assertEquals(JsHintAntTask.getFailureMessage(expectedErrors), e.getMessage());
    	}
    	
    	try {
    		executeTarget("testCustomHintFile");	
    	} catch(BuildException e) {
    		fail("This test should not find any errors\n" + e.getMessage());
    	}
    	
    	
    	File reportFile = new File("src/test/resources/temp/report.txt");
    	assertFalse(reportFile.exists());
    	try {
    		executeTarget("testReportFile");	
    	} catch(BuildException e) {
    		fail("This test should not find any errors\n" + e.getMessage());
    	}
    	assertTrue(reportFile.exists());
    	assertTrue(reportFile.length() > 0);
    	
    }
}