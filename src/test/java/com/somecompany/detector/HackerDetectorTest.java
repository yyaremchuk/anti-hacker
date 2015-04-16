/**
 * com.somecompany.detector.HackerDetectorTest.java
 * Apr 14, 2015
 * anti-hacker
 *
 */
package com.somecompany.detector;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.*;

/**
 * @author yyaremchuk
 *
 */
public class HackerDetectorTest {

	@Test
	public void testParseLine() {
		final HackerDetector detector = new HackerDetector();
		assertNull(detector.parseLine("80.238.9.179,133612947,SIGNIN_SUCCESS,Dave.Branning"));
	}
	
	@Test
	public void testCatchingAHacker() {
		final HackerDetector detector = new HackerDetector();

		final Calendar calendar = Calendar.getInstance();
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 40);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 40);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 40);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 40);		
		assertEquals("80.238.9.179", detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));

		calendar.add(Calendar.SECOND, 40);		
		assertEquals("80.238.9.179", detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
	}

	@Test
	public void testGetFailedAttempts() {
		final HackerDetector detector = new HackerDetector();
		assertEquals(0, detector.getFailures("80.238.9.179"));

		detector.parseLine("80.238.9.179,133613010,SIGNIN_FAILURE,Dave.Branning");
		assertEquals(1, detector.getFailures("80.238.9.179"));

		detector.parseLine("80.238.9.179,133613010,SIGNIN_SUCCESS,Dave.Branning");
		assertEquals(1, detector.getFailures("80.238.9.179"));
	}

	@Test
	public void testFiveFailuresWithinSixMinutes() {
		final Calendar calendar = Calendar.getInstance();
		final HackerDetector detector = new HackerDetector();

		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(1, detector.getFailures("80.238.9.179"));
		
		calendar.add(Calendar.MINUTE, 1);		
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(2, detector.getFailures("80.238.9.179"));
		
		calendar.add(Calendar.MINUTE, 1);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(3, detector.getFailures("80.238.9.179"));

		calendar.add(Calendar.MINUTE, 2);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(4, detector.getFailures("80.238.9.179"));

		calendar.add(Calendar.MINUTE, 3);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(1, detector.getFailures("80.238.9.179"));
	}
	
	/**
	 * This assumption looks premature
	 */
	@Test
	@Ignore
	public void testRemovingRecordsAfterSuccess() {
		final Calendar calendar = Calendar.getInstance();
		final HackerDetector detector = new HackerDetector();

		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(1, detector.getFailures("80.238.9.179"));
		
		calendar.add(Calendar.MINUTE, 1);		
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		assertEquals(2, detector.getFailures("80.238.9.179"));
		
		calendar.add(Calendar.MINUTE, 1);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_SUCCESS,Dave.Branning"));
		assertEquals(0, detector.getFailures("80.238.9.179"));
	}
}
