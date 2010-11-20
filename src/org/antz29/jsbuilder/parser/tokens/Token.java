package org.antz29.jsbuilder.parser.tokens;

import java.util.Arrays;
import java.util.Collection;

import org.antz29.jsbuilder.utils.StringUtils;

public class Token {
	
	public static final int ARRAY_TYPE = 1;
	public static final int STRING_TYPE = 2;
	public static final int INT_TYPE = 4;
	public static final int FLOAT_TYPE = 8;
	
	private String name;
	private int type;
	private Object value;
		
	public Token(String name,int type)
	{
		this.name = name;
		this.type = type;			
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getType()
	{
		return type;
	}
	
	public String[] getArrayValue()
	{
		return (String[]) value;
	}
	
	public Object getValue()
	{
		switch (type) {
			case Token.STRING_TYPE:
				return (String) value;
				
			case Token.ARRAY_TYPE:
				return Arrays.asList(value);
				
			case Token.INT_TYPE:
				return (Integer) value;
				
			case Token.FLOAT_TYPE:
				return (Float) value;
		}
		
		return null;	
	}
	
	public void setValue(Object value)
	{
		switch (type) {
			case Token.STRING_TYPE:
				this.value = String.valueOf(value);
				return;
			case Token.INT_TYPE:
				this.value = Integer.valueOf(value.toString());
				return;
			case Token.FLOAT_TYPE:
				this.value = Float.valueOf(value.toString());
				return;
		}	
	}
	
	public void setValue(Collection<String> value)
	{
		setValue(value.toArray());
	}
	
	public void setValue(String[] value)
	{
		switch (type) {
			case Token.ARRAY_TYPE:
				this.value = value;
				return;
			case Token.STRING_TYPE:
				this.value = value[0];
				return;
			case Token.INT_TYPE:
				this.value = Integer.valueOf(value[0]);
				return;
			case Token.FLOAT_TYPE:
				this.value = Float.valueOf(value[0]);
				return;
		}	
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	public String toString()
	{
		if (this.getType() == Token.ARRAY_TYPE) {
			return this.getName() + " = " + StringUtils.implode(this.getArrayValue(), ",");
		}
		
		return this.getName() + " = " + this.getValue().toString();
	}

}
