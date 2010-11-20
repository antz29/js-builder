package org.antz29.jsbuilder.plugins.types;

import java.io.File;

import org.antz29.jsbuilder.ModuleList;

public interface ModuleProcessor extends ProcessorPlugin {

	void processModules(ModuleList modules, File output_dir);
	
}
