package com.example.lab4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemRmmbr {
    @FXML
    private Label rmbr_numb; // Отображает индекс ячейки
    @FXML
    private Label rmbr_val; // Отображает значение ячейки

    /**
     * Устанавливает данные ячейки: индекс и значение.
     */
    public void setCellData(int index, int value) {
        rmbr_numb.setText(String.valueOf(index));
        rmbr_val.setText(String.valueOf(value));
    }



}







/*
 @FXML
    private Label rmbr_numb; // Отображает номер ячейки массива
    @FXML
    private Label rmbr_val;  // Отображает значение в этой ячейке массива

    private BCpu cpu; // Ссылка на объект класса BCpu


    Устанавливает объект CPU для взаимодействия.

public void setCpu(BCpu cpu) {
    this.cpu = cpu;
}


     Устанавливает номер ячейки массива и значение из массива.

    public void setRememberFromCpu(int index) {
        if (cpu == null) {
            throw new IllegalStateException("CPU is not set. Use setCpu() before calling this method.");
        }

        // Получаем массив значений из объекта CPU
        int[] memoryArray = cpu.getMemory();

        if (index < 0 || index >= memoryArray.length) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        // Устанавливаем номер и значение в метки
        rmbr_numb.setText(String.valueOf(index));
        rmbr_val.setText(String.valueOf(memoryArray[index]));
    }
 */