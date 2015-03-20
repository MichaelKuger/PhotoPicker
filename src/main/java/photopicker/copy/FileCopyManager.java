package photopicker.copy;

import photopicker.ui.CopyTaskCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class FileCopyManager {

    private final Set<File> filesCopied = new HashSet<File>();
    private final LinkedBlockingQueue<File> copyQueue = new LinkedBlockingQueue<File>();
    private final CopyTaskCreator taskCreator;
    private final FileCopyWorker copyWorker = new FileCopyWorker();

    public FileCopyManager(CopyTaskCreator taskCreator) {
        this.taskCreator = taskCreator;
        copyWorker.start();
    }

    public void add(File file) {
        try {
            if (filesCopied.contains(file)) {
                taskCreator.fileCopyFinished(file);
                return;
            }
            copyQueue.put(file);
            filesCopied.add(file);
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
                        File copyFile = copyQueue.take();
                        copyFile(copyFile);
                        System.out.println("Finished!");
                        taskCreator.fileCopyFinished(copyFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void copyFile(File sourceFile) throws IOException {
            try {
                Path sourcePath = sourceFile.toPath();
                File targetFile = new File(taskCreator.getTarget(), sourceFile.getName());
                Path targetPath = targetFile.toPath();
                Files.copy(sourcePath, targetPath);
            } catch (FileAlreadyExistsException e) {
                System.out.println("File already exists. Skip.");
            }
        }
    }
}
