package org.antz29.jsbuilder.plugins.types;

import java.io.File;

import org.antz29.jsbuilder.Module;
import org.antz29.jsbuilder.ModuleList;
import org.antz29.jsbuilder.OutputFile;

public interface PackagerPlugin extends Plugin {
	
	boolean isDependencySafe();	
	OutputFile packageModules(ModuleList modules, File path, String name);
	OutputFile packageModule(Module module, File path, String name);
	
}
