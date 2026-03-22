package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Ray class
 */
class RayTests {

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	private static final Point P0 = new Point(1, 2, 3);
	private static final Vector DIR = new Vector(0, 2, 0); // Length is 2, not normalized

	// ============ Expected Results ==========================
	private static final Vector EXPECTED_NORMALIZED_DIR = new Vector(0, 1, 0);

	// ============ Error Messages ============================
	private static final String ERROR_NORMALIZE_LEN = "ERROR: Ray constructor does not normalize the direction vector";
	private static final String ERROR_NORMALIZE_DIR = "ERROR: Ray constructor normalizes to incorrect vector";

	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Ensure direction vector is normalized in constructor
		Ray ray = new Ray(P0, DIR);

		assertEquals(1.0, ray.direction().length(), DELTA, ERROR_NORMALIZE_LEN);
		assertEquals(EXPECTED_NORMALIZED_DIR, ray.direction(), ERROR_NORMALIZE_DIR);
	}
}