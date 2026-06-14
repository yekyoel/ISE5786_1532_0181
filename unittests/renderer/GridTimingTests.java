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
 * Runs the heavy DNA Double Helix scene in four configurations and prints render times.
 * This scene includes all geometry types, all light types, and all effects (Kr, Kt, Shadows)
 * with a cinematic aesthetic, background glass panel, floating particles, and a front glass pane.
 */
class GridTimingTests {

    // ── Scene constants ───────────────────────────────────────────────────────

    /** Render resolution used for all benchmark images. */
    private static final int RESOLUTION = 600;

    /**
     * Default constructor for the JUnit test class.
     */
    GridTimingTests() {
    }

    // ── Materials ─────────────────────────────────────────────────────────────

    /** Soft, slightly glossy material for the blue backbones. */
    private static final Material BACKBONE_MAT = new Material()
            .setKd(0.6).setKs(0.4).setShininess(50).setKr(0.05);

    /** Matte material for the white/light-blue rungs. */
    private static final Material RUNG_MAT = new Material()
            .setKd(0.7).setKs(0.2).setShininess(20);

    /** Darker, less shiny material for the background DNA. */
    private static final Material BG_MAT = new Material()
            .setKd(0.5).setKs(0.1).setShininess(10);

    /** Highly reflective glass/mirror material for the back panel. */
    private static final Material GLASS_PANEL_MAT = new Material()
            .setKd(0.05).setKs(0.8).setShininess(300).setKr(0.65);

    /** Highly transparent glass for the front panel. */
    private static final Material FRONT_GLASS_MAT = new Material()
            .setKd(0.0).setKs(0.3).setShininess(300).setKt(0.9).setKr(0.1);

    /** Transparent glass material for floating particles. */
    private static final Material PARTICLE_MAT = new Material()
            .setKd(0.05).setKs(0.8).setShininess(150).setKt(0.8).setKr(0.2);

    // ── Helper Interface for 2D Rotation ──────────────────────────────────────

    /** Functional interface for transforming coordinates during helix rotation. */
    private interface Rotator {
        /**
         * Applies the transformation to the provided coordinates.
         *
         * @param x the x coordinate
         * @param y the y coordinate
         * @param z the z coordinate
         * @return the transformed point
         */
        Point apply(double x, double y, double z);
    }

    // ── Scene builder ─────────────────────────────────────────────────────────

