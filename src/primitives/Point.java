package primitives;

/**
 * Represents a point in 3D space using 3 coordinates.
 */
public class Point {
	/** The 3D coordinates of the point */
	protected final Double3 _xyz;

	/** The origin point (0,0,0) */
	public static final Point ZERO = new Point(Double3.ZERO);

	/**
	 * Constructs a point with the given 3 coordinates.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public Point(double x, double y, double z) {
		_xyz = new Double3(x, y, z);
	}

	/**
	 * Constructs a point using a Double3 object.
	 * 
	 * @param xyz the 3 coordinates
	 */
	public Point(Double3 xyz) {
		_xyz = xyz;
	}

	/**
	 * Subtracts another point from this point to get a vector.
	 * 
	 * @param other the point to subtract
	 * @return a new vector from the other point to this point
	 */
	public Vector subtract(Point other) {
		return new Vector(_xyz.subtract(other._xyz));
	}

	/**
	 * Adds a vector to this point.
	 * 
	 * @param vector the vector to add
	 * @return a new point after adding the vector
	 */
	public Point add(Vector vector) {
		return new Point(_xyz.add(vector._xyz));
	}

	/**
	 * Calculates the squared distance between this point and another point.
	 * 
	 * @param other the other point
	 * @return the squared distance
	 */
	public double distanceSquared(Point other) {
		double dx = _xyz._d1() - other._xyz._d1();
		double dy = _xyz._d2() - other._xyz._d2();
		double dz = _xyz._d3() - other._xyz._d3();
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Calculates the true distance between this point and another point.
	 * 
	 * @param other the other point
	 * @return the distance
	 */
	public double distance(Point other) {
		return Math.sqrt(distanceSquared(other));
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
		Point other = (Point) obj;
		return _xyz.equals(other._xyz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _xyz.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode(){return _xyz.hashCode();}
}
