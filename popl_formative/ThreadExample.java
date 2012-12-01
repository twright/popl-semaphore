package popl_formative;

import java.util.ArrayList;

/**
 * @author Thomas Wright <tdw511@york.ac.uk>
 */
public class ThreadExample {

	/**
	 * The main method of the example, which produces test output
	 * @param args The command line arguments of the program (ignored)
	 */
	public static void main(String[] args) {
		ArrayList<TestThread> threads = new ArrayList<TestThread>();
		
		// The values to be signalled on
		int signalValues[] = {1, 2, 4, 6, 10};
		
		// The Semaphore we are to be signalling upon
		QualitySemaphore semaphore = new QualitySemaphore(10);
		
		// Let the user know we have started.
		System.out.println("Starting threads...");
		
		// The store the threads
		for (int signalValue : signalValues) {
			threads.add(new TestThread(signalValue, semaphore));
		}
		
		// Start the threads
		for (TestThread thread : threads) {
			thread.start();
		}
	}

}
