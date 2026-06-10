package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices. All vertices must
 * lie in the same plane and be arranged along the polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 *
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
    /**
     * Ordered list of polygon vertices
     */
    protected final List<Point> _vertices;
    /**
     * Plane containing the polygon
     */
    protected final Plane _plane;
    /**
     * Number of vertices
     */
    private final int _size;

    /**
     * Constructs a convex polygon from ordered vertices.
     * <p>
     * The vertices must:
     * </p>
     * <ul>
     * <li>Contain at least three points</li>
     * <li>Be ordered along the polygon edge path</li>
     * <li>Lie in the same plane</li>
     * <li>Form a convex polygon</li>
     * </ul>
     *
     * @param vertices polygon vertices in edge order
     * @throws IllegalArgumentException if the vertices do not form a valid convex
     *                                  polygon
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size = vertices.length;

        // Create the supporting plane using the first three vertices.
        // The plane stores the constant normal of the polygon.
        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3)
            return; // no need for more tests for a Triangle

        Vector n = _plane.getNormal(vertices[0]);
        // Subtracting identical vertices would create a zero vector (illegal)
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        // Cross product of consecutive edges determines orientation.
        // All edge pairs must produce the same sign relative to the normal,
        // otherwise the polygon is concave or vertices are unordered.
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            // Test that the point is in the same plane as calculated originally
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            // Test the consequent edges have
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }

    /**
     * Calculates the intersections between the ray and the polygon.
     * First checks if the ray intersects the plane containing the polygon,
     * then verifies if the intersection point lies inside the polygon's boundaries.
     *
     * @param ray the ray to check
     * @return a list containing the intersection object, or null if there is no intersection
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // Pass the maxDistance to the plane
        List<Intersection> intersections = _plane.calcIntersectionsHelper(ray, maxDistance);
        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();
        int size = _vertices.size();

        // Step 2: Check if the intersection point is inside the polygon
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);

        double s = alignZero(v.dotProduct(v1.crossProduct(v2).normalize()));
        if (isZero(s)) {
            return null;
        }
        boolean positive = s > 0;

        // Loop through the rest of the edges
        for (int i = 1; i < size; i++) {
            v1 = v2;
            v2 = _vertices.get((i + 1) % size).subtract(p0);

            s = alignZero(v.dotProduct(v1.crossProduct(v2).normalize()));
            // If s is zero or has a different sign than the first edge, it's outside
            if (isZero(s) || (positive != (s > 0))) {
                return null;
            }
        }

        // CRITICAL: We passed all edges, meaning the point is inside.
        // We must wrap the point using 'this' (the Polygon), not the plane.
        return List.of(new Intersection(this, intersections.get(0).point));
    }

    @Override
    public geometries.api.BoundingBox getBoundingBox() {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        for (primitives.Point p : _vertices) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
            if (p.getZ() < minZ) minZ = p.getZ();
            if (p.getZ() > maxZ) maxZ = p.getZ();
        }
        return new geometries.api.BoundingBox(
                new primitives.Point(minX, minY, minZ), new primitives.Point(maxX, maxY, maxZ)
        );
    }
}
