package org.antz29.jsbuilder.plugins;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.antz29.jsbuilder.plugins.types.Plugin;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;

public class PluginLoader {

	private Project project;
	private HashMap<Class<? extends Plugin>,Class<? extends Plugin>> defaults = new HashMap<Class<? extends Plugin>,Class<? extends Plugin>>();
	private HashSet<Class<? extends Plugin>> types = new HashSet<Class<? extends Plugin>>();
	private HashMap<String,HashMap<String,String>> properties = new HashMap<String,HashMap<String,String>>();
	private LinkedHashMap<Class<? extends Plugin>, HashSet<Class<? extends Plugin>>> plugins = new LinkedHashMap<Class<? extends Plugin>, HashSet<Class<? extends Plugin>>>();

	public PluginLoader(Project project) {
		this.project = project;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPluginProxy(Class<?> plugin, Class<T> type, ClassLoader cl) {
												      
		try {
			cl = (cl == null) ? PluginLoader.class.getClassLoader() : cl;
			return (T) PluginProxy.getInstance().createProxy(plugin.newInstance(), plugin.getInterfaces(), cl);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Plugin> T[] get(Class<T> type) {

		HashSet<Class<? extends Plugin>> plugins = this.plugins.get(type);
		
		if (plugins == null) {
			Class<? extends Plugin> def = defaults.get(type);
			
			if (def != null) {
				plugins = new HashSet<Class<? extends Plugin>>();
				plugins.add(def);
			}
			else {
				T[] out;
				out = (T[]) Array.newInstance(type, 0);
				return out;
			}
		}
		
		T[] out = (T[]) Array.newInstance(type, plugins.size());

		int cnt = 0;
		for (Class<?> plugin : plugins) {
			T plg = (T) PluginLoader.getPluginProxy(plugin, type, plugin.getClassLoader());	
		
			plg.setProperties(this.getProperties(plugin.getName()));
			
			if (plg != null) {
				out[cnt] = plg;
				cnt++;
			}
		}

		out = (T[]) Arrays.asList(out).toArray();

		return out;
	}

	public void setProperties(String cls, HashMap<String,String> properties)
	{
		this.properties.put(cls, properties);
	}
	
	public HashMap<String,String> getProperties(String cls)
	{
		return properties.get(cls);
	}
	
	public void add(String cls) {
		load(cls, null);
	}

	public void add(String cls, File jar) {
		load(cls, jar);
	}

	public <T extends Plugin> void regester(Class<T> cls,Class<? extends T> def) {
		types.add(cls);
		defaults.put(cls, def);
	}
	
	public <T extends Plugin> void regester(Class<T> cls) {
		types.add(cls);		
	}

	private ClassLoader getClassLoader(String cls, File jar) {
		Path cp = new Path(project);
		FileList fl = new FileList();
		fl.setFiles(jar.getAbsolutePath());
		cp.addFilelist(fl);
		return new AntClassLoader(this.getClass().getClassLoader(), project,
				cp, false);
	}

	private ClassLoader getClassLoader(String cls) {
		return PluginLoader.class.getClassLoader();
	}

	private <T extends Plugin> Class<? extends Plugin> getValidType(Class<T> cls) {
		for (Class<? extends Plugin> type : types) {
			if (type.isAssignableFrom(cls)) {
				return type;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void load(String cls, File jar) {
		Class<? extends Plugin> plugin = null;

		ClassLoader loader = null;
		if (jar == null) {
			loader = getClassLoader(cls);
		} else {
			loader = getClassLoader(cls, jar);
		}

		try {
			plugin = (Class<? extends Plugin>) loader.loadClass(cls);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Class<? extends Plugin> type = getValidType(plugin);

		if (!plugins.containsKey(type)) {
			plugins.put(type, new HashSet<Class<? extends Plugin>>());
		}

		plugins.get(type).add(plugin);

	}
}
