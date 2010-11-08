package com.jsbuilder;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;


public class DefaultRenderer implements Renderer {

	protected Builder builder;
	
	private void renderPackageDev(Package pkg)
	{
		String path = builder.getOutput().getAbsolutePath() + "/" + pkg.getName();
		File pkgdir = new File(path);
		pkgdir.mkdir();
		
		for (Module module : pkg.getModules()) {
			String target_path = pkgdir.getAbsolutePath() + "/" + module.getName() + ".js";
			File target = new File(target_path);
			try {
				FileUtils.copyFile(module.getFile(), target);
			} catch (IOException e) {
				builder.getAntProject().log("Failed to copy module due to IOException.");
				e.printStackTrace();
			}
		}
	}
	
	private void renderPackageProd(Vector<Module> modules)
	{
		String path = builder.getOutput().getAbsolutePath() + "/" + builder.getAntProject().getName() + ".js";
		File pkgfile = new File(path);
		try {
			
			pkgfile.createNewFile();
			
			StringBuffer sb = new StringBuffer();
			for (Module module : modules) {
				String data;
				data = FileUtils.readFileToString(module.getFile(), "UTF-8") + "\n\n";
				sb.append(data);
			}
			
			FileUtils.write(pkgfile, sb.toString(), "UTF-8");
			
		} catch (IOException e) {
			builder.getAntProject().log("Failed to render package due to IOException.");
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
