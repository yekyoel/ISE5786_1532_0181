package renderer;

import static primitives.Util.isZero;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

/**
 * Camera class representing the viewpoint of the scene.
 * It uses the Builder design pattern for construction.
 */
public class Camera implements Cloneable {
    // Basic camera fields
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;

    // View plane geometry
    private double width;
    private double height;
    private double distance;

    // Resolution (initialized to 1 by default)
    private int nX = 1;
    private int nY = 1;

    // Computed auxiliary fields
    private Point vpCenter;
    private double pixelWidth;
    private double pixelHeight;

    /**
     * Private default constructor to prevent direct instantiation without Builder.
     */
    private Camera() {
    }

    /**
     * Gets a new Builder instance to construct a Camera.
     * @return a new Builder object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of a given pixel on the view plane.
     * @param j pixel's X index (column)
     * @param i pixel's Y index (row)
     * @return a ray originating from the camera passing through the center of the specified pixel
     */
    public Ray constructRay(int j, int i) {
        // Start from the center of the view plane
        Point pIJ = vpCenter;

        // Calculate the offsets from the center of the view plane
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

        // Move the point along the right and up vectors based on the calculated offsets.
        // We use isZero() to avoid attempting to scale by zero, which would throw an exception
        // when trying to create a zero vector.
        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(yI));
        }

        // Create and return the ray from the camera's location through the calculated pixel center
        return new Ray(p0, pIJ.subtract(p0));
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Builder class for constructing a Camera instance.
     */
    public static class Builder {
        /**
         * Default ctor for JavaDoc.
         */
        public Builder(){}

        private final Camera camera = new Camera();

        // Auxiliary fields to manage the direction setup state
        private Vector to;
        private Point target;
        private Vector up = Vector.AXIS_Y;

        /**
         * Sets the camera location.
         * @param location the camera's origin point
         * @return this Builder instance
         */
        public Builder setLocation(Point location) {
            camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera direction using two explicitly given vectors.
         * @param to the forward direction vector
         * @param up the general up direction vector
         * @return this Builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            this.to = to;
            this.up = up;
            this.target = null;
            return this;
        }

        /**
         * Sets the camera direction using a target point and a general up vector.
         * @param target the point the camera is looking at
         * @param up     the general up direction vector
         * @return this Builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            this.target = target;
            this.up = up;
            this.to = null;
            return this;
        }

        /**
         * Sets the camera direction using only a target point.
         * The up vector defaults to the Y-axis.
         * @param target the point the camera is looking at
         * @return this Builder instance
         */
        public Builder setDirection(Point target) {
            this.target = target;
            this.up = Vector.AXIS_Y;
            this.to = null;
            return this;
        }

        /**
         * Sets the physical dimensions of the view plane.
         * @param width  the view plane width
         * @param height the view plane height
         * @return this Builder instance
         */
        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         * @param distance the distance to the view plane
         * @return this Builder instance
         */
        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the grid resolution of the view plane.
         * @param nX number of pixels in the X axis (width)
         * @param nY number of pixels in the Y axis (height)
         * @return this Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Validates and constructs the final Camera object.
         * Execution order is strictly enforced here.
         * @return the constructed Camera, or null if cloning fails
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        /**
         * Checks if the resolution is valid (positive integers).
         */
        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution values must be strictly positive.");
            }
        }

        /**
         * Checks and calculates the location and direction vectors.
         */
        private void checkLocationAndDirection() {
            if (camera.p0 == null) {
                throw new MissingResourceException("Missing camera location", Camera.class.getName(), "location");
            }
            if (to == null && target == null) {
                throw new MissingResourceException("Missing camera direction", Camera.class.getName(), "direction");
            }

            // Calculate or normalize the 'to' vector
            if (to == null) {
                if (target.equals(camera.p0)) {
                    throw new IllegalArgumentException("Target point cannot be identical to the camera location.");
                }
                camera.vTo = target.subtract(camera.p0).normalize();
            } else {
                camera.vTo = to.normalize();
            }

            // Ensure an 'up' vector exists
            if (up == null) {
                up = Vector.AXIS_Y;
            }

            // Calculate orthogonal right and exact up vectors using cross products
            try {
                camera.vRight = camera.vTo.crossProduct(up).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The 'to' and 'up' direction vectors cannot be parallel.", e);
            }

            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
        }

        /**
         * Checks and calculates view plane dimensions and center point.
         */
        private void checkViewPlane() {
            if (camera.width <= 0 || camera.height <= 0) {
                throw new IllegalArgumentException("View plane size dimensions must be strictly positive.");
            }
            if (camera.distance <= 0) {
                throw new IllegalArgumentException("View plane distance must be strictly positive.");
            }

            // Calculate the 3D center point of the view plane
            camera.vpCenter = camera.p0.add(camera.vTo.scale(camera.distance));

            // Calculate physical dimensions of a single pixel
            camera.pixelWidth = camera.width / camera.nX;
            camera.pixelHeight = camera.height / camera.nY;
        }
    }
}