package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometries.
 */
public abstract class Geometry extends Intersectable {
	/** Empty default constructor for documentation tools. */
	public Geometry() {
		/* Empty default constructor for documentation tools. */}

	/**
	 * Calculates the normal vector to the geometry at a specific point.
	 * 
	 * @param point the point on the geometry surface
	 * @return The normal vector at that point.
	 */
	public abstract Vector getNormal(Point point);
}
