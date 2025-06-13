package com.example.lab4;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane; // Убедитесь, что вы импортируете правильный класс
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class HelloController {

    private ItemAdd itemAdd;

    @FXML
    private Label ra, rb, rc, rd;
    private BCpu cpu;

    private List<String[]> instructionList = new ArrayList<>(); // Все инструкции
    private Set<String[]> executedInstructions = new HashSet<>(); // Выполненные инструкции
    private Node lastHighlightedNode = null; // Ссылка на последнюю подсвеченную инструкцию
    private int lastModifiedMemoryIndex = -1; // Индекс последней изменённой ячейки

    @FXML
    private AnchorPane regInf;

    //отправка в bcpu инфы о функциях
    @FXML
    public void initialize() {
        cpu = new BCpu();

        // Установка меток для BCpu
        cpu.setLabels(ra, rb, rc, rd);
        System.out.println("Labels set in CPU.");
        cpu.testRegisterUpdate();
    }

    //получение инфы
    @FXML
    private void executeInstruction() {
        System.out.println("Execute button pressed.");

        // Находим следующую невыполненную операцию
        for (int i = 0; i < instructionList.size(); i++) {
            String[] command = instructionList.get(i);
            if (!executedInstructions.contains(command)) {
                System.out.println("Executing command: " + Arrays.toString(command));

                // Выполняем операцию через CPU
                cpu.loadInstruction(command[0], Arrays.copyOfRange(command, 1, command.length));
                cpu.exec();

                // Помечаем эту инструкцию как выполненную
                executedInstructions.add(command);

                // Сбрасываем подсветку с предыдущего узла
                if (lastHighlightedNode != null) {
                    lastHighlightedNode.setStyle(""); // Убираем пользовательский стиль
                }

                // Подсвечиваем текущий узел
                Node currentNode = instCont.getChildren().get(i);
                currentNode.setStyle("-fx-background-color: #6ddeb6;"); // Пример подсветки
                lastHighlightedNode = currentNode; // Сохраняем текущий узел как последний подсвеченный

                // Обновляем отображение регистров
                lastModifiedMemoryIndex = cpu.getLastModifiedMemoryIndex();

                updateRegisterDisplay();
                updateMemoryGrid();
                System.out.println("Last modified memory index: " + lastModifiedMemoryIndex);

                return; // Выполняем только одну операцию за раз
            }
        }

        System.out.println("No more instructions to execute!");
    }

    //обновление регистров
    private void updateRegisterDisplay() {
        int[] registers = cpu.getRegisters();
        ra.setText(String.valueOf(registers[0]));
        rb.setText(String.valueOf(registers[1]));
        rc.setText(String.valueOf(registers[2]));
        rd.setText(String.valueOf(registers[3]));
    }


    //связь с окном добавления функций
    @FXML
    private void openItemAddWindow() {
        try {
            // Загружаем FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_add.fxml"));
            Parent root = loader.load();

            // Получаем контроллер ItemAdd
            ItemAdd addController = loader.getController();

            // Устанавливаем ссылки между контроллерами
            addController.setMainController(this);
            this.itemAdd = addController;

            // Проверяем, что ItemAdd успешно инициализирован
            System.out.println("ItemAdd initialized: " + (itemAdd != null));


            // Отображаем окно
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 100));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private ScrollPane scrollinstr;

    @FXML
    private VBox instCont; // Контейнер для инструкций

    @FXML
    private AnchorPane regItem;


//добавление одной панелевой функции на скроллер
    public void addInstruction(String operation, String param1, String param2) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_instr.fxml"));
            Parent instructionPane = loader.load();

            // Создаем массив данных инструкции
            String[] command = new String[] {operation, param1, param2};
            instructionList.add(command);

        // Устанавливаем данные в интерфейс инструкции
            ItemInstr instrController = loader.getController();
            instrController.setInstruction(operation, param1, param2);
            instrController.setMainController(this);

        // Добавляем в VBox
            instCont.getChildren().add(instructionPane);
        }catch (Exception e) {
            e.printStackTrace();
    }
}

    //сброс всех функций

    public void resetProgram() {
        System.out.println("Resetting the program...");

        // Очищаем список инструкций
        instructionList.clear();
        executedInstructions.clear();
        Object lastExecutedOperation = null;

        // Очищаем отображение инструкций
        instCont.getChildren().clear();

        // Сбрасываем память и регистры CPU
        if (cpu != null) {
            cpu.resetMemory(); // Метод для очистки памяти CPU
            cpu.resetRegisters(); // Метод для сброса регистров
        }

        // Сбрасываем отображение регистров
        ra.setText("0");
        rb.setText("0");
        rc.setText("0");
        rd.setText("0");

        // Очищаем сетку памяти
        memoryGrid.getChildren().clear();

        System.out.println("Program reset complete.");
    }



    //удаление панелевых функций на скроллер
    public void removeInstruction(AnchorPane a) {
        instCont.getChildren().remove(a);
        // Обновление интерфейса
        instCont.requestLayout(); // Перерисовка VBox
        scrollinstr.requestLayout(); // Перерисовка ScrollPane

    }

    //инструкция верх
    public void moveInstructionUp(ItemInstr itemController) {
        Node currentNode = itemController.getRoot();
        int currentIndex = instCont.getChildren().indexOf(currentNode);

        if (currentIndex > 0) {
            ObservableList<Node> children = instCont.getChildren();

            // Удаляем текущий узел
            Node nodeToMove = children.remove(currentIndex);

            // Вставляем его на позицию выше
            children.add(currentIndex - 1, nodeToMove);

        } else {
            System.out.println("Cannot move up. Already at the top.");
        }
    }

    //инструкция вниз
    public void moveInstructionDown(ItemInstr itemController) {
        Node currentNode = itemController.getRoot();
        int currentIndex = instCont.getChildren().indexOf(currentNode);

        if (currentIndex > 0) {
            ObservableList<Node> children = instCont.getChildren();

            // Удаляем текущий узел
            Node nodeToMove = children.remove(currentIndex);

            // Вставляем его на позицию выше
            children.add(currentIndex + 1, nodeToMove);

        } else {
            System.out.println("Cannot move up. Already at the top.");
        }
    }

    @FXML
    private GridPane memoryGrid; // GridPane внутри ScrollPane для отображения ячеек памяти

    private void populateMemoryGrid() {
        memoryGrid.getChildren().clear(); // Очищаем текущую сетку

        int[] memory = cpu.getMemory(); // Получаем массив памяти (предположительно 1024 элемента)

        try {
            for (int i = 0; i < memory.length; i++) {
                // Загружаем FXML для каждой ячейки
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item_rmmbr.fxml"));
                Pane cellPane = loader.load();

                // Настраиваем контроллер ячейки
                ItemRmmbr cellController = loader.getController();
                cellController.setCellData(i, memory[i]);

                // Подсвечиваем последнюю модифицированную ячейку
                if (i == lastModifiedMemoryIndex) {
                    cellPane.setStyle("-fx-background-color: #6ddeb6;"); // Пример подсветки
                } else {
                    cellPane.setStyle("-fx-background-color: transparent;"); // Сбрасываем стиль для остальных
                }

                // Определяем позицию ячейки в GridPane
                int col = i % 3; // 3 колонок
                int row = i / 3;

                // Добавляем ячейку в GridPane
                memoryGrid.add(cellPane, col, row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке item_rmmbr.fxml!");
        }
    }


    public void updateMemoryGrid() {
        populateMemoryGrid();
    }


}







