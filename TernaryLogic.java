import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** TernaryLogic -- The main class of a ternary logic simulator.
 *
 *  This reads a description of a ternary logic circuit, builds a model,
 *  and if that model passes sanity checks, runs a simulation of that
 *  circuit.
 *
 *  It runs from the command line, with three command line arguments,
 *  the input file name, the interval between successive outputs, and the
 *  total duration of the simulation.
 *
 *  @author Douglas Jones
 *  @version 2017-04-20
 *  This code is largely ripped from Version 2017-04-06, with comment changes.
 *
 *  @see Wire
 *  @see Gate
 *  @see Errors
 *  @see Simulation
 *  @see ScanSupport
 *  @see #main
 */
public class TernaryLogic {
	// lists of roads and intersectins
	static final LinkedList <Wire> wires
		= new LinkedList <Wire> ();
	static final LinkedList <Gate> gates
		= new LinkedList <Gate> ();

	/** utility method to look up an gate by name
	 *  @param s is the name of the gate, a string
	 *  @return is the Gate object with that name
	 */
	public static Gate findGate( String s ) {
		for ( Gate g: gates ) {
			if (g.name.equals( s )) return g;
		}
		return null;
	}

	/** read a ternary logic system.
	 *  @param sc the scanner from which the system is read.
	 */
	public static void initializeTernary( Scanner sc ) {
		while (sc.hasNext()) {
			// until we hit the end of the file
			String command = ScanSupport.nextName( sc );
			if ("gate".equals( command )) {
				Gate g = Gate.newGate( sc );
				if (g != null) gates.add( g );

			} else if ("wire".equals( command )) {
				wires.add( new Wire( sc ) );

			} else if ("".equals( command )) { // blank or comment
				// line holding -- ends up here!
				ScanSupport.lineEnd( sc, () -> "Line" );

			} else {
				Errors.warn(
					"Command '" + command +
					"' is not gate or wire"
				);
				sc.nextLine(); // skip the rest of the error
			}
		}
	}

        /** Check the sanity of the network.
         *  @see Gate#check
         */
        public static void checkNetwork() {
                for ( Gate g: gates ) {
                        g.check();
                }
                // we could also go through the wires,
		// but there's nothing to check there.
        }

	/** write out a ternary logic system
	 */
	public static void writeTernary() {
		for ( Gate g: gates ) {
			System.out.println( g.toString() );
		}
		for ( Wire w: wires ) {
			System.out.println( w.toString() );
		}
	}

	/** output headline for logic output
	 *  @param i	the interval between successive outputs
	 */
	public static void initPrint( float i ) {
		Simulation.schedule( new PrintEvent( 0.0f, i ) );

		for( Gate g: gates ) {
			System.out.print( " " + g.name );
		}
		System.out.println();
	}

	/** Output print event */
	private static final class PrintEvent extends Simulation.Event {
		private final float printInterval;

		/** Construct a print event
                 *  @param time	the time at which to print.
                 *  @param i 	the interval between print events.
                 */
                public PrintEvent( float time, float i ) {
                        super( time );
                        printInterval = i;
                }
		
		/** Every event must provide a trigger method */
		public void trigger() {
			for( Gate g: gates ) {
				System.out.print( " " + g.printValue() );
			}
			System.out.println();

			Simulation.schedule( new PrintEvent(
				time + printInterval,
				printInterval
			) );
		}
	}

	/** Terminate Simulation Event */
	private static class ExitEvent extends Simulation.Event {
		public ExitEvent( float t ) {
			super( t );
		}
		/** Every event must provide a trigger method */
		public void trigger() {
			System.exit( 0 );
		}
	}

	/** main program that reads and writes a road network
	 *  @param args the command line arguments must hold one file name
	 */
	public static void main( String[] args ) {
		// verify that the argument exists.
		if (args.length < 1) {
			Errors.fatal( "Missing file name on command line" );
		} else if (args.length < 2) {
			Errors.fatal( "Missing interval on command line" );
		} else if (args.length < 3) {
			Errors.fatal( "Missing time limit on command line" );
		} else if (args.length > 3) {
			Errors.fatal( "Unexpected command line args" );

		} else try {
			initializeTernary( new Scanner( new File( args[0] ) ) );
			checkNetwork();
			if (Errors.count() > 0) {
				writeTernary();
			} else try {
				initPrint( Float.parseFloat( args[1] ) );
				Simulation.schedule( new ExitEvent(
					Float.parseFloat( args[2] )
				) );
				Simulation.run();
			} catch (NumberFormatException e) {
				// Bug: The error message is wrong for args[2]
				Errors.fatal(
					"'" + args[1] +
					"' is not an floating print interval"
				);
			}

		} catch (FileNotFoundException e) {
			Errors.fatal( "Could not read '" + args[0] + "'" );
		}
	}
}
