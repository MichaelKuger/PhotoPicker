package photopicker.ui;

import java.io.File;

public interface CopyTaskCreator {

    void fileCopyFinished(File file);

    File getTarget();
}
