package com.example.lab4;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class HelloController {

    private ItemAdd itemAdd;

    @FXML
    private Label ra, rb, rc, rd; // Метки для отображения значений регистров

    private BCpu cpu;

    private List<String[]> instructionList = new ArrayList<>(); // Все инструкции
    private Map<String, ItemHistory> historyMap = new HashMap<>(); // История инструкций с их количеством выполнений

    private Set<String[]> executedInstructions = new HashSet<>(); // Выполненные инструкции
    private Node lastHighlightedNode = null; // Ссылка на последнюю подсвеченную инструкцию
    private int lastModifiedMemoryIndex = -1; // Индекс последней изменённой ячейки

    @FXML
    private AnchorPane regInf; // Панель для отображения информации о регистрах

    // Инициализация контроллера и CPU
    @FXML
    public void initialize() {
        cpu = new BCpu();
        cpu.setLabels(ra, rb, rc, rd);
        System.out.println("Labels set in CPU.");
        cpu.testRegisterUpdate();
    }

    // Выполнение одной инструкции и обновление интерфейса
    @FXML
    private void executeInstruction() {
        System.out.println("Execute button pressed.");

        for (int i = 0; i < instructionList.size(); i++) {
            String[] command = instructionList.get(i);
            if (!executedInstructions.contains(command)) {
                System.out.println("Executing command: " + Arrays.toString(command));

                cpu.loadInstruction(command[0], Arrays.copyOfRange(command, 1, command.length));
                cpu.exec();

                executedInstructions.add(command);

                if (lastHighlightedNode != null) {
                    lastHighlightedNode.setStyle("");
                }

                Node currentNode = instCont.getChildren().get(i);
                currentNode.setStyle("-fx-background-color: #6ddeb6;");
                lastHighlightedNode = currentNode;

                lastModifiedMemoryIndex = cpu.getLastModifiedMemoryIndex();


                String operationKey = command[0];
                int newCount = historyMap.containsKey(operationKey) ?
                        Integer.parseInt(historyMap.get(operationKey).getParam1()) + 1 : 1;

                addHistoryInstr(operationKey, newCount);

                System.out.println("Last modified memory index: " + lastModifiedMemoryIndex);

                updateRegisterDisplay();
                updateMemoryGrid();
                return;
            }
        }

        System.out.println("No more instructions to execute!");
    }

    // Обновление значений регистров
    private void updateRegisterDisplay() {
        int[] registers = cpu.getRegisters();
        ra.setText(String.valueOf(registers[0]));
        rb.setText(String.valueOf(registers[1]));
        rc.setText(String.valueOf(registers[2]));
        rd.setText(String.valueOf(registers[3]));
    }

    // Открытие окна добавления новой инструкции
    @FXML
    private void openItemAddWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_add.fxml"));
            Parent root = loader.load();

            ItemAdd addController = loader.getController();
            addController.setMainController(this);
            this.itemAdd = addController;

            System.out.println("ItemAdd initialized: " + (itemAdd != null));

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 100));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private ScrollPane scrollinstr; // Скроллер для инструкций

    @FXML
    private VBox instCont; // Контейнер для инструкций

    @FXML
    private AnchorPane regItem; // Панель для регистров

    @FXML
    private VBox instCount;


    // Добавление или обновление истории инструкций
    public void addHistoryInstr(String op, int count) {
        try {
            // Нормализуем ключ (без пробелов и в верхнем регистре)
            String key = op.trim();

            if (historyMap.containsKey(key)) {
                // Обновляем уже существующую запись
                historyMap.get(key).setInstruction(op, String.valueOf(count));
            } else {
                // Загружаем FXML для новой истории
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item_history.fxml"));
                Parent instructionPane = loader.load();

                ItemHistory instrHistory = loader.getController();
                instrHistory.setInstruction(op, String.valueOf(count));
                instrHistory.setMainController(this);

                instCount.getChildren().add(instructionPane);

                historyMap.put(key, instrHistory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


// Добавление новой инструкции
    public void addInstruction(String operation, String param1, String param2) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_instr.fxml"));
            Parent instructionPane = loader.load();

            // Без приведения к верхнему регистру:
            String[] command = new String[]{operation, param1, param2};
            instructionList.add(command);

            ItemInstr instrController = loader.getController();
            instrController.setInstruction(operation, param1, param2);
            instrController.setMainController(this);

            instCont.getChildren().add(instructionPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Сброс программы и очистка всех данных
    public void resetProgram() {
        System.out.println("Resetting the program...");

        instructionList.clear();
        executedInstructions.clear();
        historyMap.clear();

        instCont.getChildren().clear();

        if (cpu != null) {
            cpu.resetMemory();
            cpu.resetRegisters();
        }

        ra.setText("0");
        rb.setText("0");
        rc.setText("0");
        rd.setText("0");

        memoryGrid.getChildren().clear();

        System.out.println("Program reset complete.");
    }

    // Удаление инструкции из интерфейса и откат действий
    public void removeInstruction(AnchorPane a) {
        // 1. Получаем индекс удаляемой инструкции
        int index = instCont.getChildren().indexOf(a);

        // 2. Удаляем визуально из VBox
        if (index >= 0) {
            instCont.getChildren().remove(index);
        }

        // 3. Удаляем из списка команд (instructionList)
        if (index >= 0 && index < instructionList.size()) {
            instructionList.remove(index);
        }

        // 4. Сброс CPU и всех вспомогательных данных
        cpu.resetMemory();
        cpu.resetRegisters();
        executedInstructions.clear();
        lastHighlightedNode = null;
        lastModifiedMemoryIndex = -1;

        // 5. Очищаем историю (VBox и Map)
        historyMap.clear();
        instCount.getChildren().clear();

        // 6. Переисполняем оставшиеся инструкции
        Map<String, Integer> opCounts = new HashMap<>();
        for (int i = 0; i < instructionList.size(); i++) {
            String[] command = instructionList.get(i);
            cpu.loadInstruction(command[0], Arrays.copyOfRange(command, 1, command.length));
            cpu.exec();
            executedInstructions.add(command);

            // Подсчитываем частоту каждой операции
            String opKey = command[0];
            opCounts.put(opKey, opCounts.getOrDefault(opKey, 0) + 1);

            // Обновляем подсветку (последняя будет активна)
            Node currentNode = instCont.getChildren().get(i);
            if (i == instructionList.size() - 1) {
                currentNode.setStyle("-fx-background-color: #6ddeb6;");
                lastHighlightedNode = currentNode;
            } else {
                currentNode.setStyle("");
            }
        }

        // 7. Перерисовываем историю появления инструкций
        for (Map.Entry<String, Integer> entry : opCounts.entrySet()) {
            addHistoryInstr(entry.getKey(), entry.getValue());
        }

        // 8. Обновляем интерфейс
        updateRegisterDisplay();
        updateMemoryGrid();
    }


    // Перемещение инструкции вверх
    public void moveInstructionUp(ItemInstr itemController) {
        Node currentNode = itemController.getRoot();
        int currentIndex = instCont.getChildren().indexOf(currentNode);

        if (currentIndex > 0) {
            ObservableList<Node> children = instCont.getChildren();
            Node nodeToMove = children.remove(currentIndex);
            children.add(currentIndex - 1, nodeToMove);
        } else {
            System.out.println("Cannot move up. Already at the top.");
        }
    }

    // Перемещение инструкции вниз
    public void moveInstructionDown(ItemInstr itemController) {
        Node currentNode = itemController.getRoot();
        int currentIndex = instCont.getChildren().indexOf(currentNode);

        if (currentIndex < instCont.getChildren().size() - 1) {
            ObservableList<Node> children = instCont.getChildren();
            Node nodeToMove = children.remove(currentIndex);
            children.add(currentIndex + 1, nodeToMove);
        } else {
            System.out.println("Cannot move down. Already at the bottom.");
        }
    }

    @FXML
    private GridPane memoryGrid; // Сетка памяти

    // Заполнение сетки памяти
    private void populateMemoryGrid() {
        memoryGrid.getChildren().clear();

        int[] memory = cpu.getMemory();

        try {
            for (int i = 0; i < memory.length; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item_rmmbr.fxml"));
                Pane cellPane = loader.load();

                ItemRmmbr cellController = loader.getController();
                cellController.setCellData(i, memory[i]);

                if (i == lastModifiedMemoryIndex) {
                    cellPane.setStyle("-fx-background-color: #6ddeb6;");
                } else {
                    cellPane.setStyle("-fx-background-color: transparent;");
                }

                int col = i % 3;
                int row = i / 3;

                memoryGrid.add(cellPane, col, row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке item_rmmbr.fxml!");
        }
    }

    // Обновление сетки памяти
    public void updateMemoryGrid() {
        populateMemoryGrid();
    }
}
