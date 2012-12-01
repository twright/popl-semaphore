package popl_formative;

import junit.framework.TestCase;
import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

/**
 * Tests to verify the correct operation of QualitySemaphores.
 * @author Thomas Wright <tdw511@york.ac.uk>
 */
public class TestQualitySemaphore extends TestCase {
	public static final int runCount = 50;
	
	public void testSignalBeforeWait() throws Throwable {
		TestFramework.runManyTimes(new SignalBeforeWait(), runCount);
	}
	
	public class SignalBeforeWait extends MultithreadedTestCase {

		private QualitySemaphore semaphore;
		
		@Override public void initialize() {
			semaphore = new QualitySemaphore(10);
		}
		
		public void thread1() {
			final int signalValue = 3;
			try {
				semaphore.signal(signalValue);
				fail("thread1 should have been preventing from signalling "
						+ "from a thread before waiting.");
			} catch (SemaphoreAbuseException e) {
			}
		}
		
		public void thread2() {
			try {
				final int signalValue = 3;
				semaphore.wait(signalValue);
				semaphore.signal(signalValue);
			} catch (SemaphoreAbuseException e) {
				fail("thread2 not have been stopped from signalling with a "
						+ "correct value.");
			} catch (InterruptedException e) {
				fail("thread2 should not have been interrupted.");
			}
		}
	}
	
	public void testInterruptWhilstWaiting() throws Throwable {
		TestFramework.runManyTimes(new InterruptWhilstWaiting(), runCount);
	}
	
	// See: http://www.cs.umd.edu/projects/PL/multithreadedtc/overview.html#examples2
	public class InterruptWhilstWaiting extends MultithreadedTestCase {
		
		private QualitySemaphore semaphore;
		
		private final int signalValue = 4;
		private final int initialValue = 0;
		
		@Override public void initialize() {
			semaphore = new QualitySemaphore(initialValue);
		}
		
		public void thread1() {
			try {
				semaphore.wait(signalValue);
				fail("thread1 should have been interrupted before it finished "
						+ "waiting.");
			} catch (InterruptedException e) {
				assertTick(1);
			} catch (SemaphoreWaitOverflowException e) {
				fail("thread1 should not have overflowed the Semaphore "
						+ "thread / value wait count.");
			} catch (IllegalArgumentException e) {
				fail("The Semaphore has incorrectly rejected thread1's signal "
						+ "value.");
			}
		}
		
		public void thread2() {
			waitForTick(1);
			getThread(1).interrupt();
		}
		
		@Override public void finish() {
			assertEquals(initialValue, semaphore.getValue());
		}
	}
	
	public void testSemaphoreValueUpdates() throws Throwable {
		TestFramework.runManyTimes(new SemaphoreValueUpdates(), runCount);
	}
	
	public class SemaphoreValueUpdates extends MultithreadedTestCase {
		
		private QualitySemaphore semaphore;
		private int initialValue = 100;
		private int thread1Value = 2;
		private int thread1Waits = 5;
		private int thread1Signals = 3;
		private int thread2Value = 3;
		private int thread2Waits = 7;
		private int thread2Signals = 2;
		
		@Override public void initialize() {
			semaphore = new QualitySemaphore(initialValue);
		}
		
		public void thread1() {
			performActions(thread1Value, thread1Waits, thread1Signals);
		}
		
		public void thread2() {
			performActions(thread2Value, thread2Waits, thread2Signals);
		}
		
		@Override public void finish() {
			assertEquals(initialValue
					+ thread1Value*(thread1Signals - thread1Waits)
					+ thread2Value*(thread2Signals - thread2Waits),
					semaphore.getValue());
		}
		
		private void performActions(int value, int waits, int signals) {
			while (waits-- >= 0) {
				try {
					semaphore.wait(value);
				} catch (InterruptedException e) {
					fail("We should not have been interrupted whilst waiting "
							+ "on the Semaphore.");
				} catch (SemaphoreWaitOverflowException e) {
					fail("We should not have overflowed the Semaphore thread "
							+ " / value wait count.");
				} catch (IllegalArgumentException e) {
					fail("The Semaphore has incorrectly rejected our signal"
							+ "value.");
				}
			}
			while (signals-- >= 0) {
				try {
					semaphore.signal(value);
				} catch (IllegalArgumentException e) {
					fail("The Semaphore incorrectly threw an "
							+ "IllegalArgumentException.");
				} catch (SemaphoreAbuseException e) {
					fail("The Semaphore incorrectly threw a "
							+ "SemaphoreAbuseException.");
				}
			}
		}
	}
	
	public void testSemaphoreOverflow() throws Throwable {
		TestFramework.runManyTimes(new SemaphoreOverflow(), runCount);
	}
	
	public class SemaphoreOverflow extends MultithreadedTestCase {
		
		private QualitySemaphore semaphore;
		private boolean thread1Overflowed, thread2Overflowed;
		private int thread1SignalValue, thread2SignalValue;
		
		@Override public void initialize() {
			thread1Overflowed = thread2Overflowed = false;
			thread1SignalValue = Integer.MAX_VALUE;
			thread2SignalValue = 7;
			semaphore = new QualitySemaphore(thread1SignalValue);
		}
		
		public void thread1() {
			thread1Overflowed = performActions(thread1SignalValue);
		}
		
		public void thread2() {
			thread2Overflowed = performActions(thread2SignalValue);
		}
		
		@Override public void finish() {
			assertTrue(thread1Overflowed || thread2Overflowed);
		}
		
		boolean performActions(int signalValue) {
			try {
				semaphore.wait(signalValue);
			} catch (IllegalArgumentException e) {
				fail("We have called wait with an invalid signal value.");
			} catch (InterruptedException e) {
				fail("We should not have be interrupted whilst waiting on the "
						+ "Semaphore.");
			} catch (SemaphoreWaitOverflowException e) {
				fail("We should not have overflowed our Semaphore thread / "
						+ "value wait count");
			}
			
			try {
				semaphore.signal(signalValue);
			} catch (SemaphoreOverflowException e) {
				return false;
			} catch (SemaphoreAbuseException e) {
				fail("The Semaphore has incorrectly rejected our signal "
						+ "value.");
			}
			
			return true;
		}
	}
}
