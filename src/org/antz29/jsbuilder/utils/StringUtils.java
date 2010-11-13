package org.antz29.jsbuilder.utils;

public class StringUtils {
	
	public static String[] trimArray(String[] strings) {
		for (int i = 0, length = strings.length; i < length; i++) {
			if (strings[i] != null) {
				strings[i] = strings[i].trim();
			}
		}
		return strings;
	}
	
}
