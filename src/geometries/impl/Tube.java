package geometries.impl;

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
		return null;
	}
}