package org.antz29.jsbuilder.plugins.processor;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import org.antz29.jsbuilder.Module;
import org.antz29.jsbuilder.OutputFile;
import org.antz29.jsbuilder.plugins.types.FileProcessor;

public class DominoesProcessor implements FileProcessor {
		
	private HashSet<OutputFile> output_files;
	
	private OutputFile rules;
	private OutputFile rules_min;	
	
	private String rules_file;
	private String js_root;
	
	private void log(String msg) {
		System.out.println(msg);
	}

	@Override
	public void processFiles(HashSet<OutputFile> output_files, File output_dir) {
		
		log("Generating dominoes rules...");
		
		this.output_files = output_files;
	
		// NOT COMPILED
		rules = new OutputFile(output_dir,rules_file);
		for (OutputFile of : this.output_files) {
			if (of.hasFlag(OutputFile.FLAG_COMPILED)) continue;
			processFile(of,rules);
		}
		
		// COMPILED
		rules_min = new OutputFile(output_dir,rules_file.replace(".js", ".min.js"));
		for (OutputFile of : this.output_files) {
			if (!of.hasFlag(OutputFile.FLAG_COMPILED)) continue;
			processFile(of,rules_min);
		}
		
		if (rules.getContents().length() > 0) {
			output_files.add(rules);
		}
		if (rules_min.getContents().length() > 0) {
			output_files.add(rules_min);
		}
	}
	
	private void processFile(OutputFile file, OutputFile rules_file) {		
		for (Module module : file.getModules()) {
			log("Processing " + module);
			rules_file.appendContents(generateRule(module,file));
		}		
	}
	
	private String generateRule(Module module, OutputFile file) {
		
		String deps = "";
		if (module.totalDeps() > 0) {
			for (Module dep : module.getDeps()) {
				deps = deps + " " + dep.getPackage().getName() + "."
						+ dep.getName();
			}
			deps = "( " + deps + " ) > ";
		}
		
		String modname = module.getPackage().getName() + "." + module.getName();
			
		String modpath = js_root + file.getName();
		
		return String.format("dominoes.rule('%s','( %s%s )');\n", modname,deps,modpath);
	}

	@Override
	public void setProperties(HashMap<String,String> properties) {		
		js_root = (properties.get("js_root") != null) ? properties.get("js_root") : "js/"; 
		rules_file = (properties.get("rules_file") != null) ? properties.get("rules_file") : "init.js"; 
	}
}
