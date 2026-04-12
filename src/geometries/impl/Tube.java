package geometries.impl;

import static primitives.Util.isZero;

import java.util.List;

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

	@Override
	public List<Point> findIntersections(Ray ray) {
		Vector v = ray.direction();
		Point p0 = ray.origin();
		Vector va = _axis.direction();
		Point pa = _axis.origin();

		// Vector from axis head to ray head
		Vector deltaP;
		try {
			deltaP = p0.subtract(pa);
		} catch (IllegalArgumentException e) {
			deltaP = null; // p0 == pa
		}

		// Components for the quadratic equation at^2 + bt + c = 0
		// a = (v - (v|va)*va)^2
		// b = 2 * (v - (v|va)*va) | (deltaP - (deltaP|va)*va)
		// c = (deltaP - (deltaP|va)*va)^2 - r^2

		double vDotVa = v.dotProduct(va);
		Vector vMinusVvaVa = isZero(vDotVa) ? v : v.subtract(va.scale(vDotVa));
		double a = vMinusVvaVa.lengthSquared();

		if (isZero(a))
			return null; // Ray is parallel to axis

		Vector deltaPMinusDeltaPvaVa = deltaP == null ? null
				: (isZero(deltaP.dotProduct(va)) ? deltaP : deltaP.subtract(va.scale(deltaP.dotProduct(va))));

		double b = 0;
		if (deltaPMinusDeltaPvaVa != null)
			b = 2 * vMinusVvaVa.dotProduct(deltaPMinusDeltaPvaVa);

		double c = (deltaPMinusDeltaPvaVa == null ? 0 : deltaPMinusDeltaPvaVa.lengthSquared()) - _radius * _radius;

		// Solve quadratic equation
		double discriminant = b * b - 4 * a * c;

		if (discriminant <= 0)
			return null; // No real roots or tangent

		double sqrtDisc = Math.sqrt(discriminant);
		double t1 = (-b + sqrtDisc) / (2 * a);
		double t2 = (-b - sqrtDisc) / (2 * a);

		// Return only positive t values (points in front of the ray)
		if (t1 <= 0 && t2 <= 0)
			return null;

		if (t1 > 0 && t2 > 0)
			return List.of(ray.getPoint(t1), ray.getPoint(t2));

		return t1 > 0 ? List.of(ray.getPoint(t1)) : List.of(ray.getPoint(t2));
	}
}