package primitives;

import java.util.Objects;

/**
 * Represents a ray in 3D space, consisting of an origin point and a direction.
 */
public final class Ray {
	/** The origin point of the ray */
	private final Point _origin;
	/** The normalized direction vector of the ray */
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Ray other = (Ray) obj;
		return _origin.equals(other._origin) && _direction.equals(other._direction);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_origin, _direction);
	}

	@Override
	public String toString() {
		return "Ray: " + _origin + " " + _direction;
	}
}