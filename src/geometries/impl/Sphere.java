package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space.
 */
public class Sphere extends RadialGeometry {
	/** The center point of the sphere */
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

	@Override
	public Vector getNormal(Point point) {
		return null;
	}
}