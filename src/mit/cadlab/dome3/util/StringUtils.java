// StringUtils.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

public class StringUtils
{

	private static char underscore = '_';

	public static boolean isValidCodeName(String name)
	{
		if (name == null || name.length() == 0)
			return false;
		// can only be alphanumeric and underscore
		// no digits for first character
		char c = name.charAt(0);
		if (!(Character.isLetter(c) || c == underscore))
			return false;
		for (int i = 1; i < name.length(); ++i) {
			c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == underscore))
				return false;
		}
		return true;
	}

	public static boolean containsWhitespace(String name)
	{
		if (name == null || name.length() == 0)
			return false;
		char c;
		for (int i = 0; i < name.length(); ++i) {
			c = name.charAt(i);
			if (Character.isWhitespace(c))
				return true;
		}
		return false;
	}

}
