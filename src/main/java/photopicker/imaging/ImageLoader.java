package photopicker.imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageLoader implements ImageProvider {

    private static final int LOOK_AHEAD = 3;
    private final ImageCache cache = new ImageCache();
    private final List<ImageFile> images;
    private int current = 0;

    public ImageLoader(File[] imageFiles) {
        images = Arrays.stream(imageFiles).map(f -> new ImageFile(f, this)).collect(Collectors.toList());
    }

    public ImageFile current() {
        return images.get(current);
    }

    private int increase(int i) {
        i++;
        if (i >= images.size()) {
            i = 0;
        }
        return i;
    }

    private int decrease(int i) {
        i--;
        if (i < 0) {
            i = images.size() - 1;
        }
        return i;
    }

    public ImageFile next() {
        current = increase(current);
        return current();
    }

    public ImageFile previous() {
        current = decrease(current);
        return current();
    }

    @Override
    public BufferedImage load(File f) throws ImagingException {
        return cache.get(f);
    }
}
