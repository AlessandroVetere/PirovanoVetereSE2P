package mts.queue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class TestMain {
	public static void main(String[] argc) {
		TestMain testMain = new TestMain();
		testMain.test();
	}

	private static class MockLocationManager implements LocationManager {
		@Override
		public int computeTravelTime(Address startAddress, Address arriveAddress) {
			return new Random().nextInt(10);
		}
	}

	private static class MockDispatcher implements Dispatcher {
		@Override
		public void notifyPassenger(Passenger passenger, TaxiRide taxiRide, int travelTime) {
			System.out.println("Sending to passenger " + passenger.getUsername() + " ETA of " + travelTime
					+ " for ride from " + taxiRide.getStartAddress().getName() + " to "
					+ taxiRide.getArriveAddress().getName() + " at " + taxiRide.getStartDateTime());
		}

		@Override
		public boolean notifyTaxiDriver(TaxiDriver taxiDriver, TaxiRide taxiRide, int responseTimeout) {
			System.out.println("Sending to taxi driver " + taxiDriver.getDriverID() + " a taxi ride from "
					+ taxiRide.getPassenger().getUsername());
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
			}
			boolean response = new Random().nextBoolean();
			System.out.println("Taxi driver " + taxiDriver.getDriverID() + " said: " + response);
			return response;
		}
	}

	private static class MockZone implements Zone {
		private Set<Zone> zones = new HashSet<>();

		@Override
		public Set<Zone> getNearZones() {
			return zones;
		}

		@Override
		public void addNearZone(Zone zone) {
			zones.add(zone);
		}
	}

	private static class MockTaxiDriver implements TaxiDriver {
		private Address currentAddress;
		private String driverID;

		public MockTaxiDriver(Address currentAddress, String driverID) {
			this.currentAddress = currentAddress;
			this.driverID = driverID;
		}

		@Override
		public Address getCurrentAddress() {
			return currentAddress;
		}

		@Override
		public String getDriverID() {
			return driverID;
		}
	}

	private static class MockPassenger implements Passenger {
		private String username;

		public MockPassenger(String username) {
			this.username = username;
		}

		@Override
		public String getUsername() {
			return username;
		}
	}

	private static class MockAddress implements Address {
		private String name;

		public MockAddress(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	private static class MockTaxiRide implements TaxiRide {
		private Address arriveAddress;
		private Passenger passenger;
		private Address startAddress;
		private Zone startingZone;
		private String startDateTime;

		public MockTaxiRide(Address arriveAddress, Passenger passenger, Address startAddress, Zone startingZone,
				String startDateTime) {
			this.arriveAddress = arriveAddress;
			this.passenger = passenger;
			this.startAddress = startAddress;
			this.startingZone = startingZone;
			this.startDateTime = startDateTime;
		}

		@Override
		public Address getArriveAddress() {
			return this.arriveAddress;
		}

		@Override
		public Passenger getPassenger() {
			return this.passenger;
		}

		@Override
		public Address getStartAddress() {
			return this.startAddress;
		}

		@Override
		public Zone getStartingZone() {
			return this.startingZone;
		}

		@Override
		public String getStartDateTime() {
			return this.startDateTime;
		}
	}

	private static class MockQueryManager implements QueryManager {
		// Simuation of the model
		private final Map<Zone, Queue<TaxiDriver>> zonesQueuesMap = new HashMap<>();
		private final Map<Address, Zone> addressesZonesMap = new HashMap<>();
		// Let's forget about the ordering for simplicity sake
		private final Set<TaxiRide> taxiRides = new HashSet<>();

		public MockQueryManager() {
			Zone zone_one = new MockZone();
			Zone zone_two = new MockZone();
			zone_one.addNearZone(zone_two);
			zone_two.addNearZone(zone_one);
			this.zonesQueuesMap.put(zone_one, new LinkedList<TaxiDriver>());
			this.zonesQueuesMap.put(zone_two, new LinkedList<TaxiDriver>());
			final Address address_one = new MockAddress("Via Indirizzo Uno");
			final Address address_two = new MockAddress("Via Indirizzo Due");
			addressesZonesMap.put(address_two, zone_two);
			addressesZonesMap.put(address_one, zone_one);
			TaxiDriver taxiDriverAlessandra = new MockTaxiDriver(address_one, "Alessandra");
			TaxiDriver taxiDriverJafar = new MockTaxiDriver(address_two, "Jafar");
			TaxiDriver taxiDriverJake = new MockTaxiDriver(address_two, "Jake");
			zonesQueuesMap.get(addressesZonesMap.get(taxiDriverAlessandra.getCurrentAddress()))
					.add(taxiDriverAlessandra);
			zonesQueuesMap.get(addressesZonesMap.get(taxiDriverJafar.getCurrentAddress())).add(taxiDriverJafar);
			zonesQueuesMap.get(addressesZonesMap.get(taxiDriverJake.getCurrentAddress())).add(taxiDriverJake);
			final Passenger dino = new MockPassenger("Dino");
			final Passenger carlo = new MockPassenger("Carlo");
			TaxiRide taxiRideDino = new MockTaxiRide(address_two, dino, address_one, addressesZonesMap.get(address_one),
					"10/10/2010 15:40");
			TaxiRide taxiRideCarlo = new MockTaxiRide(address_one, carlo, address_two,
					addressesZonesMap.get(address_two), "19/02/2014 14:43");
			taxiRides.add(taxiRideDino);
			taxiRides.add(taxiRideCarlo);
		}

		@Override
		public void enqueueTaxiDriver(TaxiDriver taxiDriver) {
			Zone taxiDriverZone = this.addressesZonesMap.get(taxiDriver.getCurrentAddress());
			Queue<TaxiDriver> taxiDriverQueue = zonesQueuesMap.get(taxiDriverZone);
			synchronized (taxiDriverQueue) {
				taxiDriverQueue.add(taxiDriver);
			}
		}

		@Override
		public TaxiDriver dequeueTaxiDriver(Zone taxiRideStartingZone, int timeout) {
			final Queue<TaxiDriver> taxiDriverQueue = zonesQueuesMap.get(taxiRideStartingZone);
			// Dirty solution, a spurious wakeup could lead to
			// taxiDriverQueue.poll() == null,
			// BUT
			// We do not care, in that case, the taxi ride will be served later.

			synchronized (taxiDriverQueue) {
				if (taxiDriverQueue.isEmpty()) {
					try {
						taxiDriverQueue.wait(timeout * 1000);
					} catch (InterruptedException e) {
						return null;
					}
				}

				return taxiDriverQueue.poll();
			}
		}

		@Override
		public TaxiRide getPendingTaxiRide() {
			synchronized (taxiRides) {
				while (taxiRides.isEmpty()) {
					try {
						taxiRides.wait();
					} catch (InterruptedException e) {
					}
				}

				TaxiRide taxiRide = taxiRides.iterator().next();
				taxiRides.remove(taxiRide);
				return taxiRide;
			}
		}

		@Override
		public void insertTaxiRide(TaxiRide taxiRide) {
			synchronized (taxiRides) {
				taxiRides.add(taxiRide);
				taxiRides.notify();
			}
		}

		@Override
		public void updateTaxiDriverStatus(TaxiDriver taxiDriver, TaxiDriverStatus notAvailable) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addRideDriverMatch(TaxiRide taxiRide, TaxiDriver taxiDriver) {
			// TODO Auto-generated method stub
		}
	}

	private void test() {
		LocationManager locationManager = new MockLocationManager();

		Dispatcher dispatcher = new MockDispatcher();

		QueryManager queryManager = new MockQueryManager();

		QueueManager queueManager = new QueueManager(queryManager, dispatcher, locationManager);

		queueManager.start();
	}
}
