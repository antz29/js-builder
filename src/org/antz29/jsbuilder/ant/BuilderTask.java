package org.antz29.jsbuilder.ant;

import java.io.File;

import org.antz29.jsbuilder.Builder;
import org.antz29.jsbuilder.Module;
import org.antz29.jsbuilder.Package;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class BuilderTask extends Task {
	
	private Builder builder = new Builder(this.getProject());
	
	public void setDir(File dir)
	{
		builder.setBaseDir(dir);
	}
	
	public void setPattern(String pattern)
	{
		builder.setOutputPattern(pattern);
	}
	
	public void addConfiguredModule(StaticModule module) {
		Package pkg = builder.addPackage(module.getPackage());
		Module mod = pkg.addModule(module.getName(), module.getFile());
		mod.setUnresolvedDeps(module.getDependencies());
		
		this.log("Added static module " + module.getPackage() + ":" + module.getName());
	}
	
	public void addConfiguredSource(SourceElement source) {		
		
		FileSet fileset = new FileSet();
		fileset.setDir(source.getPath());
		fileset.setIncludes(source.getInclude());
	
		DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
		
		File dir = ds.getBasedir();
		String[] filesInSet = ds.getIncludedFiles();

		for (String filename : filesInSet) {
			builder.addFile(new File(dir, filename));
		}
	}
	
	public void addConfiguredPlugin(PluginElement plugin) {
		if (plugin.getJar() != null) {
			builder.addPlugin(plugin.getClassName(),plugin.getJar(),plugin.getProperties());
		}
		else {
			builder.addPlugin(plugin.getClassName(),plugin.getProperties());
		}
	}
	
	public void execute() throws BuildException {			
		builder.build();	
	}
	
}
