
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zead shalaby
 */
public class fileserver {
    
    public static void main(String[] args)throws Exception{
        
        int serverPort = 5000;

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server started. Waiting for clients...");
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     FileInputStream fileInputStream = new FileInputStream("D:\\my projects\\last year project\\Semester 2\\distrbuted\\section\\File\\filesv.txt");
                     OutputStream outputStream = clientSocket.getOutputStream()) {
                    
                    System.out.println("Client connected. Sending file...");
                   
                    // حجم الفايل x  //
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    
                    System.out.println("File sent successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
