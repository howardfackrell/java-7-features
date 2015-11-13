package com.hlf.java7features;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by howard.fackrell on 11/6/15.
 */
public class FileWatcherTest {

    static final long SHORT_SLEEP_MILLIS = 10000;
    static final long LONG_SLEEP_MILLIS = 40000;
    static final String FILENAME = "watched.txt";
    final String DIRECTORY = "watched";

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {}
    }



    private static void createDirectory(String directoryName) {
        FileAttribute<Set<PosixFilePermission>> permissions =
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxr-x"));
        try {
            Files.createDirectory(Paths.get(directoryName), permissions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDirectory(String directoryName) {
        try {
            Files.deleteIfExists(Paths.get(directoryName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileWatch() {

        deleteDirectory(DIRECTORY);
        createDirectory(DIRECTORY);

        FileWatcher watcher = new FileWatcher(DIRECTORY);
        new Thread(watcher).start();

        sleep(SHORT_SLEEP_MILLIS);

        FileUpdater updater = new FileUpdater(DIRECTORY, FILENAME);
        new Thread(updater).start();

        sleep(LONG_SLEEP_MILLIS);

        watcher.stop();

        sleep(SHORT_SLEEP_MILLIS);

        assertTrue(watcher.events.size() == 3);
        assertTrue(watcher.events.contains(StandardWatchEventKinds.ENTRY_CREATE));
        assertTrue(watcher.events.contains(StandardWatchEventKinds.ENTRY_MODIFY));
        assertTrue(watcher.events.contains(StandardWatchEventKinds.ENTRY_DELETE));

    }
}


class FileUpdater implements Runnable {
    Path path;

    FileUpdater(String directoryName, String filename) {
        path = Paths.get(directoryName, filename);
    }

    private void updateFile(OpenOption openOption) {
        try (BufferedWriter writer = Files.newBufferedWriter(path.toAbsolutePath(), openOption)) {
            writer.write(new Date().toString());
        } catch (IOException e) {
            fail("Couldn't write to file");
        }
    }

    private void deleteFile() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            fail("couldn't delete the file");
        }
    }

    @Override
    public void run() {
        updateFile(StandardOpenOption.CREATE);
        FileWatcherTest.sleep(FileWatcherTest.SHORT_SLEEP_MILLIS);
        updateFile(StandardOpenOption.APPEND);
        FileWatcherTest.sleep(FileWatcherTest.SHORT_SLEEP_MILLIS);
        deleteFile();
    }
}

class FileWatcher implements Runnable {

    Path watchedDirectory;
    boolean running = true;
    List<WatchEvent.Kind<?>> events = new ArrayList<>();

    public FileWatcher(String fileName) {
        watchedDirectory = Paths.get(fileName);
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("Watching " + watchedDirectory.toAbsolutePath() );
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            watchedDirectory.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key = null;
            while (running) {
                try {
                    key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        System.out.println(event.context().toString() +  "->" + kind + " @ " + new Date());
                        events.add(kind);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!key.reset()) break;
            }
        }catch (IOException e) {
            e.printStackTrace();
            fail("Couldn't watch the file for changes");
        }
        System.out.println("done Watching " + watchedDirectory.toAbsolutePath() );
    }
}
