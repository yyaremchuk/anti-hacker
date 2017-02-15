/**
 * 
 */
package com.somecompany.detector;

/**
 * @author yyaremchuk
 *
 */
public class LogEntry {
	private String username;
	private long timestamp;

	public LogEntry(String username, long timestamp) {
		this.username = username;
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "LogEntry [username=" + username + ", timestamp=" + timestamp + "]";
	}
}
