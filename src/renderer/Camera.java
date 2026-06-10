package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import static primitives.Util.isZero;

/**
 * Camera class representing the viewpoint of the scene.
 * It uses the Builder design pattern for construction.
 */
public class Camera implements Cloneable {
    /**
     * Camera origin point.
     */
    private Point p0;
    /**
     * Forward direction vector.
     */
    private Vector vTo;
    /**
     * Up direction vector.
     */
    private Vector vUp;
    /**
     * Right direction vector (orthogonal basis).
     */
    private Vector vRight;
    /**
     * Image writer used to store rendered pixels.
     */
    private ImageWriter imageWriter;
    /**
     * Active ray tracer implementation.
     */
    private RayTracerBase rayTracer;

    /**
     * View-plane width.
     */
    private double width;
    /**
     * View-plane height.
     */
    private double height;
    /**
     * Distance from camera origin to view plane.
     */
    private double distance;

    /**
     * Horizontal resolution in pixels.
     */
    private int nX = 1;
    /**
     * Vertical resolution in pixels.
     */
    private int nY = 1;

    /**
     * Calculated center point of the view plane.
     */
    private Point vpCenter;
    /**
     * Calculated width of one pixel in world units.
     */
    private double pixelWidth;
    /**
     * Calculated height of one pixel in world units.
     */
    private double pixelHeight;

    /**
     * Rendering mode selector: {@code 0} = single-thread, {@code -1} = parallel stream,
     * positive values = explicit raw-thread count, {@code -2} = auto raw-thread count.
     */
    private int _threadsCount = 0;
    /**
     * Number of CPU cores to keep free when auto-selecting raw worker threads.
     */
    private static final int SPARE_THREADS = 2;
    /**
     * Progress-print interval in seconds; {@code 0} disables periodic progress output.
     */
    private double _printInterval = 0;
    /**
     * Helper that tracks remaining pixels and optionally reports rendering progress.
     */
    private PixelManager pixelManager;

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

    /**
     * Renders the scene into the image writer using the configured threading mode.
     *
     * @return this camera instance for method chaining
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, _printInterval);
        return switch (_threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Renders the image using the caller's thread only (no parallelism).
     * Used when {@link #_threadsCount} is {@code 0}.
     *
     * @return this camera instance for method chaining
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; i++)
            for (int j = 0; j < nX; j++)
                castRay(j, i);
        return this;
    }

    /**
     * Renders the image using Java's parallel {@link IntStream}.
     * The JVM manages thread creation and scheduling automatically based on
     * the available processor count.
     * Used when {@link #_threadsCount} is {@code -1}.
     *
     * @return this camera instance for method chaining
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Renders the image using {@link #_threadsCount} explicitly created
     * {@link Thread} objects. Each thread repeatedly asks the {@link PixelManager}
     * for the next unrendered pixel until all pixels are done.
     * Used when {@link #_threadsCount} is greater than {@code 0}.
     *
     * @return this camera instance for method chaining
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        int count = _threadsCount;

        // Create the requested number of worker threads
        while (count-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                // Each thread works until no pixels remain
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));

        // Start all threads
        for (var thread : threads) thread.start();

        // Wait for every thread to finish before returning
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {
        }

        return this;
    }

    /**
     * Traces and writes the color for a single pixel.
     *
     * @param j pixel column index
     * @param i pixel row index
     */
    private void castRay(int j, int i) {
        Ray ray = constructRay(j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);
        pixelManager.pixelDone();
    }

    /**
     * Renders a grid on the image at the specified interval.
     *
     * @param interval the interval between grid lines (in pixels)
     * @param color    the color of the grid lines
     * @return this Camera instance (for method chaining)
     */
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

    /**
     * Writes the rendered image to a file.
     *
     * @param imageName the name of the output image file
     */
    public void writeToImage(String imageName) {
        imageWriter.writeToImage(imageName);
    }

    /**
     * Builder class for constructing a Camera instance.
     */
    public static class Builder {
        /**
         * Constructs a new Builder instance.
         */
        public Builder() {
        }

        /**
         * The camera being configured by this builder.
         */
        private final Camera camera = new Camera();

        /**
         * Explicit forward direction, used when the builder is configured by vector.
         */
        private Vector to;
        /**
         * Look-at target point, used when the builder is configured by point.
         */
        private Point target;
        /**
         * Up vector used to construct the camera basis; defaults to {@link Vector#AXIS_Y}.
         */
        private Vector up = Vector.AXIS_Y;

        /**
         * Sets the camera location.
         *
         * @param location the camera position
         * @return this Builder instance
         */
        public Builder setLocation(Point location) {
            camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera direction using a direction vector and up vector.
         *
         * @param to the forward direction vector
         * @param up the up direction vector
         * @return this Builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            this.to = to;
            this.up = up;
            this.target = null;
            return this;
        }

        /**
         * Sets the camera direction using a target point and up vector.
         *
         * @param target the target point to look at
         * @param up     the up direction vector
         * @return this Builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            this.target = target;
            this.up = up;
            this.to = null;
            return this;
        }

        /**
         * Sets the camera direction using only a target point (uses default up vector).
         *
         * @param target the target point to look at
         * @return this Builder instance
         */
        public Builder setDirection(Point target) {
            this.target = target;
            this.up = Vector.AXIS_Y;
            this.to = null;
            return this;
        }

        /**
         * Sets the view plane size.
         *
         * @param width  the width of the view plane
         * @param height the height of the view plane
         * @return this Builder instance
         */
        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance the view plane distance
         * @return this Builder instance
         */
        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the image resolution in pixels.
         *
         * @param nX the horizontal resolution (width in pixels)
         * @param nY the vertical resolution (height in pixels)
         * @return this Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer for the camera.
         *
         * @param scene the scene to trace rays in
         * @param type  the type of ray tracer to use
         * @return this Builder instance
         */
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

        /**
         * Validates the configured location/direction and computes the camera basis vectors.
         */
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

        /**
         * Validates the view plane settings and precomputes pixel geometry.
         */
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

        /**
         * Validates the resolution and creates the image writer backing store.
         */
        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution values must be strictly positive.");
            }
            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
        }

        /**
         * Builds and returns the configured Camera instance.
         *
         * @return the constructed Camera
         */
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

        // ── Multi-threading ───────────────────────────────────────────────────

        /**
         * Configures the multi-threading rendering mode.
         * <p>
         * Parameter semantics:
         * <ul>
         *   <li>{@code -2} – raw threads, count set to {@code availableProcessors() − SPARE_THREADS}</li>
         *   <li>{@code -1} – Java parallel stream (JVM chooses thread count)</li>
         *   <li>{@code  0} – single-threaded (default)</li>
         *   <li>{@code ≥1} – raw threads, exactly this many</li>
         * </ul>
         *
         * @param threads threading mode / explicit thread count
         * @return this builder for chaining
         * @throws IllegalArgumentException if {@code threads} is less than {@code -2}
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                // Automatic: reserve SPARE_THREADS cores for the JVM
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera._threadsCount = cores <= 2 ? 1 : cores; // at least 1 thread
            } else {
                camera._threadsCount = threads;
            }
            return this;
        }

        /**
         * Sets the console progress-print interval during rendering.
         * Pass {@code 0} to suppress all progress output.
         *
         * @param interval print interval as a percentage of total pixels (e.g. {@code 1}
         *                 prints a line every 1 % of the image)
         * @return this builder for chaining
         * @throws IllegalArgumentException if {@code interval} is negative
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("Print interval must be non-negative");
            camera._printInterval = interval;
            return this;
        }
    }
}