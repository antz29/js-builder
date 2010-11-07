package com.jsbuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class Builder extends Task {

	public class Module implements Comparable<Module> {

		private Package pkg;
		private String name;
		private File file;
		private Vector<Module> deps = new Vector<Module>();
		private String[] udeps;

		public Module setPackage(Package pkg) {
			this.pkg = pkg;
			return this;
		}

		public Package getPackage() {
			return this.pkg;
		}

		public Module setName(String name) {
			this.name = name;
			return this;
		}

		public Module setUnresolvedDeps(String[] deps) {
			this.udeps = deps;
			return this;
		}

		public String getName() {
			return name;
		}

		public Integer totalDeps() {
			return deps.size();
		}

		public boolean dependsOn(Module module) {
			getProject().log(this + " depends on " + module);
			for (Module dep : deps) {
				getProject().log("\t" + module + " equals " + dep + " = " + module.equals(dep));
				if (module.equals(dep)) return true;
			}
			return false;
		}

		public void resolveDeps() {
			if (udeps == null)
				return;
			for (String dep : udeps) {
				if (!dep.contains(":"))
					dep = this.getPackage().getName() + ":" + dep;
				Module find_module = this.getPackage().getBuilder()
						.findModule(dep);
				if (find_module != null) {
					deps.add(find_module);
				} else {
					getProject().log(
							"Unable to resolve dependency "
									+ this.getPackage().getName() + ":"
									+ this.getName() + " > " + dep);
				}
			}
		}

		public Module setFile(File file) {
			this.file = file;
			return this;
		}

		public File getFile() {
			return this.file;
		}

		public Vector<Module> getDeps()
		{
			return deps;
		}
		
		public String toString()
		{
			return this.getPackage().getName() + ":" + this.getName();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((file == null) ? 0 : file.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Module other = (Module) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (file == null) {
				if (other.file != null)
					return false;
			} else if (!file.equals(other.file))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (pkg == null) {
				if (other.pkg != null)
					return false;
			} else if (!pkg.equals(other.pkg))
				return false;
			return true;
		}

		@Override
		public int compareTo(Module module) {
			
			if (this.equals(module)) return 0;	
			if (this.totalDeps().equals(0) && module.totalDeps().equals(0)) return 0;
			
			if (this.dependsOn(module)) return 1;
			if (module.dependsOn(this)) return -1;
			
			return (this.totalDeps() - module.totalDeps());
		}

		private Builder getOuterType() {
			return Builder.this;
		}
	}

	public class Package {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Package other = (Package) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private Builder builder;
		private String name;
		private Vector<Module> modules = new Vector<Builder.Module>();

		public Package setBuilder(Builder builder) {
			this.builder = builder;
			return this;
		}

		public Builder getBuilder() {
			return this.builder;
		}

		public Package setName(String name) {
			this.name = name;
			return this;
		}

		public String getName() {
			return name;
		}

		public Module addModule(String name, File file) {
			Module new_module = new Module().setName(name).setFile(file)
					.setPackage(this);

			int find_module = modules.indexOf(new_module);

			if (find_module == -1) {
				this.modules.add(new_module);
				return new_module;
			}

			return modules.get(find_module);
		}

		public Vector<Module> getModules() {
			return this.modules;
		}

		private Builder getOuterType() {
			return Builder.this;
		}

	}

	private Vector<FileSet> filesets = new Vector<FileSet>();
	private Vector<Package> packages = new Vector<Package>();

	private String mode = "prod";
	private File output;

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public void addFileSet(FileSet fileset) {
		if (!filesets.contains(fileset))
			filesets.add(fileset);
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

		Vector<Module> mods = pkg.getModules();

		Module found = null;
		for (Module module : mods) {
			if (module.getName().equals(mod[1])) {
				found = module;
			}
		}
		return found;
	}

	private Vector<File> getFiles() {
		Vector<File> out = new Vector<File>();

		for (FileSet fileset : filesets) {
			DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
			File dir = ds.getBasedir();
			String[] filesInSet = ds.getIncludedFiles();

			for (String filename : filesInSet) {
				File file = new File(dir, filename);
				if (!out.contains(file))
					out.add(file);
			}
		}

		return out;
	}

	private static String[] trim(String[] strings) {
		for (int i = 0, length = strings.length; i < length; i++) {
			if (strings[i] != null) {
				strings[i] = strings[i].trim();
			}
		}
		return strings;
	}

	private Hashtable<String, String[]> parseFile(File file) {

		Hashtable<String, String[]> tokens = new Hashtable<String, String[]>();
		Scanner scanner;

		Pattern pattern = Pattern
				.compile("#(PACKAGE|MODULE|DEPENDS):([0-9a-z.,:!\040]*)");

		try {
			scanner = new Scanner(new FileInputStream(file), "UTF-8");
		} catch (FileNotFoundException e) {
			return null;
		}

		try {
			String next_token = scanner.findWithinHorizon(pattern, 1000);
			while (next_token != null) {
				MatchResult match = scanner.match();

				String[] value = trim(match.group(2).split(","));
				String token = match.group(1).trim();
				tokens.put(token, value);

				next_token = scanner.findWithinHorizon(pattern, 1000);
			}
			return tokens;
		} finally {
			scanner.close();
		}
	}

	private Package addPackage(String name) {
		Package new_package = new Package().setName(name).setBuilder(this);

		int find_package = packages.indexOf(new_package);

		if (find_package == -1) {
			packages.add(new_package);
			return new_package;
		}

		return packages.get(find_package);
	}

	private void parsePackages(Vector<File> files) {
		for (File file : files) {
			Hashtable<String, String[]> tokens = parseFile(file);

			getProject().log("\nFile: " + file.getName());

			if (tokens.get("MODULE") == null) {
				getProject().log("No MODULE token.");
				continue;
			}

			String module_name = tokens.get("MODULE")[0];
			String package_name = (tokens.get("PACKAGE") != null) ? tokens
					.get("PACKAGE")[0] : getProject().getName();

			getProject().log("Package: " + package_name);
			getProject().log("Module: " + module_name);

			Package pkg = addPackage(package_name);
			Module mod = pkg.addModule(module_name, file);

			String[] deps = tokens.get("DEPENDS");
			if (deps != null) {
				mod.setUnresolvedDeps(deps);
			}
		}
	}

	private Vector<Module> getLoadOrder() {
		Vector<Module> order = new Vector<Module>();

		for (Package pkg : packages) {
			for (Module mod : pkg.getModules()) {
				mod.resolveDeps();

				order.add(mod);
			}
		}

		return order;
	}

	public void execute() throws BuildException {

		Vector<File> files = getFiles();

		getProject().log("Mode: " + mode);
		getProject().log("Output: " + output.getAbsolutePath());
		getProject().log("Processing " + files.size() + " file(s)");
		getProject().log("\n");

		parsePackages(files);

		Vector<Module> order = getLoadOrder();
		Collections.sort(order);

		Vector<Module> order_test = new Vector<Module>();
		
		for (Module mod : order) {
			order_test.add(mod);
			getProject().log(
					mod + ":" + mod.totalDeps() + " - " + mod.getDeps().toString());
			for (Module dep : mod.getDeps()) {
				if (order_test.contains(dep)) {
					getProject().log("\tFOUND DEP: " + dep);
				}
				else {
					getProject().log("\tNOT FOUND DEP: " + dep);
				}
			}
		}

	}
}
