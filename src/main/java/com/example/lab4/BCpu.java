package com.example.lab4;

import javafx.scene.control.Label;

import java.util.Arrays;
import java.util.List;

public class BCpu {
    private int[] registers = new int[4];
    private int[] memor = new int[51];

    public BCpu() {

    }

    private int lastModifiedIndex = -1; // Для хранения индекса последней модификации

    // Метод для обновления памяти
    public void modifyMemory(int index, int value) {
        memor[index] = value;
        lastModifiedIndex = index; // Обновляем индекс последней модификации
        System.out.println("Memory updated at index: " + index + ", value: " + value);
    }

    public void resetMemory() {
        Arrays.fill(memor, 0); // Обнуляем все ячейки памяти
        lastModifiedIndex = -1; // Сбрасываем индекс последней изменённой ячейки
    }

    public void resetRegisters() {
        Arrays.fill(registers, 0); // Обнуляем все регистры
    }


    // Метод для получения индекса последней модифицированной ячейки
    public int getLastModifiedMemoryIndex() {
        return lastModifiedIndex;
    }

    public int[] getMemory() {
        return memor;
    }
    private String operation;
    private List<String> args;

    private Label ra, rb, rc, rd;

    public void setLabels(Label ra, Label rb, Label rc, Label rd) {
        this.ra = ra;
        this.rb = rb;
        this.rc = rc;
        this.rd = rd;
    }

    public void loadInstruction(String operation, String[] args) {
        if (operation == null || operation.isEmpty()) {
            System.out.println("No operation to load!");
            return;
        }
        if (args == null || args.length == 0) {
            System.out.println("No arguments to load!");
            return;
        }

        this.operation = operation;
        this.args = Arrays.asList(args);
        System.out.println("Instruction loaded: " + operation + ", Args: " + Arrays.toString(args));
    }

    public void exec() {
        if (operation == null || operation.isEmpty()) {
            System.out.println("No operation to execute!");
            return;
        }

        System.out.println("Executing command: " + operation + ", Args: " + args);
        switch (operation) {
            case "ld":
                int regIndex = getRegisterIndex(args.get(0));
                int address = Integer.parseInt(args.get(1));
                registers[regIndex] = memor[address];
                modifyMemory(address, memor[address]);
                break;
            case "st":
                regIndex = getRegisterIndex(args.get(0));
                address = Integer.parseInt(args.get(1));
                memor[address] = registers[regIndex];
                modifyMemory(address, memor[address]);
                break;
            case "mv":
                int regR1 = getRegisterIndex(args.get(0));
                int regR2 = getRegisterIndex(args.get(1));
                registers[regR2] = registers[regR1];
                break;
            case "init":
                address = Integer.parseInt(args.get(0));
                int value = Integer.parseInt(args.get(1));
                memor[address] = value;
                modifyMemory(address, memor[address]);
                break;
            case "print":
                setRegisterValue();
                break;
            case "add":
                registers[3] = registers[0] + registers[1]; // d = a + b
                break;
            case "sub":
                registers[3] = registers[0] - registers[1]; // d = a - b
                break;
            case "mult":
                registers[3] = registers[0] * registers[1]; // d = a * b
                break;

            default:
                throw new IllegalArgumentException("ERROR ");
        }
        updateLabels();
    }

    private int getRegisterIndex(String reg) {
        return switch (reg) {
            case "a" -> 0;
            case "b" -> 1;
            case "c" -> 2;
            case "d" -> 3;
            default -> throw new IllegalArgumentException("Invalid register: " + reg);
        };
    }

    private void updateLabels() {
        if (ra == null || rb == null || rc == null || rd == null) {
            System.out.println("Labels are not properly initialized!");
            return;
        }

        ra.setText(String.valueOf(registers[0]));
        rb.setText(String.valueOf(registers[1]));
        rc.setText(String.valueOf(registers[2]));
        rd.setText(String.valueOf(registers[3]));
        System.out.println("Labels updated: a=" + registers[0] + ", b=" + registers[1] + ", c=" + registers[2] + ", d=" + registers[3]);
    }


    //печать значения регистров
    private void setRegisterValue() {
        System.out.println("Registers:" + " a = " + registers[0] + ", b = " + registers[1] + ", c = " + registers[2] + ", d = " + registers[3]);
    }

   public void testRegisterUpdate() {
        registers[0] = 0;
        registers[1] = 0;
        registers[2] = 0;
        registers[3] = 0;
        updateLabels();
    }

    public int[] getRegisters() {
        return registers;
    }
}
