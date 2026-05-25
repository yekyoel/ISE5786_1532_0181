package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a 2D triangle in 3D space.
 */
public class Triangle extends Polygon {
    /**
     * Constructs a triangle from three points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    /**
     * Calculates the intersections between the ray and the triangle.
     *
     * @param ray the ray to check
     * @return a list containing the intersection object, or null if there is no intersection
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // Pass the maxDistance to the plane
        List<Intersection> intersections = _plane.calcIntersectionsHelper(ray, maxDistance);
        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // 2. Calculate vectors from the ray head to each vertex
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);
        Vector v3 = _vertices.get(2).subtract(p0);

        // 3. Calculate normals for the three "sub-planes" formed by the ray and edges
        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        // 4. Check if the ray direction is on the same side of all three planes
        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        if (s1 == 0 || s2 == 0 || s3 == 0) {
            return null;
        }

        // The point is inside the triangle only if all scalar products have the same sign
        if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
            // CRITICAL: Wrap the plane's intersection point with 'this' triangle
            return List.of(new Intersection(this, intersections.get(0).point));
        }

        return null;
    }
}
