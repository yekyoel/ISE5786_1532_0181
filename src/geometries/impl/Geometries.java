package geometries.impl;

import java.util.LinkedList;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

/**
 * Composite class for all geometries. Implements the Composite Pattern to treat
 * a collection of geometries as a single intersectable object.
 */
public class Geometries extends Intersectable {
	/** List of intersectable geometries */
	private final List<Intersectable> geometries = new LinkedList<>();

	/**
	 * Default constructor creating an empty collection.
	 */
	public Geometries() {
	}

	/**
	 * Constructor that accepts a list of geometries to add to the collection.
	 * 
	 * @param geometries variable number of Intersectable objects
	 */
	public Geometries(Intersectable... geometries) {
		add(geometries);
	}

	/**
	 * Adds a list of geometries to the collection.
	 * 
	 * @param geometries variable number of Intersectable objects
	 */
	public void add(Intersectable... geometries) {
		if (geometries != null) {
			for (Intersectable item : geometries) {
				this.geometries.add(item);
			}
		}
	}

	/**
	 * Finds all intersection points between a ray and all geometries in the
	 * collection.
	 * 
	 * @param ray the ray to check for intersections
	 * @return a list of intersection points, or null if no intersections are found
	 */
	@Override
	// Logic inside Geometries.java
	public List<Point> findIntersections(Ray ray) {
		List<Point> result = null;
		for (Intersectable item : geometries) {
			var itemPoints = item.findIntersections(ray);
			if (itemPoints != null) {
				if (result == null)
					result = new LinkedList<>();
				result.addAll(itemPoints);
			}
		}
		return result; // Returns null if no intersections were found in any item
	}
}
