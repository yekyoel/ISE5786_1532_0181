package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * End-to-end render tests for the ping-pong scene, comparing hard and soft shadows.
 */
class PingPongSSTests {

    /**
     * Render resolution kept high for visible shadow-quality comparisons.
     */
    private static final int RESOLUTION = 1000; // Keep high-res for final comparison

    /**
     * Sun-like spotlight radius used when soft shadows are enabled.
     */
    private static final double SUN_LIGHT_SIZE = 22.0;
    /**
     * Ball-fill light radius used to soften the highlight around the ball.
     */
    private static final double BALL_LIGHT_SIZE = 6.0;

    /**
     * Default constructor required by the test framework and Javadoc generation.
     */
    PingPongSSTests() {
    }

    /**
     * Renders the ping-pong scene with hard shadows to generate the baseline image.
     */
    @Test
    void testPingPongMatchOff() {
        Scene scene = buildPingPongScene(false);
        long start = System.currentTimeMillis();

        renderer.Camera.getBuilder()
                .setLocation(new Point(200, 180, 350))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpDistance(400)
                .setVpSize(300, 300)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, renderer.RayTracerType.SIMPLE)
                .setMultithreading(-2)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage("pingPongMatch_OFF");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[PING-PONG OFF] Render time: %.2f seconds%n", elapsed / 1000.0);
    }

    /**
     * Renders the ping-pong scene with soft shadows enabled for comparison.
     */
    @Test
    void testPingPongMatchOn() {
        Scene scene = buildPingPongScene(true);
        long start = System.currentTimeMillis();

        renderer.Camera.getBuilder()
                .setLocation(new Point(200, 180, 350))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpDistance(400)
                .setVpSize(300, 300)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, renderer.RayTracerType.SIMPLE)
                .setMultithreading(-2)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage("pingPongMatch_ON");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[PING-PONG ON]  Render time: %.2f seconds%n", elapsed / 1000.0);
    }

    /**
     * Builds the ping-pong scene with either point-sized or area-sized lights.
     *
     * @param softShadows {@code true} for area lights, {@code false} for hard shadows
     * @return a fully configured scene ready to render
     */
    public Scene buildPingPongScene(boolean softShadows) {
        // 1. Initialize Scene & "Sky" Background
        Scene scene = new Scene("Ping Pong Match");
        scene.setBackground(new Color(135, 206, 235)); // Sky Blue background
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 40), Double3.ONE));

        // Floor (Large flat polygon at Y = -50)
        Polygon floor = new Polygon(
                new Point(-1000, -50, 1000),
                new Point(1000, -50, 1000),
                new Point(1000, -50, -1000),
                new Point(-1000, -50, -1000)
        );
        floor.setEmission(new Color(3, 66, 14)) // Forest/Grass Green
                .setMaterial(new Material().setKd(0.8).setKs(0.1).setShininess(5));

        // The Ping Pong Table (Dark blue, matte/non-reflective)
        Polygon table = new Polygon(
                new Point(-75, 0, 137),
                new Point(75, 0, 137),
                new Point(75, 0, -137),
                new Point(-75, 0, -137)
        );
        table.setEmission(new Color(12, 4, 148)) // Dark Blue
                .setMaterial(new Material().setKd(0.7).setKs(0.0).setShininess(1).setKr(0.0));

        // White Table Lines (Elevated to Y = 0.1 to prevent Z-fighting)
        Material lineMat = new Material().setKd(0.8).setKs(0.2).setShininess(10);
        Color lineColor = new Color(250, 250, 250); // Crisp White

        Polygon centerLine = new Polygon(new Point(-1, 0.1, 137), new Point(1, 0.1, 137), new Point(1, 0.1, -137), new Point(-1, 0.1, -137));
        Polygon leftLine = new Polygon(new Point(-75, 0.1, 137), new Point(-73, 0.1, 137), new Point(-73, 0.1, -137), new Point(-75, 0.1, -137));
        Polygon rightLine = new Polygon(new Point(73, 0.1, 137), new Point(75, 0.1, 137), new Point(75, 0.1, -137), new Point(73, 0.1, -137));
        Polygon frontLine = new Polygon(new Point(-75, 0.1, 137), new Point(75, 0.1, 137), new Point(75, 0.1, 135), new Point(-75, 0.1, 135));
        Polygon backLine = new Polygon(new Point(-75, 0.1, -135), new Point(75, 0.1, -135), new Point(75, 0.1, -137), new Point(-75, 0.1, -137));

        centerLine.setEmission(lineColor).setMaterial(lineMat);
        leftLine.setEmission(lineColor).setMaterial(lineMat);
        rightLine.setEmission(lineColor).setMaterial(lineMat);
        frontLine.setEmission(lineColor).setMaterial(lineMat);
        backLine.setEmission(lineColor).setMaterial(lineMat);

        // 4 Table Legs (Cylinders extending down from Y = 0 to Y = -50)
        Material legMat = new Material().setKd(0.5).setKs(0.5).setShininess(60);
        Color legColor = new Color(120, 120, 130); // Metallic Silver/Gray

        Cylinder legFL = new Cylinder(2.5, new Ray(new Point(-70, -50, 130), new Vector(0, 1, 0)), 50);
        Cylinder legFR = new Cylinder(2.5, new Ray(new Point(70, -50, 130), new Vector(0, 1, 0)), 50);
        Cylinder legBL = new Cylinder(2.5, new Ray(new Point(-70, -50, -130), new Vector(0, 1, 0)), 50);
        Cylinder legBR = new Cylinder(2.5, new Ray(new Point(70, -50, -130), new Vector(0, 1, 0)), 50);

        legFL.setEmission(legColor).setMaterial(legMat);
        legFR.setEmission(legColor).setMaterial(legMat);
        legBL.setEmission(legColor).setMaterial(legMat);
        legBR.setEmission(legColor).setMaterial(legMat);

        // The Net (White and translucent)
        Polygon net = new Polygon(new Point(-75, 0, 0), new Point(75, 0, 0), new Point(75, 12, 0), new Point(-75, 12, 0));
        net.setEmission(new Color(200, 200, 200)).setMaterial(new Material().setKd(0.2).setKs(0.6).setShininess(20).setKt(0.85));

        // 2 Net Posts
        Color postColor = new Color(30, 30, 30);
        Cylinder leftPost = (Cylinder) new Cylinder(1.5, new Ray(new Point(-75, 0, 0), new Vector(0, 1, 0)), 14).setEmission(postColor).setMaterial(legMat);
        Cylinder rightPost = (Cylinder) new Cylinder(1.5, new Ray(new Point(75, 0, 0), new Vector(0, 1, 0)), 14).setEmission(postColor).setMaterial(legMat);

        // Yellow Ball
        Sphere ball = new Sphere(new Point(30, 4, 40), 4);
        ball.setEmission(new Color(150, 150, 0)).setMaterial(new Material().setKd(0.6).setKs(0.8).setShininess(100));

        // ================= PLAYER 1 (Red side, Z = -160) =================
        Material p1Mat = new Material().setKd(0.7).setKs(0.3).setShininess(30);
        Color p1Color = new Color(150, 20, 20);

        Sphere p1Head = (Sphere) new Sphere(new Point(0, 45, -160), 12).setEmission(p1Color).setMaterial(p1Mat);
        Point p1Apex = new Point(0, 33, -160);
        Point p1fl = new Point(-20, -15, -140);
        Point p1fr = new Point(20, -15, -140);
        Point p1bl = new Point(-20, -15, -180);
        Point p1br = new Point(20, -15, -180);
        Triangle p1Front = (Triangle) new Triangle(p1Apex, p1fl, p1fr).setEmission(p1Color).setMaterial(p1Mat);
        Triangle p1Right = (Triangle) new Triangle(p1Apex, p1fr, p1br).setEmission(p1Color).setMaterial(p1Mat);
        Triangle p1Back = (Triangle) new Triangle(p1Apex, p1br, p1bl).setEmission(p1Color).setMaterial(p1Mat);
        Triangle p1Left = (Triangle) new Triangle(p1Apex, p1bl, p1fl).setEmission(p1Color).setMaterial(p1Mat);

        Cylinder p1PaddleFace = new Cylinder(6, new Ray(new Point(-30, 15, -142), new Vector(0, 0, 1)), 1);
        p1PaddleFace.setEmission(new Color(10, 10, 10)).setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(20));
        Cylinder p1PaddleHandle = new Cylinder(1.2, new Ray(new Point(-30, 5, -141.5), new Vector(0, 1, 0)), 10);
        p1PaddleHandle.setEmission(new Color(160, 105, 60)).setMaterial(new Material().setKd(0.6).setKs(0.2).setShininess(10));

        // ================= PLAYER 2 (Green side, Z = 160) =================
        Material p2Mat = new Material().setKd(0.7).setKs(0.3).setShininess(30);
        Color p2Color = new Color(20, 150, 20);

        Sphere p2Head = (Sphere) new Sphere(new Point(0, 45, 160), 12).setEmission(p2Color).setMaterial(p2Mat);
        Point p2Apex = new Point(0, 33, 160);
        Point p2fl = new Point(20, -15, 140);
        Point p2fr = new Point(-20, -15, 140);
        Point p2bl = new Point(20, -15, 180);
        Point p2br = new Point(-20, -15, 180);
        Triangle p2Front = (Triangle) new Triangle(p2Apex, p2fl, p2fr).setEmission(p2Color).setMaterial(p2Mat);
        Triangle p2Right = (Triangle) new Triangle(p2Apex, p2fr, p2br).setEmission(p2Color).setMaterial(p2Mat);
        Triangle p2Back = (Triangle) new Triangle(p2Apex, p2br, p2bl).setEmission(p2Color).setMaterial(p2Mat);
        Triangle p2Left = (Triangle) new Triangle(p2Apex, p2bl, p2fl).setEmission(p2Color).setMaterial(p2Mat);

        Cylinder p2PaddleFace = new Cylinder(6, new Ray(new Point(30, 15, 142), new Vector(0, 0, -1)), 1);
        p2PaddleFace.setEmission(new Color(150, 20, 20)).setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(20));
        Cylinder p2PaddleHandle = new Cylinder(1.2, new Ray(new Point(30, 5, 141.5), new Vector(0, 1, 0)), 10);
        p2PaddleHandle.setEmission(new Color(160, 105, 60)).setMaterial(new Material().setKd(0.6).setKs(0.2).setShininess(10));

        // Add geometries to the scene
        scene.geometries.add(
                floor, table, centerLine, leftLine, rightLine, frontLine, backLine,
                legFL, legFR, legBL, legBR,
                net, leftPost, rightPost, ball,
                p1Head, p1Front, p1Right, p1Back, p1Left, p1PaddleFace, p1PaddleHandle,
                p2Head, p2Front, p2Right, p2Back, p2Left, p2PaddleFace, p2PaddleHandle
        );

        // ================= LIGHTS =================
        // 1. The "Sun" Spotlight
        SpotLight sunLight = new SpotLight(new Color(255, 255, 220), new Point(-200, 350, 0), new Vector(1, -1.5, 0));
        sunLight.setKl(0.00001).setKq(0.000001);
        if (softShadows) sunLight.setSize(SUN_LIGHT_SIZE);
        scene.lights.add(sunLight);

        // 2. Sky Illumination Directional Light (Always size = 0 / Hard Shadow)
        scene.lights.add(new DirectionalLight(new Color(60, 60, 80), new Vector(1, -0.5, 1)));

        // 3. Point light highlighting the ball
        PointLight ballLight = new PointLight(new Color(100, 100, 100), new Point(30, 50, 40));
        ballLight.setKl(0.001).setKq(0.0001);
        if (softShadows) ballLight.setSize(BALL_LIGHT_SIZE);
        scene.lights.add(ballLight);

        return scene;
    }
}