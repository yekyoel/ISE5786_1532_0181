package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * Interface for all geometric objects that can be intersected by a ray. This
 * class serves as the base for the behavioral model of the project, supporting
 * the Composite design pattern for geometric sets. * @author [Your Name]
 */
public abstract class Intersectable {

	/** Empty default cnstructor for documentation tools */
	public Intersectable() {
		/* Empty default cnstructor for documentation tools */}

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