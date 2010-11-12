package org.antz29.jsbuilder.plugins;

import java.util.Vector;

import org.antz29.jsbuilder.Builder;
import org.antz29.jsbuilder.DefaultRenderer;
import org.antz29.jsbuilder.Module;
import org.antz29.jsbuilder.Package;
import org.antz29.jsbuilder.Renderer;


public class DominoesRenderer extends DefaultRenderer implements Renderer {

	private void renderProdRules(Vector<Module> modules) {
		StringBuffer rules = new StringBuffer();
		String ext = builder.getCompile() ? ".min.js" : ".js";
		String path = builder.getAntProject().getName() + ext;
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

		String target_path = builder.getOutput().getAbsolutePath() + "/init";
		writeAndCompile(rules.toString(),target_path, false);
	}

	private void renderDevRules(Vector<Module> modules) {
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
			String ext = builder.getCompile() ? ".min.js" : ".js";
			String modpath = "js/" + module.getPackage().getName() + "/" + modname + ext;
			String rule = String.format("dominoes.rule('%s','( %s%s )');\n", modname,deps,modpath);
			rules.append(rule);
		}

		String target_path = builder.getOutput().getAbsolutePath() + "/init";
		writeAndCompile(rules.toString(),target_path, false);
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
