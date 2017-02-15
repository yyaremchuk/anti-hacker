/**
 * com.somecompany.detector.HackerDetector.java
 * Apr 15, 2015
 * anti-hacker
 *
 */
package com.somecompany.detector;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yyaremchuk
 *
 */
public class HackerDetectorImpl implements HackerDetector {
	private Map<String, Deque<LogEntry>> attempts;

	private Validator validator;
	private int allowedAttempts;
	private long catchingWindow;
	
	public HackerDetectorImpl(Validator validator, int allowedAttemps, long catchingWindow) {
		this.attempts = new HashMap<String, Deque<LogEntry>>();
		this.validator = validator;
		this.allowedAttempts = allowedAttemps;
		this.catchingWindow = catchingWindow;
	}

	/**
	 * Parse log line of the following format: 
	 * 
	 * 	ip,date,action,username
	 * 
	 * @param line
	 * @return
	 */
	public String parseLine(String line) {
		
		// TODO:
		if (!validator.validate(line)) {
			throw new RuntimeException("Incorrect line format");
		}

		final String[] tokens = line.split(",");
		final String ip = tokens[0];
		final long timestamp = Long.parseLong(tokens[1]);
		final String action = tokens[2];
		final String userName = tokens[3];

		synchronized (attempts) {

			if ("SIGNIN_SUCCESS".equals(action)) {
				// clean up failing attempts if any
				attempts.remove(ip);
				return null;
			}
	
			Deque<LogEntry> queue = attempts.get(ip);
			final LogEntry logEntry = new LogEntry(userName, timestamp);
	
			System.out.println(String.format("ip = %s, queue = %s", ip, queue));
			
			if (queue == null) {
				queue = new ArrayDeque<LogEntry>();
				queue.add(logEntry);
				attempts.put(ip, queue);
			} else {
	
				// if previous entry is older then catching window -> clean the queue
				if ((logEntry.getTimestamp() - queue.getFirst().getTimestamp()) >= catchingWindow) {
					queue.clear();
				}
	
				if (queue.size() < allowedAttempts) {
					queue.addFirst(logEntry);
				} else {
					queue.removeLast();
					queue.addFirst(logEntry);
				}
				
				if (queue.size() < allowedAttempts) {
					return null;
				} else {
					final LogEntry first = queue.getLast();
	
					System.out.println("diff = " + (logEntry.getTimestamp() - first.getTimestamp()));
					
					if ((logEntry.getTimestamp() - first.getTimestamp()) <= catchingWindow) {
						return ip;
					}
				}
			}
		}

		return null;
	}
}
