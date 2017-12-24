package photopicker.imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

class ImageCache {

    private static final int CACHE_SIZE = 5;
    private final LinkedList<ImageContainer> images = new LinkedList<>();

    BufferedImage get(File file) throws ImagingException {
        synchronized (this) {
            for (ImageContainer container : images) {
                if (container.file.equals(file)) {
                    System.out.println("Serving image from cache!");
                    return container.image;
                }
            }
        }
        System.out.println("Cache miss. Loading...");
        BufferedImage image = ImageUtils.loadImage(file);
        ImageContainer container = new ImageContainer(file, image);
        put(container);
        return image;
    }

    private synchronized void put(ImageContainer container) {
        images.addFirst(container);
        if (images.size() > CACHE_SIZE) {
            images.removeLast();
        }
    }

    private class ImageContainer {
        private final File file;
        private final BufferedImage image;

        ImageContainer(File file, BufferedImage image) {
            this.file = file;
            this.image = image;
        }
    }
}
