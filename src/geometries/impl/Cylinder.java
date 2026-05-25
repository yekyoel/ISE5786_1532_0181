package geometries.impl;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

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

	/**
	 * Calculates the intersections between the ray and the finite cylinder.
	 * * @param ray the ray to intersect with the cylinder
	 * @param maxDistance maximum intersection distance
	 * @return a list of intersection objects, or null if there are no intersections
	 */
	@Override
	protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
		List<Intersection> intersections = new LinkedList<>();

		// 1. Check intersections with the infinite tube (the lateral surface)
		List<Intersection> tubeIntersections = super.calcIntersectionsHelper(ray, maxDistance);
		if (tubeIntersections != null) {
			for (Intersection inter : tubeIntersections) {
				// To check if the point is on the finite cylinder, project the vector from p0 to the point onto the axis
				double t = alignZero(_axis.direction().dotProduct(inter.point.subtract(_axis.origin())));

				// The point is on the cylinder if 0 < t < height
				if (t > 0 && t < _height) {
					// Wrap with 'this' to ensure the material/emission of the Cylinder is used
					intersections.add(new Intersection(this, inter.point));
				}
			}
		}

		// 2. Check intersections with the bases (caps)
		Point p0 = _axis.origin();
		Point p1 = p0.add(_axis.direction().scale(_height));

		// Bottom base (treat as a flat plane bounded by the radius)
		Plane bottomPlane = new Plane(p0, _axis.direction().scale(-1));
		List<Intersection> bottomInter = bottomPlane.calcIntersectionsHelper(ray, maxDistance);
		if (bottomInter != null) {
			Point p = bottomInter.get(0).point;
			// Check if the intersection point is within the circular radius
			if (alignZero(p.distanceSquared(p0) - _radiusSquared) <= 0) {
				intersections.add(new Intersection(this, p));
			}
		}

		// Top base
		Plane topPlane = new Plane(p1, _axis.direction());
		List<Intersection> topInter = topPlane.calcIntersectionsHelper(ray, maxDistance);
		if (topInter != null) {
			Point p = topInter.get(0).point;
			if (alignZero(p.distanceSquared(p1) - _radiusSquared) <= 0) {
				intersections.add(new Intersection(this, p));
			}
		}

		return intersections.isEmpty() ? null : intersections;
	}
}
