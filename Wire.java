import java.util.Scanner;

/** Wires are link by gates.
 *
 *  @author Douglas Jones
 *  @version 2017-04-17
 *
 *  @see Gate
 *  @see Errors
 *  @see TernaryLogic#findGate(String)
 */
class Wire {
	private final float delay;	// time delay of this wire
	private final Gate destination;	// where wire goes, or null
	private final Gate source;	// source of wire, or null
	// Wire name is the source-destination names

	/** Initializer scans and processes one wire definition.
	 *  @param sc The scanner from which the definition is read.
	 */
	public Wire( Scanner sc ) {
		// textual names of source and dest
		String srcName = ScanSupport.nextName( sc );
		String dstName = ScanSupport.nextName( sc );
		// if there are no next names on this line, these are ""
		// therefore, the findGate calls below will fail

		// lookup names of source and dest
		source = TernaryLogic.findGate( srcName );
		if (source == null) {
			Errors.warn(
				"Wire '" + srcName +
				"' '" + dstName +
				"' source undefined."
			);
		}
		destination = TernaryLogic.findGate( dstName );
		if (destination == null) {
			Errors.warn(
				"Wire '" + srcName +
				"' '" + dstName +
				"' destination undefined."
			);
		}

		delay = ScanSupport.nextFloat( sc );
		if (delay != delay) { // really asks if delay == NaN
			Errors.warn(
				"Wire '" + srcName +
				"' '" + dstName +
				"' has no delay."
			);
		} else if (delay < 0.0f) {
			Errors.warn(
				"Wire '" + srcName +
				"' '" + dstName +
				"' '" + delay +
				"' has negative delay."
			);
		}
		ScanSupport.lineEnd( sc, () -> this.toString() );

		// Now, tell the gates that they've been wired together
		if (destination != null) destination.addIncoming( this );
		if (source != null) source.addOutgoing( this );
	}

	/** Convert this wire to a format like that used for input
	 *  @return	The textual description of the wire
	 */
	public String toString() {
		String srcName;
		String dstName;

		if (source == null) {
			srcName = "???";
		} else {
			srcName = source.name;
		}

		if (destination == null) {
			dstName = "???";
		} else {
			dstName = destination.name;
		}
		
		return(
			"wire " + srcName + " " +
			dstName + " " +
			delay
		);
	}

	// ***** Logic Simulation *****

	/** simulation class for an input change to this wire */
	public static final class InputChangeEvent extends Simulation.Event {
		private final Wire w;   // the wire with an input that changes
		private final int oldv; // the former value on w
		private final int newv; // the new value on w

		/** Construct an input change event
		 *  @param time	the time at which the input changes.
		 *  @param w	the wire where the input changes.
		 *  @param ov	the previous logic value carried over w.
		 *  @param nv	the new logic value carried over w.
		 */
		public InputChangeEvent( float time, Wire w, int ov, int nv) {
			super( time );
			this.w = w;
			this.oldv = ov;
			this.newv = nv;
		}

		/** Every event must provide a trigger method */
		public void trigger() {
			Simulation.schedule( new OutputChangeEvent(
				time + w.delay, w, oldv, newv
			) );
		}
	};

	/** simulation class for an output change to this wire */
	public static final class OutputChangeEvent extends Simulation.Event {
		private final Wire w;   // the wire with an output that changes
		private final int oldv; // the former value on w
		private final int newv; // the new value on w

		/** Construct an output change event
		 *  @param time	the time at which the output changes.
		 *  @param w	the wire where the output changes.
		 *  @param ov	the previous logic value carried over w.
		 *  @param nv	the new logic value carried over w.
		 */
		public OutputChangeEvent( float time, Wire w, int ov, int nv ) {
			super( time );
			this.w = w;
			this.oldv = ov;
			this.newv = nv;
		}

		/** Every event must provide a trigger method */
		public void trigger() {
			Simulation.schedule( new Gate.InputChangeEvent(
				time, w.destination, oldv, newv
			) );
		}
	}
}
