package org.antz29.jsbuilder.ant;

import java.io.File;

import org.antz29.jsbuilder.utils.StringUtils;

public class StaticModule {
	
	private String pkg;
	private String name;
	private File file;
	private String[] deps;
	
	public void setPackage(String pkg)
	{		
		this.pkg = pkg;
	}
	
	public void setName(String name)
	{		
		this.name = name;
	}
	
	public void setFile(File file)
	{		
		this.file = file;
	}
	
	public void setDepends(String depends)
	{		
		this.deps = StringUtils.trimArray(depends.split(","));
	}

	public String getPackage() {
		return pkg;
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	public String[] getDependencies() {
		return deps;
	}	

}
