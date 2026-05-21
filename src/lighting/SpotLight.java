package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;

/**
 * Represents a spotlight source (a point light with a specific direction).
 */
public class SpotLight extends PointLight {
    /** Direction the spotlight is pointing (normalized). */
    private final Vector direction;
    /** Narrow-beam exponent: values >1 make the beam tighter. */
    private int narrowBeam = 1;

    /**
     * Constructs a spot light.
     *
     * @param intensity The maximum intensity of the light.
     * @param position  The position of the light.
     * @param direction The direction the spotlight is pointing.
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets the narrow beam factor (Bonus).
     *
     * @param narrowBeam The exponent for narrowing the beam.
     * @return This SpotLight instance.
     */
    public SpotLight setNarrowBeam(int narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double dirDotL = direction.dotProduct(getL(p));
        // If the point is behind the spotlight, it receives no light
        if (Util.alignZero(dirDotL) <= 0) {
            return Color.BLACK;
        }

        Color pointIntensity = super.getIntensity(p);
        double factor = dirDotL;

        // Bonus: narrow beam computation
        if (narrowBeam > 1) {
            factor = Math.pow(dirDotL, narrowBeam);
        }

        return pointIntensity.scale(factor);
    }
}