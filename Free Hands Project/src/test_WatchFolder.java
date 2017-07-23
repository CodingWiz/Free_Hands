import javax.swing.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

//import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class test_WatchFolder {

    public static void watchDirectoryPath() {
        while (true) {
            final String nomApp = "Free Hands"; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            final Path path = Paths.get(System.getProperty("user.home") + "/" + nomApp);
            boolean blnFalse = false;
            // Sanity check - Check if path is a folder
            if (Files.isDirectory(path)) {
                //JOptionPane.showMessageDialog(null, "The folder \"" + path.toString() + "\" has been erased\nIt's going to be recreated at the same path", "Folder \"" + path.toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);

                for(File file: (new File(path.toString())).listFiles()) {
                    if (file.isDirectory()) System.out.println(file.getName());
                    blnFalse = true;
                }

                if (!blnFalse) System.out.println("The folder " + path.toString() + " is empty");
            }
            else {
                System.out.println("The folder " + path.toString() + " doesn't exist");
                break;
            }

            System.out.println("Watching path: " + path);

            // We obtain the file system of the Path

            // We create the new WatchService using the new try() block
            try(WatchService service = (path.getFileSystem()).newWatchService()) {
                // We register the path to the service
                // We watch for creation events
                path.register(service, ENTRY_CREATE, ENTRY_DELETE);
                // Start the infinite polling loop
                while(true) {
                    WatchKey key = service.take();

                    // Dequeueing events
                    Kind<?> kind = null;
                    for(WatchEvent<?> watchEvent : key.pollEvents()) {
                        // Get the type of the event
                        kind = watchEvent.kind();
                        if (OVERFLOW == kind) {
                            continue; //loop
                        } else if (ENTRY_CREATE == kind) System.out.println("A new path was created: " + ((WatchEvent<Path>) watchEvent).context());
                        else if (ENTRY_DELETE == kind) System.out.println("A new path was deleted: " + ((WatchEvent<Path>) watchEvent).context());
                    }

                    if(!key.reset()) {
                        break; //loop
                    }
                }

            } catch(IOException ioe) {
                ioe.printStackTrace();
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Folder we are going to watch
        watchDirectoryPath();
    }

    /*private class watchDir implements Runnable {
        private volatile boolean blnNonExit = true;

        @Override
        public void run() {
            while (blnNonExit) {
                System.out.println("Watching path: " + path);
                // We create the new WatchService using the new try() block
                try(WatchService service = (path.getFileSystem()).newWatchService()) {
                    boolean blnExit = false;
                    // We register the path to the service
                    // We watch for creation events
                    path.register(service, ENTRY_CREATE, ENTRY_DELETE);
                    // Start the infinite polling loop
                    String strMsgWatchEvent = null;
                    while(true) {
                        WatchKey key = service.take();
                        // Dequeueing events
                        Kind<?> kind;
                        for(WatchEvent<?> watchEvent : key.pollEvents()) {
                            // Get the type of the event
                            kind = watchEvent.kind();
                            String strWatchEvent = ((WatchEvent<Path>) watchEvent).context().toString();
                            if (OVERFLOW == kind) {
                                continue; //loop
                            } else if (ENTRY_CREATE == kind) {
                                System.out.println("New path created: " + strWatchEvent);
                                strMsgWatchEvent = "A new folder " + strWatchEvent + " has been added";
                                blnExit = true;
                            }
                            else if (ENTRY_DELETE == kind) {
                                System.out.println("New path deleted: " + strWatchEvent);
                                strMsgWatchEvent = "Folder " + strWatchEvent + " has been deleted";
                                blnExit = true;
                            }
                        }

                        if(!key.reset()) {
                            break; //loop
                        }
                    }

                    if (blnExit) JOptionPane.showMessageDialog(null, strMsgWatchEvent + "\nPlease close this window and reopen it", "Folder \"" + path.toString() + "\" modified", JOptionPane.ERROR_MESSAGE);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        public void kill() {
            blnNonExit = false;
        }
    }*/
}