package automatas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class Pila_Fibonacci {
    private JFrame frame;
    private JTextField cadenaField;
    private JTable transicionesTable;
    private DefaultTableModel tableModel;
    private JLabel resultadoLabel;

    public Pila_Fibonacci() {
        frame = new JFrame("Autómata de Pila - Validar Fibonacci de 'a'");
        frame.setLayout(new BorderLayout());
        frame.setSize(1000, 500);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Cadena a validar:"));
        cadenaField = new JTextField();
        inputPanel.add(cadenaField);

        JButton validarBtn = new JButton("Validar Cadena");
        validarBtn.addActionListener(e -> validarCadena());
        inputPanel.add(validarBtn);

        frame.add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "Estado Actual", "Símbolo Entrada", "Cima de Pila",
                "Nuevo Estado", "Operación Pila", "Condición"}, 0);

        transicionesTable = new JTable(tableModel);
        frame.add(new JScrollPane(transicionesTable), BorderLayout.CENTER);

        resultadoLabel = new JLabel("", SwingConstants.CENTER);
        resultadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(resultadoLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void validarCadena() {
        String cadena = cadenaField.getText().trim();
        tableModel.setRowCount(0);

        Stack<Character> pila = new Stack<>();
        pila.push('Z'); // Símbolo inicial
        String estado = "q0";

        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            char cima = pila.peek();

            if (estado.equals("q0")) {
                if (simbolo == 'a') {
                    pila.push('A');
                    pila.push('A'); // Apilar 2 por cada 'a'
                    tableModel.addRow(new Object[]{estado, simbolo, cima, "q_count",
                            "0", "Apilar 2 A por cada a (para facilitar el conteo)."});
                    estado = "q_count";
                } else if (simbolo == 'b' || simbolo == 'c') {
                    tableModel.addRow(new Object[]{estado, simbolo, cima, "q0",
                            "2", "Ignorar símbolos no a."});
                } else {
                    resultadoLabel.setText("❌ Carácter inválido: '" + simbolo + "'"); return;
                }
            } else if (estado.equals("q_count")) {
                if (simbolo == 'a') {
                    pila.push('A');
                    pila.push('A');
                    tableModel.addRow(new Object[]{estado, simbolo, cima, "q_count",
                            "0", "Seguir contando a's."});
                } else if (simbolo == 'b' || simbolo == 'c') {
                    tableModel.addRow(new Object[]{estado, simbolo, cima, "q_count",
                            "2", "Ignorar símbolos no a."});
                } else {
                    resultadoLabel.setText("❌ Carácter inválido: '" + simbolo + "'"); return;
                }
            }
        }

        // Transición al estado de verificación
        while (!pila.isEmpty() && pila.peek() == 'A') {
            tableModel.addRow(new Object[]{"q_count", "ε", pila.peek(), "q_verify",
                    "1", "Iniciar verificación."});
            pila.pop();
            break;
        }

        // Verificación
        int contador = 1;
        while (!pila.isEmpty() && pila.peek() == 'A') {
            tableModel.addRow(new Object[]{"q_verify", "ε", pila.peek(), "q_verify",
                    "1", "Desapilar y contar."});
            pila.pop();
            contador++;
        }

        // Estado final
        if (!pila.isEmpty() && pila.peek() == 'Z') {
            tableModel.addRow(new Object[]{"q_verify", "ε", "Z", "q_accept",
                    "2", "Si el conteo es Fibonacci, aceptar."});

            if (isFibonacci(contador / 2)) {
                resultadoLabel.setText(" Cadena ACEPTADA: " + (contador / 2) + " a's (Fibonacci)");
            } else {
                resultadoLabel.setText(" Cadena RECHAZADA: " + (contador / 2) + " a's no es Fibonacci.");
            
       
    }
        }
    }

    private boolean isFibonacci(int n) {
        int x1 = 5 * n * n + 4;
        int x2 = 5 * n * n - 4;
        return isPerfectSquare(x1) || isPerfectSquare(x2);
    }

    private boolean isPerfectSquare(int x) {
        int s = (int) Math.sqrt(x);
        return s * s == x;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pila_Fibonacci::new);
    }
}  