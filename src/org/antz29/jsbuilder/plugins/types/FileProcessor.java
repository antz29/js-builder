package org.antz29.jsbuilder.plugins.types;

import java.io.File;
import java.util.HashSet;

import org.antz29.jsbuilder.OutputFile;

public interface FileProcessor extends ProcessorPlugin {

	void processFiles(HashSet<OutputFile> output_files, File output_dir);
	
}
