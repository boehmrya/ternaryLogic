import java.util.PriorityQueue;

/** Discrete event simulation support framework
 *  @author Douglas Jones
 *  @version 2017-04-17
 *  A new simulation framework based on lecture from Apr. 17.
 */
class Simulation {

        /** Events are the core of the control structure of the simulation.
         */
        public static abstract class Event {
                /** Each event has a time */
                float time;

                /** Construct and initialize a new <code>Event</code>.
                 *  @param t    the <code>time</code> of the event.
                 */
                Event( float t ) {
                        time = t;
                };

                /** Trigger the event.
                 *  Every subclass of Event must provide a trigger method
                 */
                public abstract void trigger();
        }

	/** Events are queued for {@code run} retrieve in chronological order.
	 */
	private static final PriorityQueue <Event> eventSet =
		new PriorityQueue <Event> (
			(Event e1, Event e2)->Float.compare( e1.time, e2.time )
		);

	/** Users call schedule to schedule an event at its inherent time.
	 *  usually a later time but possibly the current time.
	 *  @param e specifies when the event should occur.
	 */
	public static void schedule( Event e ) {
		eventSet.add( e );
	}

	/** the main program should build the model,
	 *  this inolves scheduling some initial events
	 *  and then, just once, it should call {@code run}.
	 */
	public static void run() {
		while (!eventSet.isEmpty()) {
			Event e = eventSet.remove();
			e.trigger();
		}
	}
}
