
/** abstract class defining generic properties of logic gates
 *  @author Ryan Boehm
 *
 *  @see Wire
 *  @see MinGate
 *  @see MaxGate
 *  @see NegGate
 *  @see IsTGate
 *  @see IsFGate
 *  @see IsUGate
 *  @see TernaryLogic#findGate(String)
 */

import java.util.LinkedList;
import java.util.Scanner;

public abstract class Gate {

	private final LinkedList <Wire> outgoing = new LinkedList <Wire> ();
	/** setter method to add outgoing wires to this gate
	 *  @param w the wire that connects from this gate
	 */
	public void addOutgoing( Wire w ) {
		outgoing.add( w );
	}

	private int incount = 0;	// how many inputs are connected?
	/** setter method to add incoming wires to this gate
	 *  @param w the wire that connects to this gate
	 */
	public void addIncoming( Wire w ) {
		// actually, we don't need w, but the public doesn't know that
		incount = incount + 1;
	}

	public final String name;	// the name of the gate

	// Gripe:  We'd like to declare the following as final, but can't
	// because they're set by the subclass constructor
	public       int    inputs;	// the type of the gate
	public       float  delay;	// the type of the gate

	/** constructor needed by subclasses to set the final fields of gate
	 *  @param n	the name of the new gate
	 */
	protected Gate( String n ) {
		name = n;
	}

	/** factory method scans and processes one gate definition
	 *  @param sc	The scanner from which input is read to build the gate.
	 *  @return the newly constructed gate.
	 */
	public static Gate newGate( Scanner sc ) {
		String myName = ScanSupport.nextName( sc );
		if ("".equals( myName )) {
			Errors.warn(
				"gate has no name"
			);
			sc.nextLine();
			return null;
		}

		if (TernaryLogic.findGate( myName ) != null) {
			Errors.warn(
				"Gate '" + myName +
				"' redefined."
			);
			sc.nextLine();
			return null;
		}

		String myType = ScanSupport.nextName( sc );
		if ("min".equals( myType )) {
			return new MinGate( sc, myName );

		} else if ("max".equals( myType )) {
			return new MaxGate( sc, myName );

		} else if ("neg".equals( myType )) {
			return new NegGate( sc, myName );

		} else if ("isfalse".equals( myType )) {
			return new IsFGate( sc, myName );

		} else if ("istrue".equals( myType )) {
			return new IsTGate( sc, myName );

		} else if ("isunknown".equals( myType )) {
			return new IsUGate( sc, myName );

		} else {
			Errors.warn(
				"Gate '" + myName +
				"' '" + myType +
				"' has an illegal type."
			);
			sc.nextLine();
			return null;
		}
	}

	/** Scan gate's delay and line end to finish initialization;
	 *  this is always called at the end of the subclass constructor.
	 *  @param sc	The scanner from which input is read to build the gate.
	 */
	protected final void finishGate( Scanner sc ) {
		delay = sc.nextFloat();
		if (delay != delay) { // really asks if delay == NaN
			Errors.warn(
				this.myString() + " -- has no delay"
			);
		} else if (delay < 0.0f) {
			Errors.warn(
				this.myString() + " -- has negative delay."
			);
		}
		ScanSupport.lineEnd( sc, () -> this.myString() );
	}

	/** Get the representation of this Gate in the format of the input file.
	 *  It would have been nice to use toString here,
	 *  but Java does not permit that to be protected.
	 *  @return the string representation of this gate.
	 */
	protected String myString() {
		return(
			"gate " + name
		);
	}

	// ***** Logic Simulation *****

	// for logic values, inputCount[v] shows how many inputs have that value
	int inputCounts[] = new int[3];

	// all of the following are initially set to unknown
	int output = 1;   // this gate's most recently computed output value
	int current = 1;  // this gate's current output for printing
	int previous = 1; // this gate's previously printed output
	

	/** Sanity check for gates */
	public void check() {
		if (incount < inputs) {
			Errors.warn(
				this.myString() + " -- has missing inputs."
			);
		} else if (incount > inputs) {
			Errors.warn(
				this.myString() + " -- has too many inputs."
			);
		}

		// initially, all the inputs are unknown
		inputCounts[0] = 0;
		inputCounts[1] = inputs;
		inputCounts[2] = 0;

		// and initially, the output is unknown
		output = 1;

		// some subclasses will add to this behavior
	}
	
	/** The textual print value for this gate as required by MP5.
	 *  The first array index is the old value,
	 *  the second array index is the new value.
	 */
	private static final String[][] printValues = {
		{ "|    ", "|_   ", "|___ " },
		{ " _|  ", "  |  ", "  |_ " },
		{ " ___|", "   _|", "    |" }
	};

	/** Get the graphical display of this gate's output since the last
	 *  time this gate was checked.
	 *  @return	 The string to be displayed.
	 */
	public String printValue() {
		String r = printValues[previous][current];
		previous = current;
		return r;
	}

	/** Every subclass must define this function;
	 *  @return the new logic value, a function of <TT>inputCounts</TT>;
	 */
	protected abstract int logicValue();


	/** simulation class for an input change to this wire */
	public static final class InputChangeEvent extends Simulation.Event {
		private final Gate g;   // the gate with an input that changes
		private final int oldv; // the former value on g
		private final int newv; // the new value on g

		/** Construct an input change event
		 *  @param time	the time at which the input changes.
		 *  @param w	the gate where the input changes.
		 *  @param ov	the previous logic value carried over g.
		 *  @param nv	the new logic value carried over g.
		 */
		public InputChangeEvent( float time, Gate g, int ov, int nv) {
			super( time );
			this.g = g;
			this.oldv = ov;
			this.newv = nv;
		}

		
		/** Every event must provide a trigger method */
		public void trigger() {
			// decrement input counts
			g.inputCounts[oldv]--;
			g.inputCounts[newv]++;

			// new output value
			final int newOut = g.logicValue();

			if ( g.output != newOut ) {
				final int old = g.output; // set old output value
				Simulation.schedule( new OutputChangeEvent(
					time + g.delay, g, old, newOut
					) 
				);
				g.output = newOut; // reset output
			}
			
		}
	};


	/** simulation class for an output change to this wire */
	public static final class OutputChangeEvent extends Simulation.Event {
		private final Gate g;   // the gate where the output changes
		private final int oldv; // the former value on g
		private final int newv; // the new value on g

		/** Construct an output change event
		 *  @param time	the time at which the output changes.
		 *  @param g	the gate where the output changes.
		 *  @param ov	the previous logic value that entered g.
		 *  @param nv	the new logic value that exits g.
		 */
		public OutputChangeEvent( float time, Gate g, int ov, int nv ) {
			super( time );
			this.g = g;
			this.oldv = ov;
			this.newv = nv;
		}

		/** Every event must provide a trigger method */
		public void trigger() {
			for ( Wire w: g.outgoing ) {
				Simulation.schedule( new Wire.InputChangeEvent(
					time, w, oldv, newv
					) 
				);
			}
			g.current = newv;
		}
	}

}
