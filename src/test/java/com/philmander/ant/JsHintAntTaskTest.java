package com.philmander.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

import com.philmander.ant.JsHintAntTask;

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
    		assertEquals(JsHintAntTask.getFailureMessage(expectedErrors), e.getMessage());
    	}
    }
    
    @Test
    public void testTask_options() throws IOException {
    	try {
    		executeTarget("testOptions");	
    	} catch(BuildException e) {
    		int expectedErrors = 2;
    		assertEquals(JsHintAntTask.getFailureMessage(expectedErrors), e.getMessage());
    	}
	}
	@Test
	public void testTask_optionsFile() throws IOException {
    	try {
    		executeTarget("testOptionsFile");	
    	} catch(BuildException e) {
    		int expectedErrors = 4;
    		assertEquals(JsHintAntTask.getFailureMessage(expectedErrors), e.getMessage());
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
    	
    	File reportFile = new File("target/temp/report.txt");
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