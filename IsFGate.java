import java.util.Scanner;

/** the is-false gate, a kind of gate.
 *  @author Ryan Boehm
 *  @see Gate
 */
public class IsFGate extends Gate {
	/** initializer scans and processes one is-false gate
	 *  @param sc the Scanner from which gate description is read
	 *  @param myName the value to be put in the name field
	 */
	IsFGate( Scanner sc, String myName ) {
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
			this.myString() + " isfalse " + delay
		);
	}

	// ***** Logic Simulation for IsFGate *****

	/** Sanity check for IsFGate */
	public void check() {
		super.check();

		// now change the output from unknown to false
		Simulation.schedule( new Gate.OutputChangeEvent(
			delay, this, 1, 0
		) );
		output = 0;
	}

	/** Return the new logic value, false unless the input is false.
         *  Every subclass of gate must define this.
	 *  @return the new logic value, a function of <TT>inputCounts</TT>;
	 */
	protected int logicValue() {
		int newOutput = 0;
		if (inputCounts[0] != 0) newOutput = 2;
		return newOutput;
	}
}
