package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a scene to be rendered.
 * Uses Passive Data Structure (PDS) architecture.
 */
public class Scene {
    /**
     * Scene name identifier.
     */
    public String name;
    /**
     * Scene background color.
     */
    public Color background = Color.BLACK;
    /**
     * Ambient light source for the scene.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;
    /**
     * Collection of all scene geometries.
     */
    public Geometries geometries = new Geometries();

    /** List of light sources present in the scene. */
    public List<LightSource> lights = new ArrayList<>();

    /**
     * Constructs a Scene with a given name.
     *
     * @param name The name of the scene.
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the scene background color.
     *
     * @param background background color
     * @return this scene instance
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the scene ambient light.
     *
     * @param ambientLight ambient light configuration
     * @return this scene instance
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Replaces the scene geometry collection.
     *
     * @param geometries geometry collection to use
     * @return this scene instance
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}
