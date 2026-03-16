package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * The abstract class for all geometires
 */
public abstract class Geometry {
	/** Empty default cnstructor for documentation tools */
	public Geometry() {
		/* Empty default cnstructor for documentation tools */}

	/**
	 * Calculates the normal vector to the geometry at a specific point.
	 * 
	 * @param point the point on the geometry surface
	 * @return The normal vector at that point.
	 */
	public abstract Vector getNormal(Point point);
}