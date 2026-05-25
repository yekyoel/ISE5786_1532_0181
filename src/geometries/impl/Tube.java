package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a tube in 3D space (an infinite cylinder).
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis of the tube
     */
    protected final Ray _axis;

    /**
     * Constructs a tube with the given axis and radius.
     *
     * @param radius the radius
     * @param axis   the central axis
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Calculate the projection scalar (t) of the vector from p0 to the point
        double t = v.dotProduct(point.subtract(p0));

        // If t is zero, the projection point O is exactly p0
        Point o = isZero(t) ? p0 : p0.add(v.scale(t));

        return point.subtract(o).normalize();
    }

    /**
     * Calculates the intersections between the ray and the infinite tube.
     *
     * @param ray the ray to intersect with the tube
     * @return a list of intersection objects, or null if there are no intersections
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Vector v = ray.direction();
        Vector va = _axis.direction();
        Point p0 = ray.origin();
        Point pa = _axis.origin();

        Vector deltaP;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            deltaP = null; // Ray starts at the axis head
        }

        double vDotVa = v.dotProduct(va);
        Vector vMinusVvaVa = v;
        if (!isZero(vDotVa)) {
            try {
                vMinusVvaVa = v.subtract(va.scale(vDotVa));
            } catch (IllegalArgumentException e) {
                return null; // Ray is parallel to the axis
            }
        }
        double a = vMinusVvaVa.lengthSquared();

        double b = 0;
        double c = -_radius * _radius;
        if (deltaP != null) {
            double dpDotVa = deltaP.dotProduct(va);
            Vector dpMinusDpvaVa = deltaP;
            if (!isZero(dpDotVa)) {
                try {
                    dpMinusDpvaVa = deltaP.subtract(va.scale(dpDotVa));
                } catch (IllegalArgumentException e) {
                    dpMinusDpvaVa = null;
                }
            }

            if (dpMinusDpvaVa != null) {
                b = 2 * vMinusVvaVa.dotProduct(dpMinusDpvaVa);
                c += dpMinusDpvaVa.lengthSquared();
            }
        }

        // Solve quadratic equation: at^2 + bt + c = 0
        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null;

        double sqrtD = Math.sqrt(discriminant);
        double t1 = alignZero((-b + sqrtD) / (2 * a));
        double t2 = alignZero((-b - sqrtD) / (2 * a));

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
