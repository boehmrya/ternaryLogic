import java.util.Scanner;

/** the min operator, a fundamental kind of ternary logic gate.
 *  @author Douglas Jones
 *  @version 2017-04-20
 *  This code is largely ripped from Version 2017-04-06, with comment changes.
 *  @see Gate
 */
public class MinGate extends Gate {
	/** initializer scans and processes one min gate
	 *  @param sc Scanner from which gate description is read
	 *  @param myName the value to be put in the name field
	 */
	MinGate( Scanner sc, String myName ) {
		// the text "gate myName min" has already been scanned
		super( myName );

		// get inputs
		if (sc.hasNextInt()) {
			inputs = sc.nextInt();
		} else {
			Errors.warn(
				this.myString() +
				" min -- has no input count"
			);
		}

		this.finishGate( sc );
	}

        /** get a representation for this Gate in the form used for input
         *  @return the representation as a string
         */
	public String toString() {
		return(
			this.myString() + " min " + inputs + " " + delay
		);
	}

	// ***** Logic Simulation for MinGate *****

        /** Return the new logic value, the min of the input values.
         *  Every subclass of gate must define this.
         *  @return the new logic value, a function of <TT>inputCounts</TT>;
         */
	protected int logicValue() {
		// find the minimum of all the inputs
		int newOutput = 0;
		while (inputCounts[newOutput] == 0) newOutput++;
		return newOutput;
	}
}
