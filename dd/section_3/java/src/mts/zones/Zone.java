package mts.zones;

import java.util.HashSet;
import java.util.Set;

public class Zone {
	private final Set<Triangle> triangles;

	public Zone(Set<Triangle> triangles) {
		this.triangles = new HashSet<>();
		this.triangles.addAll(triangles);
	}

	public boolean contains(Point p) {
		for (Triangle t : triangles)
			if (t.contains(p))
				return true;

		return false;
	}
}
