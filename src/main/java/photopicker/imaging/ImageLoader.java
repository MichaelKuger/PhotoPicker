package photopicker.imaging;

import com.google.common.cache.CacheLoader;
import photopicker.config.ImageConfig;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class ImageLoader implements ImageProvider {

    private static final int PRELOAD_SIZE = 15;
    private final ForkJoinPool pool = ForkJoinPool.commonPool();
    private final ImageCache cache;
    private final List<ImageFile> images;
    private int current = 0;

    public ImageLoader(File[] imageFiles, ImageConfig config) {
        images = Arrays.stream(imageFiles).map(f -> new ImageFile(f, this)).collect(Collectors.toList());
        CacheLoader loader = new CacheLoader<File, BufferedImage>() {
            @Override
            public BufferedImage load(File file) throws ImagingException {
                System.out.println("Loading file " + file.getName());
                return ImageUtils.loadImage(file, config);
            }
        };
        cache = new ImageCache(loader);
    }

    public ImageFile current() {
        return images.get(current);
    }

    public ImageFile next() {
        current = increase(current);
        List<ImageFile> imagesToLoad = new ArrayList<>(PRELOAD_SIZE);
        for (int i = 1; i <= PRELOAD_SIZE; i++) {
            int counter = increase(current + i);
            imagesToLoad.add(images.get(counter));
        }
        preload(imagesToLoad);
        return current();
    }

    public ImageFile previous() {
        current = decrease(current);
        List<ImageFile> imagesToLoad = new ArrayList<>(PRELOAD_SIZE);
        for (int i = 1; i <= PRELOAD_SIZE; i++) {
            int counter = decrease(current - i);
            imagesToLoad.add(images.get(counter));
        }
        preload(imagesToLoad);
        return current();
    }

    private void preload(Collection<ImageFile> images) {
        pool.submit(() -> images
                .parallelStream()
                .forEach(f ->
                        pool.submit(() -> cache.get(f.getFile())
                        )
                )
        );
    }

    @Override
    public BufferedImage load(File f) throws ImagingException {
        return cache.get(f);
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
}
