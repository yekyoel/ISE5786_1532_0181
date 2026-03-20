package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Ray class
 */
class RayTests {

	private static final double DELTA = 0.000001;

	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Ensure direction vector is normalized in constructor
		Point p0 = new Point(1, 2, 3);
		Vector dir = new Vector(0, 2, 0); // Length is 2, not normalized
		Ray ray = new Ray(p0, dir);

		assertEquals(1, ray.direction().length(), DELTA,
				"ERROR: Ray constructor does not normalize the direction vector");
		assertEquals(new Vector(0, 1, 0), ray.direction(), "ERROR: Ray constructor normalizes to incorrect vector");
	}
}