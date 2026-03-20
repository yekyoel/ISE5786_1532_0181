package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Sphere class
 */
class SphereTests {

	@Test
	void testGetNormal() {
		Sphere sphere = new Sphere(new Point(0, 0, 0), 1d);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple test for normal on the sphere
		// The vector from center (0,0,0) to point (0,0,1) is (0,0,1)
		assertEquals(new Vector(0, 0, 1), sphere.getNormal(new Point(0, 0, 1)),
				"ERROR: Sphere getNormal() wrong result");
	}
}