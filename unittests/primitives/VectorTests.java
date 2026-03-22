package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Vector class
 */
class VectorTests {

	/** Default constructor for VectorTests */
	VectorTests() {
	}

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	/** First test vector */
	private static final Vector V1 = new Vector(1, 2, 3);
	/** Second test vector */
	private static final Vector V2 = new Vector(-2, -4, -6);
	/** Third test vector, orthogonal to V1 */
	private static final Vector V3 = new Vector(0, 3, -2);

	// ============ Expected Results ==========================
	/** Expected result for addition */
	private static final Vector EXPECTED_ADD = new Vector(-1, -2, -3);
	/** Expected result for subtraction */
	private static final Vector EXPECTED_SUBTRACT = new Vector(3, 6, 9);
	/** Expected result for scale */
	private static final Vector EXPECTED_SCALE = new Vector(2, 4, 6);

	// ============ Error Messages ============================
	/** Error message for add */
	private static final String ERROR_ADD = "ERROR: Vector + Vector does not work correctly";
	/** Error message for adding opposite */
	private static final String ERROR_ADD_OPPOSITE = "ERROR: Vector + opposite vector must throw exception";
	/** Error message for subtract */
	private static final String ERROR_SUBTRACT = "ERROR: Vector - Vector does not work correctly";
	/** Error message for subtract self */
	private static final String ERROR_SUBTRACT_SELF = "ERROR: Vector - itself must throw exception";
	/** Error message for scale */
	private static final String ERROR_SCALE = "ERROR: scale() wrong result";
	/** Error message for scaling by zero */
	private static final String ERROR_SCALE_ZERO = "ERROR: scale by 0 must throw exception";
	/** Error message for dot product */
	private static final String ERROR_DOT = "ERROR: dotProduct() wrong value";
	/** Error message for orthogonal dot product */
	private static final String ERROR_DOT_ORTHOGONAL = "ERROR: dotProduct() for orthogonal vectors is not zero";
	/** Error message for cross product length */
	private static final String ERROR_CROSS_LENGTH = "ERROR: crossProduct() wrong result length";
	/** Error message for cross product orthogonality */
	private static final String ERROR_CROSS_ORTHOGONAL = "ERROR: crossProduct() result is not orthogonal to operands";
	/** Error message for parallel cross product */
	private static final String ERROR_CROSS_PARALLEL = "ERROR: crossProduct() for parallel vectors does not throw an exception";
	/** Error message for length squared */
	private static final String ERROR_LENGTH_SQ = "ERROR: lengthSquared() wrong value";
	/** Error message for length */
	private static final String ERROR_LENGTH = "ERROR: length() wrong value";
	/** Error message for normalized length */
	private static final String ERROR_NORMALIZE_LEN = "ERROR: the normalized vector is not a unit vector";
	/** Error message for normalized parallelism */
	private static final String ERROR_NORMALIZE_PARAL = "ERROR: the normalized vector is not parallel to the original one";
	/** Error message for normalized direction */
	private static final String ERROR_NORMALIZE_DIR = "ERROR: the normalized vector is opposite to the original one";

	/**
	 * Test method for {@link primitives.Vector#add(primitives.Vector)}.
	 */
	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple addition
		assertEquals(EXPECTED_ADD, V1.add(V2), ERROR_ADD);

		// =============== Boundary Values Tests ==================
		// BV01: Add vector to its opposite
		assertThrows(IllegalArgumentException.class, () -> V1.add(new Vector(-1, -2, -3)), ERROR_ADD_OPPOSITE);
	}

	/**
	 * Test method for {@link primitives.Vector#subtract(primitives.Vector)}.
	 */
	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple subtraction
		assertEquals(EXPECTED_SUBTRACT, V1.subtract(V2), ERROR_SUBTRACT);

		// =============== Boundary Values Tests ==================
		// BV01: Subtract vector from itself
		assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERROR_SUBTRACT_SELF);
	}

	/**
	 * Test method for {@link primitives.Vector#scale(double)}.
	 */
	@Test
	void testScale() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple scaling
		assertEquals(EXPECTED_SCALE, V1.scale(2), ERROR_SCALE);

		// =============== Boundary Values Tests ==================
		// BV01: Scale by 0
		assertThrows(IllegalArgumentException.class, () -> V1.scale(0), ERROR_SCALE_ZERO);
	}

	/**
	 * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
	 */
	@Test
	void testDotProduct() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple dot product
		assertEquals(-28, V1.dotProduct(V2), DELTA, ERROR_DOT);

		// =============== Boundary Values Tests ==================
		// BV01: Dot product of orthogonal vectors
		assertEquals(0, V1.dotProduct(V3), DELTA, ERROR_DOT_ORTHOGONAL);
	}

	/**
	 * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
	 */
	@Test
	void testCrossProduct() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple cross product
		Vector vr = V1.crossProduct(V3);
		assertEquals(V1.length() * V3.length(), vr.length(), DELTA, ERROR_CROSS_LENGTH);
		assertEquals(0, vr.dotProduct(V1), DELTA, ERROR_CROSS_ORTHOGONAL);
		assertEquals(0, vr.dotProduct(V3), DELTA, ERROR_CROSS_ORTHOGONAL);

		// =============== Boundary Values Tests ==================
		// BV01: Cross product of parallel vectors
		assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V2), ERROR_CROSS_PARALLEL);
	}

	/**
	 * Test method for {@link primitives.Vector#lengthSquared()}.
	 */
	@Test
	void testLengthSquared() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple length squared
		assertEquals(14, V1.lengthSquared(), DELTA, ERROR_LENGTH_SQ);
	}

	/**
	 * Test method for {@link primitives.Vector#length()}.
	 */
	@Test
	void testLength() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple length
		assertEquals(Math.sqrt(14), V1.length(), DELTA, ERROR_LENGTH);
	}

	/**
	 * Test method for {@link primitives.Vector#normalize()}.
	 */
	@Test
	void testNormalize() {
		Vector n = V1.normalize();

		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple normalize length check
		assertEquals(1, n.length(), DELTA, ERROR_NORMALIZE_LEN);

		// EP02: Check if normalized vector is parallel to original
		assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(n), ERROR_NORMALIZE_PARAL);

		// EP03: Check if normalized vector is in the same direction
		assertTrue(V1.dotProduct(n) > 0, ERROR_NORMALIZE_DIR);
	}
}