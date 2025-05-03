package automatas;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Stack;
import javax.swing.*;

public class AutomataPila {
	private JFrame frame;
	private JTextField cadena;
	private JTextArea validaciones;
	private JButton validarButton;
	private JLabel textoAdicional;

	int estadoAceptacion = 2;
	int estado;
	char simbolo;

	String entradas[] = { "z,$", "z,a", "a,a", "a,b", "b,b", "b,c", "a,c" };

	String acciones[][] = { 
			{ "-1", "0,0", "0,0", "1,0", "-1", "-1", "2,1" },
			{ "-1", "-1", "-1", "-1", "1,0", "2,1", "-1" }, 
			{ "2", "-1", "-1", "-1", "-1", "2,1", "2,1" }, };

	Stack<Character> pila = new Stack<>();

	public AutomataPila() {
		frame = new JFrame("Autómata de Pila");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLayout(new BorderLayout());

		JPanel panelSuperior = new JPanel(new GridLayout(5, 1));
		panelSuperior.add(new JLabel("Cadena a validar:"));
		cadena = new JTextField();
		panelSuperior.add(cadena);

		validarButton = new JButton("Validar Cadena");
		panelSuperior.add(validarButton);

		textoAdicional = new JLabel("Válidos: a^n b^m c^n+m  | n>0 m>=0");
		panelSuperior.add(textoAdicional);

		validaciones = new JTextArea(10, 30);
		validaciones.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(validaciones);

		frame.add(panelSuperior, BorderLayout.NORTH);
		frame.add(scrollPane, BorderLayout.CENTER);

		validarButton.addActionListener(e -> Evaluar(cadena.getText()));

		frame.setVisible(true);
	}

	private void Evaluar(String cad) {
		validaciones.setText("");
		validaciones.append("Cadena a validar: " + cad + "\n");
		pila.clear();
		pila.push('z');

		cad += "$";
		estado = 0;

		for (int i = 0; i < cad.length(); i++) {
			simbolo = cad.charAt(i);
			char tope = pila.isEmpty() ? '-' : pila.peek();
			String tupla = tope + "," + simbolo;

			int col = columnaEntrada(tupla);

			if (col == -1) {
				validaciones.append("\nCadena rechazada - Transición no definida para la tupla: " + tupla + "\n");
				return;
			}

			String accion = acciones[estado][col];
			if (accion.equals("-1")) {
				validaciones.append(
						"\nCadena rechazada - No hay transición desde estado " + estado + " con: " + tupla + "\n");
				return;
			}

			estado = Integer.parseInt(accion.substring(0, 1));

			switch (accion.charAt(2)) {
			case '0':
				validaciones.append("Apilando: " + simbolo + "\n");
				pila.push(simbolo);
				break;
			case '1':
				validaciones.append("Desapilando: " + pila.peek() + "\n");
				pila.pop();
				break;
			}
			validaciones.append("Estado actual: " + estado + ", Pila: " + pila.toString() + "\n");
		}

		validaciones.append("\nCadena acepatda\n");
	}

	private int columnaEntrada(String tupla) {
		for (int i = 0; i < entradas.length; i++) {
			if (entradas[i].equals(tupla))
				return i;
		}
		return -1;
	}

	public static void main(String[] args) {
		new AutomataPila();
	}
}