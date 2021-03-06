package checkInModel;

/**
 * Import packages to manipulate data structures.
 */
import java.util.ArrayList;
import java.util.Iterator;

/**
 * CheckInDeskCollection
 * Manages a collection of check in desks within
 * its own thread.
 * @author jamiehill
 *
 */
public class CheckInDeskCollection implements Runnable {

	/**
	 * Book and flight collection.
	 */
	private volatile FlightCollection allFlights;
	private volatile BookingCollection allBookings;
	
	/**
	 * Logging Singleton instance.
	 */
	private LoggingSingleton log;
	
	/**
	 * Thread blocking boolean, for data manipulation across threads.
	 */
	private volatile boolean inUse;
	
	/**
	 * Collection of check in desks
	 */
	private volatile ArrayList<CheckInDesk> checkInDesks = new ArrayList<CheckInDesk>();
	
	/**
	 * Total number of check in desks our system runs
	 */
	private int totalCheckInDesks = 5;
	
	/**
	 * CheckInDeskCollection
	 * Constructor, creates the check in desk collection object.
	 * @param allFlights
	 * @param allBookings
	 */
	public CheckInDeskCollection(FlightCollection allFlights, BookingCollection allBookings) {
		/**
		 * Get our logging singleton instance.
		 */
		log = LoggingSingleton.getInstance();
		/**
		 * Store our flights and bookings.
		 */
		this.allFlights = allFlights;
		this.allBookings = allBookings;
	}

	/**
	 * getTotalDesks
	 * Returns the total number of check in desks
	 * the system is using.
	 * @return int
	 */
	public int getTotalDesks() {
		return totalCheckInDesks;
	}

	/**
	 * run
	 * Runs the check in desk collection thread which
	 * starts new check in desks if there are flights
	 * ready to board passengers and removes check in
	 * desks when a flight has departed.
	 */
	@Override
	public synchronized void run() {
		/**
		 * Get all the flights to iterate through.
		 */
 		Iterator<Flight> allFlightsIt = allFlights.getFlightCollection().iterator();
 		log.addLog("Processing " + allFlights.getFlightCollection().size() + " flights for check in", "CheckInDeskCollection");
		/**
		 * Get exclusive access to the check in desk array list, so
		 * that no other thread modifies it during this operation.
		 */
		takeInUse();
 		while(allFlightsIt.hasNext()) {
			Flight aFlight = allFlightsIt.next();
			FlightStatus status = aFlight.getFlightStatus();
			log.addLog("Processing flight " + aFlight.getFlightCode() + " which is " + status, "CheckInDeskCollection");
			if(status.compareTo(FlightStatus.READY) == 0) {
				int freeThread = getFreeDesk();
				if(freeThread > -1) {
					/**
					 * If a flight has a ready status and there is a free thread slot
					 * to run a check in desk, create a new check in desk with this
					 * flights information.
					 */
					aFlight.setHasCheckInDesk();
					checkInDesks.add(new CheckInDesk(aFlight, allBookings, freeThread));
					log.addLog("Opened Check In Desk for flight " + aFlight.getFlightCode() + " at " + freeThread, "CheckInDeskCollection");
				} else {
					/**
					 * If not delay the flights departure.
					 */
					log.addLog("Added delay to flight " + aFlight.getFlightCode(), "CheckInDeskCollection");
					aFlight.addDelay();
				}
			} else if(status.compareTo(FlightStatus.DEPARTED) == 0) {
				ArrayList<Booking> missedFlight = allBookings.getBookingsByFlightCode(aFlight.getFlightCode());
				if(missedFlight.size() > 0) {
					Iterator<Booking> missedFlightIt = missedFlight.iterator();
					while(missedFlightIt.hasNext()) {
						Booking aBooking = missedFlightIt.next();
						log.addLog("Passenger " + aBooking.getPassenger().getFirstName() + " " + aBooking.getPassenger().getLastName() + " has missed flight " + aFlight.getFlightCode(), "CheckInDeskCollection");
						aBooking.getPassenger().setInQueue(PassengerQueues.MISSED_FLIGHT);
					}
				}
			}
		}
		/**
		 * Release access to the booking hash map.
		 */
 		freeInUse();
 		/**
 		 * Remove any check in desks that have finished boarding passengers.
 		 */
		removeDepartedCheckInDesk();
	}

	/**
	 * getFreeDesk
	 * Returns the index of the next free desk.
	 * @return
	 */
	private synchronized int getFreeDesk() {
		int free = -1;
		if(checkInDesks.size() < totalCheckInDesks) {
			/**
			 * If the number of check in desks in the
			 * check in desk array list is less than
			 * the total number of check in desks
			 * allowed in the system, then take the
			 * next index and return that as the next
			 * free slot to open a check in desk at.
			 */
			free = checkInDesks.size();
		}
		return free;
	}
	
	/**
	 * removeDepartedCheckInDesk
	 * Removes any check in desk that has a
	 * thread state of Terminated.
	 */
	private synchronized void removeDepartedCheckInDesk() {
		/**
		 * Get exclusive access to the check in desk array list, so
		 * that no other thread modifies it during this operation.
		 */
		takeInUse();
		int count = 0;
		while(count < checkInDesks.size()) {
			/**
			 * Loop through the check in desks 
			 * and remove any that have a thread
			 *  terminated state.
			 */
			CheckInDesk aDesk = checkInDesks.get(count);
			if(aDesk.getThreadState() == Thread.State.TERMINATED) {
				checkInDesks.remove(count);
			}
			count++;
		}
		/**
		 * Release access to the booking hash map.
		 */
		freeInUse();
	}

	/**
	 * getCheckInDesks
	 * Returns the array list of check in desks
	 * for the UI to be able to display information
	 * on them.
	 * @return ArrayList CheckInDesk
	 */
	public ArrayList<CheckInDesk> getCheckInDesks() {
		return checkInDesks;
	}

	/**
	 * takeInUse
	 * Waits for the inUse flag to be false and then
	 * sets it to true so other lines can be executed
	 * in a thread safe way.
	 */
	private synchronized void takeInUse() {
		while (inUse) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		inUse = true;
	}
	
	/**
	 * freeInUse
	 * Sets the inUse flag to false and notifies all other threads.
	 */
	public synchronized void freeInUse() {
		inUse = false;
		notifyAll();
	}
}
