package com.philmander.jshint;

/**
 * Standard interface for logging
 * @author Phil Mander
 *
 */
public interface JsHintLogger {
	
	public void log(String message);
	
	public void error(String message);
}
