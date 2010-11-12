package org.antz29.jsbuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.MessageFormatter;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.ant.AntErrorManager;

public class DefaultRenderer implements Renderer {

	protected Builder builder;
		
	protected final void compileFile(File source,File target) throws IOException
	{
		CompilationLevel compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
		WarningLevel warningLevel = WarningLevel.QUIET;
		
		Compiler.setLoggingLevel(Level.OFF);
		
		CompilerOptions options = new CompilerOptions();
		compilationLevel.setOptionsForCompilationLevel(options);
		warningLevel.setOptionsForWarningLevel(options);
		options.setManageClosureDependencies(false);
		
		Compiler compiler = new Compiler();
		MessageFormatter formatter = options.errorFormat.toFormatter(compiler,false);
		AntErrorManager errorManager = new AntErrorManager(formatter, builder);
		compiler.setErrorManager(errorManager);
		
		JSSourceFile[] externs;
		JSSourceFile[] sources;
		
		try {
			List<JSSourceFile> files = Lists.newLinkedList();
			files.addAll(CommandLineRunner.getDefaultExterns());
			externs = files.toArray(new JSSourceFile[files.size()]);
		}
		catch (IOException e) {
			throw e;
		}
		
		{
			List<JSSourceFile> files = Lists.newLinkedList();
			files.add(JSSourceFile.fromFile(source));
			sources = files.toArray(new JSSourceFile[files.size()]);
		}
		
		Result result = compiler.compile(externs, sources, options);
		
		if (result.success) {
			try {
				FileUtils.write(target, compiler.toSource(), "UTF-8");
			} catch (IOException e) {
				builder.getAntProject().log("WARNING: Failed to write compiled source to file.");
				e.printStackTrace();
			}
		} else {		
			throw new BuildException("WARNING: Compilation failed.");
		}
	}

	protected void writeAndCompile(String source, String target_path)
	{
		boolean compile = true;
		writeAndCompile(source, target_path, compile);
	}
	
	protected void writeAndCompile(String source, String target_path, boolean compile)
	{
		UUID uuid = UUID.randomUUID();
		try {
			File source_file = File.createTempFile(uuid.toString(), ".js");
			FileUtils.write(source_file, source, "UTF-8");
			writeAndCompile(source_file, target_path, compile);
			source_file.delete();			
		} catch (IOException e) {
			builder.getAntProject().log("WARNING: Failed writing to file '" + target_path + "' due to IOException.");
			e.printStackTrace();			
		}
	}	
	
	protected void writeAndCompile(File source, String target_path)
	{
		boolean compile = true;
		writeAndCompile(source, target_path, compile);
	}
	
	protected void writeAndCompile(File source, String target_path, boolean compile)
	{						 
		try {
			if (compile && builder.getCompile()) {
				target_path = target_path + ".min.js";
				File target = new File(target_path);
				compileFile(source,target);
			}
			else {
				target_path = target_path + ".js";
				File target = new File(target_path);
				FileUtils.copyFile(source, target);
			}
		} catch (IOException e) {
			builder.getAntProject().log("WARNING: Failed writing to file '" + target_path + "' due to IOException.");
			e.printStackTrace();
		}
	}
	
	private void renderPackageDev(Package pkg)
	{
		String path = builder.getOutput().getAbsolutePath() + "/" + pkg.getName();
		File pkgdir = new File(path);
		pkgdir.mkdir();
		
		for (Module module : pkg.getModules()) {
			String target_path = pkgdir.getAbsolutePath() + "/" + module.getName();
			writeAndCompile(module.getFile(),target_path);
		}
	}
	
	private void renderPackageProd(Vector<Module> modules)
	{
		String target_path = builder.getOutput().getAbsolutePath() + "/" + builder.getAntProject().getName();

		try {
						
			StringBuffer sb = new StringBuffer();
			for (Module module : modules) {
				String data;
				data = FileUtils.readFileToString(module.getFile(), "UTF-8") + "\n\n";
				sb.append(data);
			}
			
			writeAndCompile(sb.toString(),target_path);
			
		} catch (IOException e) {
			builder.getAntProject().log("WARNING: Failed to render package due to IOException.");
			e.printStackTrace();
		}	
	}
	
	@Override
	public void renderPackages(Vector<Package> packages, Vector<Module> modules) {
        switch (builder.getMode()) {
        	case Builder.MODE_DEV:
        		for (Package pkg : packages) {
        			renderPackageDev(pkg);
        		}
        		break;
        	case Builder.MODE_PROD:
        		renderPackageProd(modules);
        		break;
        }
	}

	@Override
	public void setBuilder(Builder builder) {
		this.builder = builder;
	}

	@Override
	public void renderRules(Vector<Package> packages, Vector<Module> modules) {
		
	}

}
