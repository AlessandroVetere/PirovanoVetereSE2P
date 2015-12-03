package mts.queue;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manage Queues related tasks.
 */
public class QueueManager {

	/**
	 * A class that wraps into a Runnable object the managing of a Taxi Ride.
	 */
	private class ManageRide implements Runnable {
		private final TaxiRide taxiRide;

		protected ManageRide(TaxiRide taxiRide) {
			this.taxiRide = taxiRide;
		}

		@Override
		public void run() {
			manageTaxiRide(taxiRide);
		}
	}

	/**
	 * The timeout after which the Queue Manager stop waiting for a Taxi Driver
	 * to come into an empty Queue.
	 */
	private static final int TD_SEARCH_TIMEOUT_SEC = 10;

	/**
	 * The timeout after which the Queue Manager stops waiting for a Taxi Driver
	 * to answer a Taxi Ride request.
	 */
	private static final int RESPONSE_TIMEOUT_SEC = 10;

	/**
	 * The maximum number of Taxi Rides that can be served in parallel.
	 */
	private static final int THREADS = 15;

	/**
	 * The reference to the Query Manager used to access the Model.
	 */
	private final QueryManager queryManager;

	/**
	 * The reference to the Dispatcher used to send messages and requests to
	 * Passengers and Taxi Drivers.
	 */
	private final Dispatcher dispatcher;

	/**
	 * The reference to the Location Manager used to access Google Maps API.
	 */
	private final LocationManager locationManager;

	/**
	 * Default constructor of a Queue Manager
	 * 
	 * @param queryManager
	 *            : a reference to the Query Manager
	 * @param dispatcher
	 *            : a reference to the Dispatcher
	 * @param locationManager
	 *            : a reference to the Location Manager
	 */
	public QueueManager(QueryManager queryManager, Dispatcher dispatcher, LocationManager locationManager) {
		this.queryManager = queryManager;
		this.dispatcher = dispatcher;
		this.locationManager = locationManager;
	}

	/**
	 * This method returns either a Taxi Driver that is willing to serve a Taxi
	 * Ride associated with the Zone passed as parameter, or a null reference in
	 * case no valid Taxi Driver can be found. The search starts from the Zone
	 * passed as parameter, and then moves to the Zone adjacent to that one. For
	 * each Zone the maximum time that a search can last is
	 * TD_SEARCH_TIMEOUT_SEC, then the whole search will last approximately at
	 * maximum: (1 + taxiRideStartingZone.getNearZones().size()) *
	 * TD_SEARCH_TIMEOUT_SEC.
	 * 
	 * @param taxiRideStartingZone
	 *            : the Zone in which the search process starts.
	 * @return taxiDriver : a Taxi Driver that is willing to serve the related
	 *         Taxi Ride. It is removed from the Queue in which he was.
	 * @return null : No Taxi Driver could be found.
	 */
	private TaxiDriver getTaxiDriver(Zone taxiRideStartingZone) {
		// Search for an available Taxi Driver in the Taxi Ride Starting Zone,
		// If found removes it from the queue and returns it;
		// BLOCKING METHOD, NULL returned after TD_SEARCH_TIMEOUT
		TaxiDriver taxiDriver = queryManager.dequeueTaxiDriver(taxiRideStartingZone, TD_SEARCH_TIMEOUT_SEC);

		// Taxi Driver found in Taxi Ride Starting Zone
		if (taxiDriver != null) {
			return taxiDriver;
		}

		// Taxi Driver not found in Taxi Ride Starting Zone
		// Search in near zones
		Set<Zone> nearZones = taxiRideStartingZone.getAdjacentZones();
		for (Zone nearZone : nearZones) {
			// Search for an available Taxi Driver in the selected Zone -
			// If found removes it from the queue and returns it;
			// BLOCKING METHOD, NULL returned after TD_SEARCH_TIMEOUT
			taxiDriver = queryManager.dequeueTaxiDriver(nearZone, TD_SEARCH_TIMEOUT_SEC);
			// Taxi driver found
			if (taxiDriver != null) {
				return taxiDriver;
			}
		}

		// No suitable Taxi Driver can be found
		return null;
	}

	/**
	 * This method handles the attribution of a Taxi Ride to a Taxi Driver (done
	 * in a maximum time approximately (1 +
	 * taxiRide.getStartingZone().getNearZones().size()) * TD_SEARCH_TIMEOUT_SEC
	 * in the worst case). If no valid Taxi Driver can be found, the Taxi Ride
	 * is reinserted in the set of Taxi Rides that are waiting to be served by
	 * the Queue Manager. If a valid Taxi Driver is found and he does not accept
	 * (or does not give a valid answer in RESPONSE_TIMEOUT_SEC) to serve the
	 * given Taxi Ride, then the same things happens, and the Taxi Driver is put
	 * last in his current Queue. If a valid Taxi Driver is found and he accepts
	 * the proposed Taxi Ride, the acceptation is stored in the Model, then the
	 * Taxi Driver status is updated and the Passenger is notified about the
	 * incoming Taxi Driver and his/her ETA.
	 * 
	 * @param taxiRide
	 *            : the Taxi Ride to be served.
	 */
	private final void manageTaxiRide(TaxiRide taxiRide) {
		// This call searches for a suitable Taxi Driver
		TaxiDriver taxiDriver = getTaxiDriver(taxiRide.getStartingZone());

		// This means that no suitable Taxi Driver has been found
		if (taxiDriver == null) {
			// Need To reinsert the Taxi Ride in the pendant list
			queryManager.insertTaxiRide(taxiRide);
			return;
		}

		// Ask Taxi Driver to accept the Taxi Ride Request
		boolean accepted = dispatcher.notifyTaxiDriver(taxiDriver, taxiRide, RESPONSE_TIMEOUT_SEC);

		if (!accepted) {
			// Need To reinsert the Taxi Ride in the pendant list
			queryManager.insertTaxiRide(taxiRide);
			// Reinsert the Taxi Driver in the Queue, so that he/she will be in
			// the last position of that Queue
			queryManager.enqueueTaxiDriver(taxiDriver);
		} else // Request accepted
		{
			// Add the taxiRide - taxiDriver match to the model
			queryManager.addRideDriverMatch(taxiRide, taxiDriver);
			// Set his/her status to WORKING
			queryManager.updateTaxiDriverStatus(taxiDriver, TaxiDriverStatus.WORKING);
			// Compute the Taxi Driver ETA
			int travelTime = locationManager.computeTravelTime(taxiDriver.getCurrentAddress(),
					taxiRide.getStartAddress());
			// Notify the TD ETA to the interested Passenger
			dispatcher.notifyPassenger(taxiRide.getPassenger(), taxiRide, travelTime);
		}
	}

	/**
	 * This method starts the activity of serving Taxi Rides. It is non
	 * blocking.
	 */
	public void start() {
		// Start an Executor Service with a fixed capacity
		final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

		// Creates a worker thread that runs the Queue Management logic
		Thread worker = new Thread(new Runnable() {
			@Override
			public void run() {
				// During the execution of the service
				while (true) {
					// Picks a Taxi Ride from the set of ones that are waiting
					// to be served and removes it
					// from the list - BLOCKING METHOD CALL
					TaxiRide taxiRide = queryManager.getPendingTaxiRide();
					// Execute the task to manage the picked ride
					executorService.submit(new ManageRide(taxiRide));
					// Loop
				}
			}
		});

		// Starts the worker thread
		worker.start();
	}
}
