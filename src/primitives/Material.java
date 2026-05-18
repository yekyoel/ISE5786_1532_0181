package primitives;

/**
 * Represents the material properties of a geometric body.
 */
public class Material {
    /** Ambient reflection coefficients (per-channel). */
    public Double3 kA = Double3.ONE;
    /** Diffuse reflection coefficients (per-channel). */
    public Double3 kD = Double3.ZERO;
    /** Specular reflection coefficients (per-channel). */
    public Double3 kS = Double3.ZERO;
    /** Shininess exponent for specular highlight size. */
    public int nShininess = 0;

    /**
     * Default material with sensible defaults (no diffuse/specular, unit ambient).
     */
    public Material() {
    }

    /**
     * Set ambient reflection coefficients.
     *
     * @param kA ambient coefficients per channel
     * @return this Material (for chaining)
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Set uniform ambient reflection coefficient.
     *
     * @param kA uniform ambient coefficient
     * @return this Material (for chaining)
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Set diffuse reflection coefficients.
     *
     * @param kD diffuse coefficients per channel
     * @return this Material (for chaining)
     */
    public Material setKd(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Set uniform diffuse reflection coefficient.
     *
     * @param kD uniform diffuse coefficient
     * @return this Material (for chaining)
     */
    public Material setKd(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Set specular reflection coefficients.
     *
     * @param kS specular coefficients per channel
     * @return this Material (for chaining)
     */
    public Material setKs(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Set uniform specular reflection coefficient.
     *
     * @param kS uniform specular coefficient
     * @return this Material (for chaining)
     */
    public Material setKs(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Set shininess exponent for specular highlights.
     *
     * @param nShininess shininess exponent
     * @return this Material (for chaining)
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}