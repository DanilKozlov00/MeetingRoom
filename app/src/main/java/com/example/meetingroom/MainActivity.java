
package com.example.meetingroom;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logs);

        logTextView = findViewById(R.id.logTextView);
        Button clearLogsButton = findViewById(R.id.clearLogsButton);
        Button refreshLogsButton = findViewById(R.id.refreshLogsButton);
        Button startApp = findViewById(R.id.startjazz);
        // Добавление начальных логов
        LogManager.getInstance().addLog("Версия Android: " + Build.VERSION.RELEASE);
        LogManager.getInstance().addLog("Приложение запущено");
        displayLogs();

        // Очистка логов по кнопке
        clearLogsButton.setOnClickListener(v -> logTextView.setText(""));
        refreshLogsButton.setOnClickListener(v -> displayLogs());
        startApp.setOnClickListener(v -> launchApp());


        Intent serviceIntent = new Intent(this, BackgroundServerService.class);
        System.out.println(Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
    }

    // Метод для добавления логов
    private void displayLogs() {
        List<String> logs = LogManager.getInstance().getLogs();
        StringBuilder logText = new StringBuilder();
        for (String log : logs) {
            logText.append(log).append("\n");
        }
        logTextView.setText(logText.toString());
    }

    private void launchApp() {
        LogManager.getInstance().addLog("Попытка запустить sber jazz");
        String url = "https://salutejazz.ru/calls/in0sec?psw=OBgWD0RREwQNCBEZG0UEFwICDA";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}

