package org.antz29.jsbuilder.plugins.types;

import java.io.File;

import org.antz29.jsbuilder.PackageList;

public interface PackageProcessor extends ProcessorPlugin {
	
	void processPackages(PackageList packages, File output_dir);
	
}
