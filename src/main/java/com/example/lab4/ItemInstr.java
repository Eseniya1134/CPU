package com.example.lab4;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;



public class ItemInstr {

    @FXML
    private Label op;

    @FXML
    private Label pr1;

    @FXML
    private Label pr2;


    public String getOperation() {
        return op.getText();
    }

    public String getParam1() {
        return pr1.getText();
    }

    public String getParam2() {
        return pr2.getText();
    }


    public void setInstruction(String operation, String param1, String param2) {
        op.setText(operation);
        pr1.setText(param1);
        pr2.setText(param2);
    }

    private HelloController mainController; // Ссылка на главный контроллер

    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
    } //передаем инфу в гл контр

    @FXML
    private AnchorPane itemItr;

    public void handleDelete() {
        // Передаем данные в главный контроллер
        if (mainController != null) {
            System.out.println("Attempting to remove: " + itemItr);
            mainController.removeInstruction(itemItr);
        }else {
            System.out.println("MainController is null!");
        }
    }

    @FXML
    private void handleMoveUp() {
        if (mainController != null) {
            mainController.moveInstructionUp(this);
        }else {
            System.out.println("MainController is null!");
        }


    }

    @FXML
    private void handleMoveDown() {
        if (mainController != null) {
            mainController.moveInstructionDown(this);
        }else {
            System.out.println("MainController is null!");
        }


    }

    public Node getRoot() {
        return op.getParent();
    }
}
