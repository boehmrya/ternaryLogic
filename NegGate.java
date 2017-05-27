import java.util.Scanner;

/** the negate gate, a fundamental ternary logic gate.
 *  @author Douglas Jones
 *  @version 2017-04-20
 *  This code is largely ripped from Version 2017-04-06, with comment changes.
 *  @see Gate
 */
public class NegGate extends Gate {
	/** initializer scans and processes one neg gate
	 *  @param sc Scanner from which gate description is read
	 *  @param myName the value to be put in the name field
	 */
	NegGate( Scanner sc, String myName ) {
		// the text "gate myName min" has already been scanned
		super( myName );

		inputs = 1; // it is a one-input gate

		this.finishGate( sc );
	}

        /** get a representation for this Gate in the form used for input
         *  @return the representation as a string
         */
	public String toString() {
		return(
			this.myString() + " neg " + " " + delay
		);
	}

	// ***** Logic Simulation for NegGate *****

        /** Return the new logic value, 2 minus the input value.
         *  Every subclass of gate must define this.
         *  @return the new logic value, a function of <TT>inputCounts</TT>;
         */
	protected int logicValue() {
		// Warning this is mildly tricky code
		int newOutput = 2;
		while (inputCounts[2 - newOutput] == 0) newOutput--;
		return newOutput;
	}
}
