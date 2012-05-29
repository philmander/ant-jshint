package com.philmander.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.javascript.jscomp.JSSourceFile;

/**
 * Standalone class for running jshint
 * @author Phil Mander
 * TODO: Make CLI
 */
public class JsHintRunner {
	
	private Logger logger = Logger.getLogger(JsHintRunner.class.getName());

	private String jshintSrc = null;

	/**
	 * Create new instance with default embedded jshint src version
	 */
	public JsHintRunner() {

	}

	/**
	 * Create instance with custom jshint src file 
	 * @param jshintSrc The jshint src file path. If null, the default embedded version will be used
	 */
	public JsHintRunner(String jshintSrc) {
		
		if (jshintSrc != null) {
			this.jshintSrc = jshintSrc;
		}
	}

	/**
	 * Run JSHint over a list of one or more files
	 * @param baseDir The base directory the files are relative to
	 * @param files A list of file paths relative to the base directory
	 * @param options A map of jshint options to apply
	 * @return A JSHintResult object containing the error log and the number of errors found.
	 * @throws IOException 
	 */
	public JsHintResult lint(File baseDir, String[] files, Properties options) throws IOException {

		//start rhino
		Context ctx = Context.enter();
		ctx.setLanguageVersion(Context.VERSION_1_5);
		ScriptableObject global = ctx.initStandardObjects();

		// get js hint
		String jsHintFileName = "/jshint.js";

		// get js hint source from classpath or user file
		InputStream jsHintIn = jshintSrc != null ? new FileInputStream(new File(jshintSrc)) : this.getClass()
				.getResourceAsStream(jsHintFileName);

		JSSourceFile jsHintSrc = JSSourceFile.fromInputStream(jsHintFileName, jsHintIn);

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
		for (Object key : options.keySet()) {
			boolean optionValue = Boolean.valueOf((String) options.get(key));
			jsHintOpts.defineProperty((String) key, optionValue, ScriptableObject.DONTENUM);
		}

		global.defineProperty("jsHintOpts", jsHintOpts, ScriptableObject.DONTENUM);

		// define object to store errors
		global.defineProperty("errors", ctx.newArray(global, 0), ScriptableObject.DONTENUM);

		// validate each file
		for (String file : files) {

			JSSourceFile jsFile = JSSourceFile.fromFile(baseDir.getAbsolutePath() + "/" + file);

			// set current file on scope
			global.put("currentFile", global, jsFile.getName());
			global.put("currentCode", global, jsFile.getCode());

			ctx.evaluateReader(global, runJsHint.getCodeReader(), runJsHint.getName(), 0, null);
		}

		// extract errors and report
		StringBuilder errorLog = new StringBuilder();
		Scriptable errors = (Scriptable) global.get("errors", global);
		int numErrors = ((Number) errors.get("length", global)).intValue();
		for (int i = 0; i < numErrors; i++) {

			// log detail for each error
			Scriptable errorDetail = (Scriptable) errors.get(i, global);

			Object file = errorDetail.get("file", global);
			Object reason = errorDetail.get("reason", global);
			Object line = errorDetail.get("line", global);
			Object character = errorDetail.get("character", global);
			Object evidence = errorDetail.get("evidence", global);
			errorLog.append("JSHint code check failed for " + file + "\n");

			try {
				errorLog.append(reason + " (line: " + ((Number) line).intValue() + ", character: "
						+ ((Number) character).intValue() + ")\n");
				errorLog.append(" > " + ((String) evidence).replace("^\\s*(\\S*(\\s+\\S+)*)\\s*$", "$1"));
			} catch (ClassCastException e) {

				// print error without any casting. Should work but not
				// as pretty
				errorLog.append(reason + " (line: " + line + ", character: " + character + ")\n");
				errorLog.append(" > " + evidence);

				// TODO: See issue #1. Why is this happening?
				logger.warning(("Problem casting JShint error variable for previous error. This is a minor issue (#1) and this message is here for debugging purposoes ("
						+ e.getMessage() + ")"));
			}
			errorLog.append("\n");
		}

		JsHintResult result = new JsHintResult(errorLog, numErrors);
		
		return result;
	}
}