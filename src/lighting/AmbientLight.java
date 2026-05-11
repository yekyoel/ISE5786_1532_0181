package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Represents ambient light in the scene, including the attenuation coefficient (kA).
 */
public class AmbientLight {

    /**
     * Constant for no ambient light (black)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * The calculated intensity color of this ambient light (after applying any attenuation).
     */
    private final Color _intensity;

    /**
     * Constructs an AmbientLight with a given color intensity.
     *
     * @param iA The color intensity of the ambient light.
     */
    public AmbientLight(Color iA) {
        _intensity = iA;
    }

    /**
     * Constructs an AmbientLight with a given color intensity and attenuation coefficient.
     *
     * @param iA The original color intensity of the ambient light.
     * @param kA The attenuation coefficient (Double3).
     */
    public AmbientLight(Color iA, Double3 kA) {
        // I_p = k_A * I_A
        _intensity = iA.scale(kA);
    }

    /**
     * Constructs an AmbientLight with a given color intensity and a scalar attenuation coefficient.
     *
     * @param iA The original color intensity of the ambient light.
     * @param kA The attenuation coefficient (double).
     */
    public AmbientLight(Color iA, double kA) {
        _intensity = iA.scale(kA);
    }

    /**
     * Gets the calculated intensity of the ambient light.
     *
     * @return The color intensity after attenuation.
     */
    public Color getIntensity() {
        return _intensity;
    }
}