package mts.zones;

import java.util.Set;

public class Zone {
	private final Set<Triangle> triangles;

	public Zone(Set<Triangle> triangles) {
		this.triangles = triangles;
	}

	public boolean contains(Point p) {
		for (Triangle t : triangles)
			if (t.contains(p))
				return true;

		return false;
	}
}
