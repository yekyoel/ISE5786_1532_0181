package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

/**
 * Advanced ray tracer implementation that accounts for ambient light attenuation,
 * emission colors, local lighting (Phong), and global effects (reflection, partial transparency, and shadowing).
 */
class SimpleRayTracer extends RayTracerBase {
    /**
     * Maximum recursion depth for global effect recursion.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * Minimum attenuation factor required to keep recursive contributions.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * Initial attenuation factor for recursive color calculations.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Default number of samples used for soft-shadow beams when a light requests area sampling.
     */
    private static final int DEFAULT_SHADOW_SAMPLES=81;

    /**
     * Default number of samples used for glossy/reflection-beam sampling.
     */
    private static final int DEFAULT_GLOSSY_SAMPLES=81;

    /**
     * Fallback distance used by certain beam-generation helpers when they need a reference scale.
     */
    private static final int TARGET_DISTANCE=100;
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
     * Computes recursive reflection and refraction (global) color contributions.
     * <p>
     * For each effect the material blur parameter selects the rendering mode:
     * <ul>
     *   <li>{@code kBlurR == 0} — perfect mirror: single ideal reflection ray.</li>
     *   <li>{@code kBlurR  > 0} — glossy surface: beam of rays spread around the
     *       ideal reflection direction via {@link #calcGlobalEffectBeam}.</li>
     *   <li>{@code kBlurT == 0} — clear glass: single ideal refraction ray.</li>
     *   <li>{@code kBlurT  > 0} — diffuse (blurry) glass: beam of rays spread
     *       around the incoming ray direction via {@link #calcGlobalEffectBeam}.</li>
     * </ul>
     *
     * @param gp    the current intersection (must have {@code n}, {@code v}, {@code vn} set)
     * @param level remaining recursion depth
     * @param k     accumulated attenuation factor
     * @return the combined global color contribution
     */
    private Color calcGlobalEffects(Intersection gp, int level, Double3 k) {
        Color color = Color.BLACK;

        // ── Reflection ────────────────────────────────────────────────────────
        Double3 kr = gp.material.kR;
        if (!kr.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
            // Ideal specular-reflection direction: r = v − 2(v·n)n
            Vector r = gp.v.subtract(gp.n.scale(2.0 * gp.v.dotProduct(gp.n)));

            if (Util.isZero(gp.material.kBlurR)) {
                // Perfect mirror — single ideal ray
                color = color.add(calcGlobalEffect(new Ray(gp.point, r, gp.n), level, kr, k));
            } else {
                // Glossy surface — beam of rays around the ideal reflection direction
                color = color.add(calcGlobalEffectBeam(gp, r, gp.material.kBlurR, level, kr, k));
            }
        }

        // ── Refraction (transparency) ─────────────────────────────────────────
        Double3 kt = gp.material.kT;
        if (!kt.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
            // Ideal refraction continues along the incoming ray direction (v)
            if (Util.isZero(gp.material.kBlurT)) {
                // Clear glass — single ideal ray
                color = color.add(calcGlobalEffect(new Ray(gp.point, gp.v, gp.n), level, kt, k));
            } else {
                // Diffuse glass — beam of rays around the incoming direction
                color = color.add(calcGlobalEffectBeam(gp, gp.v, gp.material.kBlurT, level, kt, k));
            }
        }

        return color;
    }

    /**
     * Calculate a single global effect for a secondary ray (transparency or reflection).
     *
     * @param ray   the secondary ray to trace
     * @param level the remaining recursion depth for secondary rays
     * @param k     the accumulated attenuation factor for recursion
     * @param kx    the material coefficient for the given effect
     * @return the color contribution from this global effect
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
     *
     * @param intersection the surface intersection
     * @param v            the view direction from the camera
     * @param n            the surface normal at the intersection
     * @return the reflection ray, or {@code null} if the ray is tangent to the surface
     */
    private Ray constructReflectionRay(Intersection intersection, Vector v, Vector n) {
        double vn = v.dotProduct(n);
        if (Util.isZero(vn)) return null;
        Vector r = v.subtract(n.scale(2 * vn)).normalize();
        return new Ray(intersection.point, r, n);
    }

    /**
     * Helper method to construct a transparency ray.
     *
     * @param intersection the surface intersection
     * @param v            the view direction from the camera
     * @param n            the surface normal at the intersection
     * @return the transparency ray starting at the surface
     */
    private Ray constructTransparencyRay(Intersection intersection, Vector v, Vector n) {
        return new Ray(intersection.point, v, n);
    }

    /**
     * Calculate and select the closest intersection for a given ray.
     *
     * @param ray the ray to trace against scene geometry
     * @return the closest intersection or {@code null} if none exist
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
     * Computes the accumulated transparency factor (ktr) from the shaded point
     * to the current light source.
     * <p>
     * Dispatches to {@link #singleRayTransparency} when {@code lightSize == 0}
     * (point/directional light, hard shadow) or {@link #softShadowTransparency}
     * when {@code lightSize > 0} (area light, soft shadow with penumbra).
     *
     * @param intersection the current intersection (light cache must be populated)
     * @return kT factor: {@link Double3#ONE} = unblocked, {@link Double3#ZERO} = full shadow
     */
    private Double3 transparency(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        double maxDistance = intersection.light.getDistance(intersection.point);
        double lightSize = intersection.light.getSize();

        if (Util.isZero(lightSize))
            return singleRayTransparency(intersection.point, pointToLight,
                    intersection.n, maxDistance);

        return softShadowTransparency(intersection, pointToLight, maxDistance, lightSize);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Shadows — single-ray helper
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Casts a single shadow ray and returns the accumulated transparency factor
     * of every geometry it passes through up to {@code maxDistance}.
     *
     * @param origin      surface point from which the shadow ray is fired
     * @param direction   direction toward the light source (or sample point)
     * @param normal      surface normal, used to offset the ray origin by ±DELTA
     * @param maxDistance maximum distance to check for blocking geometry
     * @return {@link Double3#ONE} if unblocked, {@link Double3#ZERO} if fully
     * blocked, or a component-wise product of blocking materials' kT values
     */
    private Double3 singleRayTransparency(Point origin, Vector direction,
                                          Vector normal, double maxDistance) {
        Ray shadowRay = new Ray(origin, direction, normal);
        var hits = _scene.geometries.calcIntersections(shadowRay, maxDistance);

        if (hits == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;
        for (Intersection si : hits) {
            ktr = ktr.product(si.material.kT);
            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
        }
        return ktr;
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Shadows — soft-shadow beam helper
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Computes soft shadows by sampling {@link #DEFAULT_SHADOW_SAMPLES} points on
     * the light source area and averaging the per-sample transparency factors.
     * <p>
     * The target area is centered at the light position with its basis orthogonal
     * to {@code intersection.l}.  Wrong-side samples (the "sunset effect") contribute
     * {@code 0} ktr but are counted in the denominator, as required by the spec.
     *
     * @param intersection the current intersection (light cache must be populated)
     * @param pointToLight direction from surface toward the light center
     * @param maxDistance  distance from surface to the light center
     * @param lightSize    radius of the light source area (must be &gt; 0)
     * @return the averaged ktr across all samples
     */
    private Double3 softShadowTransparency(Intersection intersection,
                                           Vector pointToLight,
                                           double maxDistance,
                                           double lightSize) {
        Point lightCenter = intersection.point.add(pointToLight.scale(maxDistance));

        List<Point> lightSamples = new Blackboard()
                .setCenter(lightCenter)
                .setSize(lightSize)
                .setNumSamples(DEFAULT_SHADOW_SAMPLES)
                .buildBasis(intersection.l)
                .getSamplePoints();

        if (lightSamples.isEmpty()) return Double3.ONE;

        double signRef = Util.alignZero(intersection.n.dotProduct(pointToLight));

        Double3 ktrSum = Double3.ZERO;
        for (Point sample : lightSamples) {
            Vector rawDir = sample.subtract(intersection.point);
            double sampleDotN = Util.alignZero(intersection.n.dotProduct(rawDir));
            if (signRef * sampleDotN <= 0) continue;  // wrong side — zero contribution

            double sampleDist = rawDir.length();
            ktrSum = ktrSum.add(
                    singleRayTransparency(intersection.point, rawDir,
                            intersection.n, sampleDist));
        }

        return ktrSum.divide(lightSamples.size());
    }

    /**
     * Calculate diffuse light contribution for the current intersection.
     *
     * @param intersection the current surface intersection
     * @return the diffuse attenuation factor for the intersection
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double nl = Math.abs(intersection.nl);
        return intersection.material.kD.scale(nl);
    }

    /**
     * Calculate specular light contribution for the current intersection.
     *
     * @param intersection the current surface intersection
     * @return the specular attenuation factor for the intersection
     */
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
     * The old, binary unshaded method kept for lecturer grading.
     *
     * @param intersection the surface intersection
     * @param lightSource  the light source being evaluated
     * @return {@code true} if no opaque geometry blocks the light
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
    /**
     * Computes a glossy reflection or diffuse transparency contribution by tracing
     * a beam of rays distributed around the ideal secondary ray direction.
     *
     * <h4>Algorithm</h4>
     * <ol>
     *   <li>A virtual target area is placed on the ideal secondary ray at distance
     *       {@link #TARGET_DISTANCE}; its half-extent equals {@code blur}.</li>
     *   <li>A {@link Blackboard} generates {@link #DEFAULT_GLOSSY_SAMPLES} sample
     *       points across that area, with a basis orthogonal to {@code idealDir}.</li>
     *   <li>For each sample, a secondary ray is cast from the surface point toward
     *       the sample.  Rays that cross to the wrong side of the surface
     *       ({@code n · sampleDir} opposite sign to {@code n · idealDir}) are
     *       discarded — preventing light from leaking through at grazing angles.</li>
     *   <li>The colors of all valid rays (each already attenuated by {@code kx}
     *       inside {@link #calcGlobalEffect}) are averaged.  If no valid sample
     *       exists, the single ideal ray is used as a fallback.</li>
     * </ol>
     *
     * <p><b>Note:</b> unlike soft shadows, wrong-side samples are excluded from
     * <em>both</em> numerator and denominator, because for glossy surfaces the
     * average represents the fraction of the visible cone — not the fraction of a
     * light disk.</p>
     *
     * @param gp       the current intersection
     * @param idealDir the ideal secondary ray direction (normalized)
     * @param blur     the material blur parameter ({@code kBlurR} or {@code kBlurT});
     *                 interpreted as the target area half-extent at {@link #TARGET_DISTANCE}
     * @param level    remaining recursion depth
     * @param kx       material effect coefficient ({@code kR} or {@code kT})
     * @param k        accumulated attenuation factor
     * @return the averaged attenuated color contribution of the beam
     */
    private Color calcGlobalEffectBeam(Intersection gp, Vector idealDir, double blur,
                                       int level, Double3 kx, Double3 k) {
        // Place the virtual target area along the ideal secondary ray
        Point targetCenter = gp.point.add(idealDir.scale(TARGET_DISTANCE));

        // Build the sampling region orthogonal to the ideal ray direction
        List<Point> samples = new Blackboard()
                .setCenter(targetCenter)
                .setSize(blur)                    // half-extent at TARGET_DISTANCE
                .setNumSamples(DEFAULT_GLOSSY_SAMPLES)
                .buildBasis(idealDir)             // area normal = ideal direction
                .getSamplePoints();

        // Reference: sign of n · idealDir tells which surface side is "correct"
        double idealDotN = Util.alignZero(gp.n.dotProduct(idealDir));

        Color color = Color.BLACK;
        int validCount = 0;

        for (Point sample : samples) {
            // Vector from surface point to this target sample
            Vector dirToSample = sample.subtract(gp.point);

            // Filter: discard rays that cross to the wrong surface side
            // (n · dirToSample must share the sign of n · idealDir)
            double sampleDotN = Util.alignZero(gp.n.dotProduct(dirToSample));
            if (idealDotN * sampleDotN <= 0) continue; // wrong side — skip

            // Trace this secondary ray; calcGlobalEffect scales result by kx
            Ray beamRay = new Ray(gp.point, dirToSample, gp.n);
            color = color.add(calcGlobalEffect(beamRay, level, kx, k));
            validCount++;
        }

        // Fallback: if all samples filtered (extreme grazing angle) use the ideal ray
        if (validCount == 0)
            return calcGlobalEffect(new Ray(gp.point, idealDir, gp.n), level, kx, k);

        // Average over valid-sample colors only
        return color.reduce(validCount);
    }
}