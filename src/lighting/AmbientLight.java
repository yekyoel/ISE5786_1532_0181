package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Represents ambient light in the scene.
 */
public class AmbientLight extends Light {

    /**
     * Constant for no ambient light (black)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, Double3.ZERO);

    /**
     * Constructs an AmbientLight with a given color intensity and attenuation coefficient.
     *
     * @param iA  the base intensity color of the ambient light
     * @param kA  attenuation coefficient (per-channel) to scale the intensity
     */
    public AmbientLight(Color iA, Double3 kA) {
        super(iA.scale(kA));
    }

    /**
     * Constructs an AmbientLight with a given color intensity and a scalar attenuation coefficient.
     *
     * @param iA the base intensity color of the ambient light
     * @param kA scalar attenuation coefficient to scale the intensity
     */
    public AmbientLight(Color iA, double kA) {
        super(iA.scale(kA));
    }

    /**
     * Constructs an AmbientLight with a given color intensity (no attenuation).
     *
     * @param iA the base intensity color of the ambient light
     */
    public AmbientLight(Color iA) {
        super(iA);
    }
}