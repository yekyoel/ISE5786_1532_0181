package primitives;

/**
 * Represents a vector in 3D space, defined by a direction and magnitude.
 */
public final class Vector extends Point {

	/** Axis X unit vector */
	public static final Vector AXIS_X = new Vector(1, 0, 0);
	/** Axis Y unit vector */
	public static final Vector AXIS_Y = new Vector(0, 1, 0);
	/** Axis Z unit vector */
	public static final Vector AXIS_Z = new Vector(0, 0, 1);

	/**
	 * Constructs a vector with the given 3 coordinates.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @throws IllegalArgumentException if the vector is the zero vector
	 */
	public Vector(double x, double y, double z) {
		super(x, y, z);
		if (Double3.ZERO.equals(_xyz)) {
			throw new IllegalArgumentException("Zero vector is not allowed");
		}
	}

	/**
	 * Constructs a vector using a Double3 object.
	 * 
	 * @param xyz the 3 coordinates
	 * @throws IllegalArgumentException if the vector is the zero vector
	 */
	public Vector(Double3 xyz) {
		super(xyz);
		if (Double3.ZERO.equals(_xyz)) {
			throw new IllegalArgumentException("Zero vector is not allowed");
		}
	}

	/**
	 * Adds another vector to this vector.
	 * 
	 * @param vector the vector to add
	 * @return a new vector resulting from the addition
	 */
	public Vector add(Vector vector) {
		return new Vector(_xyz.add(vector._xyz));
	}

	/**
	 * Scales this vector by a scalar value.
	 * 
	 * @param scalar the scaling factor
	 * @return a new scaled vector
	 */
	public Vector scale(double scalar) {
		if (scalar == 0)
			throw new IllegalArgumentException("scale by zero is not allowed");
		return new Vector(_xyz.scale(scalar));
	}

	/**
	 * Calculates the dot product of this vector and another vector.
	 * 
	 * @param vector the other vector
	 * @return the dot product value
	 */
	public double dotProduct(Vector vector) {
		return _xyz._d1() * vector._xyz._d1() + _xyz._d2() * vector._xyz._d2() + _xyz._d3() * vector._xyz._d3();
	}

	/**
	 * Calculates the cross product of this vector and another vector.
	 * 
	 * @param vector the other vector
	 * @return a new vector that is orthogonal to both vectors
	 */
	public Vector crossProduct(Vector vector) {
		return new Vector(_xyz._d2() * vector._xyz._d3() - _xyz._d3() * vector._xyz._d2(),
				_xyz._d3() * vector._xyz._d1() - _xyz._d1() * vector._xyz._d3(),
				_xyz._d1() * vector._xyz._d2() - _xyz._d2() * vector._xyz._d1());
	}

	/**
	 * Calculates the squared length of the vector.
	 * 
	 * @return the squared length
	 */
	public double lengthSquared() {
		return dotProduct(this);
	}

	/**
	 * Calculates the true length of the vector.
	 * 
	 * @return the length
	 */
	public double length() {
		return Math.sqrt(lengthSquared());
	}

	/**
	 * Normalizes this vector, creating a new vector with the same direction but a
	 * length of 1.
	 * 
	 * @return a new normalized vector
	 */
	public Vector normalize() {
		return new Vector(_xyz.divide(length()));
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
		return super.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
