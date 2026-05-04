package geometries.impl;

import static primitives.Util.isZero;

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
	 * 
	 * @param radius the radius
	 * @param axis   the central axis
	 * @param height the height
	 */
	public Cylinder(double radius, Ray axis, double height) {
		super(radius, axis);
		_height = height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vector getNormal(Point point) {
		Point p0 = _axis.origin();
		Vector v = _axis.direction();

		// Check if point is on the bottom base (including the center point p0)
		if (point.equals(p0) || isZero(v.dotProduct(point.subtract(p0)))) {
			return v.scale(-1); // Normal to bottom base is opposite to the axis direction
		}

		// Check if point is on the top base (including the center point p1)
		Point p1 = p0.add(v.scale(_height));
		if (point.equals(p1) || isZero(v.dotProduct(point.subtract(p1)))) {
			return v; // Normal to top base is in the axis direction
		}

		// Otherwise, the point is on the lateral surface, so we use the Tube's logic
		return super.getNormal(point);
	}

}
