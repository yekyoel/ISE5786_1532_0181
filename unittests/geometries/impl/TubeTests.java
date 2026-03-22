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

	/** Default constructor for TubeTests */
	TubeTests() {
	}

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	/** Origin point for the tube axis */
	private static final Point P0 = new Point(0, 0, 0);
	/** Direction vector for the tube axis */
	private static final Vector V_Z = new Vector(0, 0, 1);
	/** Central axis of the tube */
	private static final Ray AXIS = new Ray(P0, V_Z);
	/** Radius of the tube */
	private static final double RADIUS = 1.0;

	// ============ Common Expected Normals ===================
	/** Expected normal vector towards X axis */
	private static final Vector NORMAL_X = new Vector(1, 0, 0);

	// ============ Error Messages ============================
	/** Error message for tube normal */
	private static final String ERROR_NORMAL = "ERROR: Tube getNormal() wrong result";

	/**
	 * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		Tube tube = new Tube(RADIUS, AXIS);

		// ============ Equivalence Partitions Tests ==============
		// EP01: Point on the tube, strictly in front of the ray's head (t > 0)
		assertEquals(NORMAL_X, tube.getNormal(new Point(1, 0, 2)), ERROR_NORMAL + " (t > 0)");

		// EP02: Point on the tube, strictly behind the ray's head (t < 0)
		assertEquals(NORMAL_X, tube.getNormal(new Point(1, 0, -2)), ERROR_NORMAL + " (t < 0)");

		// =============== Boundary Values Tests ==================
		// BV01: Point on the tube, exactly orthogonal to the ray's head (t = 0)
		assertEquals(NORMAL_X, tube.getNormal(new Point(1, 0, 0)), ERROR_NORMAL + " at the ray's head (t = 0)");
	}
}