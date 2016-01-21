package mts.queue;

public interface Dispatcher {

	void notifyPassenger(Passenger passenger, TaxiRide taxiRide, int travelTime);

	boolean notifyTaxiDriver(TaxiDriver taxiDriver, TaxiRide taxiRide, int responseTimeout);

}
