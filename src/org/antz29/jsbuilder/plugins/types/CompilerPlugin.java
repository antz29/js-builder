package org.antz29.jsbuilder.plugins.types;

import org.antz29.jsbuilder.OutputFile;

public interface CompilerPlugin extends Plugin {

	public OutputFile compile(OutputFile file);
	
}
