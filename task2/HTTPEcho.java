import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class HTTPEcho {
    public static void main(String[] args) throws IOException {
        // Your code here
        // create socket
        int port = Integer.parseInt(args[0]);
        byte[] fromServerBuffer = new byte[1024];
        String decodedServerBuffer = null;
        StringBuilder s = new StringBuilder();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // System.err.println("Started server on port " + port);
            while (true) {
                // a "blocking" call which waits until a connection is requested
                Socket clientSocket = serverSocket.accept();
                // System.err.println("Accepted connection from client");
                clientSocket.setSoTimeout(3 * 1000);
                try {

                    int i;
                    while ((i = clientSocket.getInputStream().read(fromServerBuffer)) > 0) {
                        decodedServerBuffer = new String(fromServerBuffer, 0, i, StandardCharsets.UTF_8);
                        s.append(decodedServerBuffer);
                        System.out.println(s.toString());
                        if (s.toString().contains("\r\n"))
                            break;
                    }
                    // close IO streams, then socket
                    // System.err.println("Closing connection with client");
                } catch (SocketTimeoutException e) {
                    // System.err.println(e);
                }

                String t = "HTTP/1.1 200 OK\r\n";
                byte[] bb = t.getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                t = "Content-Type: text/plain\r\n";
                bb = t.getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                t = "Content-Length: " + s.length() + "\r\n\r\n";
                bb = t.getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                bb = s.toString().getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                s = new StringBuilder();
                clientSocket.close();

            }
        } catch (IOException e) {
            // e.printStackTrace();
        }

    }
}