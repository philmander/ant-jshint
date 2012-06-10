package com.philmander.jshint;

import java.io.File;

public class ReportType {

	private File destFile = null;
	
	private String type = null;

	public File getDestFile() {
		return destFile;
	}

	public void setDestFile(File destFile) {
		this.destFile = destFile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
