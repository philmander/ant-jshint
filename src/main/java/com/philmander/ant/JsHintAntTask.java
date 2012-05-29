package com.philmander.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Ant task to validate a set of files using JSHint
 * 
 * @author Phil Mander
 * 
 */
public class JsHintAntTask extends MatchingTask {

	private File dir;

	private boolean fail = true;

	private String jshintSrc = null;

	private String optionsFile = null;

	private String options = null;

	private String reportFile = null;

	/**
	 * Performs JSHint validation on a set of files
	 */
	@Override
	public void execute() throws BuildException {
		
		checkAttributes();

		DirectoryScanner dirScanner = getDirectoryScanner(dir);
		String[] files = dirScanner.getIncludedFiles();

		log("Validating files in " + dir.getAbsolutePath());
		
		if (files.length > 0) {

			try {
				
				//lint the code using the jshint runner
				JsHintRunner runner = new JsHintRunner(jshintSrc);
				JsHintResult result = runner.lint(dir, files, loadOptions());
				
				//get and report the results
				String errorLog = result.getErrorLog().toString();
				int numErrors = result.getNumErrors();
				log(errorLog);

				reportResults(files.length, numErrors, errorLog);

				// pass or fail ?
				if (numErrors > 0) {

					String message = getFailureMessage(numErrors);
					if (fail) {
						throw new BuildException(message);
					} else {
						log(message);
					}
				} else {
					log(getSuccessMessage(files.length));
				}

			} catch (IOException e) {
				throw new BuildException(e);
			}
		} else {
			log("0 JS files found");
		}

	}

	private Properties loadOptions() {
		Properties props = new Properties();
		if (optionsFile != null) {
			try {
				File optionsFile = new File(this.optionsFile);
				if (optionsFile.exists()) {
					FileInputStream inStream = new FileInputStream(optionsFile);
					props.load(inStream);
				} else {
					throw new FileNotFoundException("Could not find options file at " + optionsFile.getAbsolutePath());
				}
			} catch (FileNotFoundException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log("Could not load properties file");
			}
		}

		if (options != null) {
			String[] optionsList = options.split(",");
			for (String option : optionsList) {
				String[] optionPair = option.trim().split("=");
				props.put(optionPair[0].trim(), optionPair[1].trim());
			}
		}

		// log combined properties
		StringBuilder opts = new StringBuilder();
		for (Object optName : props.keySet()) {
			boolean val = Boolean.valueOf(props.getProperty((String) optName));
			opts.append(optName + "=" + val + ",");
		}
		if (props.size() > 0) {
			opts.deleteCharAt(opts.length() - 1);
		}
		log("Using options: " + opts);

		return props;
	}

	private void reportResults(int numFiles, int numErrors, String errorLog) {

		if (reportFile != null) {

			StringBuilder report = new StringBuilder();

			report.append("JSHint validation summary\n");
			report.append("----------------------------------------------------------------------\n");

			if (numErrors > 0) {
				report.append(errorLog);
			}
			report.append("----------------------------------------------------------------------\n");
			if (numErrors > 0) {
				report.append(getFailureMessage(numErrors));
			} else {
				report.append(getSuccessMessage(numFiles));
			}
			report.append("\n");
			report.append(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));

			try {
				File outFile = new File(reportFile);
				Files.createParentDirs(outFile);
				Files.touch(outFile);
				Files.write(report.toString(), outFile, Charsets.UTF_8);
			} catch (IOException e) {
				log("Could not write report file: " + e.getMessage());
			}
		}
	}

	private void checkAttributes() {
		String message = null;

		if (dir == null) {
			message = "Missing dir attribute";
		} else if (!dir.exists()) {
			message = "Directoy does not exist";
		} else if (!dir.isDirectory()) {
			message = "Dir is not a directory";
		}
		if (message != null) {
			throw new BuildException(message);
		}
	}

	protected static String getFailureMessage(int numErrors) {
		String plural = numErrors > 1 ? "s" : "";
		String message = "JSHint found " + numErrors + " error" + plural + ".";
		return message;
	}

	protected static String getSuccessMessage(int numFiles) {
		String message = numFiles + "  JS files passed JSHint code checks";
		return message;
	}

	/**
	 * The directory to scan for files to validate. Use includes to only
	 * validate js files and excludes to omit files such as compressed js
	 * libraries from js validation
	 * 
	 * @param dir
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}

	/**
	 * By default the ant task will fail the build if jshint finds any errors.
	 * Set this to false for reporting purposes
	 * 
	 * @param fail
	 */
	public void setFail(boolean fail) {
		this.fail = fail;
	}

	/**
	 * Specify jshint options in a properties file and point to the file using
	 * the options attribute
	 * 
	 * @param options
	 *            Location of the options properties file
	 */
	public void setOptionsFile(String optionsFile) {
		this.optionsFile = optionsFile;
	}

	/**
	 * Specify jshint options as a commas delimited list i.e. asi=true,
	 * evil=false. These options override any options specified using the
	 * optionsFile attribute
	 * 
	 * @param options
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * Output jshint results to a file
	 * 
	 * @param reportFile
	 */
	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	/**
	 * The JSHint ant task is packaged with an embedded copy of jshint. But a
	 * user can specify there another copy of jshint using this attribute
	 * 
	 * @param jshintSrc
	 *            The location of the jshint.js file to use
	 */
	public void setJshintSrc(String jshintSrc) {
		this.jshintSrc = jshintSrc;
	}
}
