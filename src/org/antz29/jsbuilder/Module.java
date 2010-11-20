package org.antz29.jsbuilder;

import java.io.File;
import java.util.ArrayList;

public class Module implements Comparable<Module> {

	private Package pkg;
	private String name;
	private File file;
	private ArrayList<Module> deps = new ArrayList<Module>();
	private String[] udeps;
	
	private String contents = "";

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
		for (Module dep : deps) {
			if (module.equals(dep))
				return true;
		}
		return false;
	}

	public void resolveDeps() {
		if (udeps == null)
			return;
		for (String dep : udeps) {
			if (!dep.contains(":"))
				dep = this.getPackage().getName() + ":" + dep;
			Module find_module = this.getPackage().getBuilder().findModule(dep);
			if (find_module != null) {
				deps.add(find_module);
			} else {
				System.out.println("WARNING: Unable to resolve dependency "
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

	public ArrayList<Module> getDeps() {
		return deps;
	}

	public String toString() {
		return this.getPackage().getName() + ":" + this.getName();
	}

	public String getRenderedContents()
	{
		return contents;
	}
	
	public void setRenderedContents(String contents)
	{
		this.contents = contents;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (this.equals(module))
			return 0;
		if (this.totalDeps().equals(0) && module.totalDeps().equals(0))
			return 0;
		return (this.totalDeps() - module.totalDeps());
	}
}
