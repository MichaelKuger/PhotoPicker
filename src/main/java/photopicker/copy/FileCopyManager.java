package photopicker.copy;

import photopicker.imaging.ImageFile;
import photopicker.imaging.ImagingException;
import photopicker.ui.CopyTaskCreator;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FileCopyManager {

    private final CopyTaskCreator taskCreator;
    private final ForkJoinPool pool = ForkJoinPool.commonPool();

    public FileCopyManager(CopyTaskCreator taskCreator) {
        this.taskCreator = taskCreator;
    }

    public void add(ImageFile image) {
        FileCopyTask task = new FileCopyTask(image);
        pool.submit(task);
    }

    public boolean isReadyForShutdown() {
        return pool.getQueuedTaskCount() == 0;
    }

    private class FileCopyTask extends RecursiveAction {

        private final ImageFile image;

        private FileCopyTask(ImageFile image) {
            this.image = image;
        }

        @Override
        protected void compute() {
            try {
                copyFile(image);
                taskCreator.fileCopyFinished(image.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void copyFile(ImageFile image) throws IOException {
            try {
                File targetFile = new File(taskCreator.getTarget(), image.getFile().getName());
                ImageIO.write(image.getImage(), "jpg", targetFile);
            } catch (FileAlreadyExistsException e) {
                System.out.println("File already exists. Skip.");
            } catch (ImagingException e) {
                e.printStackTrace();
            }
        }
    }
}
