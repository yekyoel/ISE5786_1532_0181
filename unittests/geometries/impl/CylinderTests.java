package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Cylinder class
 */
class CylinderTests {

	@Test
	void testGetNormal() {
		
		// Cylinder with radius 1, height 2, aligned with the Z-axis
		Cylinder cyl = new Cylinder(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 2.0);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Point on the lateral surface (side)
		assertEquals(new Vector(1, 0, 0), cyl.getNormal(new Point(1, 0, 1)), "ERROR: Cylinder lateral surface normal");

		// TC02: Point on the top base
		assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0.5, 0, 2)), "ERROR: Cylinder top base normal");

		// TC03: Point on the bottom base
		assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(0.5, 0, 0)), "ERROR: Cylinder bottom base normal");

		// =============== Boundary Values Tests ==================
		// TC11: Point at the center of the bottom base
		assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(0, 0, 0)), "ERROR: Cylinder bottom center normal");

		// TC12: Point at the center of the top base
		assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0, 0, 2)), "ERROR: Cylinder top center normal");

		// TC13: Point on the edge of the bottom base (between base and side)
		assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(1, 0, 0)), "ERROR: Cylinder bottom edge normal");

		// TC14: Point on the edge of the top base (between base and side)
		assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(1, 0, 2)), "ERROR: Cylinder top edge normal");
	}
}