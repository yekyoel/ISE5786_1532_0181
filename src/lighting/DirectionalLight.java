package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a directional light source (e.g., the Sun).
 */
public class DirectionalLight extends Light implements LightSource {
    /**
     * The (normalized) direction vector of the directional light.
     */
    private final Vector direction;

    /**
     * Constructs a directional light.
     *
     * @param intensity The intensity of the light.
     * @param direction The direction of the light.
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return super.getIntensity();
    }

    @Override
    public Vector getL(Point p) {
        return direction;
    }
}