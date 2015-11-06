//TODO one != some

/** === SIGNATURES === **/

abstract sig Boolean {}

one sig True extends Boolean {}

one sig False extends Boolean {}

abstract sig TaxiRide
{
	departureAddress : one Address,
	arrivalAddress : one Address
}

sig TaxiReservation extends TaxiRide {}

sig TaxiRequest extends TaxiRide {}

sig Username {}

abstract sig User
{
	username : one Username
}

sig RegisteredPassenger extends User
{
 	taxiRequest: lone TaxiRequest,
	taxiReservations: set TaxiReservation
}

sig TaxiDriver extends User
{
	taxiCar : one TaxiCar,
	queueManager : one QueueManager,
	taxiRide : lone TaxiRide,
	problem : lone Problem
}

sig Problem
{
	canContinueWorking : one Boolean
}

sig TaxiCar {}

sig TaxiDriversQueue
{
  	taxiDrivers : set TaxiDriver,
	zone : one Zone
}

sig Zone
{
	city : one City
}

one sig City {}

sig Address
{
	zone : one Zone
}

one sig QueueManager
{
	taxiDriversQueue : set TaxiDriversQueue
}

one sig PendingTaxiRidesList
{
	taxiRides : set TaxiRide
}

/** === FACTS === **/

fact
{
	//No useless Boolean
	Problem.canContinueWorking = Boolean
	
	//No useless Username
	User.username = Username

	//No useless Problem
	TaxiDriver.problem = Problem
	
	//No useless TaxiCar
	TaxiDriver.taxiCar = TaxiCar
	
	//No useless Address
	TaxiRide.departureAddress + TaxiRide.arrivalAddress = Address
	
	//At least two Address in a city
	#Address > 1

	//At least a Address per zone
	all z : Zone | (one a : Address | a.zone = z)

	//No useless City
	Zone.city = City

	//No useless QueueManager
	TaxiDriver.queueManager = QueueManager

	//No useless TaxiDriversQueue
	QueueManager.taxiDriversQueue = TaxiDriversQueue

	//Unique username for all users
	all u1, u2 : User | u1 != u2 iff u1.username != u2.username	

	//Every zone is associated to a to at least one TaxiDriversQueue
	all z : Zone | z in TaxiDriversQueue.zone

	//Different TaxiDriversQueue are associated to different Zone
	all tdq1, tdq2 : TaxiDriversQueue | tdq1 != tdq2 implies tdq1.zone != tdq2.zone	

	//Two different TaxiDriversQueue have no TaxiDriver in common
	all tdq1, tdq2 : TaxiDriversQueue | (tdq1 != tdq2) implies #(tdq1.taxiDrivers & tdq2.taxiDrivers) = 0

	//If a TaxiDriver is not doing a TaxiRide and has no Problem, he is in a TaxiDriversQueue
	all td : TaxiDriver | (#td.problem = 0 and #td.taxiRide = 0) implies (one tdq : TaxiDriversQueue | td in tdq.taxiDrivers)

	//If a TaxiDriver is doing a TaxiRide, he is in no TaxiDriversQueue
	all td : TaxiDriver | #td.taxiRide > 0 implies not (one tdq : TaxiDriversQueue | td in tdq.taxiDrivers)

	//Each TaxiDriver must drive a different TaxiCar
	all td1, td2 : TaxiDriver | td1 != td2 implies td1.taxiCar != td2.taxiCar

	//Different TaxiDriversQueue are associated to different Zone
	all tdq1, tdq2 : TaxiDriversQueue | tdq1 != tdq2 implies tdq1.zone != tdq2.zone

	//All TaxiRide have the DepartureAddress different from the ArrivalAddress
	all tr : TaxiRide | tr.departureAddress != tr.arrivalAddress

	//No TaxiRide is at the same time in the PendingTaxiRidesList and served by a TaxiDriver
	no tr : TaxiRide | tr in PendingTaxiRidesList.taxiRides and tr in TaxiDriver.taxiRide

	//All TaxiDriver that are serving a TaxiRide are also driving a TaxiCar
	all td : TaxiDriver | #td.taxiRide > 0 implies #td.taxiCar > 0
	
	//All TaxiDriver that have a Problem and that problem cannot let them continue working, must be serving no TaxiRide
	all td : TaxiDriver | (#td.problem > 0 and td.problem.canContinueWorking = False) implies (#td.taxiRide = 0)

	//Al TaxiDriver that have a Problem and are also serving a TaxiRide, must have a Problem that let them continue working
	all td : TaxiDriver | (#td.problem > 0 and #td.taxiRide > 0) implies (td.problem.canContinueWorking = True)

	//A TaxiRide is either in PendingTaxiRidesList or being served by a TaxiDriver
	all tr : TaxiRide | (tr in PendingTaxiRidesList.taxiRides) iff (not (tr in TaxiDriver.taxiRide))

	//Every TaxiRide is either served or pending
	TaxiDriver.taxiRide + PendingTaxiRidesList.taxiRides = TaxiRide

	//Every TaxiRide is either a requested or reserved
	RegisteredPassenger.taxiRequest + RegisteredPassenger.taxiReservations = TaxiRide

	//Different TaxiDriver are associated to different TaxiRide
	all td1, td2 : TaxiDriver | td1 != td2 implies td1.taxiRide != td2.taxiRide

	//Different TaxiReservation cannot be served to a single RegisteredPassenger
	no tr1, tr2 : TaxiReservation | tr1 != tr2 and (tr1 in TaxiDriver.taxiRide) and (tr2 in TaxiDriver.taxiRide) and (one rp : RegisteredPassenger | (tr1 in rp.taxiReservations) and (tr2 in rp.taxiReservations))

	//No RegisteredPassenged can have served at the same time a TaxiRequest and a TaxiReservation
	no tres : TaxiReservation, treq : TaxiRequest |  (tres in TaxiDriver.taxiRide) and (treq in TaxiDriver.taxiRide) and (one rp : RegisteredPassenger | (tres in rp.taxiReservations) and (treq in rp.taxiRequest))
}


/** === ASSERTIONS === **/

//A TaxiCar is driven by only a TaxiDriver
assert TaxiCarOnlyDrivenByOneTaxiDriver
{
	all tc : TaxiCar | one td : TaxiDriver | td.taxiCar = tc and (all td1 : TaxiDriver | td1 != td implies td1.taxiCar != tc)
}

//No TaxiDriver can have a serious Problem and be serving a TaxiRide
assert NoTaxiDriverWithSeriousProblemCanBeServingATaxiDrive
{
	no td : TaxiDriver | #(td.problem) > 0 and td.problem.canContinueWorking = False and  #(td.taxiRide) > 0
}

//All TaxiRides are RegisteredPassenger's generate (by at least one RegisteredPassenger, given the possibility of having TaxiSharing)
assert RideAreIssuedByAtLeastARegisteredPassenger
{
	all tr : TaxiRide | one rp : RegisteredPassenger | (tr in rp.taxiRequest) or (tr in rp.taxiReservations)
}

/** === FUNCTIONS === **/

/** === PREDICATES === **/

pred show
{
	#TaxiRide > 4
	#TaxiDriver.taxiRide > 0
	#Zone > 2
}

/** === EXECUTIONS === **/

check TaxiCarOnlyDrivenByOneTaxiDriver for 6

check NoTaxiDriverWithSeriousProblemCanBeServingATaxiDrive for 6

check RideAreIssuedByAtLeastARegisteredPassenger for 6

run show for 6
