package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Point class
 */
class PointTests {

	private static final double DELTA = 0.000001;

	@Test
	void testSubtract() {
		Point p1 = new Point(1, 2, 3);
		Point p2 = new Point(2, 4, 6);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple subtraction
		assertEquals(new Vector(1, 2, 3), p2.subtract(p1), "ERROR: Point - Point does not work correctly");

		// =============== Boundary Values Tests ==================
		// TC11: Subtracting point from itself
		assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1),
				"ERROR: Point - itself must throw exception (zero vector)");
	}

	@Test
	void testAdd() {
		Point p1 = new Point(1, 2, 3);
		Vector v1 = new Vector(1, -2, -3);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple addition
		assertEquals(new Point(2, 0, 0), p1.add(v1), "ERROR: Point + Vector does not work correctly");
	}

	@Test
	void testDistanceSquared() {
		Point p1 = new Point(1, 2, 3);
		Point p2 = new Point(1, 4, 3); // Distance is 2, squared is 4

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple distance squared
		assertEquals(4, p1.distanceSquared(p2), DELTA, "ERROR: distanceSquared() wrong result");

		// =============== Boundary Values Tests ==================
		// TC11: Distance squared to itself
		assertEquals(0, p1.distanceSquared(p1), DELTA, "ERROR: distanceSquared() to itself should be 0");
	}

	@Test
	void testDistance() {
		Point p1 = new Point(1, 2, 3);
		Point p2 = new Point(1, 4, 3); // Distance is 2

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple distance
		assertEquals(2, p1.distance(p2), DELTA, "ERROR: distance() wrong result");

		// =============== Boundary Values Tests ==================
		// TC11: Distance to itself
		assertEquals(0, p1.distance(p1), DELTA, "ERROR: distance() to itself should be 0");
	}
}