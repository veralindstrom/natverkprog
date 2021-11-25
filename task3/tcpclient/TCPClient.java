package tcpclient;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        // byte[] fromUserBuffer = ToServer.getBytes(StandardCharsets.UTF_8); // encoded
        // user bytes
        byte[] fromUserBuffer = (ToServer + "\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] fromServerBuffer = new byte[1024];
        Socket clientSocket = new Socket(hostname, port);
        StringBuilder s = new StringBuilder();
        String decodedServerBuffer = null;
        clientSocket.setSoTimeout(3 * 1000);
        try {
            clientSocket.getOutputStream().write(fromUserBuffer, 0, fromUserBuffer.length);

            for (int i = clientSocket.getInputStream().read(fromServerBuffer); i != -1; i = clientSocket
                    .getInputStream().read(fromServerBuffer)) {
                decodedServerBuffer = new String(fromServerBuffer, 0, i, StandardCharsets.UTF_8);
                s.append(decodedServerBuffer);
            }
        } catch (SocketTimeoutException e) {
            // System.err.println(e);
        }

        clientSocket.close();

        return s.toString();
    }

    public static String askServer(String hostname, int port) throws IOException {
        byte[] fromServerBuffer = new byte[1];
        Socket clientSocket = new Socket(hostname, port);
        clientSocket.setSoTimeout(1000);
        StringBuilder s = new StringBuilder();
        String decodedServerBuffer = null;
        try {

            for (int i = clientSocket.getInputStream().read(fromServerBuffer); i != -1; i = clientSocket
                    .getInputStream().read(fromServerBuffer)) {
                decodedServerBuffer = new String(fromServerBuffer, 0, i, StandardCharsets.UTF_8);
                s.append(decodedServerBuffer);
            }
        } catch (SocketTimeoutException e) {
            // System.err.println(e);
        }

        clientSocket.close();

        return s.toString();
    }
}
