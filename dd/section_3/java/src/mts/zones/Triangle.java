package mts.zones;

public class Triangle {
	private final Point p1;
	private final Point p2;
	private final Point p3;

	private final double a;
	private final double b;
	private final double c;
	private final double d;

	public Triangle(Point p1, Point p2, Point p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;

		this.a = p1.getX() - p3.getX();
		this.b = p2.getX() - p3.getX();
		this.c = p1.getY() - p3.getY();
		this.d = p2.getY() - p3.getY();
	}

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
