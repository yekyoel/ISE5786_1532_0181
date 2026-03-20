package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Triangle class
 */
class TriangleTests {

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
}