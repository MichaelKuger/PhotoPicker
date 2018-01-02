package photopicker.imaging;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;

class ImageCache {

    final LoadingCache<File, BufferedImage> images;

    ImageCache(CacheLoader<File, BufferedImage> loader) {
        images = CacheBuilder
                .newBuilder()
                .maximumSize(50)
                .softValues()
                .build(loader);
    }

    BufferedImage get(File file) {
        try {
            return images.get(file);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
