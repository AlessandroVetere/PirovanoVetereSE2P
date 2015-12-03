package mts.zones;

public class TestMain {
	public static void main(String[] args) {
		TestMain t = new TestMain();
		t.test();
	}

	private void test() {
		Point p1 = new Point(0, 0);
		Point p2 = new Point(0, 1);
		Point p3 = new Point(1, 0);

		Point p = new Point(0.01, 0.01);

		Triangle t = new Triangle(p1, p2, p3);

		System.out.println(t.contains(p));
	}
}
