package mts.zones;

public class Triangle {
	private final Point p3;

	private final double a;
	private final double b;
	private final double c;
	private final double d;

	public Triangle(Point p1, Point p2, Point p3) {
		this.p3 = p3;

		this.a = p1.getX() - p3.getX();
		this.b = p2.getX() - p3.getX();
		this.c = p1.getY() - p3.getY();
		this.d = p2.getY() - p3.getY();
	}

	// This methods check if the given point is inside this Triangle,
	// or on its borders, vertices included, with a given
	// approximation error.
	// It spoils a theorem about convex polygons that says that a point P
	// is inside a given convex polygon (like a Triangle is) if the given
	// vector associated to the point P is a convex combination
	// (a linear combination with all the coefficients
	// non negative that sum to 1) of the polygon vertices.
	// So, here we calculate if such coefficients exists solving
	// a vector equation:
	// 1) P = alpha1 * P1 + alpha2 * P2 + alpha3 * P3;
	// with P, P1, P2, P3 in R^2;
	// with alpha1, alpha2, alpha3 in R;
	// and alpha1 >= 0, alpha2 >= 0, alpha3 >= 0;
	// and alpha1 + alpha2 + alpha3 = 1.
	// 2) P = alpha1 * P1 + alpha2 * P2 + (1 - alpha1 - alpha2) * P3.
	// 3) P - P3 = alpha1 * (P1 - P3) + alpha2 * (P2 - P3).
	// This equation can then be split into two scalar linear equations
	// in the x and y components.
	// The system is solved using Cramer's rule and then it is
	// checked that alpha1 and alpha2 (and alpha3) found by
	// solving the system satisfy the constraints.
	public boolean contains(Point p) {
		double e = p.getX() - p3.getX();
		double f = p.getY() - p3.getY();

		double delta = a * d - b * c;

		if (delta == 0)
			return false;

		double delta_alpha_1 = e * d - f * b;

		double alpha_1 = delta_alpha_1 / delta;

		if (alpha_1 < 0)
			return false;

		double delta_alpha_2 = a * f - c * e;

		double alpha_2 = delta_alpha_2 / delta;

		if (alpha_2 < 0)
			return false;

		double alpha_3 = 1 - alpha_1 - alpha_2;

		if (alpha_3 < 0)
			return false;

		return true;
	}
}
