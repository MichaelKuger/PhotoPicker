package photopicker.imaging;

import org.junit.Test;
import photopicker.config.ImageConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ImageTest {

    private ImageConfig config = new ImageConfig(1024, 768);

    @Test
    public void testLandscapeMode() throws URISyntaxException, ImagingException, IOException {
        File imageFile = getFile("SAM_3977.JPG");
        BufferedImage image = ImageUtils.loadImage(imageFile, config);
        assertEquals(1024, image.getWidth());
        assertEquals(683, image.getHeight());
        writeImage(image);
    }

    @Test
    public void testPortraitMode() throws IOException, URISyntaxException, ImagingException {
        File imageFile = getFile("SAM_3990.JPG");
        BufferedImage image = ImageUtils.loadImage(imageFile, config);
        assertEquals(512, image.getWidth());
        assertEquals(768, image.getHeight());
        writeImage(image);
    }

    private void writeImage(BufferedImage image) throws IOException {
        File outputFile = File.createTempFile("photopicker-", ".jpg");
        outputFile.deleteOnExit();
        System.out.println(outputFile.getAbsolutePath());
        ImageIO.write(image, "jpg", outputFile);
        // breakpoint here. Files get deleted after VM shutdown
        System.out.println("foo");
    }

    private File getFile(String filename) throws URISyntaxException {
        URL url = getClass().getResource("/" + filename);
        return new File(url.toURI());
    }
}
