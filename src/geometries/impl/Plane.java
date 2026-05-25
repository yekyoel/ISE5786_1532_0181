package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents an infinite plane in 3D space.
 */
public class Plane extends Geometry {
    /**
     * A reference point on the plane
     */
    private final Point _point;
    /**
     * The normalized normal vector to the plane
     */
    private final Vector _normal;

    /**
     * Constructs a plane from three points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;

        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        _normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param point  a point on the plane
     * @param normal the normal vector
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    /**
     * Calculates the intersections between the ray and the plane.
     *
     * @param ray the ray to intersect with the plane
     * @return a list containing the intersection object, or null if there is no intersection
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        double nv = alignZero(_normal.dotProduct(ray.direction()));
        if (nv == 0) return null; // Ray is parallel to the plane

        // If the ray starts exactly at the plane's reference point, t is 0 (no valid forward intersection).
        // Guarding here avoids creating a forbidden zero vector in Point.subtract.
        if (_point.equals(ray.origin())) return null;

        double t = alignZero(_normal.dotProduct(_point.subtract(ray.origin())) / nv);

        // Check if t > 0 AND t <= maxDistance
        if (t > 0 && alignZero(t - maxDistance) <= 0) {
            return List.of(new Intersection(this, ray.getPoint(t)));
        }
        return null;
    }
}
