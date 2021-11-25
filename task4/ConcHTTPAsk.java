import java.net.*;
import java.io.*;
import myrunnable.MyRunnable;

public class ConcHTTPAsk {
    public static void main(String[] args) {
        // Your code here
        // create socket
        int serverPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            // System.err.println("Started server on port " + port);
            while (true) {
                // a "blocking" call which waits until a connection is requested
                Socket clientSocket = serverSocket.accept();
                //System.err.println("Accepted connection from client");
                Thread thread = new Thread(new MyRunnable(clientSocket));
                thread.start();

            }
        } catch (IOException e) {
            // e.printStackTrace();

        }
    }

}
