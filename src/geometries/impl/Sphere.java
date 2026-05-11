package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space.
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere
     */
    private final Point _center;

    /**
     * Constructs a sphere with the given center and radius.
     *
     * @param center the center point
     * @param radius the radius
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getNormal(Point point) {
        // The normal of a sphere is the vector from the center to the point on the
        // surface, normalized
        return point.subtract(_center).normalize();
    }

    /**
     * Calculates the intersections between the ray and the sphere using algebraic substitution.
     *
     * @param ray the ray to intersect with the sphere
     * @return a list of intersection objects, or null if there are no intersections
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector u;

        try {
            u = _center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts exactly at the center of the sphere
            return List.of(new Intersection(this, ray.getPoint(_radius)));
        }

        double tm = alignZero(v.dotProduct(u));
        double dSquared = u.lengthSquared() - tm * tm;
        double thSquared = alignZero(_radius * _radius - dSquared);

        if (thSquared <= 0) return null; // No intersections

        double th = Math.sqrt(thSquared);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 <= 0 && t2 <= 0) {
            return null;
        }

        if (t1 > 0 && t2 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2)));
        }
        if (t1 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1)));
        }
        if (t2 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t2)));
        }
        return null;
    }
}
