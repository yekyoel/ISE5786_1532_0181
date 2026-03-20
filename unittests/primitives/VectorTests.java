package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Vector class
 */
class VectorTests {

	private static final double DELTA = 0.000001;
	private static final Vector V1 = new Vector(1, 2, 3);
	private static final Vector V2 = new Vector(-2, -4, -6);
	private static final Vector V3 = new Vector(0, 3, -2); // Orthogonal to V1

	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple addition
		assertEquals(new Vector(-1, -2, -3), V1.add(V2), "ERROR: Vector + Vector does not work correctly");

		// =============== Boundary Values Tests ==================
		// TC11: Add vector to its opposite
		assertThrows(IllegalArgumentException.class, () -> V1.add(new Vector(-1, -2, -3)),
				"ERROR: Vector + opposite vector must throw exception");
	}

	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple subtraction
		assertEquals(new Vector(3, 6, 9), V1.subtract(V2), "ERROR: Vector - Vector does not work correctly");

		// =============== Boundary Values Tests ==================
		// TC11: Subtract vector from itself
		assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1),
				"ERROR: Vector - itself must throw exception");
	}

	@Test
	void testScale() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple scaling
		assertEquals(new Vector(2, 4, 6), V1.scale(2), "ERROR: scale() wrong result");

		// =============== Boundary Values Tests ==================
		// TC11: Scale by 0
		assertThrows(IllegalArgumentException.class, () -> V1.scale(0), "ERROR: scale by 0 must throw exception");
	}

	@Test
	void testDotProduct() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple dot product
		assertEquals(-28, V1.dotProduct(V2), DELTA, "ERROR: dotProduct() wrong value");

		// =============== Boundary Values Tests ==================
		// TC11: Dot product of orthogonal vectors
		assertEquals(0, V1.dotProduct(V3), DELTA, "ERROR: dotProduct() for orthogonal vectors is not zero");
	}

	@Test
	void testCrossProduct() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple cross product
		Vector vr = V1.crossProduct(V3);
		// Test that length of cross-product is proper (orthogonal vectors length
		// requirement)
		assertEquals(V1.length() * V3.length(), vr.length(), DELTA, "ERROR: crossProduct() wrong result length");
		// Test cross-product result orthogonality to its operands
		assertEquals(0, vr.dotProduct(V1), DELTA, "ERROR: crossProduct() result is not orthogonal to 1st operand");
		assertEquals(0, vr.dotProduct(V3), DELTA, "ERROR: crossProduct() result is not orthogonal to 2nd operand");

		// =============== Boundary Values Tests ==================
		// TC11: Cross product of parallel vectors
		assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V2),
				"ERROR: crossProduct() for parallel vectors does not throw an exception");
	}

	@Test
	void testLengthSquared() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple length squared
		assertEquals(14, V1.lengthSquared(), DELTA, "ERROR: lengthSquared() wrong value");
	}

	@Test
	void testLength() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple length
		assertEquals(Math.sqrt(14), V1.length(), DELTA, "ERROR: length() wrong value");
	}

	@Test
	void testNormalize() {
		Vector v = new Vector(1, 2, 3);
		Vector n = v.normalize();

		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple normalize length check
		assertEquals(1, n.length(), DELTA, "ERROR: the normalized vector is not a unit vector");

		// TC02: Check if normalized vector is parallel to original
		assertThrows(IllegalArgumentException.class, () -> v.crossProduct(n),
				"ERROR: the normalized vector is not parallel to the original one");

		// TC03: Check if normalized vector is in the same direction
		assertTrue(v.dotProduct(n) > 0, "ERROR: the normalized vector is opposite to the original one");
	}
}