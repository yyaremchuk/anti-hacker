/**
 * com.somecompany.detector.HackerDetector.java
 * Apr 15, 2015
 * anti-hacker
 *
 */
package com.somecompany.detector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yyaremchuk
 *
 */
public class HackerDetector {
	private Map<String, Entry> attempts;

	public HackerDetector() {
		this.attempts = new HashMap<String, Entry>();
	}
	
	/**
	 * Parse log line of the following format: ip,date,action,username
	 * @param line
	 * @return
	 */
	public String parseLine(String line) {
		final String[] tokens = line.split(",");

		if (tokens.length == 4) {
			final String key = tokens[0];
			final long timestamp = Long.valueOf(tokens[1]);
			Entry entry = attempts.get(key);

			if (entry != null && entry.isExpired(timestamp)) {
				attempts.remove(key);
				entry = null;
			}
			
			if ("SIGNIN_FAILURE".equals(tokens[2])) {

				if (entry == null) {
					attempts.put(tokens[0], new Entry(timestamp));
				} else {
					entry.registerFailure();

					if (entry.getFailures() >= 5) {
						return tokens[0];
					}
 				}
			}
		}

		return null;
	}

	public int getFailures(String ip) {
		final Entry entry = attempts.get(ip); 
		return entry != null ? entry.getFailures() : 0;
	}

	public static class Entry {
		private long timestamp;
		private int failures;
		
		public Entry(long timestamp) {
			this.timestamp = timestamp;
			this.failures = 1;
		}

		public void resetFailures() {
			this.failures = 1;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public int getFailures() {
			return failures;
		}

		public boolean isExpired(long now) {
			return now - timestamp > 300000L;
		}

		public void registerFailure() {
			this.failures++;
		}
	}
}
