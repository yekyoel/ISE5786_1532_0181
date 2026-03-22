package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Point class
 */
class PointTests {

	/** Default constructor for PointTests */
	PointTests() {
	}

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	/** First test point */
	private static final Point P1 = new Point(1, 2, 3);
	/** Second test point */
	private static final Point P2 = new Point(2, 4, 6);
	/** Third test point */
	private static final Point P3 = new Point(1, 4, 3);
	/** Test vector */
	private static final Vector V1 = new Vector(1, -2, -3);

	// ============ Expected Results ==========================
	/** Expected result for subtraction */
	private static final Vector EXPECTED_SUBTRACT = new Vector(1, 2, 3);
	/** Expected result for addition */
	private static final Point EXPECTED_ADD = new Point(2, 0, 0);

	// ============ Error Messages ============================
	/** Error message for subtract */
	private static final String ERROR_SUBTRACT = "ERROR: Point - Point does not work correctly";
	/** Error message for subtract self */
	private static final String ERROR_SUBTRACT_SELF = "ERROR: Point - itself must throw exception (zero vector)";
	/** Error message for add */
	private static final String ERROR_ADD = "ERROR: Point + Vector does not work correctly";
	/** Error message for distance squared */
	private static final String ERROR_DIST_SQ = "ERROR: distanceSquared() wrong result";
	/** Error message for distance squared to itself */
	private static final String ERROR_DIST_SQ_SELF = "ERROR: distanceSquared() to itself should be 0";
	/** Error message for distance */
	private static final String ERROR_DIST = "ERROR: distance() wrong result";
	/** Error message for distance to itself */
	private static final String ERROR_DIST_SELF = "ERROR: distance() to itself should be 0";

	/**
	 * Test method for {@link primitives.Point#subtract(primitives.Point)}.
	 */
	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple subtraction
		assertEquals(EXPECTED_SUBTRACT, P2.subtract(P1), ERROR_SUBTRACT);

		// =============== Boundary Values Tests ==================
		// BV01: Subtracting point from itself
		assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), ERROR_SUBTRACT_SELF);
	}

	/**
	 * Test method for {@link primitives.Point#add(primitives.Vector)}.
	 */
	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple addition
		assertEquals(EXPECTED_ADD, P1.add(V1), ERROR_ADD);
	}

	/**
	 * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
	 */
	@Test
	void testDistanceSquared() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple distance squared
		assertEquals(4, P1.distanceSquared(P3), DELTA, ERROR_DIST_SQ);

		// =============== Boundary Values Tests ==================
		// BV01: Distance squared to itself
		assertEquals(0, P1.distanceSquared(P1), DELTA, ERROR_DIST_SQ_SELF);
	}

	/**
	 * Test method for {@link primitives.Point#distance(primitives.Point)}.
	 */
	@Test
	void testDistance() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple distance
		assertEquals(2, P1.distance(P3), DELTA, ERROR_DIST);

		// =============== Boundary Values Tests ==================
		// BV01: Distance to itself
		assertEquals(0, P1.distance(P1), DELTA, ERROR_DIST_SELF);
	}
}