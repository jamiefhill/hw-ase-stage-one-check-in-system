package CheckIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBookingGenerator implements Runnable {

	private LoggingSingleton log;
	private String[] passengerFirstNames = { "John", "Sally", "Haikah", "Nadia", "Sam", "Amy", "Jamie", "Kira", "Isambard", "Tyler" };
	private String[] passengerLastNames = { "McFarland", "Abulhawa", "Ghoghari", "Muir", "Hill", "Smith", "Johnson", "Brunel", "Kirk", "Riker" };
	private FlightCollection flights;
	private BookingCollection bookings;
	
	public RandomBookingGenerator(FlightCollection flights, BookingCollection bookings) {
		log = LoggingSingleton.getInstance();
		this.flights = flights;
		this.bookings = bookings;
	}

	private synchronized ArrayList<Booking> getRandomBookings(String flightCode, int amount) {
		int count = 0;
		ArrayList<Booking> randomBookings = new ArrayList<Booking>();
		while(count < amount) {
			String firstName = pickRandomFromList(passengerFirstNames);
			String lastName = pickRandomFromList(passengerLastNames);
			try {
				randomBookings.add(new Booking(flightCode + "-" + RandomFlightGenerator.getNumericCode(), firstName, lastName, flightCode));
			} catch (BookingException e) {
				log.addLog("There was an error creating the random booking " + e.getMessage());
			}
			count++;
		}
		return randomBookings;
	}
	
	private String pickRandomFromList(String[] passengerNames) {
		int index = ThreadLocalRandom.current().nextInt(0, passengerNames.length - 1);
		return passengerNames[index];
	}

	@Override
	public void run() {
		log.addLog("Creating bookings");
		Iterator<Flight> flightsIt = flights.getFlightCollection().iterator();
		while(flightsIt.hasNext()) {
			Flight aFlight = flightsIt.next();
			ArrayList<Booking> allBookings = bookings.getBookingByFlightCode(aFlight.getFlightCode());
			log.addLog("Flight " + aFlight.getFlightCode() + " has " + allBookings.size() + " bookings with a total capacity of " + aFlight.getMaximumPassengers());
			if(bookings.getBookingByFlightCode(aFlight.getFlightCode()).size() <= 0) {
				int thirtyPercCapacity = (int) ((int) aFlight.getMaximumPassengers() * 0.3);
				int createBookings = ThreadLocalRandom.current().nextInt(thirtyPercCapacity, aFlight.getMaximumPassengers());
				log.addLog("Creating " + createBookings + " for flight " + aFlight.getFlightCode());
				ArrayList<Booking> newBookings = getRandomBookings(aFlight.getFlightCode(), createBookings);
				bookings.addBatchBookings(newBookings);
			}
		}
	}
}