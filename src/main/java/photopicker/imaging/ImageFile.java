package photopicker.imaging;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageFile {

    private final File file;
    private BufferedImage image;

    public ImageFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public BufferedImage getImage() throws ImagingException {
        if (image == null) {
            image = ImageUtils.loadImage(file);
        }
        return image;
    }

    @Override
    public String toString() {
        return "ImageFile{" +
                "file=" + file +
                '}';
    }
}
