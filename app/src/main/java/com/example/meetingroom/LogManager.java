package com.example.meetingroom;

import java.util.ArrayList;
import java.util.List;

public class LogManager {

    private static LogManager instance;
    private final List<String> logs = new ArrayList<>();

    private LogManager() {
        // Приватный конструктор для Singleton
    }

    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    // Добавить лог
    public synchronized void addLog(String log) {
        logs.add(log);
    }

    // Получить все логи
    public synchronized List<String> getLogs() {
        return new ArrayList<>(logs); // Возвращаем копию списка для безопасности
    }

    // Очистить все логи
    public synchronized void clearLogs() {
        logs.clear();
    }
}
