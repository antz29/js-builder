package org.antz29.jsbuilder.plugins.renderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antz29.jsbuilder.Module;
import org.antz29.jsbuilder.plugins.types.RendererPlugin;
import org.apache.commons.io.FileUtils;

public class DefaultRenderer implements RendererPlugin {
	
	@Override
	public void render(Module module) {
		
		String contents;
		
		try {
			contents = FileUtils.readFileToString(module.getFile());
		} catch (IOException e) {
			contents = "";
			e.printStackTrace();
		}
		
		// strip out JSBuilder comments
		Pattern rx = Pattern.compile("^//\\s*#[A-Z]+:\\s*[a-z\\-\\,\\_]+$");
		Matcher matcher = rx.matcher(contents);
		contents = matcher.replaceAll("");
		
		contents = "\n// " + module.getFile().getName() + "\n" + contents;
		
		module.setRenderedContents(contents);
		
	}

	@Override
	public void setProperties(HashMap<String,String> properties) {}

}
