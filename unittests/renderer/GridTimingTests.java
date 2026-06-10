package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * MP2 performance benchmark — Regular Grid acceleration.
 * <p>
 * Runs the same heavy scene in four configurations and prints render times,
 * allowing a fair comparison of the acceleration and multi-threading speedups.
 * <p>
 * Configurations:
 * <ol>
 *   <li>No acceleration, no threading</li>
 *   <li>No acceleration, with threading</li>
 *   <li>Grid acceleration, no threading</li>
 *   <li>Grid acceleration, with threading</li>
 * </ol>
 */
class GridTimingTests {

    // ── Scene constants ───────────────────────────────────────────────────────

    /**
     * Resolution for all timing runs — high enough to make times meaningful.
     */
    private static final int RESOLUTION = 600;

    /** Radius of the main overhead point light. */
    private static final double MAIN_LIGHT_SIZE = 18;
    /** Radius of the left fill point light. */
    private static final double FILL_LIGHT_SIZE = 12;
    /** Radius of the right accent spot light. */
    private static final double RIM_LIGHT_SIZE = 8;
    /** Radius of the rear rim spot light. */
    private static final double ACCENT_LIGHT_SIZE = 6;

    /** Default constructor for the JUnit test class. */
    GridTimingTests() {
    }

    // ── Materials ─────────────────────────────────────────────────────────────

    /**
     * Polished reflective floor.
     */
    private static final Material FLOOR_MAT = new Material()
            .setKd(0.35).setKs(0.5).setShininess(80).setKr(0.2);

    /**
     * Plain matte wall.
     */
    private static final Material WALL_MAT = new Material()
            .setKd(0.7).setKs(0.1).setShininess(10);

    /**
     * Glossy metallic sphere.
     */
    private static final Material METAL_MAT = new Material()
            .setKd(0.15).setKs(0.75).setShininess(250).setKr(0.65);

    /**
     * Translucent blue glass sphere.
     */
    private static final Material GLASS_MAT = new Material()
            .setKd(0.05).setKs(0.4).setShininess(180).setKt(0.75).setKr(0.1);

    /**
     * Matte red sphere.
     */
    private static final Material RED_MAT = new Material()
            .setKd(0.65).setKs(0.25).setShininess(40);

    /**
     * Matte green sphere.
     */
    private static final Material GREEN_MAT = new Material()
            .setKd(0.65).setKs(0.2).setShininess(30);

    /**
     * Yellow-gold shiny material.
     */
    private static final Material GOLD_MAT = new Material()
            .setKd(0.3).setKs(0.65).setShininess(130).setKr(0.25);

    /**
     * Matte purple.
     */
    private static final Material PURPLE_MAT = new Material()
            .setKd(0.7).setKs(0.15).setShininess(20);

    /**
     * White pedestal.
     */
    private static final Material WHITE_MAT = new Material()
            .setKd(0.8).setKs(0.1).setShininess(10);

    /**
     * Cyan matte.
     */
    private static final Material CYAN_MAT = new Material()
            .setKd(0.6).setKs(0.3).setShininess(50);

    // ── Scene builder ─────────────────────────────────────────────────────────

