package org.antz29.jsbuilder;

import java.util.ArrayList;
import java.util.Collections;

public class ModuleList extends ArrayList<Module> {

	private static final long serialVersionUID = 7621902711688116624L;
	private boolean resolved = false;
	
	private void log(Object msg) {
		System.out.println(msg.toString());
	}
	
	public boolean add(Module mod)
	{
		if (!this.contains(mod)) return super.add(mod);
		return false;
	}
	
	public void resolveAllDependencies()
	{
		for (Module mod : this) {
			mod.resolveDeps();
		}		
		resolved = true;
	}
	
	private ArrayList<Module> testLoadOrder() {
		ArrayList<Module> order_test = new ArrayList<Module>();
		ArrayList<Module> bad_order = new ArrayList<Module>();

		for (Module mod : this) {
			order_test.add(mod);
			for (Module dep : mod.getDeps()) {
				if (!order_test.contains(dep)) {
					bad_order.add(mod);
				}
			}
		}

		return bad_order;
	}
	
	private Integer getCorrectPosition(Module mod) {
		ArrayList<Integer> dep_locs = new ArrayList<Integer>();
		ArrayList<Module> deps = mod.getDeps();

		for (Module dep : deps) {
			int loc = this.indexOf(dep);
			if (loc == -1) {
				log("WARNING: " + mod + " has unresolvable dependency: " + dep);
			}
			dep_locs.add(loc);
		}

		Collections.sort(dep_locs);
		Collections.reverse(dep_locs);

		return dep_locs.get(0);
	}

	private void fixLoadOrder(ArrayList<Module> bad_order) {
		
		for (Module mod : bad_order) {
			int location = this.indexOf(mod);
			this.remove(location);
			int new_location = getCorrectPosition(mod);
			this.add((new_location + 1), mod);
		}
		
	}
	
	public void sort()
	{
		if (!resolved) this.resolveAllDependencies();
		
		Collections.sort(this);
			
		ArrayList<Module> bad_order = testLoadOrder();
		
		int sanity = 0;
		
		while (bad_order.size() > 0 && sanity < 10) {
			sanity++;
			fixLoadOrder(bad_order);
			bad_order = testLoadOrder();
		}
		
		if (bad_order.size() > 0) {
			log("WARNING: Failed to resolve dependencies for all modules (possible circular dependency?\n" + "These modules have problems: " + bad_order);
		}		
	}

}
