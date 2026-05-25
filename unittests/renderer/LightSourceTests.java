package renderer;

import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for Light Sources focusing on the light propagation model.
 * Tests getIntensity and getL methods directly without rendering.
 */
class LightSourceTests {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    LightSourceTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test method for {@link lighting.DirectionalLight}.
     * EP01: Directional light has constant intensity and direction regardless of the point.
     */
    @Test
    void testDirectionalLight() {
        Color intensity = new Color(100, 100, 100);
        Vector direction = new Vector(0, 0, -1);
        DirectionalLight light = new DirectionalLight(intensity, direction);
        Point p = new Point(1, 2, 3);

        // EP01: Verify that the light intensity remains constant
        assertEquals(intensity, light.getIntensity(p), "ERROR: DirectionalLight intensity should be constant");

        // EP01: Verify that the light direction remains constant
        assertEquals(direction, light.getL(p), "ERROR: DirectionalLight direction should be constant");
    }

    /**
     * Test method for {@link lighting.PointLight}.
     * Tests distance attenuation and boundary case of a point coinciding with the light source.
     */
    @Test
    void testPointLight() {
        Color intensity = new Color(100, 100, 100);
        Point position = new Point(0, 0, 1);
        PointLight light = new PointLight(intensity, position).setKc(1).setKl(0.5).setKq(0.5);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Distance attenuation check
        // The distance from point (0,0,3) to light (0,0,1) is d=2.
        // attenuation = kC + kL*d + kQ*d^2 = 1 + 0.5*2 + 0.5*4 = 4.
        // Expected intensity = 100 / 4 = 25.
        Point p1 = new Point(0, 0, 3);
        assertEquals(new Color(25, 25, 25), light.getIntensity(p1), "ERROR: PointLight distance attenuation is wrong");

        // =============== Boundary Values Tests ==================
        // BV01: Boundary case where the point coincides with the light source
        // d=0, so attenuation = kC = 1.
        Point pCoinciding = new Point(0, 0, 1);

        // Verify that getIntensity returns the original intensity
        assertEquals(intensity, light.getIntensity(pCoinciding), "ERROR: PointLight coinciding point intensity should be original");

        // Verify that getL throws an exception (due to attempting to create a zero vector)
        assertThrows(IllegalArgumentException.class, () -> light.getL(pCoinciding),
                "ERROR: PointLight getL() should throw exception for coinciding point");
    }

    /**
     * Test method for {@link lighting.SpotLight}.
     * Tests angular attenuation, direction angles, and boundary cases.
     */
    @Test
    void testSpotLight() {
        Color intensity = new Color(100, 100, 100);
        Point position = new Point(0, 0, 1);
        Vector direction = new Vector(0, 0, 1);
        // By default: kC=1, kL=0, kQ=0 (no distance attenuation)
        SpotLight light = new SpotLight(intensity, position, direction);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is inside the light beam
        // The point (0,3,5) creates an L vector whose angle with the direction vector gives a cosine of 0.8
        // dotProduct = 0.8. Therefore, the expected intensity is: 100 * 0.8 = 80
        Point pInside = new Point(0, 3, 5);
        assertEquals(new Color(80, 80, 80), light.getIntensity(pInside), "ERROR: SpotLight intensity inside beam is wrong");

        // EP02: Point is behind the spotlight (obtuse angle)
        Point pBehind = new Point(0, 0, -1);
        assertEquals(Color.BLACK, light.getIntensity(pBehind), "ERROR: SpotLight intensity behind spotlight should be BLACK");

        // =============== Boundary Values Tests ==================

        // BV01: Boundary case where the angle between the spotlight direction and the point is exactly 90 degrees
        Point p90 = new Point(2, 0, 1); // Vector L is (1,0,0), orthogonal to direction (0,0,1)
        assertEquals(Color.BLACK, light.getIntensity(p90), "ERROR: SpotLight intensity at exactly 90 degrees should be BLACK");

        // BV02: Boundary case where the point coincides with the light source
        Point pCoinciding = new Point(0, 0, 1);
        assertThrows(IllegalArgumentException.class, () -> light.getL(pCoinciding),
                "ERROR: SpotLight getL() should throw exception for coinciding point");
    }

    /**
     * Test method for {@link lighting.SpotLight} with Narrow-beam (Bonus).
     * EP01: Point inside the beam with narrowBeam exponent.
     */
    @Test
    void testSpotLightNarrowBeam() {
        Color intensity = new Color(100, 100, 100);
        Point position = new Point(0, 0, 1);
        Vector direction = new Vector(0, 0, 1);

        // Creating a spotlight with a narrow beam factor of 2 (Bonus)
        SpotLight light = new SpotLight(intensity, position, direction).setNarrowBeam(2);

        // EP01: Test inside the beam
        // For the point (0,3,5), the cosine (dirDotL) is 0.8.
        // Because narrowBeam = 2, the multiplication factor will be 0.8^2 = 0.64.
        // Expected intensity: 100 * 0.64 = 64.
        Point pInside = new Point(0, 3, 5);
        assertEquals(new Color(64, 64, 64), light.getIntensity(pInside), "ERROR: Narrow-beam SpotLight intensity is wrong");
    }
}