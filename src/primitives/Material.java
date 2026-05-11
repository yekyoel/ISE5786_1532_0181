package primitives;

/**
 * Represents the material properties of a geometric body.
 * This class defines how a surface interacts with light in the scene.
 */
public class Material {
    /**
     * Ambient attenuation coefficient.
     * Defines how much ambient light the material reflects. Default is 1.0 (no attenuation).
     */
    public Double3 kA = Double3.ONE;

    /**
     * Explicit default constructor.
     * <p>
     * Present so tools that expect a documented constructor do not warn about
     * the implicit default constructor.
     */
    public Material() {
    }

    /**
     * Sets the ambient attenuation coefficient using a Double3 vector.
     *
     * @param kA the ambient attenuation factor for RGB components
     * @return this Material object for method chaining
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient attenuation coefficient using a single scalar value.
     *
     * @param kA the ambient attenuation factor applied uniformly to all RGB components
     * @return this Material object for method chaining
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}