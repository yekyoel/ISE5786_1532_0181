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
 * Bonus Stage 8: Complex scene with 10+ shapes demonstrating all features.
 * Includes Plane, Cylinder, Triangles, Spheres, Tube, and Polygon.
 * Demonstrates shadows, transparency (Kt), and reflections (Kr).
 */
class Stage8BonusTests {

    /**
     * Creates the Stage 8 bonus test fixture.
     */
    public Stage8BonusTests() {
    }

    /**
     * Renders a mystical altar scene.
     */
    @Test
    void testAbstractAltarScene() {
        // 1. Initialize Scene
        Scene scene = new Scene("Abstract Altar Scene");
        scene.setBackground(new Color(5, 5, 10)); // Deep dark blue space background
        scene.setAmbientLight(new AmbientLight(new Color(5, 20, 7), Double3.ONE));

        // 2. Shape 1: Plane (Highly reflective mirror floor)
        Plane floor = new Plane(new Point(0, -50, 0), new Vector(0, 1, 0));
        floor.setEmission(new Color(10, 10, 15))
                .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100).setKr(0.75));

        // 3. Shape 2: Cylinder (The altar pedestal)
        Cylinder pedestal = new Cylinder(20, new Ray(new Point(0, -50, 0), new Vector(0, 1, 0)), 40);
        pedestal.setEmission(new Color(15, 15, 15))
                .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(30));

        // 4. Shapes 3-6: Triangles (Transparent Glass Pyramid rotated 45 degrees to reveal 3D depth)
        Point apex = new Point(0, 40, 0);
        Point v1 = new Point(0, -10, 28);   // Front corner pointing directly towards the camera
        Point v2 = new Point(-28, -10, 0);  // Left corner
        Point v3 = new Point(0, -10, -28);  // Back corner
        Point v4 = new Point(28, -10, 0);   // Right corner

        Material glassMat = new Material().setKd(0.2).setKs(0.8).setShininess(60).setKt(0.85);
        Color glassColor = new Color(0, 40, 40); // Light cyan tint

        // Create the 4 distinct faces of the glass pyramid
        Triangle t1 = (Triangle) new Triangle(apex, v1, v2).setEmission(glassColor).setMaterial(glassMat); // Front-Left face
        Triangle t2 = (Triangle) new Triangle(apex, v2, v3).setEmission(glassColor).setMaterial(glassMat); // Back-Left face
        Triangle t3 = (Triangle) new Triangle(apex, v3, v4).setEmission(glassColor).setMaterial(glassMat); // Back-Right face
        Triangle t4 = (Triangle) new Triangle(apex, v4, v1).setEmission(glassColor).setMaterial(glassMat); // Front-Right face
        // 5. Shape 7: Sphere (Perfect Mirror Orb floating on the left)
        Sphere mirrorSphere = new Sphere(new Point(-60, 10, 40), 25);
        mirrorSphere.setEmission(new Color(0, 0, 0))
                .setMaterial(new Material().setKd(0.1).setKs(0.8).setShininess(100).setKr(0.85));

        // 6. Shape 8: Sphere (Matte Red Orb resting on the floor to the right)
        Sphere matteSphere = new Sphere(new Point(60, -30, 60), 20);
        matteSphere.setEmission(new Color(40, 0, 0))
                .setMaterial(new Material().setKd(0.8).setKs(0.2).setShininess(10));

        // 7. Shape 9: Sphere (Glowing yellow orb hovering in the background)
        Sphere glowingSphere = new Sphere(new Point(0, 70, -100), 15);
        glowingSphere.setEmission(new Color(255, 200, 50))
                .setMaterial(new Material().setKd(0.2).setKs(0.2).setShininess(10));

        // 8. Shape 10: Tube (Glossy cyan piping sweeping across the background)
        Tube archTube = new Tube(4, new Ray(new Point(-120, -50, -80), new Vector(1, 0.8, 0)));
        archTube.setEmission(new Color(50, 30, 30))
                .setMaterial(new Material().setKd(0.6).setKs(0.4).setShininess(50));

        // 9. Shape 11: Polygon (Reflective tilted dark glass wall in the far back)
        Polygon backWall = new Polygon(
                new Point(-150, -50, -200),
                new Point(150, -50, -200),
                new Point(150, 150, -200),
                new Point(-150, 150, -200)
        );
        backWall.setEmission(new Color(5, 5, 10))
                .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(30).setKr(0.3));

        // Add all 11 shapes to the scene
        scene.geometries.add(floor, pedestal, t1, t2, t3, t4, mirrorSphere, matteSphere, glowingSphere, archTube, backWall);

        // 10. Lights setup
        // Main spotlight beaming down on the glass pyramid
        scene.lights.add(new SpotLight(new Color(250, 250, 250), new Point(0, 150, 100), new Vector(0, -1, -0.5))
                .setKl(0.0001).setKq(0.00001));

        // Purple point light near the matte sphere for a nice color contrast
        scene.lights.add(new PointLight(new Color(200, 0, 200), new Point(80, 40, 80))
                .setKl(0.001).setKq(0.0001));

        // Soft directional light filling in shadows from the left
        scene.lights.add(new DirectionalLight(new Color(40, 40, 40), new Vector(-1, -0.5, -1)));

        // 11. Camera setup
        Camera.Builder cameraBuilder =Camera.getBuilder()
                .setVpDistance(200)
                .setVpSize(200, 200)
                .setResolution(1000, 1000) // High res for crisp reflections
                .setRayTracer(scene, RayTracerType.SIMPLE);

        // 11. Rendering the original image
        cameraBuilder
                .setLocation(new Point(0, 70, 350)) // Placed high and far back
                .setDirection(new Point(0, 10, 0), Vector.AXIS_Y) // Looking slightly downward at the altar
                .build()
                .renderImage()
                .writeToImage("bonus_stage8_10plus_shapes_altar");

        // 12. Rendering the original image with a higher angle
        cameraBuilder
                .setLocation(new Point(0, 70, 350)) // Placed high and far back
                .setDirection(new Point(0, 10, 0), Vector.AXIS_Y) // Looking slightly downward at the altar
                .move(new Vector(0,400,0))
                .rotate(-10,0,0)
                .build()
                .renderImage()
                .writeToImage("bonus_stage8_10plus_shapes_altar_different1");

        // 13. Rendering the original image from a side view
        cameraBuilder
                .setLocation(new Point(0, 70, 350)) // Placed high and far back
                .setDirection(new Point(0, 10, 0), Vector.AXIS_Y) // Looking slightly downward at the altar
                .move(new Vector(350,0,-350))
                .rotate(0,-10,0)
                .build()
                .renderImage()
                .writeToImage("bonus_stage8_10plus_shapes_altar_different2");

        // 14. Rendering the original image rotated
        cameraBuilder
                .setLocation(new Point(0, 70, 350)) // Placed high and far back
                .setDirection(new Point(0, 10, 0), Vector.AXIS_Y) // Looking slightly downward at the altar
                .rotate(0,0,45)
                .build()
                .renderImage()
                .writeToImage("bonus_stage8_10plus_shapes_altar_different3");


    }


    /**
     * Renders a Ping Pong match between two geometric "players".
     */
    @Test
    void testPingPongMatch() {
        // 1. Initialize Scene & "Sky" Background
        Scene scene = new Scene("Ping Pong Match");
        scene.setBackground(new Color(135, 206, 235)); // Sky Blue background
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 40), Double3.ONE));

        // --- NEW: Grass Green Floor (Large flat polygon at Y = -50) ---
        Polygon floor = new Polygon(
                new Point(-1000, -50, 1000),
                new Point(1000, -50, 1000),
                new Point(1000, -50, -1000),
                new Point(-1000, -50, -1000)
        );
        floor.setEmission(new Color(3, 66, 14)) // Forest/Grass Green
                .setMaterial(new Material().setKd(0.8).setKs(0.1).setShininess(5));
        // 2. The Ping Pong Table (Dark blue, slightly reflective)
        Polygon table = new Polygon(
                new Point(-75, 0, 137),
                new Point(75, 0, 137),
                new Point(75, 0, -137),
                new Point(-75, 0, -137)
        );
        table.setEmission(new Color(12, 4, 148)) // Dark Blue
                .setMaterial(new Material().setKd(0.7).setKs(0.0).setShininess(1).setKr(0.0));
