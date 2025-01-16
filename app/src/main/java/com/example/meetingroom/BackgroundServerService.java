package com.example.meetingroom;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BackgroundServerService extends Service {

    private static final int PORT = 3232; // Порт для сервера
    private static final String CHANNEL_ID = "ServerChannel";
    private ServerSocket serverSocket;

    private LinkService linkService;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MeetingRoom service")
                .setContentText("Server is running")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build());
        linkService = new LinkService(this);
        startServer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                LogManager.getInstance().addLog("Сервер запущен!");
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                }
            } catch (Exception e) {
                LogManager.getInstance().addLog(e.getMessage());
            }
        }).start();
    }

    private void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (Exception e) {
            LogManager.getInstance().addLog(e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                String response = "FAIL";
                String request = in.readLine();
                LogManager.getInstance().addLog("Получена команда: " + request);
                String queryString = extractQueryString(request);
                if (queryString != null) {
                    Map<String, String> params = parseQueryString(queryString);
                    if (params.containsKey("url")) {
                        launchApp(params.get("url"));
                        response = "OK";
                    }else  if (params.containsKey("call")) {
                        linkService.addLink(params.get("call"), params.get("desc"));
                        //MainActivity.STATIC_APP.addButton(params.get("desc"), params.get("call"));
                        response = "OK";
                    }else  if (params.containsKey("delete")) {
                        removeLink(params.get("delete"));
                        response = "OK";
                    }
                }

                String htmlResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain; charset=UTF-8\r\n" +
                        "Content-Length: " + ("Server received: " + request).length() + "\r\n\r\n" +
                        "Server response: " + response;
                out.println(htmlResponse);
                out.flush();

                clientSocket.close();
            } catch (Exception e) {
                LogManager.getInstance().addLog(e.getMessage());
            }
        }).start();
    }

    private void removeLink(String name) {
        linkService.removeLink(name);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Server Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private String extractQueryString(String request) {

        if (request != null && request.contains(" ")) {
            String[] parts = request.split(" ");
            if (parts.length > 1) {
                String path = parts[1];
                int questionMarkIndex = path.indexOf('?');
                if (questionMarkIndex != -1) {
                    return path.substring(questionMarkIndex + 1);
                }
            }
        }
        return null;
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                params.put(key, value);
            }
        }
        return params;
    }

    private void launchApp(String url) {
        LogManager.getInstance().addLog("Попытка запустить url");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
