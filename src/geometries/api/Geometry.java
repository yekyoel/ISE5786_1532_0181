package geometries.api;

import primitives.Point;
import primitives.Vector;

public abstract class Geometry {

	// At this stage, there is no constructor per your teacher's instructions.

	/**
	 * Calculates the normal vector to the geometry at a specific point. * @param
	 * point The point on the geometry's surface.
	 * 
	 * @return The normal vector at that point.
	 */
	public abstract Vector getNormal(Point point);
}