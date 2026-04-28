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

	/** Default constructor for CylinderTests */
	CylinderTests() {
	}

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	/** Origin point for the cylinder axis */
	private static final Point P0 = new Point(0, 0, 0);
	/** Direction vector for the cylinder axis */
	private static final Vector V_Z = new Vector(0, 0, 1);
	/** Central axis of the cylinder */
	private static final Ray AXIS = new Ray(P0, V_Z);
	/** Radius of the cylinder */
	private static final double RADIUS = 1.0;
	/** Height of the cylinder */
	private static final double HEIGHT = 2.0;

	// ============ Common Expected Normals ===================
	/** Expected normal vector towards X axis */
	private static final Vector NORMAL_X = new Vector(1, 0, 0);
	/** Expected normal vector towards Z axis */
	private static final Vector NORMAL_Z = new Vector(0, 0, 1);
	/** Expected normal vector towards minus Z axis */
	private static final Vector NORMAL_MINUS_Z = new Vector(0, 0, -1);

	// ============ Error Messages ============================
	/** Error message for lateral surface normal */
	private static final String ERROR_LATERAL = "ERROR: Cylinder lateral surface normal is wrong";
	/** Error message for top base normal */
	private static final String ERROR_TOP = "ERROR: Cylinder top base normal is wrong";
	/** Error message for bottom base normal */
	private static final String ERROR_BOTTOM = "ERROR: Cylinder bottom base normal is wrong";

	/**
	 * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		Cylinder cyl = new Cylinder(RADIUS, AXIS, HEIGHT);

		// ============ Equivalence Partitions Tests ==============
		// EP01: Point on the lateral surface (side)
		assertEquals(NORMAL_X, cyl.getNormal(new Point(1, 0, 1)), ERROR_LATERAL);

		// EP02: Point on the top base
		assertEquals(NORMAL_Z, cyl.getNormal(new Point(0.5, 0, 2)), ERROR_TOP);

		// EP03: Point on the bottom base
		assertEquals(NORMAL_MINUS_Z, cyl.getNormal(new Point(0.5, 0, 0)), ERROR_BOTTOM);

		// =============== Boundary Values Tests ==================
		// BV01: Point at the center of the bottom base
		assertEquals(NORMAL_MINUS_Z, cyl.getNormal(P0), ERROR_BOTTOM);

		// BV02: Point at the center of the top base
		assertEquals(NORMAL_Z, cyl.getNormal(new Point(0, 0, 2)), ERROR_TOP);

		// BV03: Point on the edge of the bottom base (between base and side)
		assertEquals(NORMAL_MINUS_Z, cyl.getNormal(new Point(1, 0, 0)), ERROR_BOTTOM);

		// BV04: Point on the edge of the top base (between base and side)
		assertEquals(NORMAL_Z, cyl.getNormal(new Point(1, 0, 2)), ERROR_TOP);
	}

}