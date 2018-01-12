import java.util.Scanner;

/** the is-undefined gate, a kind of gate.
 *  @author Ryan Boehm
 *  @see Gate
 */
public class IsUGate extends Gate {
	/** initializer scans and processes one is-undefined gate
	 *  @param sc Scanner from which gate description is read
	 *  @param myName the value to be put in the name field
	 */
	IsUGate( Scanner sc, String myName ) {
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
			this.myString() + " isundefined " + delay
		);
	}

	// ***** Logic Simulation for IsUGate *****

	/** Sanity check for IsUGate */
	public void check() {
		super.check();

		// now change the output from unknown to true
		Simulation.schedule( new Gate.OutputChangeEvent(
			delay, this, 1, 2
		) );
		output = 2;
	}

        /** Return the new logic value, false unless the input is undefined.
         *  Every subclass of gate must define this.
         *  @return the new logic value, a function of <TT>inputCounts</TT>;
         */
	protected int logicValue() {
		int newOutput = 0;
		if (inputCounts[1] != 0) newOutput = 2;
		return newOutput;
	}
}
