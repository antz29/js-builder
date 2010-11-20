package org.antz29.jsbuilder.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.antz29.jsbuilder.parser.tokens.Token;
import org.antz29.jsbuilder.parser.tokens.TokenList;
import org.antz29.jsbuilder.utils.StringUtils;

public class ParsedFile extends File {
	
	private static final long serialVersionUID = 5792285496175564996L;
	
	private TokenList tokens;
	private LinkedHashMap<String, Integer> spec = new LinkedHashMap<String, Integer>();
	
	protected void addToken(String name, int type)
	{
		spec.put(name, type);
	}
	
	public ParsedFile(String parent, String child) {
		super(parent, child);
		init();
		parse();
	}
	
	public ParsedFile(File parent, String child) {
		super(parent, child);
		init();
		parse();
	}
	
	public ParsedFile(File file) {
		super(file.getParentFile(),file.getName());
		init();
		parse();
	}
	
	public ParsedFile(String file) {
		super(file);
		init();
		parse();
	}
		
	private void init()
	{
		this.addToken("PACKAGE", Token.STRING_TYPE);
		this.addToken("MODULE", Token.STRING_TYPE);
		this.addToken("DEPENDS", Token.ARRAY_TYPE);
	}
	
	private void parse() {

		tokens = new TokenList();
		
		Scanner scanner;
		
		Pattern pattern = Pattern.compile("#("+ StringUtils.implode(spec.keySet(),"|") +"):([-._:A-Za-z0-9,\040]*)");

		try {
			scanner = new Scanner(new FileInputStream(this), "UTF-8");
		} catch (FileNotFoundException e) {
			return;
		}

		try {
			String next_token = scanner.findWithinHorizon(pattern, 1000);
			while (next_token != null) {
				MatchResult match = scanner.match();

				String[] value = StringUtils.trimArray(match.group(2).split(","));
				String token = match.group(1).trim();
								
				Token tok = new Token(token,this.spec.get(token));
				tok.setValue(value);
				tokens.add(tok);

				next_token = scanner.findWithinHorizon(pattern, 1000);
			}
		} finally {
			scanner.close();
		}
	}
	
	public TokenList getTokens()
	{
		return tokens;
	}
	

}
