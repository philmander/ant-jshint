package com.philmander.jshint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.LogLevel;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.philmander.jshint.report.CheckstyleReporter;
import com.philmander.jshint.report.JsHintReporter;
import com.philmander.jshint.report.PlainJsHintReporter;
import com.philmander.jshint.report.XmlJsHintReporter;
import com.philmander.jshint.report.XmlJsLintReporter;

/**
 * Ant task to validate a set of files using JSHint
 * 
 * @author Phil Mander
 * 
 */
public class JsHintAntTask extends MatchingTask implements JsHintLogger {

	protected final String PLAIN_REPORT = "plain";
	
	protected final String XML_REPORT = "xml"; 
	
	protected final String JSLINT_XML_REPORT = "jslint-xml";
    
    protected final String CHECKSTYLE_REPORT = "checkstyle";

    private final String JSHINTRC_FILE = ".jshintrc";
	
	private File dir;

	private boolean fail = true;

	private String jshintSrc = null;
	
	private String globals = null;
	
	private String globalsFile = null;

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
				JsHintReport report = runner.lint(absFiles, loadOptions(), loadGlobals());

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
	
	private Map<Object, Object> loadOptions() {

        if(optionsFile == null) {
            //default to use jshint rc
            optionsFile = getProject().getBaseDir() + "/" + JSHINTRC_FILE;
        }

      Map<Object, Object> props = loadProperties(options, optionsFile);
		if (props.size() > 0) {
		   logProperties("custom options", props);
      }

		return props;
	}
	
	private Map<Object, Object> loadGlobals() {
	   Map<Object, Object> props = loadProperties(globals, globalsFile);
      if (props.size() > 0) {
         logProperties("custom globals", props);
      }
		return props;
	}
	
	private Map<Object, Object> loadProperties(String propertiesList, String propertiesFilePath) {
		Properties props = new Properties();
				
		if (propertiesFilePath != null) {			
							
			//default to properties format
			try {
				File propertiesFile = new File(propertiesFilePath);
				if (propertiesFile.exists()) {
					if(propertiesFilePath.endsWith(".json") || propertiesFilePath.endsWith(JSHINTRC_FILE)) {
						ObjectMapper mapper = new ObjectMapper();
						Map<?,?> jsonProps = mapper.readValue(propertiesFile, Map.class);
						props.putAll(jsonProps);
					} else {							
						FileInputStream inStream = new FileInputStream(propertiesFile);
						props.load(inStream);
					}
				} else {
					throw new FileNotFoundException("Could not find properties file at " + propertiesFile.getAbsolutePath());
				}
			} catch (FileNotFoundException e) {
				log(e.getMessage(), Project.MSG_ERR);
			} catch (IOException e) {
            log("Could not load properties file: " + e.getMessage(), Project.MSG_ERR);
			}
		}
	
		if (propertiesList != null) {
			String[] optionsList = propertiesList.split(",");
			for (String option : optionsList) {
				String[] optionPair = option.trim().split("[=:]");
				props.put(optionPair[0].trim(), optionPair[1].trim());
			}
		}

		return props;
	}
	
   private void logProperties(String attrName, Map<Object, Object> props) {
		// log combined properties
		StringBuilder propsBuilder = new StringBuilder();
		for (Object propName : props.keySet()) {
		   Object val = props.get(propName);
			propsBuilder.append(propName + "=" + val + ",");
		}
		if (props.size() > 0) {
			propsBuilder.deleteCharAt(propsBuilder.length() - 1);
		}
		log("Using " + attrName + ":" + propsBuilder);
	}

	private void reportResults(JsHintReport report) {

		for (ReportType reportType : reports) {

			JsHintReporter reporter = null;

			// pick a reporter implementation
            switch(reportType.getType().trim()) {
                case CHECKSTYLE_REPORT:
                    reporter = new CheckstyleReporter(report);
                    break;
                case XML_REPORT:
                    reporter = new XmlJsHintReporter(report);
                    break;
                case JSLINT_XML_REPORT:
                    reporter = new XmlJsLintReporter(report);
                    break;
                default:
                    reporter = new PlainJsHintReporter(report);
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
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}

	/**
	 * By default the ant task will fail the build if jshint finds any errors.
	 * Set this to false for reporting purposes
	 */
	public void setFail(boolean fail) {
		this.fail = fail;
	}

	/**
	 * Specify jshint options in a properties file and point to the file using
	 * the options attribute
	 * 
	 * @param optionsFile  Location of the options properties file
	 */
	public void setOptionsFile(String optionsFile) {
		this.optionsFile = optionsFile;
	}

	/**
	 * Specify jshint options as a commas delimited list i.e. asi=true,
	 * evil=false. These options override any options specified using the
	 * optionsFile attribute
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
	 * A comma separated list of global variables to use when linting all files
	 */
	public void setGlobals(String globals) {
		this.globals = globals;
	}
	
	/**
	 * Specify jshint globals in a properties file and point to the file using
	 * the options attribute
	 * 
	 * @param globalsFile Location of the global properties file
	 */
	public void setGlobalsFile(String globalsFile) {
		this.globalsFile = globalsFile;
	}

	/**
	 * Ant error logging.
	 */
	@Override
   public void error(String msg) {
		log(msg, LogLevel.ERR.getLevel());
	}
}