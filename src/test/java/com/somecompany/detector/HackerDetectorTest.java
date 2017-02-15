/**
 * com.somecompany.detector.HackerDetectorTest.java
 * Apr 14, 2015
 * anti-hacker
 *
 */
package com.somecompany.detector;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.*;

/**
 * @author yyaremchuk
 *
 */
public class HackerDetectorTest {

	@Test
	public void validateLine() {
		// line in the correct format
		final HackerDetector detector = new HackerDetectorImpl(new Validator(), 5, 300000L);
		assertNull(detector.parseLine("80.238.9.179,133612947,SIGNIN_SUCCESS,Dave.Branning"));

		// username is missing
		try {
			detector.parseLine("80.238.9.179,133612947,SIGNIN_SUCCESS");
			fail();
		} catch (Exception e) {
			assertEquals("Incorrect line format", e.getMessage());
		}

		// some incorrect IP
		try {
			detector.parseLine("blah-blah,133612947,SIGNIN_SUCCESS");
			fail();
		} catch (Exception e) {
			assertEquals("Incorrect line format", e.getMessage());
		}

		// some incorrect TIME
		try {
			detector.parseLine("80.238.9.179,sfsdfhlsk,SIGNIN_SUCCESS,Dave.name");
			fail();
		} catch (Exception e) {
			assertEquals("Incorrect line format", e.getMessage());
		}

		// some incorrect Status
		try {
			detector.parseLine("80.238.9.179,133612947,NO_SUCCESS,Dave.name");
			fail();
		} catch (Exception e) {
			assertEquals("Incorrect line format", e.getMessage());
		}
	}
	
	@Test
	public void testCatchingAHacker() {
		final HackerDetectorImpl detector = new HackerDetectorImpl(new Validator(), 5, 300000L);

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
	public void testFiveFailuresWithinSixMinutes() {
		final Calendar calendar = Calendar.getInstance();
		final HackerDetectorImpl detector = new HackerDetectorImpl(new Validator(), 5, 300000L);

		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.MINUTE, 1);		
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));

		calendar.add(Calendar.MINUTE, 1);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));

		calendar.add(Calendar.MINUTE, 2);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));

		calendar.add(Calendar.MINUTE, 3);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 20);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 15);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 15);
		assertEquals("80.238.9.179", detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
	}
	
	@Test
	public void testRemovingRecordsAfterSuccess() {
		final Calendar calendar = Calendar.getInstance();
		final HackerDetectorImpl detector = new HackerDetectorImpl(new Validator(), 5, 300000L);

		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.MINUTE, 1);		
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.MINUTE, 1);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_SUCCESS,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 20);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 20);		
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 20);		
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 40);
		assertNull(detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));
		
		calendar.add(Calendar.SECOND, 40);		
		assertEquals("80.238.9.179", detector.parseLine("80.238.9.179," + calendar.getTimeInMillis() + ",SIGNIN_FAILURE,Dave.Branning"));		
	}
}