    /**
     * Builds the heavy benchmark scene. Always called with {@code softShadows = true}
     * so that MP1's improvement is active at high quality in every run.
     * <p>
     * The scene contains:
     * <ul>
     *   <li>3 infinite planes (floor + 2 walls)</li>
     *   <li>180 spheres arranged in a 6 × 6 × 5 grid</li>
     *   <li>6 cylinders (pillars)</li>
     *   <li>2 decorative triangles on the floor</li>
     *   <li>5 light sources of all three supported types</li>
     *   <li>Shadow, reflection, and transparency all active</li>
     * </ul>
     *
     * @return fully configured scene ready for rendering
     */
    private static Scene buildHeavyScene() {
        Scene scene = new Scene("MP2 Grid Benchmark")
                .setAmbientLight(new AmbientLight(new Color(8, 8, 10)));

        // ── Room geometry ─────────────────────────────────────────────────────

        // Floor
        scene.geometries.add(
                new Plane(new Point(0, -80, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(25, 25, 25))
                        .setMaterial(FLOOR_MAT));

        // Back wall
        scene.geometries.add(
                new Plane(new Point(0, 0, -300), new Vector(0, 0, 1))
                        .setEmission(new Color(35, 35, 45))
                        .setMaterial(WALL_MAT));

        // Left wall
        scene.geometries.add(
                new Plane(new Point(-200, 0, 0), new Vector(1, 0, 0))
                        .setEmission(new Color(45, 35, 35))
                        .setMaterial(WALL_MAT));

        // ── 6 × 6 × 5 sphere grid (180 spheres) ──────────────────────────────
        // Alternates between metal, glass, red, green, gold, purple, cyan, white
        Material[] mats = {METAL_MAT, GLASS_MAT, RED_MAT, GREEN_MAT,
                GOLD_MAT, PURPLE_MAT, CYAN_MAT, WHITE_MAT};
        Color[] cols = {
                new Color(50, 50, 55),   // metal
                new Color(10, 20, 90),   // glass
                new Color(160, 20, 20),  // red
                new Color(20, 140, 30),  // green
                new Color(180, 140, 10), // gold
                new Color(100, 20, 120), // purple
                new Color(10, 120, 130), // cyan
                new Color(160, 160, 160) // white
        };

        int matIdx = 0;
        for (int gx = 0; gx < 6; gx++) {
            for (int gy = 0; gy < 5; gy++) {
                for (int gz = 0; gz < 6; gz++) {
                    double x = -125 + gx * 50;
                    double y = -72 + gy * 40;
                    double z = -260 + gz * 45;
                    int idx = matIdx % mats.length;
                    scene.geometries.add(
                            new Sphere(new Point(x, y, z), 12)
                                    .setEmission(cols[idx])
                                    .setMaterial(mats[idx]));
                    matIdx++;
                }
            }
        }

        // ── 6 cylinders as pillars ────────────────────────────────────────────
        double[] pillarX = {-160, -80, 0, 80, 160, -160};
        double[] pillarZ = {-280, -280, -280, -280, -280, -100};
        for (int i = 0; i < 6; i++) {
            scene.geometries.add(
                    new Cylinder(8,
                            new Ray(new Point(pillarX[i], -80, pillarZ[i]),
                                    new Vector(0, 1, 0)),
                            160)
                            .setEmission(new Color(130, 120, 90))
                            .setMaterial(GOLD_MAT));
        }

        // ── 2 decorative triangles on the floor ───────────────────────────────
        scene.geometries.add(
                new Triangle(
                        new Point(-80, -79, 20),
                        new Point(-20, -79, 80),
                        new Point(-120, -79, 90))
                        .setEmission(new Color(100, 30, 120))
                        .setMaterial(PURPLE_MAT));

        scene.geometries.add(
                new Triangle(
                        new Point(60, -79, 30),
                        new Point(130, -79, 10),
                        new Point(110, -79, 100))
                        .setEmission(new Color(170, 120, 10))
                        .setMaterial(GOLD_MAT));

        // ── 5 lights — all three types, at different positions ────────────────

        // Light 1: Main overhead PointLight — warm white, large area
        PointLight mainLight = new PointLight(
                new Color(160, 150, 130),
                new Point(0, 220, -120))
                .setKl(0.0001).setKq(0.00001)
                .setSize(MAIN_LIGHT_SIZE);
        scene.lights.add(mainLight);

        // Light 2: Left fill PointLight — cool blue
        PointLight fillLight = new PointLight(
                new Color(40, 55, 140),
                new Point(-200, 120, 60))
                .setKl(0.0001).setKq(0.00001)
                .setSize(FILL_LIGHT_SIZE);
        scene.lights.add(fillLight);

        // Light 3: Right accent SpotLight — warm amber
        SpotLight accentLight = new SpotLight(
                new Color(210, 160, 40),
                new Point(220, 160, 80),
                new Vector(-1, -0.7, -0.5))
                .setKl(0.0001).setKq(0.00001)
                .setNarrowBeam(2)
                .setSize(ACCENT_LIGHT_SIZE);
        scene.lights.add(accentLight);

        // Light 4: Back SpotLight — red-purple rim light from behind
        SpotLight rimLight = new SpotLight(
                new Color(180, 30, 160),
                new Point(0, 100, -310),
                new Vector(0, -0.3, 1))
                .setKl(0.0001).setKq(0.00001)
                .setSize(RIM_LIGHT_SIZE);
        scene.lights.add(rimLight);

        // Light 5: Directional background fill — very dim, no soft shadow needed
        scene.lights.add(
                new DirectionalLight(
                        new Color(10, 10, 18),
                        new Vector(1, -0.5, -1)));

        return scene;
    }

    // ── Test runner ───────────────────────────────────────────────────────────

    /**
     * Renders the benchmark scene with the given ray-tracer type and thread count,
     * writes the image, and prints the elapsed time.
     *
     * @param label   output image file name and console label
     * @param type    {@link RayTracerType#SIMPLE} or {@link RayTracerType#GRID}
     * @param threads {@code 0} = single-threaded, {@code -2} = auto raw threads
     */
    private void runTest(String label, RayTracerType type, int threads) {
        Scene scene = buildHeavyScene();
        long start = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 60, 350))
                .setDirection(new Point(0, -10, -120), new Vector(0, 1, 0))
                .setVpDistance(300)
                .setVpSize(220, 220)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, type)
                .setMultithreading(threads)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage(label);

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[%-35s] %.2f seconds%n", label, elapsed / 1000.0);
    }

    // ── 4 required test configurations ───────────────────────────────────────

    /**
     * Configuration 1: No acceleration, no multi-threading.
     * This is the baseline — the slowest configuration.
     */
    @Test
    void testNoAccelNoThreads() {
        runTest("Timing_NoAccel_NoThreads", RayTracerType.SIMPLE, 0);
    }

    /**
     * Configuration 2: No acceleration, with raw multi-threading.
     * Shows the threading speedup alone.
     */
    @Test
    void testNoAccelWithThreads() {
        runTest("Timing_NoAccel_WithThreads", RayTracerType.SIMPLE, -2);
    }

    /**
     * Configuration 3: Regular Grid acceleration, no multi-threading.
     * Shows the grid speedup alone.
     */
    @Test
    void testAccelNoThreads() {
        runTest("Timing_Accel_NoThreads", RayTracerType.GRID, 0);
    }

    /**
     * Configuration 4: Regular Grid acceleration with raw multi-threading.
     * The fastest configuration — shows combined speedup.
     */
    @Test
    void testAccelWithThreads() {
        runTest("Timing_Accel_WithThreads", RayTracerType.GRID, -2);
    }
}