package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Base type for all geometric objects that can be intersected by a ray.
 * Implements the Non-Virtual Interface (NVI) pattern to standardize intersection logic.
 */
public abstract class Intersectable {

    /**
     * Helper Passive Data Structure (PDS) class that binds a specific intersection point
     * to the geometry that was intersected, caching its material.
     */
    public static class Intersection {
        /**
         * The geometry that was intersected
         */
        public final Geometry geometry;
        /**
         * The exact point of intersection
         */
        public final Point point;
        /**
         * The material of the intersected geometry
         */
        public final Material material;

        /**
         * Constructs an Intersection object.
         * Initializes the material from the given geometry.
         *
         * @param geometry the geometry that was intersected (may be null for helper usage)
         * @param point    the point of intersection
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? new Material() : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof Intersection other)
                // Note: Geometry is compared by reference (==) intentionally per requirements
                return this.geometry == other.geometry && this.point.equals(other.point);
            return false;
        }

        @Override
        public String toString() {
            return "Intersection{geometry=" + geometry + ", point=" + point + "}";
        }
    }

    /**
     * Empty default constructor for documentation tools.
     */
    public Intersectable() {
    }

    /**
     * Finds all intersection points between a given ray and the geometric shape.
     * Maintains backward compatibility by extracting Point objects from Intersections.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                  .map(intersection -> intersection.point)
                  .toList();
    }

    /**
     * Calculates the intersections between a ray and the geometry.
     * This is the public, non-overridable method of the NVI pattern.
     *
     * @param ray the ray to trace
     * @return a list of Intersection objects, or null if none are found
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Abstract helper method to be implemented by specific geometries to calculate intersections.
     * This is the protected method of the NVI pattern that child classes override.
     *
     * @param ray the ray to trace
     * @return a list of Intersection objects, or null if none are found
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);
}