package photopicker.config;

import java.io.File;

public class PhotoPickerConfig {

    private ImageConfig imageConfig;
    private File sourceDir;
    private File targetDir;

    public void setImageConfig(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;
    }

    public ImageConfig getImageConfig() {
        return imageConfig;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    @Override
    public String toString() {
        return "PhotoPickerConfig{" +
                "imageConfig=" + imageConfig +
                ", sourceDir=" + sourceDir +
                ", targetDir=" + targetDir +
                '}';
    }
}
