package photopicker.imaging;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageLoader {

    private final List<ImageFile> images;

    public ImageLoader(File[] imageFiles) {
        images = Arrays.stream(imageFiles).map(ImageFile::new).collect(Collectors.toList());
    }

    public int size() {
        return images.size();
    }

    public ImageFile imageAt(int counter) throws ImagingException {
        return images.get(counter);
    }
}
