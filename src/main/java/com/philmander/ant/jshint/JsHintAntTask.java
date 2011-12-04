package com.philmander.ant.jshint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.javascript.jscomp.JSSourceFile;

public class JsHintAntTask extends MatchingTask {

	private File dir;

	private boolean fail = true;

	private String jshintFile = null;

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

		Context ctx = Context.enter();
		ctx.setLanguageVersion(Context.VERSION_1_5);
		ScriptableObject global = ctx.initStandardObjects();

		try {
			// get js hint
			String jsHintFile = "/jshint.js";

			// get js hint source from classpath or user file
			InputStream jsHintIn = jshintFile != null ? new FileInputStream(new File(jshintFile)) : this.getClass()
					.getResourceAsStream(jsHintFile);
			JSSourceFile jsHintSrc = JSSourceFile.fromInputStream(jsHintFile, jsHintIn);

			String runJsHintFile = "/jshint-runner.js";
			InputStream runJsHintIn = this.getClass().getResourceAsStream(runJsHintFile);
			JSSourceFile runJsHint = JSSourceFile.fromInputStream(runJsHintFile, runJsHintIn);

			// load jshint
			ctx.evaluateReader(global, jsHintSrc.getCodeReader(), jsHintSrc.getName(), 0, null);

			// define properties to store current js source info
			global.defineProperty("currentFile", "", ScriptableObject.DONTENUM);
			global.defineProperty("currentCode", "", ScriptableObject.DONTENUM);

			// jshint options
			ScriptableObject jsHintOpts = (ScriptableObject) ctx.newObject(global);
			jsHintOpts.defineProperty("rhino", true, ScriptableObject.DONTENUM);

			// user options
			Properties options = loadOptions();
			for (Object key : options.keySet()) {
				boolean optionValue = Boolean.valueOf((String)options.get(key));
				jsHintOpts.defineProperty((String) key, optionValue, ScriptableObject.DONTENUM);
			}

			global.defineProperty("jsHintOpts", jsHintOpts, ScriptableObject.DONTENUM);

			// define object to store errors
			global.defineProperty("errors", ctx.newArray(global, 0), ScriptableObject.DONTENUM);

			log("Validating files in " + dir.getAbsolutePath());

			if (files.length > 0) {

				// validate each file
				for (String file : files) {

					JSSourceFile jsFile = JSSourceFile.fromFile(dir.getAbsolutePath() + "/" + file);

					// set current file on scope
					global.put("currentFile", global, jsFile.getName());
					global.put("currentCode", global, jsFile.getCode());

					ctx.evaluateReader(global, runJsHint.getCodeReader(), runJsHint.getName(), 0, null);
				}

				// extract errors and report
				StringBuilder errorLog = new StringBuilder();
				Scriptable errors = (Scriptable) global.get("errors", global);
				int numErrors = ((Double) errors.get("length", global)).intValue();
				for (int i = 0; i < numErrors; i++) {
					Scriptable errorDetail = (Scriptable) errors.get(i, global);
					String file = (String) errorDetail.get("file", global);
					String reason = (String) errorDetail.get("reason", global);
					int line = ((Double) errorDetail.get("line", global)).intValue();
					int character = ((Double) errorDetail.get("character", global)).intValue();
					String evidence = (String) errorDetail.get("evidence", global);

					errorLog.append("JSHint code check failed for " + file + "\n");
					errorLog.append(reason + " (line: " + line + ", character: " + character + ")\n");
					errorLog.append(" > " + evidence.replace("^\\s*(\\S*(\\s+\\S+)*)\\s*$", "$1"));
					errorLog.append("\n");
				}

				log(errorLog.toString());

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

				reportResults(files.length, numErrors, errorLog.toString());
			} else {
				log("0 JS files found");
			}

		} catch (IOException e) {
			throw new BuildException(e);
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
	 * @param jshint
	 *            The location of the jshint.js file to use
	 */
	public void setJshintFile(String jshint) {
		this.jshintFile = jshint;
	}
}
