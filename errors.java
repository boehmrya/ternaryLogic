/** Utility package for error handling.
 *
 *  General purpose error reporting package for command-line applications.
 *  It allows reporting fatal errors and warnings to the user.
 *
 *  @author Douglas Jones
 *  @version 2017-04-05
 *  this code is ripped from RoadNetwork.java version 2017-03-31.
 */
public class Errors {
	private Errors(){}; // you may never instantiate this class

	private static int count = 0; // warning count, really public read only
	/** Provide public read only access to the count of warnings.
         *  @return the count of the non-fatal warnings
	 */
	public static int count() {
		return count;
	}

	/** Warn of non fatal errors with a message on <code>system.err</code>
	 *  @param message   the string to output as an error message.
	 */
	public static void warn( String message ) {
		System.err.println( "Warning: " + message );
		count = count + 1;
	}

	/** Report fatal errors with a message on <code>system.err</code>
	 *  and then exit the application.
	 *  @param message   the string to output as an error message.
	 */
	public static void fatal( String message ) {
		System.err.println( "Fatal error: " + message );
		System.exit( -1 );
	}
}
