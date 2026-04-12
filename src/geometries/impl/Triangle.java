package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;

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
		return super.findIntersections(ray);
	}
}