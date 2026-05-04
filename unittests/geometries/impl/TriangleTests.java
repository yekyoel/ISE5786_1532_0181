package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Triangle class
 */
class TriangleTests {
	/** Default constructor for TriangleTests */
	TriangleTests() {
	}

	/**
	 * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// We create a triangle on the plane parallel to X-Y (z=1)
		Triangle tr = new Triangle(new Point(0, 0, 1), new Point(1, 0, 1), new Point(0, 1, 1));

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple test for normal inside the triangle
		// The normal should be (0,0,1) or (0,0,-1) depending on vertex order
		Vector expectedNormal = new Vector(0, 0, 1);
		Vector actualNormal = tr.getNormal(new Point(0.25, 0.25, 1));

		assertTrue(expectedNormal.equals(actualNormal) || expectedNormal.scale(-1).equals(actualNormal),
				"ERROR: Triangle getNormal() does not work correctly");
	}

	/**
	 * Test method
	 * for {@link geometries.impl.Triangle#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Triangle triangle = new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1));

		// ============ Equivalence Partitions Tests ==============
		// EP01: Inside triangle (1 point)
		assertEquals(1, triangle.findIntersections(new Ray(new Point(-1, -1, -1), new Vector(1, 1, 1))).size(),
				"EP01: Inside triangle");

		// EP02: Outside against edge (0 points)
		assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(1, 1, 0))),
				"EP02: Outside against edge");

		// EP03: Outside against vertex (0 points)
		assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(-1, -1, 0))),
				"EP03: Outside against vertex");

		// =============== Boundary Values Tests ==================
		// BV11: Intersection point is on edge (0 points)
		assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0.5, 0.5, 1))), "BV11: On edge");

		// BV12: Intersection point is in vertex (0 points)
		assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(1, 0, 1))), "BV12: On vertex");

		// BV13: Intersection point is on edge's continuation (0 points)
		assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(2, -1, 1))),
				"BV13: On edge continuation");
	}
}
