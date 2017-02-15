/**
 * 
 */
package com.somecompany.detector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * @author yyaremchuk
 *
 */
public class ConcurrentTest {

	protected static void assertConcurrent(final String message, final List<? extends Runnable> runnables, 
			final int maxTimeoutSeconds) throws InterruptedException {
	    final int numThreads = runnables.size();
	    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
	    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
	    try {
	        final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
	        final CountDownLatch afterInitBlocker = new CountDownLatch(1);
	        final CountDownLatch allDone = new CountDownLatch(numThreads);
	        
	        for (final Runnable submittedTestRunnable : runnables) {
	            threadPool.submit(new Runnable() {
	                public void run() {
	                    allExecutorThreadsReady.countDown();
	        
	                    try {
	                        afterInitBlocker.await();
	                        submittedTestRunnable.run();
	                    } catch (final Throwable e) {
	                        exceptions.add(e);
	                    } finally {
	                        allDone.countDown();
	                    }
	                }
	            });
	        }

	        // wait until all threads are ready
	        assertTrue("Timeout initializing threads! Perform long lasting initializations "
	        		+ "before passing runnables to assertConcurrent", 
	        		allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));

	        // start all test runners
	        afterInitBlocker.countDown();
	        assertTrue(message +" timeout! More than" + maxTimeoutSeconds + "seconds", 
	        		allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
	    } finally {
	        threadPool.shutdownNow();
	    }

	    assertTrue(message + " failed with exception(s)" + exceptions, exceptions.isEmpty());
	}
	
	@Test
	public void doNotMissTheHacker() throws InterruptedException {
		final String format = "80.238.9.179,%d,SIGNIN_FAILURE,Dave.Branning";

		final List<Runnable> tasks = new ArrayList<Runnable>();
		final AtomicInteger counter = new AtomicInteger();
		final HackerDetector detecor = new HackerDetectorImpl(new Validator(), 5, 300000L);

		for (int i = 0; i < 100; i++) {
			tasks.add(new Runnable() {

				public void run() {
					final String value = String.format(format, System.currentTimeMillis());
					final String result = detecor.parseLine(value);
					
					System.out.println("OUT ====>: " + result);
					
					if (result != null) {
						counter.incrementAndGet();
					}
				}
			});
		}

		assertConcurrent("Test concurrency", tasks, 120);
		assertEquals(96, counter.get());
	}
}