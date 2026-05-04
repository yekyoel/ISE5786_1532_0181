package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Geometries class
 */
class GeometriesTests {

	/** Default constructor for GeometriesTests */
	GeometriesTests() {
	}

	/**
	 * Test method
	 * for {@link geometries.impl.Geometries#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: A few geometries intersect (but not all)
		Geometries geos = new Geometries(new Sphere(new Point(1, 0, 0), 1),
				new Plane(new Point(0, 0, -1), new Vector(0, 0, 1)));
		assertEquals(1, geos.findIntersections(new Ray(new Point(0, 0, -2), new Vector(0, 0, 1))).size(),
				"EP01: Some geometries intersect");

		// =============== Boundary Values Tests ==================
		// BV11: Empty collection (0 points)
		assertNull(new Geometries().findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
				"BV11: Empty collection");

		// BV12: No geometry intersects (0 points)
		assertNull(geos.findIntersections(new Ray(new Point(-5, -5, -5), new Vector(-1, -1, -1))),
				"BV12: No intersection");

		// BV13: Only one geometry intersects (1 point)
		assertEquals(1, geos.findIntersections(new Ray(new Point(3, 0, -2), new Vector(0, 0, 1))).size(),
				"BV13: One geometry intersects");

		// BV14: All geometries intersect
		// (Ray designed to hit the plane and go through the sphere)
		assertEquals(3, geos.findIntersections(new Ray(new Point(1, 0, -2), new Vector(0, 0, 1))).size(),
				"BV14: All geometries intersect");
	}

}
