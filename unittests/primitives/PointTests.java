package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Point class
 */
class PointTests {

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	private static final Point P1 = new Point(1, 2, 3);
	private static final Point P2 = new Point(2, 4, 6);
	private static final Point P3 = new Point(1, 4, 3);
	private static final Vector V1 = new Vector(1, -2, -3);

	// ============ Expected Results ==========================
	private static final Vector EXPECTED_SUBTRACT = new Vector(1, 2, 3);
	private static final Point EXPECTED_ADD = new Point(2, 0, 0);

	// ============ Error Messages ============================
	private static final String ERROR_SUBTRACT = "ERROR: Point - Point does not work correctly";
	private static final String ERROR_SUBTRACT_SELF = "ERROR: Point - itself must throw exception (zero vector)";
	private static final String ERROR_ADD = "ERROR: Point + Vector does not work correctly";
	private static final String ERROR_DIST_SQ = "ERROR: distanceSquared() wrong result";
	private static final String ERROR_DIST_SQ_SELF = "ERROR: distanceSquared() to itself should be 0";
	private static final String ERROR_DIST = "ERROR: distance() wrong result";
	private static final String ERROR_DIST_SELF = "ERROR: distance() to itself should be 0";

	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple subtraction
		assertEquals(EXPECTED_SUBTRACT, P2.subtract(P1), ERROR_SUBTRACT);

		// =============== Boundary Values Tests ==================
		// BV01: Subtracting point from itself
		assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), ERROR_SUBTRACT_SELF);
	}

	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple addition
		assertEquals(EXPECTED_ADD, P1.add(V1), ERROR_ADD);
	}

	@Test
	void testDistanceSquared() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple distance squared (Distance is 2, squared is 4)
		assertEquals(4, P1.distanceSquared(P3), DELTA, ERROR_DIST_SQ);

		// =============== Boundary Values Tests ==================
		// BV01: Distance squared to itself
		assertEquals(0, P1.distanceSquared(P1), DELTA, ERROR_DIST_SQ_SELF);
	}

	@Test
	void testDistance() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple distance (Distance is 2)
		assertEquals(2, P1.distance(P3), DELTA, ERROR_DIST);

		// =============== Boundary Values Tests ==================
		// BV01: Distance to itself
		assertEquals(0, P1.distance(P1), DELTA, ERROR_DIST_SELF);
	}
}