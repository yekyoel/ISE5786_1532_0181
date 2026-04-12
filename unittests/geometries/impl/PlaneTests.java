package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Plane class
 */
class PlaneTests {

	/** Default constructor for PlaneTests */
	PlaneTests() {
	}

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	/** First vertex of the plane */
	private static final Point P1 = new Point(0, 0, 1);
	/** Second vertex of the plane */
	private static final Point P2 = new Point(1, 0, 0);
	/** Third vertex of the plane */
	private static final Point P3 = new Point(0, 1, 0);

	// ============ Error Messages ============================
	/** Error message for failed construction */
	private static final String ERROR_CONSTRUCT_FAIL = "Failed constructing a correct plane";
	/** Error message for coinciding points */
	private static final String ERROR_CONSTRUCT_COINC = "Constructed a plane with coinciding points";
	/** Error message for colinear points */
	private static final String ERROR_CONSTRUCT_LINE = "Constructed a plane with 3 points on the same line";
	/** Error message for normal length */
	private static final String ERROR_NORMAL_LENGTH = "ERROR: Plane normal is not a unit vector";
	/** Error message for mismatched normals */
	private static final String ERROR_NORMAL_MATCH = "ERROR: Normal at reference point is different from normal at other point";

	/**
	 * Test method for constructors in {@link geometries.impl.Plane}.
	 */
	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Correct plane construction with 3 points
		assertDoesNotThrow(() -> new Plane(P1, P2, P3), ERROR_CONSTRUCT_FAIL);

		// EP02: Plane(Point, Vector) - Ensure the normal is properly normalized
		Vector unnormalizedVector = new Vector(0, 2, 0);
		Plane planeWithVector = new Plane(new Point(1, 2, 3), unnormalizedVector);
		assertEquals(1.0, planeWithVector.getNormal(new Point(1, 2, 3)).length(), DELTA, ERROR_NORMAL_LENGTH);

		// =============== Boundary Values Tests ==================
		// BV01: 2 points coincide
		assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P3), ERROR_CONSTRUCT_COINC);

		// BV02: 3 points coincide
		assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P1), ERROR_CONSTRUCT_COINC);

		// BV03: 3 points on the same line
		Point pLine1 = new Point(1, 1, 1);
		Point pLine2 = new Point(2, 2, 2);
		Point pLine3 = new Point(3, 3, 3);
		assertThrows(IllegalArgumentException.class, () -> new Plane(pLine1, pLine2, pLine3), ERROR_CONSTRUCT_LINE);
	}

	/**
	 * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		Plane plane = new Plane(P1, P2, P3);

		// ============ Equivalence Partitions Tests ==============
		// EP01: Normal at a point on the plane (not the reference point)
		Vector n = plane.getNormal(new Point(0.5, 0.5, 0));
		assertEquals(1.0, n.length(), DELTA, ERROR_NORMAL_LENGTH);

		// =============== Boundary Values Tests ==================
		// BV01: Normal at the reference point itself
		Vector nRef = plane.getNormal(P1);
		assertEquals(1.0, nRef.length(), DELTA, ERROR_NORMAL_LENGTH);

		// Check that normals match everywhere on the plane
		assertEquals(n, nRef, ERROR_NORMAL_MATCH);
	}

	/**
	 * Test method
	 * for{@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

		// ============ Equivalence Partitions Tests ==============
		// EP01: Ray intersects the plane (1 point)
		assertEquals(1, plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1))).size(),
				"EP01: Ray should intersect plane");

		// EP02: Ray does not intersect the plane (0 points)
		assertNull(plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(1, 1, 1))),
				"EP02: Ray points away from plane");

		// =============== Boundary Values Tests ==================
		// **** Group: Ray is parallel to the plane
		// BV11: Ray is parallel and outside the plane (0 points)
		assertNull(plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(1, 0, 0))),
				"BV11: Parallel ray outside");

		// BV12: Ray is parallel and included in the plane (0 points)
		assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0))),
				"BV12: Parallel ray inside plane");

		// **** Group: Ray is orthogonal to the plane
		// BV13: Ray is orthogonal and starts before the plane (1 point)
		assertEquals(1, plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))).size(),
				"BV13: Orthogonal starts before");

		// BV14: Ray is orthogonal and starts at the plane (0 points)
		assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
				"BV14: Orthogonal starts at plane");

		// BV15: Ray starts at the plane but is not orthogonal (0 points)
		assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))), "BV15: Starts at plane");
	}
}