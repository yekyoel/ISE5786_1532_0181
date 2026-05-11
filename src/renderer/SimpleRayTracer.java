package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * Basic ray tracer implementation that accounts for ambient light attenuation and emission colors.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Creates a simple ray tracer for the given scene.
     *
     * @param scene the scene to trace
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    Color traceRay(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null) {
            return _scene.background;
        }

        Intersection closestIntersection = ray.findClosestIntersection(intersections);
        return calcColor(closestIntersection);
    }

    /**
     * Computes the local color for an intersection point.
     * Calculates the color based on ambient light scaled by the material's attenuation
     * factor, plus the geometry's innate emission color.
     *
     * @param intersection the intersection object containing the point, geometry, and material
     * @return the calculated resulting color
     */
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)           // Scale ambient light by material's kA
                .add(intersection.geometry.getEmission()); // Add the object's emission color
    }
}