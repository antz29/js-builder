package org.antz29.jsbuilder.utils;

import java.util.Collection;

public class StringUtils {

	public static String[] trimArray(String[] strings) {
		for (int i = 0, length = strings.length; i < length; i++) {
			if (strings[i] != null) {
				strings[i] = strings[i].trim();
			}
		}
		return strings;
	}

	public static String implode(Object[] array,String separator) {
		if (array.length == 0) {
			return "";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(array[0]);
			for (int i = 1; i < array.length; i++) {
				sb.append(separator);
				sb.append(array[i]);
			}
			return sb.toString();
		}
	}
	
	public static String implode(Collection<String> collection,String separator) {
		return implode(collection.toArray(),separator);
	}

}
