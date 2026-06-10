package renderer;

import geometries.impl.*;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Mini-Project 1 — Super-sampling demonstration tests.
 *
 * <p>Demonstrates <b>Soft Shadows</b> via area-light super-sampling.
 * Each test renders the same scene with identical camera and geometry settings;
 * the only difference is whether {@link PointLight#setSize(double)} and
 * {@link SpotLight#setSize(double)} are called on the light sources.</p>
 *
 * <p>Requirements satisfied:
 * <ul>
 * <li>≥ 10 distinct geometric bodies</li>
 * <li>≥ 3 light sources at different positions</li>
 * <li>Two test methods for the same scene: one without the improvement,
 * one with the improvement</li>
 * <li>Distinct output file names for easy visual comparison</li>
 * <li>Console render-time output for both runs</li>
 * </ul>
 * </p>
 */
class SuperSamplingTests {

    /** Default constructor to satisfy the JavaDoc generator. */
    SuperSamplingTests() {}

    // ──────────────────────────────────────────────────────────────────────────
    //  Shared scene constants
    // ──────────────────────────────────────────────────────────────────────────

    /** Render resolution — use 600-800 for final images, 400 for quick drafts. */
    private static final int RESOLUTION = 600;

    /** Number of shadow-beam samples per light when soft shadows are enabled. */
    private static final int SHADOW_SAMPLES = 81;

    /** Radius sizes for the area lights (enabled only in the "ON" test). */
    private static final double MAIN_LIGHT_SIZE   = 15;
    private static final double FILL_LIGHT_SIZE   = 10;
    private static final double ACCENT_LIGHT_SIZE = 8;

    // ── Materials ──────────────────────────────────────────────────────────────

    private static final Material FLOOR_MAT = new Material()
            .setKd(0.5).setKs(0.5).setShininess(60).setKr(0.2); // Semi-reflective floor

    private static final Material WALL_MAT = new Material()
            .setKd(0.8).setKs(0.1).setShininess(10); // Matte backdrop

    private static final Material GLASS_MAT = new Material()
            .setKd(0.1).setKs(0.8).setShininess(200).setKt(0.85).setKr(0.1); // Highly transparent

    private static final Material MIRROR_MAT = new Material()
            .setKd(0.1).setKs(0.8).setShininess(200).setKr(0.85); // Perfect mirror

    private static final Material MATTE_CYAN_MAT = new Material()
            .setKd(0.7).setKs(0.1).setShininess(10); // Flat cyan

    private static final Material GLOSSY_ORANGE_MAT = new Material()
            .setKd(0.5).setKs(0.6).setShininess(80); // Shiny plastic orange

    private static final Material GLOSSY_PURPLE_MAT = new Material()
            .setKd(0.6).setKs(0.5).setShininess(70); // Shiny plastic purple

    // ──────────────────────────────────────────────────────────────────────────
    //  Tests — without improvement (hard shadows)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    void testSoftShadowsOff() {
        Scene scene = buildScene(false);
        long start = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 80, 350))
                .setDirection(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setVpSize(200, 200)
                .setVpDistance(250)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultithreading(-2)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage("softShadows_OFF");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[OFF] Render time: %.2f seconds%n", elapsed / 1000.0);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Tests — with improvement (soft shadows)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    void testSoftShadowsOn() {
        Scene scene = buildScene(true);
        long start = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 80, 350))
                .setDirection(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setVpSize(200, 200)
                .setVpDistance(250)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultithreading(-2)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage("softShadows_ON");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[ON]  Render time: %.2f seconds%n", elapsed / 1000.0);
        System.out.printf("      (%d shadow samples per light, %d lights)%n", SHADOW_SAMPLES, 3);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Scene builder (Abstract Geometry Garden)
    // ──────────────────────────────────────────────────────────────────────────

    private Scene buildScene(boolean softShadows) {
        Scene scene = new Scene("Abstract Geometry Garden")
                .setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        // ── 1. Floor (Plane) ─────────────────────────────────────────────────
        scene.geometries.add(
                new Plane(new Point(0, -50, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(20, 25, 30))
                        .setMaterial(FLOOR_MAT)
        );

        // ── 2. Back wall (Plane) ─────────────────────────────────────────────
        scene.geometries.add(
                new Plane(new Point(0, 0, -250), new Vector(0, 0, 1))
                        .setEmission(new Color(10, 10, 15))
                        .setMaterial(WALL_MAT)
        );

        // ── 3. Central Pedestal (Cylinder) ───────────────────────────────────
        scene.geometries.add(
                new Cylinder(30, new Ray(new Point(0, -50, 0), new Vector(0, 1, 0)), 15)
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(WALL_MAT)
        );

        // ── 4. Main Focus (Sphere) - Resting on the pedestal ─────────────────
        scene.geometries.add(
                new Sphere(new Point(0, 5, 0), 40)
                        .setEmission(new Color(5, 20, 40))
                        .setMaterial(GLASS_MAT)
        );

        // ── 5. Orbiting Mirror (Sphere) - Floating left ──────────────────────
        scene.geometries.add(
                new Sphere(new Point(-65, 20, 40), 18)
                        .setEmission(new Color(0, 0, 0))
                        .setMaterial(MIRROR_MAT)
        );

        // ── 6. Sweeping Background Pipe (Tube) ───────────────────────────────
        scene.geometries.add(
                new Tube(6, new Ray(new Point(-150, -20, -100), new Vector(1, 0.4, 0)))
                        .setEmission(new Color(10, 100, 100))
                        .setMaterial(MATTE_CYAN_MAT)
        );

        // ── Floating Pyramid on the right (Triangles 7, 8, 9) ────────────────
        Point pApex = new Point(80, 50, 20);
        Point pBase1 = new Point(60, -10, 40);
        Point pBase2 = new Point(100, -10, 40);
        Point pBase3 = new Point(80, -10, -10);

        scene.geometries.add(
                // 7. Pyramid Front Face
                new Triangle(pApex, pBase1, pBase2)
                        .setEmission(new Color(150, 60, 10))
                        .setMaterial(GLOSSY_ORANGE_MAT),
                // 8. Pyramid Left Face
                new Triangle(pApex, pBase1, pBase3)
                        .setEmission(new Color(120, 40, 10))
                        .setMaterial(GLOSSY_ORANGE_MAT),
                // 9. Pyramid Right Face
                new Triangle(pApex, pBase2, pBase3)
                        .setEmission(new Color(180, 80, 20))
                        .setMaterial(GLOSSY_ORANGE_MAT)
        );

        // ── 10. Small floating sphere inside/near the pyramid ────────────────
        scene.geometries.add(
                new Sphere(new Point(80, 10, 15), 10)
                        .setEmission(new Color(100, 20, 150))
                        .setMaterial(GLOSSY_PURPLE_MAT)
        );

        // ── 11. Left Column (Cylinder) ───────────────────────────────────────
        scene.geometries.add(
                new Cylinder(12, new Ray(new Point(-100, -50, -50), new Vector(0, 1, 0)), 60)
                        .setEmission(new Color(20, 150, 150))
                        .setMaterial(MATTE_CYAN_MAT)
        );

        // ── 12. Red Matte Sphere resting on the floor front-left ─────────────
        scene.geometries.add(
                new Sphere(new Point(-40, -35, 80), 15)
                        .setEmission(new Color(180, 20, 20))
                        .setMaterial(WALL_MAT) // Using matte wall material
        );


        // ── Lights (Unchanged to maintain the specific shadow layout) ────────

        // Light 1: Main overhead
        PointLight mainLight = new PointLight(
                new Color(150, 140, 120),
                new Point(0, 180, -50))
                .setKl(0.0002).setKq(0.00002);
        if (softShadows) mainLight.setSize(MAIN_LIGHT_SIZE);
        scene.lights.add(mainLight);

        // Light 2: Left fill
        PointLight fillLight = new PointLight(
                new Color(50, 60, 130),
                new Point(-180, 100, 80))
                .setKl(0.0002).setKq(0.00002);
        if (softShadows) fillLight.setSize(FILL_LIGHT_SIZE);
        scene.lights.add(fillLight);

        // Light 3: Right accent
        SpotLight accentLight = new SpotLight(
                new Color(200, 150, 40),
                new Point(200, 150, 100),
                new Vector(-1, -0.8, -0.6))
                .setKl(0.0002).setKq(0.00002)
                .setNarrowBeam(3);
        if (softShadows) accentLight.setSize(ACCENT_LIGHT_SIZE);
        scene.lights.add(accentLight);

        // Light 4 (bonus): Directional background light (Hard shadow always)
        scene.lights.add(
                new DirectionalLight(new Color(12, 12, 20), new Vector(1, -0.5, -1))
        );

        return scene;
    }
}