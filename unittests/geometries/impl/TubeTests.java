package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Tube class
 */
class TubeTests {

	@Test
	void testGetNormal() {
		// Tube with radius 1, aligned with the Z-axis
		Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));

		// ============ Equivalence Partitions Tests ==============
		// TC01: Point on the tube, strictly in front of the ray's head (t > 0)
		assertEquals(new Vector(1, 0, 0), tube.getNormal(new Point(1, 0, 2)),
				"ERROR: Tube getNormal() wrong result (t > 0)");

		// TC02: Point on the tube, strictly behind the ray's head (t < 0)
		assertEquals(new Vector(1, 0, 0), tube.getNormal(new Point(1, 0, -2)),
				"ERROR: Tube getNormal() wrong result (t < 0)");

		// =============== Boundary Values Tests ==================
		// TC11: Point on the tube, exactly orthogonal to the ray's head (t = 0)
		assertEquals(new Vector(1, 0, 0), tube.getNormal(new Point(1, 0, 0)),
				"ERROR: Tube getNormal() wrong result at the ray's head (t = 0)");
	}
}