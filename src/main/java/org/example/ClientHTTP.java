package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientHTTP {
    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient("localhost", 10000);
        String response = httpClient.get("/HW_Wiki.html");
        //System.out.println("-= Response =-");
        System.out.println(response);
    }
}

class HttpClient {
    private final String host;
    private final int port;

    public HttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String request(String httpRequest) {
        StringBuilder responseBuilder = new StringBuilder();
        // tcp connection
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port));
            if (socket.isConnected()) {
                // http protocol
                try (OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    out.write(httpRequest.getBytes()); // send request to server
                    String line;
                    while ((line = reader.readLine()) != null) { // get response
                        responseBuilder.append(line).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseBuilder.toString();
    }

    String get(String path) {
        String request = getRequest(path);
        //System.out.println("-= Request =-");
        //System.out.println(request);
        return request(request);
    }

    private String getRequest(String path) {
        return "GET %s HTTP/1.1\r\n".formatted(path) +
                "Host: %s\r\n".formatted(host) +
                "User-Agent: Java Http Client\r\n" +
                "Accepted: text/http\r\n" +
                "Keep-Alive: close\r\n" +
                "\r\n";
    }
}
