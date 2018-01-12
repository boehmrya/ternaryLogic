import java.util.Scanner;
import java.util.regex.Pattern;

/** Support methods for scanning input files.
 *  @author Ryan Boehm
 *  @see Errors
 */
public class ScanSupport {

	/** Pattern for recognizing identifers
	 */
	public static final Pattern name // letter followed by alphanumeric
		= Pattern.compile( "[a-zA-Z][a-zA-Z0-9_]*|" );

	/** Pattern for recognizing floating point numbers
	 */
	public static final Pattern numb // Digits.Digits or .Digits or nothing
		= Pattern.compile( "[0-9]+\\.?[0-9]*|\\.[0-9]+|" );

	/** Pattern for recognzing whitespace excluding newlines
	 */
	public static final Pattern whitespace
		= Pattern.compile( "[ \t]*" );

	/** Get next name without skipping to next line (unlike sc.Next()).
	 *  @param sc	the scanner from which end of line is scanned.
	 *  @return	the name, if there was one, or an empty string.
	 */
	public static String nextName( Scanner sc ) {
		sc.skip( whitespace );

		// the following is weird code, it skips the name
		// and then returns the string that matched what was skipped
		sc.skip( name );
		return sc.match().group();
	}

	/** Get next float without skipping lines (unlike sc.nextFloat()).
	 *  @param sc	the scanner from which end of line is scanned.
	 *  @return	the name, if there was one, or NaN if not.
	 */
	public static Float nextFloat( Scanner sc ) {
		sc.skip( whitespace );

		// the following is weird code, it skips the name
		// and then returns the string that matched what was skipped
		sc.skip( numb );
		String f = sc.match().group();

		// now convert what we can or return NaN
		if (!"".equals( f )) {
			return Float.parseFloat( f );
		} else {
			return Float.NaN;
		}
	}

	/** Class used only for deferred evaluation of lambda expressions
	 *  passed to <code>lineEnd</code>.
	 */
	public interface EndMessage {
		/** Method to compute the error message text
		 *  @return  the text the error message
		 */
		public abstract String myString();
	}

	/** Advance to next line and complain if there is junk at the line end;
	 *  call this when all useful content has been consumed from the line
	 *  it skips optional line-end comments and complains about anything
	 *  it finds while advancing to the next line.
	 *  @see Errors
	 *  @see EndMessage
	 *  @param sc	the scanner from which end of line is scanned.
	 *  @param message	will be evaluated only when there is an error;
	 *	it is typically passed as a lambda expression, for example,
	 *	{@code ScanSupport.lineEnd( sc, () -> "this " + x + " that" );}
	 */
	public static void lineEnd( Scanner sc, EndMessage message ) {
		sc.skip( whitespace );
		String lineEnd = sc.nextLine();
		if ( (!lineEnd.equals( "" ))
		&&   (!lineEnd.startsWith( "--" )) ) {
			Errors.warn(
				"" + message.myString() +
				" followed unexpected by '" + lineEnd + "'"
			);
		}
	}
}
