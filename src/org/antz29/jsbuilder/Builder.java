package org.antz29.jsbuilder;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import org.antz29.jsbuilder.parser.ParsedFile;
import org.antz29.jsbuilder.parser.tokens.TokenList;
import org.antz29.jsbuilder.plugins.PluginLoader;
import org.antz29.jsbuilder.plugins.compiler.ClosureCompiler;
import org.antz29.jsbuilder.plugins.packager.DefaultPackager;
import org.antz29.jsbuilder.plugins.renderer.DefaultRenderer;
import org.antz29.jsbuilder.plugins.types.CompilerPlugin;
import org.antz29.jsbuilder.plugins.types.FileProcessor;
import org.antz29.jsbuilder.plugins.types.ModuleProcessor;
import org.antz29.jsbuilder.plugins.types.PackageProcessor;
import org.antz29.jsbuilder.plugins.types.PackagerPlugin;
import org.antz29.jsbuilder.plugins.types.ProcessorPlugin;
import org.antz29.jsbuilder.plugins.types.RendererPlugin;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class Builder {
	
	private PluginLoader plugins;
	
	private ModuleList modules = new ModuleList();
	private PackageList packages = new PackageList();
	private HashSet<OutputFile> output_files = new HashSet<OutputFile>();
	
	private File base_dir;
	private String output_pattern;
	
	private static void log(Object msg) {
		System.out.println(msg.toString());
	}
	
	public Builder(Project project) {
		plugins = new PluginLoader(project);
		plugins.regester(RendererPlugin.class,DefaultRenderer.class);
		plugins.regester(PackagerPlugin.class,DefaultPackager.class);
		plugins.regester(CompilerPlugin.class,ClosureCompiler.class);
		plugins.regester(ProcessorPlugin.class);
	}
	
	public void addPlugin(String cls, HashMap<String,String> properties) {
		plugins.add(cls);
		plugins.setProperties(cls, properties);
	}
	
	public void addPlugin(String cls, File jar, HashMap<String,String> properties) {
		plugins.add(cls,jar);
		plugins.setProperties(cls,properties);
	}

	public void setBaseDir(File base) {
		this.base_dir = base;
	}
	
	public void setOutputPattern(String pattern) {
		this.output_pattern = pattern;
	}
	
	public void registerModule(Module mod) {
		log("Registered module " + mod);
		modules.add(mod);
	}
	
	public void addFile(File file) {
		ParsedFile pfile = new ParsedFile(file);
		addFile(pfile);
	}
	
	public void addFile(ParsedFile file) {
		TokenList tokens = file.getTokens();
		
		if (tokens.get("MODULE") == null) {
			log("WARNING: " + file.getName() + " has no MODULE token. Ignoring file.");
			return;
		}

		String module_name = (String) tokens.get("MODULE").getValue();
		String package_name = (tokens.get("PACKAGE") != null) ? (String) tokens.get("PACKAGE").getValue() : "default-package";
		Package pkg = addPackage(package_name);
		Module mod = pkg.addModule(module_name, file);
		String[] deps = (tokens.get("DEPENDS") != null) ? tokens.get("DEPENDS").getArrayValue() : new String[0];
		if (deps != null) {
			mod.setUnresolvedDeps(deps);
		}
		registerModule(mod);
	}
	
	public Package findPackage(String search) {
		Package found = null;
		for (Package pkg : packages) {
			if (pkg.getName().equals(search)) {
				found = pkg;
			}
		}
		return found;
	}

	public Module findModule(String search) {
		String[] mod = search.split(":");

		if (mod.length != 2)
			return null;

		Package pkg;
		pkg = findPackage(mod[0]);

		if (pkg == null)
			return null;

		ModuleList mods = pkg.getModules();

		Module found = null;
		for (Module module : mods) {
			if (module.getName().equals(mod[1])) {
				found = module;
			}
		}
		return found;
	}

	public Package addPackage(String name) {
		Package new_package = new Package().setName(name).setBuilder(this);

		int find_package = packages.indexOf(new_package);

		if (find_package == -1) {
			packages.add(new_package);
			return new_package;
		}

		return packages.get(find_package);
	} 

	private void resolveDependencies()
	{
		log("\nResolving dependencies...");		
		modules.sort();
	}
	
	private void renderModules()
	{
		log("\nRendering " + modules.size() + " modules...");
		
		RendererPlugin[] renderers = plugins.get(RendererPlugin.class);
		if (renderers.length > 1) {
			log("WARNING: Multiple module renderer plugins have been defined, only using the first definition.");
		}
				
		for (Module mod : modules) {
			log("Rendering " + mod);
			renderers[0].render(mod);
		}
	}
	
	private void packageFiles()
	{
		log("\nPackaging files...");
		
		PackagerPlugin[] packagers = plugins.get(PackagerPlugin.class);
		
		if (packagers.length > 1) {
			log("WARNING: Multiple module packager plugins have been defined, only using the first definition.");
		}
		
		PackagerPlugin packager = packagers[0];
		
		if (output_pattern.contains("{PACKAGE}") || output_pattern.contains("{MODULE}")) {
			
			if (!output_pattern.contains("{MODULE}")) {
				packagePackages(packager);
				return;
			}
			
			packageModules(packager);
			return;
		}
		
		packageToFile(packager);
	}
	
	private void packageToFile(PackagerPlugin packager)
	{	
		String base_path = output_pattern.replace(".min.js", ".js");
		
		if (!output_pattern.endsWith(".js")) {
			log("WARNING: You are packaging to just one file but you have not specified the name defaulting to 'output.js'");
			output_files.add(packager.packageModules(modules, this.base_dir, "output.js"));
		}
		else {
			output_files.add(packager.packageModules(modules, this.base_dir,base_path));
		}
	}
	
	private void packagePackages(PackagerPlugin packager)
	{
		String base_path = output_pattern.replace(".min.js", ".js");

		boolean usePackageName = !base_path.endsWith(".js");
		
		for (Package pkg : packages) {
			String path = base_path.replace("{PACKAGE}", pkg.getName());			
			if (usePackageName) {
				output_files.add(packager.packageModules(pkg.getModules(),this.base_dir,path + pkg.getName() + ".js"));				
			}
			else {				
				output_files.add(packager.packageModules(pkg.getModules(),this.base_dir,path));
			}
		}		
	}
	
	private void packageModules(PackagerPlugin packager)
	{
		String base_path = output_pattern.replace(".min.js", ".js");
		
		boolean useModuleName = !base_path.endsWith(".js");

		for (Package pkg : packages) {
			for (Module mod : pkg.getModules()) {
				String path = base_path.replace("{MODULE}", mod.getName()).replace("{PACKAGE}", pkg.getName());		
				
				if (useModuleName) {
					output_files.add(packager.packageModule(mod,this.base_dir,path + mod.getName() + ".js"));				
				}
				else {
					output_files.add(packager.packageModule(mod,this.base_dir,path));
				}	
			}
		}
	}
	
	private void compileFiles() {
		log("\nCompiling files...");
		
		CompilerPlugin[] compilers = plugins.get(CompilerPlugin.class);

		if (compilers.length > 1) {
			log("WARNING: Multiple module compiler plugins have been defined, only using the first definition.");
		}
		
		CompilerPlugin compiler = compilers[0];
		
		HashSet<OutputFile> compiled = new HashSet<OutputFile>();		
		for (OutputFile of : output_files) {			
			compiled.add(compiler.compile(of));
		}		
		
		output_files.addAll(compiled);
	}
	
	private void process()
	{	
		ProcessorPlugin[] processors = plugins.get(ProcessorPlugin.class);
		
		if (processors.length == 0) return;
		
		for (ProcessorPlugin processor : processors) {
			if (processor instanceof ModuleProcessor) {
				log("Processing modules...");
				ModuleProcessor.class.cast(processor).processModules(modules,this.base_dir);
			}
			if (processor instanceof PackageProcessor) {
				log("Processing packages...");
				PackageProcessor.class.cast(processor).processPackages(packages,this.base_dir);
			}
			if (processor instanceof FileProcessor) {
				log("Processing files...");
				FileProcessor.class.cast(processor).processFiles(output_files,this.base_dir);
			}
		}
	}
	
	// RenderModules -> PackageFiles -> [CompileFiles] -> [ProcessFiles] -> WriteData
	public void build() throws BuildException {

		resolveDependencies();
		renderModules();
		packageFiles();
	
		boolean compile = output_pattern.endsWith(".min.js");
		if (compile) compileFiles();

		process();

		log("\nWriting files to disk...");
		for (OutputFile of : output_files) {
			log(of);
			of.save();
		}
	}
}
