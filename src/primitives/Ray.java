package primitives;

import geometries.api.Intersectable.Intersection;

import java.util.List;
import java.util.Objects;

import static primitives.Util.isZero;

/**
 * Represents a ray in 3D space, consisting of an origin point and a direction.
 */
public final class Ray {
    /**
     * The origin point of the ray
     */
    private final Point _origin;
    /**
     * The normalized direction vector of the ray
     */
    private final Vector _direction;

    // Inside Ray.java

    /**
     * Constant offset size to avoid self-shading / self-intersection
     */
    public static final double DELTA = 0.1;

    /**
     * Constructor for secondary rays that automatically shifts the ray head
     * along the normal vector to avoid numerical precision errors (self-intersection).
     * @param origin    the original intersection point on the geometry
     * @param direction the direction vector of the new secondary ray
     * @param normal    the normal vector of the geometry at the intersection point
     */
    public Ray(Point origin, Vector direction, Vector normal) {
        this._direction = direction.normalize();

        // Check the dot product of the direction and the normal
        double nv = normal.dotProduct(this._direction);

        if (primitives.Util.isZero(nv)) {
            // If they are perpendicular, do not shift the head
            this._origin = origin;
        } else {
            // Shift along the normal: positive direction if nv > 0, negative if nv < 0
            Vector deltaVector = normal.scale(nv > 0 ? DELTA : -DELTA);
            this._origin = origin.add(deltaVector);
        }
    }

    /**
     * Constructs a ray with the given origin point and direction vector. The
     * direction vector is automatically normalized before being saved.
     *
     * @param origin    the origin point
     * @param direction the direction vector
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Return the origin point
     *
     * @return the origin point
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Return the normalized direction vector
     *
     * @return the direction
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Calculates a point on the ray at a specific distance from the origin. P = P0
     * + t * v
     *
     * @param t the distance from the ray's head (p0)
     * @return the calculated point
     */
    public Point getPoint(double t) {
        if (isZero(t)) {
            return _origin;
        }
        return _origin.add(_direction.scale(t));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Ray: " + _origin + " " + _direction;
    }

    /**
     * Finds the closest intersection to the ray's origin from a given list of intersections.
     *
     * @param intersections A list of intersections to evaluate.
     * @return The closest Intersection object, or null if the list is empty or null.
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Intersection closest = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Intersection inter : intersections) {
            double distanceSquared = inter.point.distanceSquared(_origin);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = inter;
            }
        }
        return closest;
    }

    /**
     * Finds the closest point to the ray's origin from a given list of points.
     * This method delegates its logic to {@link #findClosestIntersection(List)}.
     *
     * @param points A list of points to evaluate.
     * @return The closest Point, or null if the list is empty or null.
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestIntersection(
                points.stream()
                .map(point -> new Intersection(null, point))
                .toList()
        ).point;
    }
}