    /**
     * Builds the benchmark scene featuring a cinematic DNA double helix,
     * floating glass particles, reflective/transparent planes, and all light types.
     *
     * @return the fully constructed benchmark scene
     */
    private static Scene buildDnaScene() {
        Scene scene = new Scene("MP2 Final Presentation Image")
                .setAmbientLight(new AmbientLight(new Color(0, 0, 0)));

        // ── 1. Plane Geometries (Transparency & Reflection) ───────────────────

        // Foreground Glass Panel (Forces heavy Kt refraction calculations)
        // Placed close to the camera (Z=70) and slightly tilted to catch reflections
        scene.geometries.add(
                new Plane(new Point(0, 0, 70), new Vector(0.05, 0.05, 1).normalize())
                        .setEmission(new Color(0, 0, 0))
                        .setMaterial(FRONT_GLASS_MAT));

        // Massive reflective glass panel behind the DNA (Forces heavy Kr calculations)
        scene.geometries.add(
                new Plane(new Point(0, 0, -350), new Vector(0, 0, 1))
                        .setEmission(new Color(2, 4, 8))
                        .setMaterial(GLASS_PANEL_MAT));

        // Dark floor to ground the scene and catch downward reflections
        scene.geometries.add(
                new Plane(new Point(0, -200, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(1, 2, 4))
                        .setMaterial(new Material().setKd(0.1).setKs(0.5).setShininess(80).setKr(0.1)));

        // ── 2. Sphere & Cylinder Geometries (DNA) ─────────────────────────────

        double slantAngle = Math.toRadians(35);

        // MAIN DNA (Center) - Generates Spheres and Cylinders
        buildHelix(scene, 50, 22, 2.8, slantAngle, -190, 380, 0, -150, 1.0, true);

        // SIDE DNA (Top-Left)
        buildHelix(scene, 25, 10, 1.5, slantAngle, -100, 200, 110, -220, 0.5, false);

        // SIDE DNA (Bottom-Right)
        buildHelix(scene, 25, 10, 1.5, slantAngle, -100, 200, -110, -220, 0.5, false);

        // ── 3. Triangle Geometry (Microscopic Floating Particles) ─────────────

        // Generates VERY small floating triangles scattered randomly through the air volume
        int numParticles = 250;
        for (int i = 0; i < numParticles; i++) {
            double px = -250 + Math.random() * 500;
            double py = -150 + Math.random() * 400;
            double pz = -300 + Math.random() * 250;

            double size = 0.3 + Math.random() * 0.6;

            Point p1 = new Point(px, py + size, pz);
            Point p2 = new Point(px - size, py - size, pz + (size / 2));
            Point p3 = new Point(px + size, py - size, pz - (size / 2));

            scene.geometries.add(new Triangle(p1, p2, p3)
                    .setEmission(new Color(10, 25, 50))
                    .setMaterial(PARTICLE_MAT));
        }

        // ── 4. All Light Types & Soft Shadows ─────────────────────────────────

        // PointLight 1: Blue Emission (Bottom-Left)
        scene.lights.add(new PointLight(
                new Color(20, 80, 255),
                new Point(-120, -80, -120))
                .setKl(0.0001).setKq(0.00001)
                .setSize(10));

        // PointLight 2: Blue Emission (Center)
        scene.lights.add(new PointLight(
                new Color(30, 100, 255),
                new Point(0, 0, -120))
                .setKl(0.0001).setKq(0.00001)
                .setSize(10));

        // SpotLight 3: Blue Emission (Top-Right)
        scene.lights.add(new SpotLight(
                new Color(20, 80, 255),
                new Point(120, 80, -120),
                new Vector(-1, -0.5, 0))
                .setKl(0.0001).setKq(0.00001)
                .setSize(10));

        // PointLight 4: Main Soft Top-Light (White)
        scene.lights.add(new PointLight(
                new Color(150, 160, 180),
                new Point(0, 180, -50))
                .setKl(0.0001).setKq(0.00001)
                .setSize(12));

        // DirectionalLight 5: Dark Fill
        scene.lights.add(new DirectionalLight(
                new Color(2, 4, 8),
                new Vector(1, -1, -1)));

        return scene;
    }

    /**
     * Helper method to generate a DNA helix.
     *
     * @param scene the scene that receives the generated geometry
     * @param steps the number of discrete steps along the helix
     * @param radius the helix radius
     * @param turns the number of turns in the helix
     * @param angle the slant rotation angle applied to the helix
     * @param xStart the starting x coordinate
     * @param xSpan the total x-axis span covered by the helix
     * @param yOffset the vertical offset applied before rotation
     * @param zCenter the z-axis center of the helix
     * @param scaleMult scale multiplier for spheres and cylinders
     * @param isMain whether this is the main central DNA structure
     */
    private static void buildHelix(Scene scene, int steps, double radius, double turns,
                                   double angle, double xStart, double xSpan,
                                   double yOffset, double zCenter, double scaleMult, boolean isMain) {

        Rotator rotSlant = (x, y, z) -> new Point(
                x * Math.cos(angle) - y * Math.sin(angle),
                x * Math.sin(angle) + y * Math.cos(angle),
                z
        );

        Material backboneMat = isMain ? BACKBONE_MAT : BG_MAT;
        Material rungMat = isMain ? RUNG_MAT : BG_MAT;

        Color backboneGlow = isMain ? new Color(5, 15, 40) : new Color(1, 3, 10);
        Color rungGlow = isMain ? new Color(10, 12, 15) : new Color(3, 4, 6);

        double sphereRadius = 7 * scaleMult;
        double cylRadius = 3 * scaleMult;

        for (int i = 0; i < steps; i++) {
            double progress = i / (double) (steps - 1);
            double t = progress * (turns * 2 * Math.PI);
            double xBase = xStart + progress * xSpan;

            Point p1 = rotSlant.apply(xBase, yOffset + radius * Math.cos(t), zCenter + radius * Math.sin(t));
            Point p2 = rotSlant.apply(xBase, yOffset + radius * Math.cos(t + Math.PI), zCenter + radius * Math.sin(t + Math.PI));

            scene.geometries.add(new Sphere(p1, sphereRadius).setEmission(backboneGlow).setMaterial(backboneMat));
            scene.geometries.add(new Sphere(p2, sphereRadius).setEmission(backboneGlow).setMaterial(backboneMat));

            Vector direction = p2.subtract(p1).normalize();
            double distance = 2 * radius;

            scene.geometries.add(
                    new Cylinder(cylRadius, new Ray(p1, direction), distance)
                            .setEmission(rungGlow)
                            .setMaterial(rungMat)
            );
        }
    }

    // ── Test runner ───────────────────────────────────────────────────────────

    /**
     * Renders the benchmark scene with the requested ray tracer configuration and thread count.
     *
     * @param label the output image name
     * @param type the ray tracer implementation to use
     * @param threads the multithreading setting passed to the camera builder
     */
    private void runTest(String label, RayTracerType type, int threads) {
        Scene scene = buildDnaScene();
        long start = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 0, 140))
                .setDirection(new Point(0, 0, -1), new Vector(0, 1, 0))
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

    /** Runs the benchmark without acceleration and without multithreading. */
    @Test
    void testNoAccelNoThreads() {
        runTest("Timing_NoAccel_NoThreads", RayTracerType.SIMPLE, 0);
    }

    /** Runs the benchmark without acceleration but with multithreading enabled. */
    @Test
    void testNoAccelWithThreads() {
        runTest("Timing_NoAccel_WithThreads", RayTracerType.SIMPLE, -2);
    }

    /** Runs the benchmark with grid acceleration and without multithreading. */
    @Test
    void testAccelNoThreads() {
        runTest("Timing_Accel_NoThreads", RayTracerType.GRID, 0);
    }

    /** Runs the benchmark with grid acceleration and with multithreading enabled. */
    @Test
    void testAccelWithThreads() {
        runTest("Timing_Accel_WithThreads", RayTracerType.GRID, -2);
    }
}