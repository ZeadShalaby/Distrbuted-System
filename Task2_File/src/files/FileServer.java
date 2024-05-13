package files;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zead shalaby
 */

public class FileServer {
    public static final int SERVER_PORT = 3000;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     FileInputStream fileInputStream = new FileInputStream("D:\\my projects\\last year project\\Semester 2\\distrbuted\\section\\Task2_File\\filesv.txt");
                     OutputStream outputStream = clientSocket.getOutputStream()) {

                    System.out.println("Client connected. Sending file...");

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("File sent successfully.");
                } catch (FileNotFoundException e) {
                    System.err.println("Error: File not found - " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error: Failed to send file - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Failed to start server - " + e.getMessage());
        }
    }
}
