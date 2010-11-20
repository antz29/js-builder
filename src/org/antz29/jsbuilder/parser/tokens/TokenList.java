package org.antz29.jsbuilder.parser.tokens;

import java.util.LinkedHashSet;

import org.antz29.jsbuilder.utils.StringUtils;

public class TokenList extends LinkedHashSet<Token> {

	private static final long serialVersionUID = -3999636778230578127L;
	
	public Token get(String name)
	{
		for (Token token : this) {
			if (token.getName().equals(name)) return token;
		}
		
		return null;
	}
	
	public String toString()
	{
		return StringUtils.implode(this.toArray(), ",");
	}
	
}
