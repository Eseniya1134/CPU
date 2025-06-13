package com.example.lab4;


import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для оптимизации работы приложения
 */
public class PerformanceOptimizer {

    private final GridPane memoryGrid;
    private final int[] memory;
    private final Map<Integer, Label> cellCache = new HashMap<>(); // Кэш для ячеек памяти

    public PerformanceOptimizer(GridPane memoryGrid, int[] memory) {
        this.memoryGrid = memoryGrid;
        this.memory = memory;
    }

    /**
     * Инициализация ячеек памяти с использованием кэша для ускорения рендера.
     */
    public void initializeMemoryGrid() {
        memoryGrid.getChildren().clear();

        for (int i = 0; i < memory.length; i++) {
            Label cell = createMemoryCell(i, memory[i]);
            cellCache.put(i, cell);
            int col = i % 3;
            int row = i / 3;
            memoryGrid.add(cell, col, row);
        }
    }

    /**
     * Обновляет ячейку памяти по индексу, избегая полного рендера.
     *
     * @param index Индекс памяти
     * @param value Новое значение памяти
     */
    public void updateMemoryCell(int index, int value) {
        if (!cellCache.containsKey(index)) {
            System.err.println("Ошибка: Ячейка памяти с индексом " + index + " не найдена в кэше!");
            return;
        }

        Label cell = cellCache.get(index);
        Platform.runLater(() -> {
            cell.setText(String.valueOf(value));
            cell.setStyle("-fx-background-color: #ff9999; -fx-border-color: black;"); // Подсветка

            // Убираем подсветку через 1 секунду
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> cell.setStyle("-fx-border-color: black;"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    /**
     * Создаёт ячейку памяти для GridPane.
     *
     * @param index Индекс памяти
     * @param value Значение памяти
     * @return Label для отображения
     */
    private Label createMemoryCell(int index, int value) {
        Label cell = new Label(String.valueOf(value));
        cell.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-alignment: center;");
        cell.setPrefWidth(50);
        cell.setPrefHeight(30);
        return cell;
    }

    /**
     * Выполняет операцию в отдельном потоке для повышения отзывчивости UI.
     *
     * @param operation Задача для выполнения
     * @param onComplete Действие, которое нужно выполнить после завершения
     */
    public void executeAsync(Task<Void> operation, Runnable onComplete) {
        operation.setOnSucceeded(event -> Platform.runLater(onComplete));
        new Thread(operation).start();
    }

    /**
     * Создаёт задачу для асинхронного выполнения.
     *
     * @param runnable Действие для выполнения
     * @return Task<Void> задача
     */
    public static Task<Void> createTask(Runnable runnable) {
        return new Task<>() {
            @Override
            protected Void call() {
                runnable.run();
                return null;
            }
        };
    }
}
