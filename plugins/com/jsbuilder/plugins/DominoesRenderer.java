package com.jsbuilder.plugins;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import com.jsbuilder.Builder;
import com.jsbuilder.DefaultRenderer;
import com.jsbuilder.Module;
import com.jsbuilder.Package;
import com.jsbuilder.Renderer;

public class DominoesRenderer extends DefaultRenderer implements Renderer {

	private void renderProdRules(Vector<Module> modules) {

		try {

			StringBuffer rules = new StringBuffer();
			String path = builder.getAntProject().getName() + ".js";
			String mainrule = String.format("dominoes.rule('main','js/%s');\n",
					path);
			rules.append(mainrule);

			for (Module module : modules) {
				String deps = "";
				if (module.totalDeps() > 0) {
					for (Module dep : module.getDeps()) {
						deps = deps + " " + dep.getPackage().getName() + "."
								+ dep.getName();
					}
					deps = "( " + deps + " ) > ";
				}
				String modname = module.getPackage().getName() + "."
						+ module.getName();
				String rule = String.format(
						"dominoes.rule('%s','( main )');\n", modname);
				rules.append(rule);
			}

			String target = builder.getOutput().getAbsolutePath() + "/init.js";

			File tgt = new File(target);
			tgt.createNewFile();
			FileUtils.write(tgt, rules.toString(), "UTF-8");

		} catch (Exception e) {
			builder.getAntProject().log(
					"Failed to render dominues rules due to IOException.");
			e.printStackTrace();
		}
	}

	private void renderDevRules(Vector<Module> modules) {
		try {
			StringBuffer rules = new StringBuffer();

			for (Module module : modules) {
				String deps = "";
				if (module.totalDeps() > 0) {
					for (Module dep : module.getDeps()) {
						deps = deps + " " + dep.getPackage().getName() + "."
								+ dep.getName();
					}
					deps = "( " + deps + " ) > ";
				}
				String modname = module.getPackage().getName() + "." + module.getName();
				String modpath = "js/" + module.getPackage().getName() + "/" + modname + ".js";
				String rule = String.format("dominoes.rule('%s','( %s%s )');\n", modname,deps,modpath);
				rules.append(rule);
			}

			String target = builder.getOutput().getAbsolutePath() + "/init.js";

			File tgt = new File(target);
			tgt.createNewFile();
			FileUtils.write(tgt, rules.toString(), "UTF-8");
		} catch (Exception e) {
			builder.getAntProject().log(
					"Failed to render dominues rules due to IOException.");
			e.printStackTrace();
		}
	}

	@Override
	public void renderRules(Vector<Package> packages, Vector<Module> modules) {
		try {
			switch (builder.getMode()) {
			case Builder.MODE_PROD:
				renderProdRules(modules);
				break;
			case Builder.MODE_DEV:
				renderDevRules(modules);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
