package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Vector class
 */
class VectorTests {

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	// ============ Common Geometric Objects ==================
	private static final Vector V1 = new Vector(1, 2, 3);
	private static final Vector V2 = new Vector(-2, -4, -6);
	private static final Vector V3 = new Vector(0, 3, -2); // Orthogonal to V1

	// ============ Expected Results ==========================
	private static final Vector EXPECTED_ADD = new Vector(-1, -2, -3);
	private static final Vector EXPECTED_SUBTRACT = new Vector(3, 6, 9);
	private static final Vector EXPECTED_SCALE = new Vector(2, 4, 6);

	// ============ Error Messages ============================
	private static final String ERROR_ADD = "ERROR: Vector + Vector does not work correctly";
	private static final String ERROR_ADD_OPPOSITE = "ERROR: Vector + opposite vector must throw exception";
	private static final String ERROR_SUBTRACT = "ERROR: Vector - Vector does not work correctly";
	private static final String ERROR_SUBTRACT_SELF = "ERROR: Vector - itself must throw exception";
	private static final String ERROR_SCALE = "ERROR: scale() wrong result";
	private static final String ERROR_SCALE_ZERO = "ERROR: scale by 0 must throw exception";
	private static final String ERROR_DOT = "ERROR: dotProduct() wrong value";
	private static final String ERROR_DOT_ORTHOGONAL = "ERROR: dotProduct() for orthogonal vectors is not zero";
	private static final String ERROR_CROSS_LENGTH = "ERROR: crossProduct() wrong result length";
	private static final String ERROR_CROSS_ORTHOGONAL = "ERROR: crossProduct() result is not orthogonal to operands";
	private static final String ERROR_CROSS_PARALLEL = "ERROR: crossProduct() for parallel vectors does not throw an exception";
	private static final String ERROR_LENGTH_SQ = "ERROR: lengthSquared() wrong value";
	private static final String ERROR_LENGTH = "ERROR: length() wrong value";
	private static final String ERROR_NORMALIZE_LEN = "ERROR: the normalized vector is not a unit vector";
	private static final String ERROR_NORMALIZE_PARAL = "ERROR: the normalized vector is not parallel to the original one";
	private static final String ERROR_NORMALIZE_DIR = "ERROR: the normalized vector is opposite to the original one";

	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple addition
		assertEquals(EXPECTED_ADD, V1.add(V2), ERROR_ADD);

		// =============== Boundary Values Tests ==================
		// BV01: Add vector to its opposite
		assertThrows(IllegalArgumentException.class, () -> V1.add(new Vector(-1, -2, -3)), ERROR_ADD_OPPOSITE);
	}

	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple subtraction
		assertEquals(EXPECTED_SUBTRACT, V1.subtract(V2), ERROR_SUBTRACT);

		// =============== Boundary Values Tests ==================
		// BV01: Subtract vector from itself
		assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERROR_SUBTRACT_SELF);
	}

	@Test
	void testScale() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple scaling
		assertEquals(EXPECTED_SCALE, V1.scale(2), ERROR_SCALE);

		// =============== Boundary Values Tests ==================
		// BV01: Scale by 0
		assertThrows(IllegalArgumentException.class, () -> V1.scale(0), ERROR_SCALE_ZERO);
	}

	@Test
	void testDotProduct() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple dot product
		assertEquals(-28, V1.dotProduct(V2), DELTA, ERROR_DOT);

		// =============== Boundary Values Tests ==================
		// BV01: Dot product of orthogonal vectors
		assertEquals(0, V1.dotProduct(V3), DELTA, ERROR_DOT_ORTHOGONAL);
	}

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

	@Test
	void testLengthSquared() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple length squared
		assertEquals(14, V1.lengthSquared(), DELTA, ERROR_LENGTH_SQ);
	}

	@Test
	void testLength() {
		// ============ Equivalence Partitions Tests ==============
		// EP01: Simple length
		assertEquals(Math.sqrt(14), V1.length(), DELTA, ERROR_LENGTH);
	}

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