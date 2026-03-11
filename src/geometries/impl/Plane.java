package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Represents an infinite plane in 3D space.
 */
public class Plane implements Geometry {
    /** A reference point on the plane */
    private final Point _q;
    /** The normalized normal vector to the plane */
    private final Vector _normal;

    /**
     * Constructs a plane from three points.
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _q = p1;
        // Calculating the normal using the cross product of two vectors on the plane
        _normal = p2.subtract(p1).crossProduct(p3.subtract(p1)).normalize();
    }

    /**
     * Constructs a plane from a point and a normal vector.
     * @param point a point on the plane
     * @param normal the normal vector
     */
    public Plane(Point point, Vector normal) {
        _q = point;
        _normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }
}