// --- NEW: White Table Lines (Elevated to Y = 0.1 to prevent Z-fighting) ---
        Material lineMat = new Material().setKd(0.8).setKs(0.2).setShininess(10);
        Color lineColor = new Color(250, 250, 250); // Crisp White

        // Center Line
        Polygon centerLine = new Polygon(
                new Point(-1, 0.1, 137), new Point(1, 0.1, 137),
                new Point(1, 0.1, -137), new Point(-1, 0.1, -137));
        centerLine.setEmission(lineColor).setMaterial(lineMat);

        // Left Edge Line
        Polygon leftLine = new Polygon(
                new Point(-75, 0.1, 137), new Point(-73, 0.1, 137),
                new Point(-73, 0.1, -137), new Point(-75, 0.1, -137));
        leftLine.setEmission(lineColor).setMaterial(lineMat);

        // Right Edge Line
        Polygon rightLine = new Polygon(
                new Point(73, 0.1, 137), new Point(75, 0.1, 137),
                new Point(75, 0.1, -137), new Point(73, 0.1, -137));
        rightLine.setEmission(lineColor).setMaterial(lineMat);

        // Front Edge Line (P2 side)
        Polygon frontLine = new Polygon(
                new Point(-75, 0.1, 137), new Point(75, 0.1, 137),
                new Point(75, 0.1, 135), new Point(-75, 0.1, 135));
        frontLine.setEmission(lineColor).setMaterial(lineMat);

        // Back Edge Line (P1 side)
        Polygon backLine = new Polygon(
                new Point(-75, 0.1, -135), new Point(75, 0.1, -135),
                new Point(75, 0.1, -137), new Point(-75, 0.1, -137));
        backLine.setEmission(lineColor).setMaterial(lineMat);
        // --- NEW: 4 Table Legs (Cylinders extending down from Y = 0 to Y = -50) ---
        Material legMat = new Material().setKd(0.5).setKs(0.5).setShininess(60);
        Color legColor = new Color(120, 120, 130); // Metallic Silver/Gray

        // Front-Left Leg
        Cylinder legFL = new Cylinder(2.5, new Ray(new Point(-70, -50, 130), new Vector(0, 1, 0)), 50);
        legFL.setEmission(legColor).setMaterial(legMat);

        // Front-Right Leg
        Cylinder legFR = new Cylinder(2.5, new Ray(new Point(70, -50, 130), new Vector(0, 1, 0)), 50);
        legFR.setEmission(legColor).setMaterial(legMat);

        // Back-Left Leg
        Cylinder legBL = new Cylinder(2.5, new Ray(new Point(-70, -50, -130), new Vector(0, 1, 0)), 50);
        legBL.setEmission(legColor).setMaterial(legMat);

        // Back-Right Leg
        Cylinder legBR = new Cylinder(2.5, new Ray(new Point(70, -50, -130), new Vector(0, 1, 0)), 50);
        legBR.setEmission(legColor).setMaterial(legMat);
        // 3. The Net (White and slightly transparent)
        Polygon net = new Polygon(
                new Point(-75, 0, 0),
                new Point(75, 0, 0),
                new Point(75, 12, 0),
                new Point(-75, 12, 0)
        );
        net.setEmission(new Color(200, 200, 200))
                .setMaterial(new Material().setKd(0.2).setKs(0.6).setShininess(20).setKt(0.85));

        // --- NEW: 2 Net Posts (Cylinders on the sides of the net) ---
        Color postColor = new Color(30, 30, 30); // Dark grey iron
        Cylinder leftPost = (Cylinder) new Cylinder(1.5, new Ray(new Point(-75, 0, 0), new Vector(0, 1, 0)), 14)
                .setEmission(postColor).setMaterial(legMat);
        Cylinder rightPost = (Cylinder) new Cylinder(1.5, new Ray(new Point(75, 0, 0), new Vector(0, 1, 0)), 14)
                .setEmission(postColor).setMaterial(legMat);
        // 4. The Yellow Ball on the table
        Sphere ball = new Sphere(new Point(30, 4, 40), 4);
        ball.setEmission(new Color(150, 150, 0)) // Yellow
                .setMaterial(new Material().setKd(0.6).setKs(0.8).setShininess(100));

        // ================= PLAYER 1 (Red side, Z = -160) =================
        Material p1Mat = new Material().setKd(0.7).setKs(0.3).setShininess(30);
        Color p1Color = new Color(150, 20, 20); // Deep Red

        // P1 Head
        Sphere p1Head = (Sphere) new Sphere(new Point(0, 45, -160), 12)
                .setEmission(p1Color).setMaterial(p1Mat);

        // P1 Body (Pyramid)
        Point p1Apex = new Point(0, 33, -160);
        Point p1fl = new Point(-20, -15, -140);
        Point p1fr = new Point(20, -15, -140);
        Point p1bl = new Point(-20, -15, -180);
        Point p1br = new Point(20, -15, -180);
        Triangle p1Front = (Triangle) new Triangle(p1Apex, p1fl, p1fr).setEmission(p1Color).setMaterial(p1Mat);
        Triangle p1Right = (Triangle) new Triangle(p1Apex, p1fr, p1br).setEmission(p1Color).setMaterial(p1Mat);
        Triangle p1Back  = (Triangle) new Triangle(p1Apex, p1br, p1bl).setEmission(p1Color).setMaterial(p1Mat);
        Triangle p1Left  = (Triangle) new Triangle(p1Apex, p1bl, p1fl).setEmission(p1Color).setMaterial(p1Mat);

        // --- CHANGED: P1 Paddle moved to the Right Hand (X = -30) ---
        // P1 Paddle Face (Wide, flat cylinder)
        Cylinder p1PaddleFace = new Cylinder(6, new Ray(new Point(-30, 15, -142), new Vector(0, 0, 1)), 1);
        p1PaddleFace.setEmission(new Color(10, 10, 10)) // Black rubber face
                .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(20));

        // P1 Paddle Handle (Long, thin cylinder pointing up to the face)
        Cylinder p1PaddleHandle = new Cylinder(1.2, new Ray(new Point(-30, 5, -141.5), new Vector(0, 1, 0)), 10);
        p1PaddleHandle.setEmission(new Color(160, 105, 60)) // Wood brown
                .setMaterial(new Material().setKd(0.6).setKs(0.2).setShininess(10));


        // ================= PLAYER 2 (Green side, Z = 160) =================
        Material p2Mat = new Material().setKd(0.7).setKs(0.3).setShininess(30);
        Color p2Color = new Color(20, 150, 20); // Deep Green

        // P2 Head
        Sphere p2Head = (Sphere) new Sphere(new Point(0, 45, 160), 12)
                .setEmission(p2Color).setMaterial(p2Mat);

        // P2 Body (Pyramid)
        Point p2Apex = new Point(0, 33, 160);
        Point p2fl = new Point(20, -15, 140);
        Point p2fr = new Point(-20, -15, 140);
        Point p2bl = new Point(20, -15, 180);
        Point p2br = new Point(-20, -15, 180);
        Triangle p2Front = (Triangle) new Triangle(p2Apex, p2fl, p2fr).setEmission(p2Color).setMaterial(p2Mat);
        Triangle p2Right = (Triangle) new Triangle(p2Apex, p2fr, p2br).setEmission(p2Color).setMaterial(p2Mat);
        Triangle p2Back  = (Triangle) new Triangle(p2Apex, p2br, p2bl).setEmission(p2Color).setMaterial(p2Mat);
        Triangle p2Left  = (Triangle) new Triangle(p2Apex, p2bl, p2fl).setEmission(p2Color).setMaterial(p2Mat);

        // --- CHANGED: P2 Paddle moved to the Right Hand (X = 30) ---
        // P2 Paddle Face (Wide, flat cylinder facing the opposite way)
        Cylinder p2PaddleFace = new Cylinder(6, new Ray(new Point(30, 15, 142), new Vector(0, 0, -1)), 1);
        p2PaddleFace.setEmission(new Color(150, 20, 20)) // Red rubber face
                .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(20));

        // P2 Paddle Handle (Long, thin cylinder pointing up to the face)
        Cylinder p2PaddleHandle = new Cylinder(1.2, new Ray(new Point(30, 5, 141.5), new Vector(0, 1, 0)), 10);
        p2PaddleHandle.setEmission(new Color(160, 105, 60)) // Wood brown
                .setMaterial(new Material().setKd(0.6).setKs(0.2).setShininess(10));
        // Add all 28 shapes to the scene
        scene.geometries.add(
                floor ,table, centerLine, leftLine, rightLine, frontLine, backLine,
                legFL, legFR, legBL, legBR,
                net, leftPost, rightPost, ball,
                p1Head, p1Front, p1Right, p1Back, p1Left, p1PaddleFace, p1PaddleHandle,
                p2Head, p2Front, p2Right, p2Back, p2Left, p2PaddleFace, p2PaddleHandle
        );

        // ================= LIGHTS =================
        // 1. The "Sun": A bright yellowish SpotLight shining down dynamically
        scene.lights.add(new SpotLight(new Color(255, 255, 220), new Point(-200, 350, 0), new Vector(1, -1.5, 0))
                .setKl(0.00001).setKq(0.000001));

        // 2. A subtle directional light acting as soft sky illumination
        scene.lights.add(new DirectionalLight(new Color(60, 60, 80), new Vector(1, -0.5, 1)));

        // 3. A point light near the ball to give it a nice pop of localized highlight
        scene.lights.add(new PointLight(new Color(100, 100, 100), new Point(30, 50, 40))
                .setKl(0.001).setKq(0.0001));

        // ================= CAMERA & RENDER =================
        Camera.getBuilder()
                // Positioned high up and off to the side to see the whole table, players, and shadows
                .setLocation(new Point(200, 180, 350))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y) // Looking right at the center of the net
                .setVpDistance(400)
                .setVpSize(300, 300)
                .setResolution(1000, 1000) // High-res for sharp shadows and reflections
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("bonus_stage8_ping_pong_match");
    }
}