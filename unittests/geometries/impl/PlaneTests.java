package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Plane class
 */
class PlaneTests {

	private static final double DELTA = 0.000001;

	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Correct plane construction with 3 points.
		assertDoesNotThrow(() -> new Plane(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0)),
				"Failed constructing a correct plane");

		// =============== Boundary Values Tests ==================
		// TC11: 2 points coincide.
		assertThrows(IllegalArgumentException.class,
				() -> new Plane(new Point(0, 0, 1), new Point(0, 0, 1), new Point(0, 1, 0)),
				"Constructed a plane with 2 coinciding points");

		// TC12: 3 points on the same line.
		assertThrows(IllegalArgumentException.class,
				() -> new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
				"Constructed a plane with 3 points on the same line");
	}

	@Test
	void testGetNormal() {
		Plane plane = new Plane(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0));

		// ============ Equivalence Partitions Tests ==============
		// TC01: Normal at a point on the plane (not the reference point)
		Vector n = plane.getNormal(new Point(0.5, 0.5, 0)); // A point on the plane
		assertEquals(1, n.length(), DELTA, "ERROR: Plane normal is not a unit vector");

		// =============== Boundary Values Tests ==================
		// TC11: Normal at the reference point itself.
		Vector nRef = plane.getNormal(new Point(0, 0, 1));
		assertEquals(1, nRef.length(), DELTA, "ERROR: Plane normal at reference point is not a unit vector");

		// Check that normals match
		assertEquals(n, nRef, "ERROR: Normal at reference point is different from normal at other point");
	}
}