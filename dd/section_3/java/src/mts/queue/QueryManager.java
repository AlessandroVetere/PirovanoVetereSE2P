package mts.queue;

public interface QueryManager {

	void enqueueTaxiDriver(TaxiDriver taxiDriver);

	TaxiDriver dequeueTaxiDriver(Zone taxiRideStartingZone, int timeout);

	TaxiRide getPendingTaxiRide();

	void insertTaxiRide(TaxiRide taxiRide);

	void updateTaxiDriverStatus(TaxiDriver taxiDriver, TaxiDriverStatus notAvailable);

	void addRideDriverMatch(TaxiRide taxiRide, TaxiDriver taxiDriver);

}
