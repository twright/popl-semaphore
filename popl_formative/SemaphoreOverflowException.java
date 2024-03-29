package popl_formative;

/**
 * An exception to be thrown if the Semaphore value overflows.
 * @author Thomas Wright <tdw511@york.ac.uk>
 */
public class SemaphoreOverflowException extends SemaphoreAbuseException {
	
	public SemaphoreOverflowException() {
		super();
	}

	public SemaphoreOverflowException(String string) {
		super(string);
	}

	/**
	 * An autogenerated serialisation version ID, allowing safe serialisation.
	 */
	private static final long serialVersionUID = 1576209865150822547L;

}
