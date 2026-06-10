package geometries.impl;

import geometries.api.Intersectable;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;

/**
 * Composite class for all geometries. Implements the Composite Pattern to treat
 * a collection of geometries as a single intersectable object.
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries
     */
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
     * Calculates the intersections between a ray and all geometries in the collection.
     * Uses the NVI pattern. Calls the public calcIntersections method on its children.
     *
     * @param ray the ray to check for intersections
     * @return a list of intersection objects, or null if no intersections are found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;
        for (Intersectable item : geometries) {
            // CRITICAL: Call the public method, not the helper!
            var itemIntersections = item.calcIntersections(ray, maxDistance);

            if (itemIntersections != null) {
                if (result == null) {
                    result = new java.util.LinkedList<>();
                }
                result.addAll(itemIntersections);
            }
        }
        return result;
    }

    public List<Intersectable> getChildren() {
        return this.geometries;
    }

    @Override
    public geometries.api.BoundingBox getBoundingBox() {
        if (geometries.isEmpty()) return null;
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        boolean hasBox = false;
        for (Intersectable item : geometries) {
            var box = item.getBoundingBox();
            if (box != null) {
                hasBox = true;
                if (box.min.getX() < minX) minX = box.min.getX();
                if (box.min.getY() < minY) minY = box.min.getY();
                if (box.min.getZ() < minZ) minZ = box.min.getZ();
                if (box.max.getX() > maxX) maxX = box.max.getX();
                if (box.max.getY() > maxY) maxY = box.max.getY();
                if (box.max.getZ() > maxZ) maxZ = box.max.getZ();
            }
        }
        return hasBox ? new geometries.api.BoundingBox(new primitives.Point(minX, minY, minZ), new primitives.Point(maxX, maxY, maxZ)) : null;
    }
}
