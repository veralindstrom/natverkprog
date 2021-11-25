package myrunnable;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import tcpclient.TCPClient;
import java.net.*;
import java.io.*;

public class MyRunnable implements Runnable {
    Socket parameter;

    public MyRunnable(Socket parameter) {
        // store parameter for later user
        this.parameter = parameter;
    }

    public void run() {
        byte[] fromServerBuffer = new byte[1024];
        String decodedServerBuffer = null;
        StringBuilder s = new StringBuilder();
        String serverOutput = null;
        int i;
        try {
            while ((i = parameter.getInputStream().read(fromServerBuffer)) > 0) {
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
                parameter.getOutputStream().write(bb, 0, bb.length);

                t = "Content-Type: text/plain\r\n\r\n";
                bb = t.getBytes(StandardCharsets.UTF_8);
                parameter.getOutputStream().write(bb, 0, bb.length);
            } catch (Exception e) {
                String t = "HTTP/1.1 400 Bad Request\r\n";
                byte[] bb = t.getBytes(StandardCharsets.UTF_8);
                parameter.getOutputStream().write(bb, 0, bb.length);

                t = "Content-Type: text/plain\r\n\r\n";
                bb = t.getBytes(StandardCharsets.UTF_8);
                parameter.getOutputStream().write(bb, 0, bb.length);
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
            parameter.getOutputStream().write(bb, 0, bb.length);

            t = "Content-Type: text/plain\r\n\r\n";
            bb = t.getBytes(StandardCharsets.UTF_8);
            parameter.getOutputStream().write(bb, 0, bb.length);

            bb = serverOutput.getBytes(StandardCharsets.UTF_8);
            parameter.getOutputStream().write(bb, 0, bb.length);

            // close IO streams, then socket
            parameter.close();
        } catch (IOException e) {
        }
    }
}