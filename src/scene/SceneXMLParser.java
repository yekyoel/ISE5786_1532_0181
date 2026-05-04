package scene;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import primitives.Color;
import primitives.Point;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * XML parser for loading a {@link Scene} from a file.
 * <p>
 * Supported elements:
 * </p>
 * <ul>
 * <li><code>&lt;scene background-color="r g b"&gt;</code></li>
 * <li><code>&lt;ambient-light color="r g b"/&gt;</code></li>
 * <li><code>&lt;geometries&gt;...&lt;/geometries&gt;</code></li>
 * <li><code>&lt;sphere center="x y z" radius="r"/&gt;</code></li>
 * <li><code>&lt;triangle p0="x y z" p1="x y z" p2="x y z"/&gt;</code></li>
 * </ul>
 */
public final class SceneXMLParser {
    /**
     * Private constructor for utility class pattern.
     */
    private SceneXMLParser() {
    }

    /**
     * Parses an XML file into the given scene instance.
     *
     * @param scene   scene object to populate
     * @param xmlName xml file name or base name
     */
    public static void parse(Scene scene, String xmlName) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null.");
        }

        Path xmlPath = resolveXmlPath(xmlName);
        Document document = parseDocument(xmlPath);

        Element sceneElement = document.getDocumentElement();
        if (sceneElement == null || !"scene".equals(sceneElement.getTagName())) {
            throw new IllegalArgumentException("Invalid scene XML: root element must be <scene>.");
        }

        String backgroundColor = sceneElement.getAttribute("background-color");
        if (backgroundColor != null && !backgroundColor.isBlank()) {
            scene.setBackground(parseColor(backgroundColor));
        }

        NodeList ambientLights = sceneElement.getElementsByTagName("ambient-light");
        if (ambientLights.getLength() > 0) {
            Element ambientLight = (Element) ambientLights.item(0);
            String colorText = ambientLight.getAttribute("color");
            if (colorText != null && !colorText.isBlank()) {
                scene.setAmbientLight(new AmbientLight(parseColor(colorText)));
            }
        }

        NodeList geometriesNodes = sceneElement.getElementsByTagName("geometries");
        if (geometriesNodes.getLength() == 0) {
            return;
        }

        Element geometries = (Element) geometriesNodes.item(0);
        NodeList children = geometries.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element geometry = (Element) node;
            String tagName = geometry.getTagName();
            if ("sphere".equals(tagName)) {
                Point center = parsePoint(geometry.getAttribute("center"));
                double radius = parseDouble(geometry.getAttribute("radius"), "sphere radius");
                scene.geometries.add(new Sphere(center, radius));
            } else if ("triangle".equals(tagName)) {
                Point p0 = parsePoint(geometry.getAttribute("p0"));
                Point p1 = parsePoint(geometry.getAttribute("p1"));
                Point p2 = parsePoint(geometry.getAttribute("p2"));
                scene.geometries.add(new Triangle(p0, p1, p2));
            }
        }
    }

    /**
     * Parses an XML file into a DOM document with secure parser settings.
     *
     * @param xmlPath XML file path
     * @return parsed document
     */
    private static Document parseDocument(Path xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            return factory.newDocumentBuilder().parse(xmlPath.toFile());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Failed to parse scene XML file: " + xmlPath, e);
        }
    }

    /**
     * Resolves the XML file path from a provided base name or path.
     *
     * @param xmlName XML file name, base name, or relative path
     * @return resolved existing file path
     */
    private static Path resolveXmlPath(String xmlName) {
        String normalizedName = xmlName == null ? "" : xmlName.trim();
        Path[] candidates = {
                Paths.get(normalizedName),
                Paths.get(normalizedName + ".xml"),
                Paths.get("xml", normalizedName),
                Paths.get("xml", normalizedName + ".xml"),
                Paths.get("docs", normalizedName),
                Paths.get("docs", normalizedName + ".xml"),
                Paths.get("xml")
        };

        for (Path candidate : candidates) {
            if (candidate.toString().isBlank()) {
                continue;
            }
            Path absolute = Paths.get("").toAbsolutePath().resolve(candidate).normalize();
            if (Files.isRegularFile(absolute)) {
                return absolute;
            }
        }

        throw new IllegalArgumentException("Could not find scene XML file for name: " + xmlName);
    }

    /**
     * Parses a color from a three-number string.
     *
     * @param triplet text in format {@code "r g b"}
     * @return parsed color
     */
    private static Color parseColor(String triplet) {
        double[] values = parseTriplet(triplet, "color");
        return new Color(values[0], values[1], values[2]);
    }

    /**
     * Parses a point from a three-number string.
     *
     * @param triplet text in format {@code "x y z"}
     * @return parsed point
     */
    private static Point parsePoint(String triplet) {
        double[] values = parseTriplet(triplet, "point");
        return new Point(values[0], values[1], values[2]);
    }

    /**
     * Parses exactly three numeric components from a whitespace-separated string.
     *
     * @param triplet   input string
     * @param fieldName field name used in error messages
     * @return parsed components
     */
    private static double[] parseTriplet(String triplet, String fieldName) {
        if (triplet == null || triplet.isBlank()) {
            throw new IllegalArgumentException("Missing " + fieldName + " attribute.");
        }

        String[] parts = triplet.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid " + fieldName + " value '" + triplet + "': expected three numeric components.");
        }

        return new double[]{
                parseDouble(parts[0], fieldName + "[0]"),
                parseDouble(parts[1], fieldName + "[1]"),
                parseDouble(parts[2], fieldName + "[2]")
        };
    }

    /**
     * Parses a numeric value.
     *
     * @param value     string value to parse
     * @param fieldName field name used in error messages
     * @return parsed value
     */
    private static double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid numeric value for " + fieldName + ": '" + value + "'.", e);
        }
    }
}
