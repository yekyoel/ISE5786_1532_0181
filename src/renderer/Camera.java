package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

/**
 * Camera class representing the viewpoint of the scene.
 * It uses the Builder design pattern for construction.
 */
public class Camera implements Cloneable {
    /** Camera origin point. */
    private Point p0;
    /** Forward direction vector. */
    private Vector vTo;
    /** Up direction vector. */
    private Vector vUp;
    /** Right direction vector (orthogonal basis). */
    private Vector vRight;
    /** Image writer used to store rendered pixels. */
    private ImageWriter imageWriter;
    /** Active ray tracer implementation. */
    private RayTracerBase rayTracer;

    /** View-plane width. */
    private double width;
    /** View-plane height. */
    private double height;
    /** Distance from camera origin to view plane. */
    private double distance;

    /** Horizontal resolution in pixels. */
    private int nX = 1;
    /** Vertical resolution in pixels. */
    private int nY = 1;

    /** Calculated center point of the view plane. */
    private Point vpCenter;
    /** Calculated width of one pixel in world units. */
    private double pixelWidth;
    /** Calculated height of one pixel in world units. */
    private double pixelHeight;

    /**
     * Private default constructor to prevent direct instantiation without Builder.
     */
    private Camera() {
    }

    /**
     * Gets a new Builder instance to construct a Camera.
     *
     * @return a new Builder object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of a given pixel on the view plane.
     *
     * @param j pixel's X index (column)
     * @param i pixel's Y index (row)
     * @return a ray originating from the camera passing through the center of the specified pixel
     */
    public Ray constructRay(int j, int i) {
        Point pIJ = vpCenter;

        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(yI));
        }

        return new Ray(p0, pIJ.subtract(p0));
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Camera renderImage() {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    private void castRay(int j, int i) {
        Ray ray = constructRay(j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);
    }

    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (j % interval == 0 || i % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    public void writeToImage(String imageName) {
        imageWriter.writeToImage(imageName);
    }

    /**
     * Builder class for constructing a Camera instance.
     */
    public static class Builder {
        public Builder() {
        }

        private final Camera camera = new Camera();

        private Vector to;
        private Point target;
        private Vector up = Vector.AXIS_Y;

        public Builder setLocation(Point location) {
            camera.p0 = location;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {
            this.to = to;
            this.up = up;
            this.target = null;
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            this.target = target;
            this.up = up;
            this.to = null;
            return this;
        }

        public Builder setDirection(Point target) {
            this.target = target;
            this.up = Vector.AXIS_Y;
            this.to = null;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        // =========================================================
        // NEW BUILDER METHODS: MOVEMENT & ROTATION
        // =========================================================

        /**
         * Translates the camera location by a given offset vector before building.
         * Location must be set prior to calling this method.
         *
         * @param offset the vector by which to translate the camera
         * @return this Builder instance
         */
        public Builder move(Vector offset) {
            if (camera.p0 == null) {
                throw new IllegalStateException("Camera location must be set before moving.");
            }
            camera.p0 = camera.p0.add(offset);
            return this;
        }

        /**
         * Rotates the camera along its local axes (Pitch, Yaw, Roll) before building.
         * Both location and direction must be set prior to calling this method.
         *
         * @param pitch rotation around the local right axis (look up/down) in degrees
         * @param yaw   rotation around the local up axis (look left/right) in degrees
         * @param roll  rotation around the local forward axis (tilt left/right) in degrees
         * @return this Builder instance
         */
        public Builder rotate(double pitch, double yaw, double roll) {
            // Validate and establish the base vectors needed for rotation
            checkLocationAndDirection();

            double p = Math.toRadians(pitch);
            double y = Math.toRadians(yaw);
            double r = Math.toRadians(roll);

            Vector vToTemp = camera.vTo;
            Vector vUpTemp = camera.vUp;
            Vector vRightTemp = camera.vRight;

            // Apply Pitch (rotate around vRight)
            if (!isZero(p)) {
                double cosP = Math.cos(p);
                double sinP = Math.sin(p);
                Vector vToCos = isZero(cosP) ? null : vToTemp.scale(cosP);
                Vector vUpSin = isZero(sinP) ? null : vUpTemp.scale(sinP);

                if (vToCos == null) vToTemp = vUpSin;
                else if (vUpSin == null) vToTemp = vToCos;
                else vToTemp = vToCos.add(vUpSin);

                vToTemp = vToTemp.normalize();
                vUpTemp = vRightTemp.crossProduct(vToTemp).normalize();
            }

            // Apply Yaw (rotate around vUp)
            if (!isZero(y)) {
                double cosY = Math.cos(y);
                double sinY = Math.sin(y);
                Vector vToCos = isZero(cosY) ? null : vToTemp.scale(cosY);
                Vector vRightSin = isZero(sinY) ? null : vRightTemp.scale(sinY);

                if (vToCos == null) vToTemp = vRightSin.scale(-1);
                else if (vRightSin == null) vToTemp = vToCos;
                else vToTemp = vToCos.subtract(vRightSin);

                vToTemp = vToTemp.normalize();
                vRightTemp = vToTemp.crossProduct(vUpTemp).normalize();
            }

            // Apply Roll (rotate around vTo)
            if (!isZero(r)) {
                double cosR = Math.cos(r);
                double sinR = Math.sin(r);
                Vector vRightCos = isZero(cosR) ? null : vRightTemp.scale(cosR);
                Vector vUpSin = isZero(sinR) ? null : vUpTemp.scale(sinR);

                if (vRightCos == null) vRightTemp = vUpSin;
                else if (vUpSin == null) vRightTemp = vRightCos;
                else vRightTemp = vRightCos.add(vUpSin);

                vRightTemp = vRightTemp.normalize();
                vUpTemp = vRightTemp.crossProduct(vToTemp).normalize();
            }

            // Override builder setup with calculated absolute vectors.
            // Target is cleared to enforce the new explicit direction.
            this.to = vToTemp;
            this.up = vUpTemp;
            this.target = null;

            return this;
        }

        // =========================================================

        private void checkLocationAndDirection() {
            if (camera.p0 == null) {
                throw new MissingResourceException("Missing camera location", Camera.class.getName(), "location");
            }
            if (to == null && target == null) {
                throw new MissingResourceException("Missing camera direction", Camera.class.getName(), "direction");
            }

            if (to == null) {
                if (target.equals(camera.p0)) {
                    throw new IllegalArgumentException("Target point cannot be identical to the camera location.");
                }
                camera.vTo = target.subtract(camera.p0).normalize();
            } else {
                camera.vTo = to.normalize();
            }

            if (up == null) {
                up = Vector.AXIS_Y;
            }

            try {
                camera.vRight = camera.vTo.crossProduct(up).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The 'to' and 'up' direction vectors cannot be parallel.", e);
            }

            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
        }

        private void checkViewPlane() {
            if (camera.width <= 0 || camera.height <= 0) {
                throw new IllegalArgumentException("View plane size dimensions must be strictly positive.");
            }
            if (camera.distance <= 0) {
                throw new IllegalArgumentException("View plane distance must be strictly positive.");
            }

            camera.vpCenter = camera.p0.add(camera.vTo.scale(camera.distance));
            camera.pixelWidth = camera.width / camera.nX;
            camera.pixelHeight = camera.height / camera.nY;
        }

        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution values must be strictly positive.");
            }
            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
        }

        public Camera build() {
            checkResolution();
            // checkLocationAndDirection maps builder state into the camera's basis vectors correctly
            checkLocationAndDirection();
            // view plane is correctly mapped to wherever the camera ended up
            checkViewPlane();

            if (camera.rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }
}