package org.antz29.jsbuilder.plugins.renderer;

import java.io.IOException;
import java.util.HashMap;

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
		
		module.setRenderedContents(contents);
		
	}

	@Override
	public void setProperties(HashMap<String,String> properties) {}

}
