package org.antz29.jsbuilder;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class OutputFile {
	
	public static final int FLAG_COMPILED = 1;
	
	private File fs_path;
	private String name;	
	private File file_container;
	
	private int flags = 0;
	private ModuleList modules = new ModuleList();
	private StringBuffer contents = new StringBuffer();

	public OutputFile copy(File fs_path, String new_name)
	{
		OutputFile out = new OutputFile(fs_path, new_name);
		
		out.modules = modules;
		out.flags = flags;
		out.contents = new StringBuffer(contents.toString());
		
		return out;
	}
	
	public OutputFile copy(String new_name)
	{
		return copy(this.fs_path,new_name);
	}
	
	public OutputFile(File fs_path, String name) {
		this.fs_path = fs_path;
		this.name = name;		
		this.file_container = new File(fs_path,name);
	}

	public void addFlag(int flag)
	{
		this.flags = this.flags | flag;
	}
	
	public void removeFlag(int flag)
	{
		this.flags = this.flags & ~flag;
	}
	
	public boolean hasFlag(int flag)
	{
		return ((this.flags & flag) == flag);
	}
	
	public File getFile()
	{
		return file_container;
	}
	
	public void appendContents(String contents) {
		this.contents.append(contents);
	}
	
	public void setContents(String contents) {
		this.contents = new StringBuffer(contents);
	}
	
	public String getContents() {
		return contents.toString();
	}
	
	public File getFsPath()
	{
		return fs_path;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void addModule(Module module) {
		modules.add(module);
	}
	
	public ModuleList getModules() {
		return modules;
	}
	
	public void generateContentsFromModules()
	{
		this.setContents("");
		for (Module mod : modules) {
			this.appendContents(mod.getRenderedContents());
		}
	}
	
	public void save()
	{
		try {
			file_container.getParentFile().mkdirs();
			FileUtils.write(file_container, contents.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public String toString()
	{
		return file_container.getAbsolutePath() + " : " + contents.toString().length();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file_container == null) ? 0 : file_container.getAbsolutePath().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutputFile other = (OutputFile) obj;
		if (file_container == null) {
			if (other.file_container != null)
				return false;
		} else if (!file_container.getAbsolutePath().equals(other.file_container.getAbsolutePath()))
			return false;
		return true;
	}
	
}
