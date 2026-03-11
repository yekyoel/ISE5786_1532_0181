package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a finite cylinder in 3D space.
 */
public class Cylinder extends Tube {
    /** The height of the cylinder */
    private final double _height;

    /**
     * Constructs a cylinder with the given axis, radius, and height.
     * @param radius the radius
     * @param axis the central axis
     * @param height the height
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        // A cylinder's lateral surface normal equals the tube normal
        return super.getNormal(point);
    }
}