package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * Basic ray tracer implementation.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Creates a simple ray tracer for the given scene.
     *
     * @param scene scene to trace
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Color traceRay(Ray ray) {
        List<Point> intersections = _scene.geometries.findIntersections(ray);
        if (intersections == null) {
            return _scene.background;
        }

        Point closestPoint = ray.findClosestPoint(intersections);
        return closestPoint == null ? _scene.background : calcColor(closestPoint);
    }

    /**
     * Computes the local color for an intersection point.
     *
     * @param intersection intersection point
     * @return resulting color
     */
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();
    }
}
