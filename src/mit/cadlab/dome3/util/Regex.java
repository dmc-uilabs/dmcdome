// Regex.java
package mit.cadlab.dome3.util;

import org.apache.oro.text.regex.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Regex
{

	private static PatternCompiler compiler = new Perl5Compiler();
	private static PatternMatcher matcher = new Perl5Matcher();

	public static Pattern whitespace = createPattern("\\s+"); // 1+ whitespace
	public static Pattern endline = createPattern("\\r\\n|\\r|\\n");
	public static Pattern endlineAtEnd = createPattern("(\\r\\n|\\r|\\n)$");

	public synchronized static Pattern createPattern(String regularExpression)
	{
		try {
			return compiler.compile(regularExpression);
		} catch (MalformedPatternException ex) {
			System.err.print("Bad pattern: " + ex);
			throw new NullPointerException("Bad pattern: " + regularExpression + "\n  " + ex);
		}
	}

	/**
	 * Removes the last endline from the string.
	 * @param input
	 * @return String with the last endline removed, if it exists
	 */
	public synchronized static String chomp(String input) {
		return substitute(endlineAtEnd, "", input,1);
	}

	/**
	 * Splits up a String instance and stores results as a Collection
	 * of all its substrings using a regular expression as the delimiter.
	 */
	public synchronized static List split(String regularExpression, String input)
	{
		Pattern pattern = createPattern(regularExpression);
		ArrayList results = new ArrayList();
		Util.split(results, matcher, pattern, input, Util.SPLIT_ALL);
		return results;
	}

	public synchronized static List split(Pattern pattern, String input)
	{
		ArrayList results = new ArrayList();
		Util.split(results, matcher, pattern, input, Util.SPLIT_ALL);
		return results;
	}

	/**
	 * Splits up a String instance and stores results as a List of substrings
	 * numbering no more than a specified limit.
	 */
	public synchronized static List split(String regularExpression, String input, int limit)
	{
		Pattern pattern = createPattern(regularExpression);
		ArrayList results = new ArrayList(limit);
		Util.split(results, matcher, pattern, input, limit);
		return results;
	}

	public synchronized static List split(Pattern pattern, String input, int limit)
	{
		ArrayList results = new ArrayList(limit);
		Util.split(results, matcher, pattern, input, limit);
		return results;
	}

	/**
	 *Searches a string for a pattern and substitutes all occurences of the pattern.
	 */
	public synchronized static String substitute(String regularExpression, String substitution, String input)
	{
		Pattern pattern = createPattern(regularExpression);
		return Util.substitute(matcher, pattern,
		                       new Perl5Substitution(substitution, Perl5Substitution.INTERPOLATE_ALL),
		                       input, Util.SUBSTITUTE_ALL);
	}

	public synchronized static String substitute(Pattern pattern, String substitution, String input)
	{
		if (pattern == null)
			throw new NullPointerException("pattern is null");
		if (matcher == null)
			throw new NullPointerException("matcher is null");
		if (substitution == null)
			throw new NullPointerException("sub is null");
		if (input == null)
			throw new NullPointerException("input is null");
		return Util.substitute(matcher, pattern,
		                       new Perl5Substitution(substitution, Perl5Substitution.INTERPOLATE_ALL),
		                       input, Util.SUBSTITUTE_ALL);
	}

	/**
	 * Searches a string for a pattern and replaces the first occurrences of the pattern
	 * with a Substitution up to the number of substitutions specified by the numSubs parameter.
	 */
	public synchronized static String substitute(String regularExpression, String substitution, String input, int numSubs)
	{
		Pattern pattern = createPattern(regularExpression);
		return Util.substitute(matcher, pattern,
		                       new Perl5Substitution(substitution, Perl5Substitution.INTERPOLATE_ALL),
		                       input, numSubs);
	}

	public synchronized static String substitute(Pattern pattern, String substitution, String input, int numSubs)
	{
		return Util.substitute(matcher, pattern,
		                       new Perl5Substitution(substitution, Perl5Substitution.INTERPOLATE_ALL),
		                       input, numSubs);
	}

	/**
	 * returns first match
	 */
	public synchronized static MatchResult search(String regularExpression, String input)
	{
		Pattern pattern = createPattern(regularExpression);
		if (matcher.contains(input, pattern))
			return matcher.getMatch();
		else
			return null;
	}

	public synchronized static MatchResult search(Pattern pattern, String input)
	{
		if (matcher.contains(input, pattern))
			return matcher.getMatch();
		else
			return null;
	}

	/**
	 * returns all matches
	 */
	public synchronized static Iterator findAll(String regularExpression, String input)
	{
		Pattern pattern = createPattern(regularExpression);
		return new MatchIterator(pattern, input);
	}

	public synchronized static Iterator findAll(Pattern pattern, String input)
	{
		return new MatchIterator(pattern, input);
	}

	private static class MatchIterator implements Iterator
	{
		Pattern pattern;
		PatternMatcherInput matchInput;
		PatternMatcher matcher;
		boolean hasAnotherMatch;

		public MatchIterator(Pattern pattern, String input)
		{
			this.pattern = pattern;
			matchInput = new PatternMatcherInput(input);
			this.matcher = new Perl5Matcher();
			hasAnotherMatch = this.matcher.contains(matchInput, pattern);
		}

		public boolean hasNext()
		{
			return hasAnotherMatch;
		}

		public Object next()
		{
			if (!hasAnotherMatch)
				throw new NoSuchElementException();
			MatchResult result = this.matcher.getMatch();
			hasAnotherMatch = this.matcher.contains(matchInput, pattern);
			return result;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

}
