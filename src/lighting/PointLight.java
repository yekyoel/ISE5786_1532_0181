package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a point light source (e.g., a light bulb).
 */
public class PointLight extends Light implements LightSource {
    /** Position of the point light in the scene. */
    private final Point position;
    /**
     * Attenuation factors: constant, linear and quadratic.
     * kC - constant term, kL - linear term, kQ - quadratic term.
     */
    private double kC = 1, kL = 0, kQ = 0;

    /**
     * Constructs a point light.
     *
     * @param intensity The intensity of the light.
     * @param position  The position of the light in the scene.
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Set constant attenuation factor.
     *
     * @param kC constant attenuation coefficient
     * @return this PointLight instance (for method chaining)
     */
    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * Set linear attenuation factor.
     *
     * @param kL linear attenuation coefficient
     * @return this PointLight instance (for method chaining)
     */
    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * Set quadratic attenuation factor.
     *
     * @param kQ quadratic attenuation coefficient
     * @return this PointLight instance (for method chaining)
     */
    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = position.distance(p);
        double attenuation = kC + kL * d + kQ * d * d;
        // Scale down intensity according to distance attenuation
        return getIntensity().scale(1.0 / attenuation);
    }

    @Override
    public Vector getL(Point p) {
        // Vector from the light source to the point
        return p.subtract(position).normalize();
    }
}