package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for ImageWriter class.
 */
class ImageWriterTests {
    /** Default constructor to satisfy JavaDoc generator. */
    ImageWriterTests() { /* default constructor */ }

    /**
     * Verifies that an image with a colored grid can be generated and written.
     */
    @Test
    void testImageWriter() {
        int nX = 800;
        int nY = 500;
        int step = 50;

        ImageWriter imageWriter = new ImageWriter(nX, nY);
        Color backgroundColor = new Color(255, 255, 0); // Yellow
        Color gridColor = new Color(255, 0, 0);       // Red

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                // If it's the border of the 50x50 square, color it red, else yellow
                if (i % step == 0 || j % step == 0) {
                    imageWriter.writePixel(j, i, gridColor);
                } else {
                    imageWriter.writePixel(j, i, backgroundColor);
                }
            }
        }

        imageWriter.writeToImage("testImageWriter");
    }
}
