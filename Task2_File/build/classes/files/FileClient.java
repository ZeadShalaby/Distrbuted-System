package files;

import java.io.*;
import java.net.Socket;

public class FileClient {

    // Server IP address and port
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 3000;

    public static void main(String[] args) throws Exception {
        // Create tasks
        Runnable client1 = new FileReceiverTask("filect.txt");
        Runnable client2 = new FileReceiverTask("filect2.txt");

        // Create threads
        Thread thread1 = new Thread(client1);
        Thread thread2 = new Thread(client2);

        // Start threads
        thread1.start();
        thread2.start();
    }
}

// Task for receiving a file from the server
class FileReceiverTask implements Runnable {
    private final String fileName;
    private static final String DOWNLOAD_DIR = "D:\\my projects\\last year project\\Semester 2\\distrbuted\\section\\Task2_File"; // Directory to save downloaded files

    public FileReceiverTask(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(FileClient.SERVER_IP, FileClient.SERVER_PORT);
             InputStream is = socket.getInputStream();
             FileOutputStream fos = new FileOutputStream(new File(DOWNLOAD_DIR, fileName))) {

            byte[] buffer = new byte[1024]; // Buffer for reading data
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            System.out.println("File received successfully: " + fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + fileName);
        } catch (IOException e) {
            System.err.println("Error: Failed to receive file - " + fileName + " : Server not Respond :(...!");
            
        }
    }
}
