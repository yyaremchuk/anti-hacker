/**
 * 
 */
package com.somecompany.detector;

/**
 * @author yyaremchuk
 *
 */
public interface HackerDetector {
	
	/**
	 * This method will return an IP if any suspicious activity is identified
	 * or null.
	 * 
	 * @param line
	 * @return
	 */
	public String parseLine(String line);
}
