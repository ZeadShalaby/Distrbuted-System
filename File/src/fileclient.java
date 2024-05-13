
import java.io.FileOutputStream;
import java.io.InputStream;
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
public class fileclient {
    
    public static void main(String[] args)throws Exception{
        
        String serverIp = "127.0.0.1";
        int serverPort = 5000;
        
         try (Socket socket = new Socket(serverIp, serverPort);
             InputStream is = socket.getInputStream();
             FileOutputStream fos = new FileOutputStream("D:\\my projects\\last year project\\Semester 2\\distrbuted\\section\\File\\filect.txt")) {
            
            // حجم الفايل x  //
            byte[] buffer = new byte[1024]; // Use a reasonable buffer size
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            
            System.out.println("File received successfully.");
        } catch (Exception e) {
            System.out.println(" num : 500"+"\n"+" msg : The Server not respond :(...!");
        }
        



        
    }
}
