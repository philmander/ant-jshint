package com.philmander.jshint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.LogLevel;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.philmander.jshint.report.JsHintReporter;
import com.philmander.jshint.report.PlainJsHintReporter;
import com.philmander.jshint.report.XmlJsHintReporter;

/**
 * Ant task to validate a set of files using JSHint
 * 
 * @author Phil Mander
 * 
 */
public class JsHintAntTask extends MatchingTask implements JsHintLogger {

	private File dir;

	private boolean fail = true;

	private String jshintSrc = null;

	private String optionsFile = null;

	private String options = null;

	private List<ReportType> reports = new ArrayList<ReportType>();

	public void addConfiguredReport(ReportType report) {
		reports.add(report);
	}

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

				// lint the code using the jshint runner
				JsHintRunner runner = new JsHintRunner(jshintSrc);
				runner.setLogger(this);

				// create abolute file path for each file before passing to jshint
				String[] absFiles = new String[files.length];
				for (int i = 0; i < files.length; i++) {
					absFiles[i] = dir + System.getProperty("file.separator") + files[i];
				}
				JsHintReport report = runner.lint(absFiles, loadOptions());

				//TODO: move reporting to runner so it works for CLI too
				reportResults(report);

				// pass or fail ?
				if (report.getTotalErrors() > 0) {

					String message = PlainJsHintReporter.getFailureMessage(report.getTotalErrors());
					if (fail) {
						throw new BuildException(message);
					} else {
						log(message);
					}
				} else {
					log(PlainJsHintReporter.getSuccessMessage(report.getNumFiles()));
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

	private void reportResults(JsHintReport report) {

		for (ReportType reportType : reports) {

			JsHintReporter reporter = null;

			// pick a reporter implementation
			if (reportType.getType().trim().equalsIgnoreCase("plain")) {
				reporter = new PlainJsHintReporter(report);
			} else if (reportType.getType().trim().equalsIgnoreCase("xml")) {
				// default to plain reporter
				reporter = new XmlJsHintReporter(report);
			}

			if (reportType.getDestFile() == null) {
				error("Could not write a report, destFile attribute was not set");
				continue;
			}

			if (reporter != null) {
				try {
					log("Writing report to " + reportType.getDestFile().getAbsolutePath());
					File outFile = reportType.getDestFile();
					Files.createParentDirs(outFile);
					Files.touch(outFile);
					Files.write(reporter.createReport(), outFile, Charsets.UTF_8);
				} catch (IOException e) {
					error("Could not write report file: " + e.getMessage());
				}
			} else {
				error("Cannot write report. [" + reportType.getType() + "] is not a valid report type.");
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
	 * The JSHint ant task is packaged with an embedded copy of jshint. But a
	 * user can specify there another copy of jshint using this attribute
	 * 
	 * @param jshintSrc
	 *            The location of the jshint.js file to use
	 */
	public void setJshintSrc(String jshintSrc) {
		this.jshintSrc = jshintSrc;
	}

	/**
	 * Ant error logging
	 */
	public void error(String msg) {
		log(msg, LogLevel.ERR.getLevel());
	}
}
