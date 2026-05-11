package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface representing a general light source in the 3D scene.
 * Defines the necessary methods to calculate lighting on a specific point.
 */
public interface LightSource {

    /**
     * Calculates the intensity of the light from this source at a specific point in the scene.
     *
     * @param p The point in the scene where the light intensity is measured.
     * @return The color/intensity of the light reaching the point.
     */
    public Color getIntensity(Point p);

    /**
     * Calculates the normalized directional vector from the light source to a specific point.
     *
     * @param p The target point in the scene.
     * @return A normalized Vector pointing from the light source towards the point.
     */
    public Vector getL(Point p);
}