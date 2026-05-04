package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * Base type for all geometric objects that can be intersected by a ray.
 * <p>
 * Serves as the behavioral root for geometry implementations and geometry
 * composites.
 * </p>
 *
 * @author Dan Zilberstein
 */
public abstract class Intersectable {

	/** Empty default constructor for documentation tools. */
	public Intersectable() {
		/* Empty default constructor for documentation tools. */}

	/**
	 * Finds all intersection points between a given ray and the geometric shape.
	 * 
	 * @param ray The ray to intersect with the geometry.
	 * 
	 * @return A list of intersection points, or {@code null} if there are no
	 *         intersections. Note: An empty list should not be returned; return
	 *         {@code null} instead.
	 */
	public abstract List<Point> findIntersections(Ray ray);
}
