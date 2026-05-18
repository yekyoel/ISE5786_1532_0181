package lighting;

import primitives.Color;

/**
 * Abstract class representing a light in the scene.
 */
abstract class Light {
    /**
     * The original intensity of the light.
     */
    protected final Color _intensity;

    /**
     * Constructs a light with a given intensity.
     *
     * @param intensity The color intensity of the light.
     */
    protected Light(Color intensity) {
        _intensity = intensity;
    }

    /**
     * Gets the original intensity of the light.
     *
     * @return The color intensity.
     */
    public Color getIntensity() {
        return _intensity;
    }
}