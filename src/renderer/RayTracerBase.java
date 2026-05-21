package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * Base class for ray tracing strategies.
 */
abstract class RayTracerBase {
    /**
     * Scene used for ray tracing calculations.
     */
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

    /**
     * Initializes caching fields related to the intersection and camera view.
     *
     * @param intersection intersection object to prepare (its cached fields will be set)
     * @param v            the view vector (from the intersection point toward the camera)
     * @return true if preprocessing indicates the surface faces the camera (non-zero n.v), false otherwise
     */
    protected boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.n = intersection.geometry.getNormal(intersection.point);
        intersection.v = v;
        intersection.nv = Util.alignZero(intersection.n.dotProduct(v));
        return !Util.isZero(intersection.nv);
    }

    /**
     * Initializes caching fields related to the light source.
     *
     * @param intersection intersection object to prepare (its cached light fields will be set)
     * @param light        the light source being considered
     * @return true if the light and view are on the same side of the surface normal, false otherwise
     */
    protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
        intersection.l = light.getL(intersection.point);
        intersection.nl = Util.alignZero(intersection.n.dotProduct(intersection.l));
        return Util.compareSign(intersection.nl, intersection.nv);
    }
}
