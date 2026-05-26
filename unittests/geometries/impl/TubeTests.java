package geometries.impl;

import geometries.api.Intersectable.Intersection;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for geometries.impl.Tube class
 */
class TubeTests {

    /**
     * Default constructor for TubeTests
     */
    TubeTests() {
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    // ============ Common Geometric Objects ==================
    /**
     * Origin point for the tube axis
     */
    private static final Point P0 = new Point(0, 0, 0);
    /**
     * Direction vector for the tube axis
     */
    private static final Vector V_Z = new Vector(0, 0, 1);
    /**
     * Central axis of the tube
     */
    private static final Ray AXIS = new Ray(P0, V_Z);
    /**
     * Radius of the tube
     */
    private static final double RADIUS = 1.0;

    // ============ Common Expected Normals ===================
    /**
     * Expected normal vector towards X axis
     */
    private static final Vector NORMAL_X = new Vector(1, 0, 0);

    // ============ Error Messages ============================
    /**
     * Error message for tube normal
     */
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

    /**
     * Helper method to verify intersection results
     *
     * @param tube     The tube to test
     * @param ray      The ray to intersect with
     * @param expected The expected list of points (null if no intersections)
     * @param message  Test case description
     */
    private void assertIntersections(Tube tube, Ray ray, List<Point> expected, String message) {
        List<Point> result = tube.findIntersections(ray);

        if (expected == null) {
            assertNull(result, message + ": should return null");
            return;
        }

        assertNotNull(result, message + ": should not return null");
        assertEquals(expected.size(), result.size(), message + ": wrong number of points");

        // Check each expected point against the results using a distance-based epsilon
        for (Point expP : expected) {
            boolean found = false;
            for (Point resP : result) {
                // Use a small epsilon for coordinate comparison
                if (resP.distance(expP) < 0.0001) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, message + ": expected point " + expP + " not found. Got: " + result);
        }
    }

    /**
     * Test method for tube-ray intersections.
     */
    @Test
    void testFindIntersections() {
        // Tube: Center axis along Z, Radius 1
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0, axis);

        // ============ Equivalence Partitions Tests (10 Cases) =============

        // TC01: Ray starts outside and crosses tube (2 points)
        assertIntersections(tube, new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0)),
                List.of(new Point(1, 0, 1), new Point(-1, 0, 1)), "TC01");

        // TC02: Ray starts inside and exits (1 point)
        assertIntersections(tube, new Ray(new Point(0.5, 0, 1), new Vector(1, 0, 0)), List.of(new Point(1, 0, 1)),
                "TC02");

        // TC03: Ray starts outside and misses (0 points)
        assertIntersections(tube, new Ray(new Point(2, 2, 1), new Vector(1, 1, 0)), null, "TC03");

        // TC04: Ray starts after the tube (0 points)
        assertIntersections(tube, new Ray(new Point(2, 0, 1), new Vector(1, 0, 0)), null, "TC04");

        // TC05: Ray starts on surface and goes inside (1 point)
        assertIntersections(tube, new Ray(new Point(1, 0, 1), new Vector(-1, 0, 0)), List.of(new Point(-1, 0, 1)),
                "TC05");

        // TC06: Ray starts on surface and goes outside (0 points)
        assertIntersections(tube, new Ray(new Point(1, 0, 1), new Vector(1, 0, 0)), null, "TC06");

        // TC07: Ray starts at the axis and goes out (1 point)
        assertIntersections(tube, new Ray(new Point(0, 0, 1), new Vector(1, 0, 0)), List.of(new Point(1, 0, 1)),
                "TC07");

        // TC08: Ray starts inside, parallel to axis (0 points)
        assertIntersections(tube, new Ray(new Point(0.5, 0, 1), new Vector(0, 0, 1)), null, "TC08");

        // TC09: Ray starts outside, parallel to axis (0 points)
        assertIntersections(tube, new Ray(new Point(2, 0, 1), new Vector(0, 0, 1)), null, "TC09");

        // TC10: Ray perpendicular, crosses axis from outside (2 points)
        assertIntersections(tube, new Ray(new Point(0, -2, 5), new Vector(0, 1, 0)),
                List.of(new Point(0, -1, 5), new Point(0, 1, 5)), "TC10");

        // =============== Boundary Values Tests (30 Cases) ==================

        // ---- Group: Tangency (0 points) ----
        // TC11-13: Tangent ray starting before, at, and after tangent point
        assertIntersections(tube, new Ray(new Point(1, -2, 0), new Vector(0, 1, 0)), null, "TC11");
        assertIntersections(tube, new Ray(new Point(1, 0, 0), new Vector(0, 1, 0)), null, "TC12");
        assertIntersections(tube, new Ray(new Point(1, 2, 0), new Vector(0, 1, 0)), null, "TC13");

        // ---- Group: Parallel to Axis (0 points) ----
        // TC14: Ray on surface
        assertIntersections(tube, new Ray(new Point(1, 0, 0), new Vector(0, 0, 1)), null, "TC14");
        // TC15: Ray on axis
        assertIntersections(tube, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), null, "TC15");
        // TC16: Ray inside
        assertIntersections(tube, new Ray(new Point(0.5, 0, 0), new Vector(0, 0, 1)), null, "TC16");

        // ---- Group: Ray through Axis Head P0 ----
        // TC17: Perpendicular through P0 starting outside (2 points)
        assertIntersections(tube, new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0)),
                List.of(new Point(1, 0, 0), new Point(-1, 0, 0)), "TC17");
        // TC18: Oblique through P0 (2 points)
        assertIntersections(tube, new Ray(new Point(2, 0, -2), new Vector(-1, 0, 1)),
                List.of(new Point(1, 0, -1), new Point(-1, 0, 1)), "TC18");

        // ---- Group: Ray Head on Surface (1 point) ----
        // TC19: Perpendicular inward
        assertIntersections(tube, new Ray(new Point(0, 1, 5), new Vector(0, -1, 0)), List.of(new Point(0, -1, 5)),
                "TC19");
        // TC20: Oblique inward
        assertIntersections(tube, new Ray(new Point(1, 0, 0), new Vector(-1, 0, 1)), List.of(new Point(-1, 0, 2)),
                "TC20");

        // ---- Group: Ray Head on Surface (0 points) ----
        // TC21: Perpendicular outward
        assertIntersections(tube, new Ray(new Point(0, 1, 5), new Vector(0, 1, 0)), null, "TC21");
        // TC22: Oblique outward
        assertIntersections(tube, new Ray(new Point(1, 0, 0), new Vector(1, 0, 1)), null, "TC22");

        // ---- Group: Oblique crossing through axis (2 points) ----
        // TC23: Crosses axis at P(0,0,10)
        assertIntersections(tube, new Ray(new Point(2, 0, 8), new Vector(-1, 0, 1)),
                List.of(new Point(1, 0, 9), new Point(-1, 0, 11)), "TC23");

        // ---- Group: High Z values (Infinite test) ----
        // TC24: Intersection at Z=100
        assertIntersections(tube, new Ray(new Point(2, 0, 100), new Vector(-1, 0, 0)),
                List.of(new Point(1, 0, 100), new Point(-1, 0, 100)), "TC24");
        // TC25: Intersection at Z=-100
        assertIntersections(tube, new Ray(new Point(2, 0, -100), new Vector(-1, 0, 0)),
                List.of(new Point(1, 0, -100), new Point(-1, 0, -100)), "TC25");

        // ---- Group: Octant shifts (Testing different axes) ----
        // TC26: Crossing Y-axis (2 points)
        assertIntersections(tube, new Ray(new Point(0, 2, 0), new Vector(0, -1, 0)),
                List.of(new Point(0, 1, 0), new Point(0, -1, 0)), "TC26");
        // TC27: Crossing diagonal XY (2 points)
        double sqrt2inv = 1.0 / Math.sqrt(2);
        assertIntersections(tube, new Ray(new Point(2, 2, 0), new Vector(-1, -1, 0)),
                List.of(new Point(sqrt2inv, sqrt2inv, 0), new Point(-sqrt2inv, -sqrt2inv, 0)), "TC27");

        // ---- Group: Precision/Epsilon (Checking boundary logic) ----
        // TC28: Ray starts very close to surface inside (1 point)
        assertIntersections(tube, new Ray(new Point(0.999999, 0, 1), new Vector(1, 0, 0)), List.of(new Point(1, 0, 1)),
                "TC28");
        // TC29: Ray starts very close to surface outside (0 points)
        assertIntersections(tube, new Ray(new Point(1.000001, 0, 1), new Vector(1, 0, 0)), null, "TC29");

        // ---- Group: Different axis directions (Tube not on Z-axis) ----
        Tube tubeY = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 1, 0)));
        // TC30: Crossing tube oriented on Y-axis (2 points)
        assertIntersections(tubeY, new Ray(new Point(2, 1, 0), new Vector(-1, 0, 0)),
                List.of(new Point(1, 1, 0), new Point(-1, 1, 0)), "TC30");

        // ---- Group: Non-origin axis head ----
        Tube tubeMoved = new Tube(1.0, new Ray(new Point(10, 10, 10), new Vector(0, 0, 1)));
        // TC31: Crossing moved tube (2 points)
        assertIntersections(tubeMoved, new Ray(new Point(12, 10, 11), new Vector(-1, 0, 0)),
                List.of(new Point(11, 10, 11), new Point(9, 10, 11)), "TC31");

        // ---- Group: Ray starts at Axis but not at P0 ----
        // TC32: Starts at (0,0,5) goes out (1 point)
        assertIntersections(tube, new Ray(new Point(0, 0, 5), new Vector(0, 1, 0)), List.of(new Point(0, 1, 5)),
                "TC32");

        // ---- Group: Tangency at different angles ----
        // TC33: Tangent at (0, 1, 0)
        assertIntersections(tube, new Ray(new Point(-2, 1, 0), new Vector(1, 0, 0)), null, "TC33");
        // TC34: Tangent at (-1, 0, 0)
        assertIntersections(tube, new Ray(new Point(-1, -2, 0), new Vector(0, 1, 0)), null, "TC34");

        // ---- Group: Oblique starting outside, missing (0 points) ----
        // TC35: Head(2,0,0), Dir(0,1,1)
        assertIntersections(tube, new Ray(new Point(2, 0, 0), new Vector(0, 1, 1)), null, "TC35");
        // TC36: Head(0,2,0), Dir(1,0,1)
        assertIntersections(tube, new Ray(new Point(0, 2, 0), new Vector(1, 0, 1)), null, "TC36");

        // ---- Group: Ray through the tube "shell" at high angle ----
        // TC37: Skimming through the side
        assertIntersections(tube, new Ray(new Point(0.9, -2, 0), new Vector(0, 1, 0)),
                List.of(new Point(0.9, -0.43588989435, 0), new Point(0.9, 0.43588989435, 0)), "TC37");

        // ---- Group: Negative direction crossing ----
        // TC38: Direction (-1, -1, -1) crossing
        assertIntersections(tube, new Ray(new Point(2, 2, 2), new Vector(-1, -1, -1)),
                List.of(new Point(sqrt2inv, sqrt2inv, 2 - (2 - sqrt2inv)),
                        new Point(-sqrt2inv, -sqrt2inv, 2 - (2 + sqrt2inv))),
                "TC38");

        // ---- Group: Vector not normalized check ----
        // TC39: Ray with direction length != 1
        assertIntersections(tube, new Ray(new Point(2, 0, 0), new Vector(-10, 0, 0)),
                List.of(new Point(1, 0, 0), new Point(-1, 0, 0)), "TC39");

        // ---- Group: Start exactly at Axis head ----
        // TC40: Perpendicular from (0,0,0) (1 point)
        assertIntersections(tube, new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)), List.of(new Point(1, 0, 0)),
                "TC40");
    }

    /**
     * Tests {@link Tube#calcIntersections(Ray, double)} with a max-distance limit.
     */
    @Test
    void testFindIntersectionsWithMaxDistance() {
        // Tube centered along the Z-axis with a radius of 1.0
        Tube tube = new Tube(1d, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));

        // Ray pointing directly across the tube from the right side (moving left along X-axis)
        // It should hit the tube at t1 = 1.0 (Point(1,0,0)) and t2 = 3.0 (Point(-1,0,0))
        Ray ray = new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0));

        // TC01: maxDistance is BEFORE the first intersection (t = 0.5)
        assertNull(tube.calcIntersections(ray, 0.5),
                "TC01: maxDistance is too short, should yield no intersections");

        // TC02: maxDistance is EXACTLY ON the first intersection (t = 1.0)
        List<Intersection> result2 = tube.calcIntersections(ray, 1.0);
        assertEquals(1, result2.size(), "TC02: maxDistance exactly on first intersection should yield 1 point");
        assertEquals(new Point(1, 0, 0), result2.get(0).point, "TC02: Wrong intersection point");

        // TC03: maxDistance is BETWEEN the two intersections (t = 2.0)
        List<Intersection> result3 = tube.calcIntersections(ray, 2.0);
        assertEquals(1, result3.size(), "TC03: maxDistance between intersections should yield 1 point");
        assertEquals(new Point(1, 0, 0), result3.get(0).point, "TC03: Wrong intersection point");

        // TC04: maxDistance is EXACTLY ON the second intersection (t = 3.0)
        List<Intersection> result4 = tube.calcIntersections(ray, 3.0);
        assertEquals(2, result4.size(), "TC04: maxDistance exactly on second intersection should yield 2 points");

        // TC05: maxDistance is AFTER both intersections (t = 4.0)
        List<Intersection> result5 = tube.calcIntersections(ray, 4.0);
        assertEquals(2, result5.size(), "TC05: maxDistance after both intersections should yield 2 points");
    }
}
