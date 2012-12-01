package popl_formative;

/**
 * An Exception class used for errors arising from misuse of Semaphores
 */
public class SemaphoreAbuseException extends Exception {
	
	public SemaphoreAbuseException() {
		super();
	}

	public SemaphoreAbuseException(String string) {
		super(string);
	}

	/**
	 * An autogenerated serialVersionUID to allow serialisation across restructuring.
	 */
	private static final long serialVersionUID = 3110810087199690803L;
}