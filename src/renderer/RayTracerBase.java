package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Base class for ray tracing strategies.
 */
abstract class RayTracerBase {
    /** Scene used for ray tracing calculations. */
    protected Scene _scene;

    /**
     * Creates a ray tracer for the given scene.
     *
     * @param scene scene to trace
     */
    public RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /**
     * Traces a single ray and computes its resulting color.
     *
     * @param ray ray to trace
     * @return traced color
     */
    abstract Color traceRay(Ray ray);
}
