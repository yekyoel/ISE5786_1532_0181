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
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        if (p0.equals(_center)) {
            // If the ray starts exactly at the center, t = radius
            if (alignZero(_radius - maxDistance) <= 0) {
                return List.of(new Intersection(this, ray.getPoint(_radius)));
            }
            return null;
        }

        Vector u = _center.subtract(p0);
        double tm = alignZero(v.dotProduct(u));
        double d = alignZero(Math.sqrt(u.lengthSquared() - tm * tm));

        if (d >= _radius) return null;

        double th = alignZero(Math.sqrt(_radius * _radius - d * d));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // Filter valid t values using maxDistance
        boolean isValidT1 = t1 > 0 && alignZero(t1 - maxDistance) <= 0;
        boolean isValidT2 = t2 > 0 && alignZero(t2 - maxDistance) <= 0;

        if (isValidT1 && isValidT2) {
            return List.of(
                    new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2))
            );
        } else if (isValidT1) {
            return List.of(new Intersection(this, ray.getPoint(t1)));
        } else if (isValidT2) {
            return List.of(new Intersection(this, ray.getPoint(t2)));
        }
        return null;
    }
}
