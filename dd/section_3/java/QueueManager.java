package mts.queue;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueManager {
	private class ManageRide implements Runnable {
		private TaxiRide taxiRide;

		protected ManageRide(TaxiRide taxiRide) {
			this.taxiRide = taxiRide;
		}

		@Override
		public void run() {
			manageTaxiRide(taxiRide);
		}
	}

	private static final int TD_SEARCH_TIMEOUT_SEC = 10;
	private static final int RESPONSE_TIMEOUT_SEC = 10;
	private static final int THREADS = 15;

	private QueryManager queryManager;
	private Dispatcher dispatcher;
	private LocationManager locationManager;

	public QueueManager(QueryManager queryManager, Dispatcher dispatcher, LocationManager locationManager) {
		this.queryManager = queryManager;
		this.dispatcher = dispatcher;
		this.locationManager = locationManager;
	}

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
		Set<Zone> nearZones = taxiRideStartingZone.getNearZones();
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

	public void start() {
		// Start an Executor Service with a fixed capacity
		final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

		// Creates a worker thread that runs the Queue Management logic
		Thread worker = new Thread(new Runnable() {
			@Override
			public void run() {
				// During the execution of the service
				while (true) {
					// Pick a Taxi Ride from the pendant list and removes it
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
