package org.antz29.jsbuilder.ant;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class PluginElement {
		
	private File jar;
	private String cls;
	private HashSet<PluginPropertyElement> properties = new HashSet<PluginPropertyElement>();
	
	public void setJar(File jar)
	{
		this.jar = jar;
	}
	
	public void setClass(String cls)
	{
		this.cls = cls;
	}
		
	public File getJar()
	{
		return jar;
	}
	
	public String getClassName()
	{
		return cls;
	}
	
	public void addProperty(PluginPropertyElement prop) {
		properties.add(prop);
	}
	
	public HashMap<String,String> getProperties() {
		HashMap<String,String> out = new HashMap<String,String>();
		
		for (PluginPropertyElement prop : properties) {
			out.put(prop.getName(), prop.getValue());
		}
		
		return out;
	}	
}