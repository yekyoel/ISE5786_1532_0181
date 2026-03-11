package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Interface for all geometric bodies in the rendering engine.
 */
public interface Geometry {
    /**
     * Calculates the normal vector to the geometry at the given point.
     * @param point the point on the geometry surface
     * @return the normal vector
     */
    Vector getNormal(Point point);
}