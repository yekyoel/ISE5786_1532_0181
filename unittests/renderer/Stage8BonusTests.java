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

        // 11. Camera setup and Rendering
        Camera.getBuilder()
                .setLocation(new Point(0, 70, 350)) // Placed high and far back
                .setDirection(new Point(0, 10, 0), Vector.AXIS_Y) // Looking slightly downward at the altar
                .setVpDistance(200)
                .setVpSize(200, 200)
                .setResolution(1000, 1000) // High res for crisp reflections
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("bonus_stage8_10plus_shapes_altar");
    }
}