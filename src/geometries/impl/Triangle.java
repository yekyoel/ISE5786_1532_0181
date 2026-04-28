package geometries.impl;

import static primitives.Util.alignZero;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a 2D triangle in 3D space.
 */
public class Triangle extends Polygon {
	/**
	 * Constructs a triangle from three points.
	 * 
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param p3 the third point
	 */
	public Triangle(Point p1, Point p2, Point p3) {
		super(p1, p2, p3);
	}

	@Override
	public List<Point> findIntersections(Ray ray) {
		// 1. Get the intersection with the plane containing the polygon
		// Since Triangle extends Polygon, we use the inherited plane field
		List<Point> intersections = _plane.findIntersections(ray);
		if (intersections == null)
			return null;

		Point p0 = ray.origin();
		Vector v = ray.direction();

		// 2. Calculate vectors from the ray head to each vertex
		// _vertices is inherited from Polygon
		Vector v1 = _vertices.get(0).subtract(p0);
		Vector v2 = _vertices.get(1).subtract(p0);
		Vector v3 = _vertices.get(2).subtract(p0);

		// 3. Calculate normals for the three "sub-planes" formed by the ray and edges
		// n = normalize(vi x vi+1)
		Vector n1 = v1.crossProduct(v2).normalize();
		Vector n2 = v2.crossProduct(v3).normalize();
		Vector n3 = v3.crossProduct(v1).normalize();

		// 4. Check if the ray direction is on the same side of all three planes
		// We use the dot product of the ray direction and the calculated normals
		double s1 = alignZero(v.dotProduct(n1));
		double s2 = alignZero(v.dotProduct(n2));
		double s3 = alignZero(v.dotProduct(n3));

		// If any dot product is zero, the point is on an edge or vertex.
		// According to the requirements, these are not considered intersections.
		if (s1 == 0 || s2 == 0 || s3 == 0)
			return null;

		// The point is inside the triangle only if all scalar products have the same
		// sign
		if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
			return intersections;
		}

		return null;
	}
}