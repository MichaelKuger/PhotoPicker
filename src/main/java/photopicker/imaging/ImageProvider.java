package photopicker.imaging;

import java.awt.image.BufferedImage;
import java.io.File;

interface ImageProvider {

    BufferedImage load(File f) throws ImagingException;
}
