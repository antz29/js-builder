package org.antz29.jsbuilder.plugins.compiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.antz29.jsbuilder.OutputFile;
import org.antz29.jsbuilder.plugins.types.CompilerPlugin;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.WarningLevel;

public class ClosureCompiler implements CompilerPlugin {
		
	private void log(String msg) {
		System.out.println(msg);
	}
	
	public OutputFile compile(OutputFile file) {
		CompilationLevel compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
		
		WarningLevel warningLevel = WarningLevel.QUIET;		
		Compiler.setLoggingLevel(Level.OFF);
		
		CompilerOptions options = new CompilerOptions();
		compilationLevel.setOptionsForCompilationLevel(options);
		warningLevel.setOptionsForWarningLevel(options);
		options.setManageClosureDependencies(false);
		
		Compiler compiler = new Compiler();
		
		JSSourceFile[] externs;
		JSSourceFile[] sources;
		
		try {
			List<JSSourceFile> files = Lists.newLinkedList();
			files.addAll(CommandLineRunner.getDefaultExterns());
			externs = files.toArray(new JSSourceFile[files.size()]);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		{
			List<JSSourceFile> files = Lists.newLinkedList();
			files.add(JSSourceFile.fromCode(file.getFile().getAbsolutePath(),file.getContents()));
			sources = files.toArray(new JSSourceFile[files.size()]);
		}
		
		Result result = compiler.compile(externs, sources, options);
		
		if (result.success) {
			
			OutputFile of = file.copy(file.getName().replace(".js", ".min.js"));
			of.setContents(compiler.toSource());
			of.addFlag(OutputFile.FLAG_COMPILED);

			return of;
		} else {
			log("WARNING: Compilation failed.");
			return null;
		}
	}

	@Override
	public void setProperties(HashMap<String,String> properties) {
		
	}
	
}
