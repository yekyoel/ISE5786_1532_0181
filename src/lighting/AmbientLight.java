package lighting;

import primitives.Color;

/**
 * Represents ambient light in the scene.
 */
public class AmbientLight {
    /**
     * Constant for no ambient light (black)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /** Ambient light intensity color. */
    private final Color _intensity;

    /**
     * Constructs an AmbientLight with a given intensity.
     *
     * @param intensity The color intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        _intensity = intensity;
    }

    /**
     * Gets the intensity of the ambient light.
     *
     * @return The color intensity.
     */
    public Color getIntensity() {
        return _intensity;
    }
}
