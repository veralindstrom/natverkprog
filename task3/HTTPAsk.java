import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import tcpclient.TCPClient;

public class HTTPAsk {
    public static void main(String[] args) {
        // Your code here
        // create socket
        int serverPort = Integer.parseInt(args[0]);
        byte[] fromServerBuffer = new byte[1024];
        String decodedServerBuffer = null;
        StringBuilder s = new StringBuilder();
        String serverOutput = null;

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            // System.err.println("Started server on port " + port);
            while (true) {
                // a "blocking" call which waits until a connection is requested
                Socket clientSocket = serverSocket.accept();
                // System.err.println("Accepted connection from client");
                int i;
                while ((i = clientSocket.getInputStream().read(fromServerBuffer)) > 0) {

                    decodedServerBuffer = new String(fromServerBuffer, 0, i, StandardCharsets.UTF_8);
                    s.append(decodedServerBuffer);
                    if (s.toString().contains("\r\n"))
                        break;
                }

                String req = s.toString();
                String attr = null;
                String userInput = null;
                String hostname = null;
                int port = 0;

                try {
                    String[] temp = req.split("\r\n");
                    String firstLine = temp[0];
                    String[] firstLineSplit = firstLine.split(" ");
                    if (!firstLineSplit[1].contains("/ask"))
                        throw new IOException();
                    else if (!firstLineSplit[0].contains("GET") || !firstLineSplit[2].contains("HTTP"))
                        throw new Exception();

                    String[] arr = firstLineSplit[1].split("[?]");

                    attr = arr[1];
                    String splitAttr[] = attr.split("[&]");

                    hostname = splitAttr[0].split("hostname=")[1];
                    port = Integer.parseInt(splitAttr[1].split("port=")[1]);
                    if (splitAttr.length > 2) {
                        userInput = splitAttr[2].split("string=")[1];
                    }

                } catch (IOException e) {
                    String t = "HTTP/1.1 404 Not Found\r\n";
                    byte[] bb = t.getBytes(StandardCharsets.UTF_8);
                    clientSocket.getOutputStream().write(bb, 0, bb.length);

                    t = "Content-Type: text/plain\r\n\r\n";
                    bb = t.getBytes(StandardCharsets.UTF_8);
                    clientSocket.getOutputStream().write(bb, 0, bb.length);
                    break;
                } catch (Exception e) {
                    String t = "HTTP/1.1 400 Bad Request\r\n";
                    byte[] bb = t.getBytes(StandardCharsets.UTF_8);
                    clientSocket.getOutputStream().write(bb, 0, bb.length);

                    t = "Content-Type: text/plain\r\n\r\n";
                    bb = t.getBytes(StandardCharsets.UTF_8);
                    clientSocket.getOutputStream().write(bb, 0, bb.length);
                    break;
                }

                try {
                    if (userInput != null)
                        serverOutput = TCPClient.askServer(hostname, port, userInput);
                    else
                        serverOutput = TCPClient.askServer(hostname, port);
                    // System.out.printf("%s:%d says:\n%s", hostname, port, serverOutput);
                } catch (UnknownHostException e) {
                    serverOutput = "Service does not exist";
                }

                String t = "HTTP/1.1 200 OK\r\n";
                byte[] bb = t.getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                t = "Content-Type: text/plain\r\n\r\n";
                bb = t.getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                bb = serverOutput.getBytes(StandardCharsets.UTF_8);
                clientSocket.getOutputStream().write(bb, 0, bb.length);

                // close IO streams, then socket

                s = new StringBuilder();
                clientSocket.close();

            }
        } catch (IOException e) {
            // e.printStackTrace();

        }
    }

}
