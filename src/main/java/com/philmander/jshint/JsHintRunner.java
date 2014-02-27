package com.philmander.jshint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.javascript.jscomp.JSSourceFile;
import com.philmander.jshint.report.PlainJsHintReporter;

/**`
 * Standalone class for running jshint
 * 
 * @author Phil Mander
 */
public class JsHintRunner {
	
	private static final String JSHINT_LOC = "/com/philmander/jshint/jshint.js";
	
	private static final String JSHINT_RUNNER_LOC = "/com/philmander/jshint/jshint-runner.js";

	private JsHintLogger logger = null;

	private String jshintSrc = null;

	/**
	 * Basic, intital CLI implementation
	 */
	public static void main(String[] args) {

		/*
		 * TODO: options and reporting
		 */

		File currentDir = new File(".");

		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();

		try {

			JsHintLogger logger = new JsHintLogger() {

				@Override
            public void log(String msg) {
					System.out.println("[jshint] " + msg);
				}

				@Override
            public void error(String msg) {
					System.err.println("[jshint] " + msg);
				}
			};

			CommandLine cl = parser.parse(options, args);

			JsHintRunner runner = new JsHintRunner();
			runner.setLogger(logger);

			List<String> files = new ArrayList<String>();
			for (Object arg : cl.getArgList()) {

				File testFile = new File(currentDir, (String) arg);
				if (testFile.exists()) {
					files.add(testFile.getAbsolutePath());
				} else {
					logger.error("Couldn't find " + testFile.getAbsolutePath());
				}
			}

			logger.log("Running JSHint on " + files.size() + " files");
			Properties optionsProps = new Properties();
			Properties globalsProps = new Properties();
			JsHintReport report = runner.lint(files.toArray(new String[files.size()]), optionsProps, globalsProps);

			if (report.getTotalErrors() > 0) {
				logger.error(PlainJsHintReporter.getFailureMessage(report.getTotalErrors()));
			} else {
				logger.log(PlainJsHintReporter.getSuccessMessage(report.getNumFiles()));
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	/**
	 * Create new instance with default embedded jshint src version
	 */
	public JsHintRunner() {

	}

	/**
	 * Create instance with custom jshint src file
	 * 
	 * @param jshintSrc
	 *            The jshint src file path. If null, the default embedded
	 *            version will be used
	 */
	public JsHintRunner(String jshintSrc) {

		if (jshintSrc != null) {
			this.jshintSrc = jshintSrc;
		}
	}

	/**
	 * Run JSHint over a list of one or more files
	 * 
	 * @param files
	 *            A list of absolute files
	 * @param options
	 *            A map of jshint options to apply
	 * @return A JSHintReport object containing the full results data
	 */
	public JsHintReport lint(String[] files, Map<Object, Object> options, Map<Object, Object> undefs) throws IOException {

		JsHintReport report = new JsHintReport(files.length);

		// start rhino
		Context ctx = Context.enter();
		ctx.setLanguageVersion(Context.VERSION_1_7);
        // workaround for the 64k limit of rhino with enabled optimizations: set the Optimization Level to -1. See https://github.com/jshint/jshint/issues/1333
		ctx.setOptimizationLevel(-1);
		ScriptableObject global = ctx.initStandardObjects();

		String[] names = { "print" };
		global.defineFunctionProperties(names, JsHintRunner.class, ScriptableObject.DONTENUM);

		// get js hint source from classpath or user file
		InputStream jsHintIn = jshintSrc != null ? new FileInputStream(new File(jshintSrc)) : this.getClass()
				.getResourceAsStream(JSHINT_LOC);
		JSSourceFile jsHintSrc = JSSourceFile.fromInputStream(JSHINT_LOC, jsHintIn);

		//get jshint runner js
		InputStream runJsHintIn = this.getClass().getResourceAsStream(JSHINT_RUNNER_LOC);
		JSSourceFile runJsHint = JSSourceFile.fromInputStream(JSHINT_RUNNER_LOC, runJsHintIn);

		// load jshint
        //ctx.evaluateReader(global, runEnv.getCodeReader(), runEnv.getName(), 0, null);
		ctx.evaluateReader(global, jsHintSrc.getCodeReader(), jsHintSrc.getName(), 0, null);

		// define properties to store current js source info
		global.defineProperty("currentFile", "", ScriptableObject.DONTENUM);
		global.defineProperty("currentCode", "", ScriptableObject.DONTENUM);

		// jshint options
		ScriptableObject jsHintOpts = (ScriptableObject) ctx.newObject(global);
		
		for (Object key : options.keySet()) {
			Object optionValue = options.get(key);
			if(optionValue instanceof String) {
				optionValue = parseOption((String)options.get(key));
			}			
			jsHintOpts.put((String) key, jsHintOpts, optionValue);
		}
		global.defineProperty("jsHintOpts", jsHintOpts, ScriptableObject.DONTENUM);

		// jshint globals
		ScriptableObject jsHintGlobals = (ScriptableObject) ctx.newObject(global);
		for (Object key : undefs.keySet()) {
			boolean globalValue = Boolean.valueOf(undefs.get(key).toString());			
			jsHintGlobals.put((String) key, jsHintGlobals, globalValue);
		}
		global.defineProperty("jsHintGlobals", jsHintGlobals, ScriptableObject.DONTENUM);

		// define object to store errors
		global.defineProperty("errors", ctx.newArray(global, 0), ScriptableObject.DONTENUM);

		// validate each file
		for (String file : files) {

			JsHintResult result = new JsHintResult(file);

			JSSourceFile jsFile = JSSourceFile.fromFile(file);

			String jsFileName = jsFile.getName();
			String jsFileCode = jsFile.getCode();
			if (jsFileCode.trim().length() == 0) {
				logger.error(jsFileName + " is empty. Linting will be skipped");
				continue;
			}

			// set current file on scope
			global.put("currentFile", global, jsFileName);
			global.put("currentCode", global, jsFileCode);
			global.put("errors", global, ctx.newArray(global, 0));

			ctx.evaluateReader(global, runJsHint.getCodeReader(), runJsHint.getName(), 0, null);

			// extract lint errors
			Scriptable errors = (Scriptable) global.get("errors", global);
			int numErrors = ((Number) errors.get("length", global)).intValue();

			if (numErrors > 0) {
				if (logger != null) {
					logger.error(PlainJsHintReporter.getFileFailureMessage(jsFileName));
				}
			}
			for (int i = 0; i < numErrors; i++) {

				// log detail for each error
				Scriptable errorDetail = (Scriptable) errors.get(i, global);

				try {
					String reason = errorDetail.get("reason", global).toString();
					int line = ((Number) errorDetail.get("line", global)).intValue();
					int character = ((Number) errorDetail.get("character", global)).intValue();
					String evidence = ((String) errorDetail.get("evidence", global)).replace(
							"^\\s*(\\S*(\\s+\\S+)*)\\s*$", "$1");

					JsHintError hintError = new JsHintError(reason, evidence, line, character);
					result.addError(hintError);

					if (logger != null) {
						logger.log(PlainJsHintReporter.getIssueMessage(reason, evidence, line, character));
					}
				} catch (ClassCastException e) {

					if (logger != null) {
						// TODO: See issue #1. Why is this happening?
						logger.error(("Problem casting JShint error variable for previous error. See issue (#1) ("
								+ e.getMessage() + ")"));
					} else {
						throw e;
					}
				}
			}
			report.addResult(result);
		}

		return report;
	}
	
	public static Object parseOption(String option) {
		
		try {
			Number numberVal = Double.parseDouble(option);	
			return numberVal;
		} catch(NumberFormatException e) {			
		}
		
		if(option.equalsIgnoreCase("false") || option.equalsIgnoreCase("true")) {
			boolean boolVal = Boolean.parseBoolean(option);
			return boolVal;
		}
		
		return option;		
	}

	public void setLogger(JsHintLogger logger) {
		this.logger = logger;
	}

	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		for (int i = 0; i < args.length; i++) {
			if (i > 0)
				System.out.print(" ");

			// Convert the arbitrary JavaScript value into a string form.
			String s = Context.toString(args[i]);

			System.out.print(s);
		}
		System.out.println();
	}
}