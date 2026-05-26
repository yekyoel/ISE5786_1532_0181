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
 * Advanced ray tracer implementation that accounts for ambient light attenuation,
 * emission colors, local lighting (Phong), and global effects (reflection, partial transparency, and shadowing).
 */
class SimpleRayTracer extends RayTracerBase {
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

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
        Intersection closestIntersection = findClosestIntersection(ray);
        return closestIntersection == null ? _scene.background : calcColor(closestIntersection, ray);
    }

    /**
     * Calculate the final color for a given intersection and the incoming ray.
     * Combines ambient lighting and initiates the recursive color calculation.
     *
     * @param intersection the intersection being shaded
     * @param ray          the ray that produced the intersection
     * @return the computed color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        Color color = _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA);

        if (preprocessIntersection(intersection, ray.direction())) {
            color = color.add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
        }
        return color;
    }

    /**
     * Recursive method for color calculation including local and global effects.
     *
     * @param intersection the intersection point
     * @param level        the remaining recursion depth
     * @param k            the accumulated attenuation factor
     * @return the computed color
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculate global effects combining reflection and transparency rays.
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Vector n = intersection.n;
        Vector v = intersection.v;

        Ray reflectionRay = constructReflectionRay(intersection, v, n);
        Ray transparencyRay = constructTransparencyRay(intersection, v, n);

        return calcGlobalEffect(reflectionRay, level, k, intersection.material.kR)
                .add(calcGlobalEffect(transparencyRay, level, k, intersection.material.kT));
    }

    /**
     * Calculate a single global effect for a secondary ray (transparency or reflection).
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);

        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) return _scene.background.scale(kx);

        return preprocessIntersection(intersection, ray.direction())
                ? calcColor(intersection, level - 1, kkx).scale(kx)
                : Color.BLACK;
    }

    /**
     * Helper method to construct a reflection ray.
     */
    private Ray constructReflectionRay(Intersection intersection, Vector v, Vector n) {
        double vn = v.dotProduct(n);
        if (Util.isZero(vn)) return null;
        Vector r = v.subtract(n.scale(2 * vn)).normalize();
        return new Ray(intersection.point, r, n);
    }

    /**
     * Helper method to construct a transparency ray.
     */
    private Ray constructTransparencyRay(Intersection intersection, Vector v, Vector n) {
        return new Ray(intersection.point, v, n);
    }

    /**
     * Method centralizing the calculation of intersections and selecting the closest one.
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null) return null;
        return ray.findClosestIntersection(intersections);
    }

    /**
     * Calculate local lighting contributions (emission + diffuse + specular) from all scene lights,
     * factoring in partial shadows through transparent objects.
     *
     * @param intersection the intersection being shaded
     * @param k            the accumulated global attenuation factor
     * @return the color contribution from local light sources
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {

                // Step 4: Replace the unshaded call with the new transparency method
                Double3 ktr = transparency(intersection);

                // Check if the accumulated product of ktr and k is significant enough to affect the color
                if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);
                    color = color.add(
                            lightIntensity.scale(calcDiffuse(intersection)),
                            lightIntensity.scale(calcSpecular(intersection))
                    );
                }
            }
        }
        return color;
    }

    /**
     * Steps 1+2+3: Calculates the transparency and accumulated attenuation coefficient
     * along the shadow ray. Uses the max-distance optimization (Bonus 3) to pre-filter distant objects.
     * * @param intersection the intersection point on the surface
     * @return the accumulated attenuation coefficient (1.0 = fully lit, 0.0 = fully shaded)
     */
    private Double3 transparency(Intersection intersection) {
        Vector lightDirection = intersection.l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, lightDirection, intersection.n);

        double lightDistance = intersection.light.getDistance(intersection.point);
        List<Intersection> intersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);

        if (intersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;
        for (Intersection geo : intersections) {
            // Accumulated multiplication of kT values of the objects blocking the light
            ktr = ktr.product(geo.geometry.getMaterial().kT);

            // Performance optimization: early exit if the light is almost completely blocked
            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                return Double3.ZERO;
            }
        }
        return ktr;
    }

    private Double3 calcDiffuse(Intersection intersection) {
        double nl = Math.abs(intersection.nl);
        return intersection.material.kD.scale(nl);
    }

    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(intersection.n.scale(2 * intersection.nl)).normalize();
        double minusVR = Util.alignZero(intersection.v.scale(-1).dotProduct(r));
        if (minusVR <= 0) {
            return Double3.ZERO;
        }
        double max = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(max);
    }

    /**
     * The old, binary unshaded method - kept in the code per the instructions for lecturer grading.
     */
    @SuppressWarnings("unused")
    private boolean unshaded(Intersection intersection, LightSource lightSource) {
        Vector lightDirection = lightSource.getL(intersection.point).scale(-1);
        Ray shadowRay = new Ray(intersection.point, lightDirection, intersection.n);
        double lightDistance = lightSource.getDistance(intersection.point);

        List<Intersection> intersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);

        if (intersections == null) return true;

        for (Intersection geo : intersections) {
            if (geo.geometry.getMaterial().kT.isLowerThan(MIN_CALC_COLOR_K)) {
                return false;
            }
        }
        return true;
    }
}