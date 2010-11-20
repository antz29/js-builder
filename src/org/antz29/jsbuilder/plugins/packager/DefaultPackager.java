package org.antz29.jsbuilder.plugins.packager;

import java.io.File;
import java.util.HashMap;

import org.antz29.jsbuilder.Module;
import org.antz29.jsbuilder.ModuleList;
import org.antz29.jsbuilder.OutputFile;
import org.antz29.jsbuilder.plugins.types.PackagerPlugin;

public class DefaultPackager implements PackagerPlugin {
		
	@Override
	public OutputFile packageModules(ModuleList modules, File path, String name) {		
		OutputFile out = new OutputFile(path,name);
		
		for (Module module : modules) {
			out.addModule(module);			
		}
		
		out.generateContentsFromModules();
		
		return out;
	}

	@Override
	public OutputFile packageModule(Module module, File path, String name) {
		OutputFile out = new OutputFile(path,name);
		out.addModule(module);
		out.generateContentsFromModules();
		return out;
	}

	@Override
	public void setProperties(HashMap<String,String> properties) {}

}
