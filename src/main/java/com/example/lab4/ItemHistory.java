package com.example.lab4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ItemHistory {

    @FXML
    private Label op1;

    @FXML
    private Label cnt1;

    @FXML
    private AnchorPane itemHistory;

    private HelloController mainController; // Ссылка на главный контроллер

    // Получить название операции
    public String getOperation() {
        return op1.getText();
    }

    // Получить количество выполнений
    public String getParam1() {
        return cnt1.getText();
    }

    // Установить данные инструкции (операция + количество)
    public void setInstruction(String operation, String count) {
        op1.setText(operation);
        cnt1.setText(count);
    }

    // Установить главный контроллер
    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
    }

}
