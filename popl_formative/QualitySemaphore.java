package popl_formative;

import java.util.HashMap;

/**
 * An interface describing the specification of a Quality Semaphore.
 * A Semaphore is a non-negative integer which can be atomically updated, and 
 * induce, tasks to be blocked based on their attempts to modify its value.
 * @author Thomas Wright <tdw511@york.ac.uk>
 */
public class QualitySemaphore {
	/**
	 * The current value of the Semaphore (A non-negative integer).
	 */
	private int value;
	/**
	 * The required signal values for each waiting thread (by Thread ID).
	 * (Warning: the uniqueness of Thread IDs my break down after a few
	 * millenia). 
	 */
	private HashMap<Long, HashMap<Integer, Integer>> waitingValues;
	
	/**
	 * A constructor setting the initial value of the Semaphore.
	 * @param n The initial value of the Semaphore (a non-negative Integer).
	 */
	public QualitySemaphore(int n) {
		// Precondition -- None
		assert(true);
		
		// Initialise signalValues;
		waitingValues = new HashMap<Long, HashMap<Integer, Integer>>();
		
		// Set the value of the Semaphore
		value = n;
		
		// Postcondition -- The value of the Semaphore should be non-negative 
		// and equal to n.
		assert(value >= 0 && value == n);
	}
	
	/**
	 * Block the task until the value of the Semaphore can be decreased by n.
	 * @param n The positive integer to be subtracted from the value of the 
	 * Semaphore.
	 * @throws InterruptedException If we are interrupted whilst waiting.
	 * @throws SemaphoreAbuseException If we have overflowed our wait count
	 * for a thread / value
	 */
	public synchronized void wait(int n) throws InterruptedException,
			IllegalArgumentException, SemaphoreWaitOverflowException {
		// Precondition -- The value of the Semaphore should be non-negative.
		assert(value >= 0);
		
		// Check that n is positive
		if (n <= 0)
			throw new IllegalArgumentException("n must be a positive integer");
		
		/* Complicated code to handle 2.2 */
		// Add this value to waitingValues identifying by the current thread ID.
		long threadID = Thread.currentThread().getId();
		// Initialise the HashMap for the current Thread ID if one does not 
		// exist.
		if (!waitingValues.containsKey(threadID))
			waitingValues.put(threadID, new HashMap<Integer, Integer>());
		// Get the HashMap for the current Thread ID
		HashMap<Integer, Integer> threadValues = waitingValues.get(threadID);
		
		if (threadValues.containsKey(n)
				&& Integer.MAX_VALUE == threadValues.get(n))
			// We have overflowed the wait count for this thread / value
			throw new SemaphoreWaitOverflowException("Thread has overflowed "
					+ "its wait count for this value");
		
		// Either initialise or increment the count for the current thread
		threadValues.put(n, threadValues.containsKey(n)
				? threadValues.get(n) + 1 : 1);
		
		// Wait until we are able to decrease the Semaphore value.
		// (we are using the wait method of Object, not this one)
		while (n > value) super.wait();
		
		// Decrease the Semaphore value.
		value -= n;
		
		// Output the Semaphore value
		System.out.println("Semaphore value decreased to " + value);
		
		// Postcondition -- The value of the Semaphore should still be 
		// non-negative.
		assert(value >= 0);
	}
	
	/**
	 * Increase the value of the Semaphore by n (this can unblock waiting 
	 * tasks).
	 * @param n The positive integer to be added to the value of the semaphore
	 * @throws IllegalArgumentException if we are passed an invalid n
	 * @throws SemaphoreOverflowException if the Semaphore value overflows
	 * @throws SemaphoreAbuseException if we violate the safe use conditions
	 * from Part B.
	 */
	public synchronized void signal(int n) throws IllegalArgumentException,
			SemaphoreOverflowException, SemaphoreAbuseException {
		// Precondition -- The value of the Semaphore should be non-negative.
		assert(value >= 0);
		
		// Check that n is positive
		if (n <= 0)
			throw new IllegalArgumentException("n must be a positive "
					+ "integer");
		
		// Check for potential overflow and throw an error if necessary.
		if (Integer.MAX_VALUE - value < n)
			throw new SemaphoreOverflowException();
		
		/* Complicated code to handle 2.2 */
		// Get the ID of the current thread
		long threadID = Thread.currentThread().getId();
		// Check that this thread has already signalled
		if (waitingValues.containsKey(threadID)) {
			// Get the map of values to the number of them we are waiting on.
			HashMap<Integer, Integer> threadValues
					= waitingValues.get(threadID);
			// If we are waiting on a given value
			if (threadValues.containsKey(n)) {
				// Get the number of times we are waiting on the value.
				int threadValue = threadValues.get(n);
				if (threadValue > 1)
					// If we are waiting on the value more than once,
					// decrement the number.
					threadValues.put(n, threadValue - 1);
				else if (threadValue == 1)
					// If we are waiting on the value only once, remove the
					// value.
					threadValues.remove(n);
				else
					// If we are waiting on the value less than once, this is
					// an error.
					assert(false);
			} else
				// This thread must be waiting on a value before we can signal
				// on it.
				throw new SemaphoreAbuseException("This Thread is not "
						+ "waiting on this value");
		} else
			// This thread must be waiting before we can signal.
			throw new SemaphoreAbuseException("This Thread is not waiting on "
					+ "any value");
		
		// Increase the Semaphore value.
		value += n;
		
		// Output the Semaphore value
		System.out.println("Semaphore value increased to " + value);
		
		// Notify processes which are waiting on the Semaphore being 
		// incremented. We cannot know which process will have a low enough 
		// value of n to make progress (if any), so we need to wake them all.
		notifyAll();
		
		// Postcondition -- The value of the Semaphore should be at least n.
		assert(value >= n);
	}
	
	/**
	 * Get the value currently stored in the Semaphore.
	 * @return int
	 */
	public int getValue() {
		// Precondition -- The value is a non-negative integer.
		assert(value >= 0);
		
		return value;
	}
}
