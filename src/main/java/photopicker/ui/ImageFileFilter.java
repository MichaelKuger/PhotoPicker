package photopicker.ui;

import java.io.File;
import java.io.FileFilter;

class ImageFileFilter implements FileFilter {

    private static final String[] extensions = new String[]{"jpg", "png", "jpeg"};

    @Override
    public boolean accept(File pathname) {
        for (String extension : extensions) {
            if (pathname.getName().toLowerCase().endsWith(extension))
                return true;
        }
        return false;
    }
}
