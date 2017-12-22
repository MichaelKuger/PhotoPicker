package photopicker.copy;

import photopicker.imaging.ImageFile;
import photopicker.imaging.ImagingException;
import photopicker.ui.CopyTaskCreator;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class FileCopyManager {

    private final Set<File> filesCopied = new HashSet<File>();
    private final LinkedBlockingQueue<ImageFile> copyQueue = new LinkedBlockingQueue<>();
    private final CopyTaskCreator taskCreator;
    private final FileCopyWorker copyWorker = new FileCopyWorker();

    public FileCopyManager(CopyTaskCreator taskCreator) {
        this.taskCreator = taskCreator;
        copyWorker.start();
    }

    public void add(ImageFile image) {
        try {
            if (filesCopied.contains(image.getFile())) {
                taskCreator.fileCopyFinished(image.getFile());
                return;
            }
            copyQueue.put(image);
            filesCopied.add(image.getFile());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isReadyForShutdown() {
        return copyQueue.isEmpty();
    }

    private class FileCopyWorker extends Thread {
        private FileCopyWorker() {
            super("FileCopyWorker");
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!interrupted()) {
                    try {
                        ImageFile image = copyQueue.take();
                        copyFile(image);
                        System.out.println("Finished!");
                        taskCreator.fileCopyFinished(image.getFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
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
