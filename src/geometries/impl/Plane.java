package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        double nv = _normal.dotProduct(ray.direction());

        // Ray is parallel to the plane (nv == 0)
        if (isZero(nv)) {
            return null;
        }

        try {
            Vector p0Q0 = _point.subtract(ray.origin());
            double t = alignZero(_normal.dotProduct(p0Q0) / nv);

            // Intersection must be in the direction of the ray (t > 0)
            return t <= 0 ? null : List.of(new Intersection(this, ray.getPoint(t)));
        } catch (IllegalArgumentException e) {
            // Ray starts exactly at the plane's reference point Q0
            return null;
        }
    }
}
