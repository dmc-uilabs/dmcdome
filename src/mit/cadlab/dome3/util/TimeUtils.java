// TimeUtils.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.util.Date;

public class TimeUtils
{

	/**
	 * Prints the difference between two specified dates in seconds.
	 * For example:
	 *   running time: 1503 seconds
	 * @param identifier prefix at beginning of printed line; colon and space is added after this string
	 * @param startTime
	 * @param endTime
	 */
	public static void printTimeDifference(String identifier, Date startTime, Date endTime) {
		System.out.println(identifier + ": " + ((endTime.getTime() - startTime.getTime()) / 1000.0) + " seconds");
	}

	/**
	 * Prints the difference between a specified date and now in seconds.
	 * For example:
	 *   running time: 1503 seconds
	 * @param identifier prefix at beginning of printed line; colon and space is added after this string
	 * @param startTime
	 */ 
	public static void printTimeFromDate(String identifier, Date startTime)
	{
		System.out.println(identifier + ": " + ((new Date().getTime() - startTime.getTime()) / 1000.0) + " seconds");
	}

}
