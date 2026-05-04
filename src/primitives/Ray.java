package primitives;

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
     * Finds the closest point to the ray's origin from a given list of points.
     *
     * @param points List of points to check.
     * @return The closest point, or null if the list is empty or null.
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        Point closestPoint = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Point p : points) {
            double distanceSquared = p.distanceSquared(_origin);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestPoint = p;
            }
        }

        return closestPoint;
    }
}
