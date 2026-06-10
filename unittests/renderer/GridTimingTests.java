package renderer;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

class GridTimingTests {

    // Helper method to set up your heavy scene (Use your PingPong or Teapot scene here)
    private Scene buildHeavyScene() {
        // Paste the contents of your buildPingPongScene() or prepareTeapot() here
        // Make sure it has soft shadows enabled!
        return new PingPongSSTests().buildPingPongScene(true);
    }

    private void runTest(String testName, RayTracerType type, int threads) {
        Scene scene = buildHeavyScene();
        long start = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(200, 180, 350))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpDistance(400)
                .setVpSize(300, 300)
                .setResolution(800, 800)
                .setRayTracer(scene, type)
                .setMultithreading(threads)
                .setDebugPrint(10)
                .build()
                .renderImage()
                .writeToImage(testName);

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[%s] Render time: %.2f seconds%n", testName, elapsed / 1000.0);
    }

    @Test
    void testNoAccelNoThreads() {
        runTest("Timing_NoAccel_NoThreads", RayTracerType.SIMPLE, 0);
    }

    @Test
    void testNoAccelWithThreads() {
        runTest("Timing_NoAccel_WithThreads", RayTracerType.SIMPLE, -2); // -2 uses raw threads
    }

    @Test
    void testAccelNoThreads() {
        runTest("Timing_Accel_NoThreads", RayTracerType.GRID, 0);
    }

    @Test
    void testAccelWithThreads() {
        runTest("Timing_Accel_WithThreads", RayTracerType.GRID, -2);
    }
}