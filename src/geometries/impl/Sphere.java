package geometries.impl;

import static primitives.Util.alignZero;

import java.util.List;

import primitives.Point;
import primitives.Ray;
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
	 * {@inheritDoc}
	 */
	@Override
	public List<Point> findIntersections(Ray ray) {
		Point p0 = ray.origin();
		Vector v = ray.direction();
		Vector u;

		try {
			u = _center.subtract(p0);
		} catch (IllegalArgumentException e) {
			// Ray starts at center
			return List.of(ray.getPoint(_radius));
		}

		double tm = alignZero(v.dotProduct(u));
		double dSquared = u.lengthSquared() - tm * tm;
		double thSquared = alignZero(_radius * _radius - dSquared);

		if (thSquared <= 0)
			return null; // No intersections

		double th = Math.sqrt(thSquared);
		double t1 = alignZero(tm - th);
		double t2 = alignZero(tm + th);

		if (t1 <= 0 && t2 <= 0)
			return null;

		if (t1 > 0 && t2 > 0)
			return List.of(ray.getPoint(t1), ray.getPoint(t2));
		if (t1 > 0)
			return List.of(ray.getPoint(t1));
		if (t2 > 0)
			return List.of(ray.getPoint(t2));

		return null;
	}
}
