package popl_formative;

/**
 * A Thread which repeatedly waits and signals on a Semaphore
 * @author Thomas Wright <tdw511@york.ac.uk>
 */
public class TestThread extends Thread {
	/**
	 * The number of times the Semaphore is to Signalled upon.
	 */
	private final int numberOfSignals = 5;
	/**
	 * The value to signal the Semaphore with (this limits the number of
	 * concurrent threads).
	 */
	private int signalValue;
	/**
	 * The QualitySemaphore over which the thread is Signalling.
	 */
	private QualitySemaphore semaphore;
	
	/**
	 * Constructor setting the values of private variables.
	 * @param signalValue The signalValue.
	 * @param semaphore The semaphore to be used for Signalling (a reference).
	 * @throws IllegalArgumentException If we are given an invalid 
	 * signalValue.
	 */
	public TestThread(int signalValue, QualitySemaphore semaphore)
			throws IllegalArgumentException {
		// Check we have been given a positive integer to signal with
		if (signalValue <= 0)
			throw new IllegalArgumentException("signalValue should be a "
					+ "positive integer");
		
		// Set the signal value
		this.signalValue = signalValue;
		
		// Set the Semaphore
		this.semaphore = semaphore;
	}
	
	/**
	 * The method to be run when the thread is being executed.
	 */
	public void run() {
		// Precondition -- signalValue should be a positive integer.
		assert(signalValue > 0);
		
		try {
			for (int signalNo = 0; signalNo < numberOfSignals; ++signalNo) {
				// Tell user we are waiting
				System.out.println("Waiting on Semaphore...");
				// Wait upon the Semaphore
				semaphore.wait(signalValue);
				// Tell user we are signalling
				System.out.println("Signalling Semaphore...");
				// Signal upon the Semaphore
				semaphore.signal(signalValue);
			}
		} catch (IllegalArgumentException e) {
			// This should never occur as excluded by the Precondition.
			assert(false);
		} catch (InterruptedException e) {
			// The it is possible for the thread to be interrupted whilst
			// waiting
			System.out.println("Thread interrupted when waiting or signalling "
					+ "on Semaphore.");
		} catch (SemaphoreWaitOverflowException e) {
			// It is possible for threads to overflow their wait / value count.
			System.out.println("Thread overflowed Semaphore thread / value "
					+ "wait count.");
		} catch (SemaphoreOverflowException e) {
			// It is possible for threads to overflow the value stored in the
			// Semaphore.
			System.out.println("Thread overflowed Semaphore.");
		} catch (SemaphoreAbuseException e) {
			// We should be using the Semaphore correctly; if not there is an
			// error in the code.
			assert(false);
		}
	}
}
