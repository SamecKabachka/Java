package Integral;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntegralCalculatorApp extends JFrame {

    private JTextField lowerBoundField;
    private JTextField upperBoundField;
    private JTextField stepField;
    private JTextField sectionsField;
    private JTable table;
    private DefaultTableModel tableModel;

    public IntegralCalculatorApp() {
        // Настройка окна
        setTitle("Integral Calculator");
        setSize(810, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель для полей ввода и кнопок
        JPanel inputPanel = new JPanel(new GridLayout(1, 6));

        lowerBoundField = new JTextField();
        upperBoundField = new JTextField();
        stepField = new JTextField();
        sectionsField = new JTextField();

        inputPanel.add(new JLabel("Нижний предел:"));
        inputPanel.add(lowerBoundField);
        inputPanel.add(new JLabel("Верхний предел:"));
        inputPanel.add(upperBoundField);
        inputPanel.add(new JLabel("Шаг:"));
        inputPanel.add(stepField);
        inputPanel.add(new JLabel("Отрезки:"));
        inputPanel.add(sectionsField);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

        JButton addButton = new JButton("Добавить");
        JButton removeButton = new JButton("Удалить");
        JButton calculateButton = new JButton("Вычислить");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(calculateButton);

        // Таблица
        String[] columnNames = {"Нижний предел", "Верхний предел", "Шаг", "Отрезки", "Результат"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Только первые четыре колонки редактируемы
                return column < 4;
            }
        };
        table = new JTable(tableModel);

        // Добавление компонентов в окно
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий для кнопок
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeRow();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateIntegral();
            }
        });
    }

    // Метод для добавления строки в таблицу
    private void addRow() {
        try {
            double lowerBound = Double.parseDouble(lowerBoundField.getText());
            double upperBound = Double.parseDouble(upperBoundField.getText());
            double step = Double.parseDouble(stepField.getText());
            double sections = Double.parseDouble(sectionsField.getText());

            tableModel.addRow(new Object[]{lowerBound, upperBound, step, sections, null});
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный ввод! Пожалуйста, введите действительные числа.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Метод для удаления выделенной строки из таблицы
    private void removeRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Строка не выбрана!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Функция для вычисления натурального логарифма
    private double ln(double y) {
        if (y <= 0) {
            throw new IllegalArgumentException("Аргумент должен быть положительным числом");
        }

        // Преобразуем y в форму (1 + x), где |x| < 1
        double x = (y - 1) / (y + 1);
        double result = 0;
        double term = x;
        double xSquared = x * x;
        int n = 1;

        // Используем ряд Тейлора для аппроксимации
        while (term > 1e-10 || term < -1e-10) { // Точность до 10^-10
            result += term / n;
            term *= xSquared;
            n += 2;
        }

        return 2 * result; // Умножаем на 2, так как ln(y) = 2 * (x + x^3/3 + x^5/5 + ...)
    }

    private void calculateIntegral() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            try {
                double lowerBound = (double) tableModel.getValueAt(selectedRow, 0);
                double upperBound = (double) tableModel.getValueAt(selectedRow, 1);
                double step = (double) tableModel.getValueAt(selectedRow, 2);
                double sections = (double) tableModel.getValueAt(selectedRow, 3);

                //double result = integrate(lowerBound, upperBound, step);
                double h = (upperBound-lowerBound)/sections, tmp = 0.0;
                double x[] = new double[(int) ((upperBound-lowerBound)/step+1)];

                for (int i = (int) lowerBound, j = 0; i <= (int) upperBound; i+=step, j++){
                    x[j] = 1/ln(i);
                }

                for (int i = (int) lowerBound+1, j = 0; i<(int) upperBound; i+=step, j++){
                    tmp = x[j];
                }

                double result = h * ((x[0] + x[(int) ((upperBound-lowerBound)/step)]/2) + tmp);

                tableModel.setValueAt(result, selectedRow, 4);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка  вычесления интерграла!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
