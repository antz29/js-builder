package org.antz29.jsbuilder;

import java.util.Vector;

public interface Renderer {
	
	public void renderPackages(Vector<Package> packages, Vector<Module> modules);
	public void renderRules(Vector<Package> packages, Vector<Module> modules);
	public void setBuilder(Builder builder);
	
}
