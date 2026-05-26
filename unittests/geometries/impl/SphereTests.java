package geometries.impl;

import geometries.api.Intersectable.Intersection;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for geometries.impl.Sphere class
 */
class SphereTests {
    /**
     * Default constructor for SphereTests
     */
    SphereTests() {
    }

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1d);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Simple test for normal on the sphere
        // The vector from center (0,0,0) to point (0,0,1) is (0,0,1)
        assertEquals(new Vector(0, 0, 1), sphere.getNormal(new Point(0, 0, 1)),
                "ERROR: Sphere getNormal() wrong result");
    }

    /**
     * Test method
     * for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 2, 0))),
                "EP01: Ray's line out of sphere");

        // EP02: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        Point p2 = new Point(1.53484692283495, 0.844948974278318, 0);
        Ray ray = new Ray(new Point(-1, 0, 0), new Vector(3, 1, 0));
        List<Point> result1 = sphere.findIntersections(ray);

        assertEquals(2, result1.size(), "EP02: Wrong number of points");

        // Sort by distance from ray head to ensure order is [closer point, further
        // point]
        if (result1.get(0).distanceSquared(ray.origin()) > result1.get(1).distanceSquared(ray.origin())) {
            result1 = List.of(result1.get(1), result1.get(0));
        }

        assertEquals(List.of(p1, p2), result1, "EP02: Ray crosses sphere");

        assertEquals(List.of(p1, p2), result1, "EP02: Ray crosses sphere - points or order incorrect");
        // EP03: Ray starts inside the sphere (1 point)
        List<Point> result2 = sphere.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0)));
        assertEquals(1, result2.size(), "EP03: Ray starts inside sphere");

        // EP04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))),
                "EP04: Ray starts after sphere");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // BV11: Ray starts at sphere and goes inside (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, -1, 0))).size(),
                "BV11: Ray starts at sphere and goes inside");

        // BV12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))),
                "BV12: Ray starts at sphere and goes outside");

        // **** Group 2: Ray's line goes through the center
        // BV21: Ray starts before the sphere (2 points)
        assertEquals(2, sphere.findIntersections(new Ray(new Point(1, -2, 0), new Vector(0, 1, 0))).size(),
                "BV21: Ray starts before sphere through center");

        // BV22: Ray starts at sphere and goes inside (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0))).size(),
                "BV22: Ray starts at sphere through center");

        // BV23: Ray starts inside (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, -0.5, 0), new Vector(0, 1, 0))).size(),
                "BV23: Ray starts inside through center");

        // BV24: Ray starts at the center (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 1, 0))).size(),
                "BV24: Ray starts at center");

        // BV25: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
                "BV25: Ray starts at sphere and goes outside");

        // **** Group 3: Ray's line is tangent to the sphere
        // BV31: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))),
                "BV31: Tangent ray starts before");

        // BV32: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
                "BV32: Tangent ray starts at point");
    }

    /**
     * Tests {@link Sphere#calcIntersections(Ray, double)} with a max-distance limit.
     */
    @Test
    void testFindIntersectionsWithMaxDistance() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);

        // ============ Ray starts outside ============
        // Ray intersects at t1 = 1.0 and t2 = 3.0
        Ray rayOutside = new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0));

        // TC01: ray1 - maxDistance is BEFORE the first intersection (e.g., distance 0.5)
        assertNull(sphere.calcIntersections(rayOutside, 0.5),
                "TC01: maxDistance is too short, should yield no intersections");

        // TC02: ray2 - maxDistance is EXACTLY ON the first intersection (e.g., distance 1.0)
        List<Intersection> result2 = sphere.calcIntersections(rayOutside, 1.0);
        assertEquals(1, result2.size(), "TC02: maxDistance exactly on first intersection should yield 1 point");
        assertEquals(new Point(0, 0, 0), result2.get(0).point, "TC02: Wrong intersection point");

        // TC03: ray3 - maxDistance is BETWEEN the two intersections (e.g., distance 2.0)
        List<Intersection> result3 = sphere.calcIntersections(rayOutside, 2.0);
        assertEquals(1, result3.size(), "TC03: maxDistance between intersections should yield 1 point");
        assertEquals(new Point(0, 0, 0), result3.get(0).point, "TC03: Wrong intersection point");


        // ============ Ray starts inside ============
        // Ray intersects at t = 1.5
        Ray rayInside = new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0));

        // TC04: ray4 - maxDistance is AFTER the intersection (e.g., distance 2.0)
        List<Intersection> result4 = sphere.calcIntersections(rayInside, 2.0);
        assertEquals(1, result4.size(), "TC04: maxDistance after intersection should yield 1 point");
        assertEquals(new Point(2, 0, 0), result4.get(0).point, "TC04: Wrong intersection point");

        // TC05: ray5 - maxDistance is EXACTLY ON the intersection (e.g., distance 1.5)
        List<Intersection> result5 = sphere.calcIntersections(rayInside, 1.5);
        assertEquals(1, result5.size(), "TC05: maxDistance exactly on intersection should yield 1 point");
        assertEquals(new Point(2, 0, 0), result5.get(0).point, "TC05: Wrong intersection point");

        // TC06: ray6 - maxDistance is BEFORE the intersection (e.g., distance 1.0)
        assertNull(sphere.calcIntersections(rayInside, 1.0),
                "TC06: maxDistance before intersection should yield no intersections");
    }
}
