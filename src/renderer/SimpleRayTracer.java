package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
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
        return calcColor(closestIntersection, ray);
    }


    /**
     * Calculate the final color for a given intersection and the incoming ray.
     * Combines ambient, emission and local lighting contributions.
     *
     * @param intersection the intersection being shaded
     * @param ray          the ray that produced the intersection
     * @return the computed color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        Color color = _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(intersection.geometry.getEmission());

        // Calculate local effects only if the camera isn't perpendicular to the normal
        if (preprocessIntersection(intersection, ray.direction())) {
            color = color.add(calcLocalEffects(intersection));
        }
        return color;
    }

    /**
     * Calculate local lighting contributions (diffuse + specular) from all scene lights.
     *
     * @param intersection the intersection being shaded
     * @return the color contribution from local light sources
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = Color.BLACK;
        for (LightSource lightSource : _scene.lights) {
            // Checks if the light hits the same side that the camera sees
            if (preprocessLightSource(intersection, lightSource)) {
                Color lightIntensity = lightSource.getIntensity(intersection.point);
                color = color.add(
                        lightIntensity.scale(calcDiffuse(intersection)),
                        lightIntensity.scale(calcSpecular(intersection))
                );
            }
        }
        return color;
    }

    /**
     * Compute diffuse term for the intersection, scaled by the material's kD.
     *
     * @param intersection the intersection being shaded
     * @return diffuse contribution as a Double3 multiplier
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double nl = Math.abs(intersection.nl);
        return intersection.material.kD.scale(nl);
    }

    /**
     * Compute specular term for the intersection using Phong model.
     *
     * @param intersection the intersection being shaded
     * @return specular contribution as a Double3 multiplier
     */
    private Double3 calcSpecular(Intersection intersection) {
        // Reflection vector: r = l - 2 * (l * n) * n
        Vector r = intersection.l.subtract(intersection.n.scale(2 * intersection.nl)).normalize();

        // v * r
        double minusVR = Util.alignZero(intersection.v.scale(-1).dotProduct(r));
        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        double max = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(max);
    }
}