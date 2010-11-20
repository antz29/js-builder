package org.antz29.jsbuilder;

import java.io.File;

public class Package {

	private Builder builder;
	private String name;
	private ModuleList modules = new ModuleList();

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

	public ModuleList getModules() {
		return this.modules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
