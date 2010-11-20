package org.antz29.jsbuilder.ant;

import java.io.File;

public class SourceElement {
	
	private File path; 
	private String include = "**/*.js";
	
	public void setPath(File path) {
		this.path = path;
	}
	
	public void setInclude(String include) {
		this.include = include;
	}

	public File getPath() {
		return path;
	}

	public String getInclude() {
		return include;
	}
	
	public String toString()
	{
		return this.path.getAbsolutePath() + " : " + this.include;
	}
}
