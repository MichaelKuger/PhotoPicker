package photopicker.config;

public class ImageConfig {
    public final double height, width;

    public ImageConfig(double height, double width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public String toString() {
        return "ImageConfig{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}
