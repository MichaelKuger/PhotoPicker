package photopicker.imaging;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static class ImageInformation {
        final int orientation;
        final int width;
        final int height;

        ImageInformation(int orientation, int width, int height) {
            this.orientation = orientation;
            this.width = width;
            this.height = height;
        }

        public String toString() {
            return String.format("%dx%d,%d", this.width, this.height, this.orientation);
        }
    }

    static BufferedImage loadImage(File f) throws ImagingException {
        try {
            BufferedImage image = ImageIO.read(f);
            ImageInformation imageInformation = readImageInformation(f);
            AffineTransform transform = getExifTransformation(imageInformation);
            return transformImage(image, transform);
        } catch (Exception e) {
            throw new ImagingException("Could not load image.", e);
        }
    }


    private static ImageInformation readImageInformation(File imageFile) throws IOException, ImageProcessingException, MetadataException {
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

        int orientation = 1;
        try {
            orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (MetadataException me) {
            System.out.println("Could not get orientation");
        }
        int width = jpegDirectory.getImageWidth();
        int height = jpegDirectory.getImageHeight();

        return new ImageInformation(orientation, width, height);
    }

    private static AffineTransform getExifTransformation(ImageInformation info) {
        AffineTransform t = new AffineTransform();

        switch (info.orientation) {
            case 1:
                break;
            case 2: // Flip X
                t.scale(-1.0, 1.0);
                t.translate(-info.width, 0);
                break;
            case 3: // PI rotation
                t.translate(info.width, info.height);
                t.rotate(Math.PI);
                break;
            case 4: // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -info.height);
                break;
            case 5: // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6: // -PI/2 and -width
                t.translate(info.height, 0);
                t.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-info.height, 0);
                t.translate(0, info.width);
                t.rotate(3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                t.translate(0, info.width);
                t.rotate(3 * Math.PI / 2);
                break;
        }
        return t;
    }

    private static BufferedImage transformImage(BufferedImage image, AffineTransform transform) throws Exception {
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        BufferedImage destinationImage = op.createCompatibleDestImage(image, ColorModel.getRGBdefault());
        destinationImage = op.filter(image, destinationImage);


        int width = destinationImage.getWidth();
        int height = destinationImage.getHeight();
        final int imageType = BufferedImage.TYPE_INT_RGB;

        BufferedImage rgbBufferedImage = new BufferedImage(width, height, imageType);
        Graphics2D graphics = rgbBufferedImage.createGraphics();
        graphics.drawImage(destinationImage, 0, 0, null);
        graphics.dispose();
        return rgbBufferedImage;
    }
}
