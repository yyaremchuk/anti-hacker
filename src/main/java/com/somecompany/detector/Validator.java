/**
 * 
 */
package com.somecompany.detector;

import java.util.regex.Pattern;

/**
 * @author yyaremchuk
 *
 */
public class Validator {
//	private static final Pattern pattern = Pattern.compile("(\\.),(\\d+),(SIGNIN_SUCCESS|SIGNIN_FAILURE),(\\s+.\\s+)");
	private static final Pattern pattern = Pattern.compile("((([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])),(\\d*),(SIGNIN_SUCCESS|SIGNIN_FAILURE),(\\w*.\\w*)");

	/**
	 * ip,date,action,username
	 * 
	 * @param line
	 * @return
	 */
	public boolean validate(String line) {

		if (line == null || line.isEmpty()) {
			return false;
		}

		return pattern.matcher(line).matches();
	}
}
