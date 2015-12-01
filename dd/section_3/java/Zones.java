package mts.zones;

import java.util.HashSet;
import java.util.Set;

public class Zones {
	private final Set<Zone> zones;

	public Zones(Set<Zone> zones) {
		this.zones = new HashSet<>();
		this.zones.addAll(zones);
	}
	
	public Zone getContainingZone(GPSData gpsData)
	{
		Point p = new Point(gpsData.getLongitude(), gpsData.getLatitude());
		
		for (Zone z : zones)
			if (z.contains(p))
				return z;
		
		return null;
	}
}
