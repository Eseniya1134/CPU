package com.example.lab4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemAdd implements Initializable {
    @FXML
    private Label label;

    @FXML
    private ComboBox<String> combolist;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Создаем список для ComboBox
        ObservableList<String> list = FXCollections.observableArrayList(
                "ld", "st", "mv", "init", "print", "add", "sub", "mult"
        );

        // Устанавливаем элементы в ComboBox
        combolist.setItems(list);

    }

    private HelloController mainController; // Ссылка на главный контроллер

    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField prm1;

    @FXML
    private TextField prm2;

    @FXML
    private void handleAdd() {
        // Получаем данные
        String operation = combolist.getValue();
        String param1 = prm1.getText();
        String param2 = prm2.getText();

        // Передаем данные в главный контроллер
        if (mainController != null) {
            mainController.addInstruction(operation, param1, param2);
        }
    }

    public String getOperation() {
        return combolist.getValue();
    }

    public String[] getArguments() {
        return new String[]{prm1.getText(), prm2.getText()};
    }




}
