
package com.example.meetingroom;

import static android.view.View.INVISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private TextView logTextView;

    public static MainActivity STATIC_APP;

    private LinkService linkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linkService = new LinkService(this);
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

        TableLayout table = findViewById(R.id.BtnTable);
        addButtons(table);

        STATIC_APP = this;

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
        String url = "https://salutejazz.ru";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void addButtons(TableLayout tl) {

       ArrayList<Link> linkList = linkService.readLinks();
        for (Link link: linkList) {
            TableRow tr = new TableRow(this);
            Button btn = new Button(this);
            btn.setText(link.getName());
            btn.setOnClickListener(l -> onBtnClick(link.getUrl()));
            tr.addView(btn);
            tl.addView(tr);
        }
    }

    protected void addButton(String name, String url) {

//            TableRow tr = new TableRow(this);
//            Button btn = new Button(this);
//            btn.setText(name);
//            btn.setOnClickListener(l->onBtnClick(url));
//            tr.addView(btn);
//
//            TableLayout table = findViewById(R.id.BtnTable);
//            table.addView(tr);

        this.recreate();
    }

    private void onBtnClick(String url) {
        LogManager.getInstance().addLog("launch " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}

