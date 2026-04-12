package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Ray class
 */
class RayTests {

	/** Default constructor for RayTests */
	RayTests() {
	}

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	/** Origin point of the ray */
	private static final Point P0 = new Point(1, 2, 3);
	/** Direction vector of the ray, not normalized */
	private static final Vector DIR = new Vector(0, 2, 0);

	// ============ Expected Results ==========================
	/** Expected normalized direction vector */
	private static final Vector EXPECTED_NORMALIZED_DIR = new Vector(0, 1, 0);

	// ============ Error Messages ============================
	/** Error message for missing normalization length */
	private static final String ERROR_NORMALIZE_LEN = "ERROR: Ray constructor does not normalize the direction vector";
	/** Error message for wrong normalization direction */
	private static final String ERROR_NORMALIZE_DIR = "ERROR: Ray constructor normalizes to incorrect vector";

	/**
	 * Test method for
	 * {@link primitives.Ray#Ray(primitives.Point, primitives.Vector)}.
	 */
	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Ensure direction vector is normalized in constructor
		Ray ray = new Ray(P0, DIR);

		assertEquals(1.0, ray.direction().length(), DELTA, ERROR_NORMALIZE_LEN);
		assertEquals(EXPECTED_NORMALIZED_DIR, ray.direction(), ERROR_NORMALIZE_DIR);
	}

	/**
	 * Test method for {@link primitives.Ray#getPoint(double t)}.
	 */
	@Test
	void testGetPoint() {
		Ray ray = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));

		// ============ Equivalence Partitions Tests ==============

		// EP01: Positive distance (t > 0)
		assertEquals(new Point(2, 0, 0), ray.getPoint(1), "EP01: getPoint() with positive distance failed");

		// EP02: Negative distance (t < 0)
		assertEquals(new Point(0, 0, 0), ray.getPoint(-1), "EP02: getPoint() with negative distance failed");

		// =============== Boundary Values Tests ==================

		// BV01: Zero distance (t = 0)
		assertEquals(new Point(1, 0, 0), ray.getPoint(0),
				"BV01: getPoint() with zero distance failed to return ray head");
	}
}