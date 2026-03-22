package geometries.impl;

import static primitives.Util.isZero;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube in 3D space (an infinite cylinder).
 */
public class Tube extends RadialGeometry {
	/** The central axis of the tube */
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
}