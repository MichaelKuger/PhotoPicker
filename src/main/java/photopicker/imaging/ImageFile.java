package photopicker.imaging;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageFile {

    private final File file;
    private final ImageProvider provider;

    ImageFile(File file, ImageProvider provider) {
        this.file = file;
        this.provider = provider;
    }

    public File getFile() {
        return file;
    }

    public BufferedImage getImage() throws ImagingException {
        return provider.load(file);
    }

    @Override
    public String toString() {
        return "ImageFile{" +
                "file=" + file +
                '}';
    }
}
