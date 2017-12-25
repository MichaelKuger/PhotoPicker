package photopicker.imaging;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;

class ImageCache {

    LoadingCache<File, BufferedImage> images = CacheBuilder
            .newBuilder()
            .maximumSize(50)
            .softValues()
            .build(
                    new CacheLoader<File, BufferedImage>() {
                        @Override
                        public BufferedImage load(@Nonnull File file) throws ImagingException {
                            System.out.println("Loading file " + file.getName());
                            return ImageUtils.loadImage(file);
                        }
                    }
            );

    BufferedImage get(File file) {
        try {
            return images.get(file);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
