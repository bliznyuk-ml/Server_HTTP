package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * 1. Створити простий http однопоточний сервер для статичних веб сторінок
 * <p>
 * * Сервер повинен слухати порт 8080
 * * Статичні веб сторінки повинні знаходитися в папці html проекту.
 * * Якщо сторінка, що запитує клієнт, знаходиться в html, повернути її з кодом 200
 * * У випадку якщо потрібного файлу в html не буде, повернути відповідь з кодом 404 і сторінкою 404.html (повинна бути в html)
 * * Перевірити, що браузер дозволяє відтворити сторінки, що повертає сервер
 * * Сервер повинен продовжувати роботу після відповіді на перший запит і повертати сторінки при наступних запитах
 * <p>
 * 2. Додаткове завдання
 * *  Створити багатопоточний веб сервер
 */
public class ServerHTTP {

    public static final int PORT = 8080;



    public static void main(String[] args) throws IOException {

        Date date = new Date();
        while (true) {
            String response200 = "HTTP/1.1 200 OK\r\n" +
                    "Date: " + date + "\r\n" +
                    "Server: localhost\r\n" +
                    "Content-Length: \r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "\r\n";
            String response404 = "HTTP/1.1 404 Not Found\r\n" +
                    "Date: " + date + "\r\n" +
                    "Server: localhost\r\n" +
                    "Content-Length: \r\n" +
                    "\r\n";

            ServerSocket server = new ServerSocket(PORT);
            StringBuilder sb = new StringBuilder();
            System.out.println("Wait for connection...");
            Socket remoteClient = server.accept();
            System.out.println("Client connected at " + remoteClient.getRemoteSocketAddress().toString());
            try (InputStream inResponse = remoteClient.getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inResponse))
            ) {
                String line = bufferedReader.readLine();
                String[] index = line.split("\\/|\\s");
                int fileNameIndex = 2;
                String fileName = "html\\" + index[fileNameIndex];
                Path path = Paths.get(fileName);
                boolean exists = Files.exists(path);
                if (exists) {
                    try (InputStream inFile = new FileInputStream(fileName);
                         OutputStream out = remoteClient.getOutputStream()) {
                        while (inFile.available() > 0) {
                            byte[] bytes = inFile.readNBytes(1024);
                            out.write(response200.getBytes());
                            out.write(bytes);
                        }
                    }
                } else {
                    try (InputStream inFile = new FileInputStream("html\\404.html");
                         OutputStream out = remoteClient.getOutputStream()) {
                        while (inFile.available() > 0) {
                            byte[] bytes = inFile.readNBytes(1024);
                            out.write(response404.getBytes());
                            out.write(bytes);
                        }
                    }
                }
                server.close();
            }
        }
    }
}