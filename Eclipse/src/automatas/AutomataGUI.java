package automatas;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class AutomataGUI {
	private JFrame frame;
	private JTextField txtAlfabeto, txtEstados, txtFinales, txtCadena;
	private JTable tablaTransiciones;
	private DefaultTableModel modeloTabla;
	private int estados;
	private String alfabeto;
	private Set<Integer> estadosFinales = new HashSet<>();
	private JTextArea txtAreaTransiciones;

	public AutomataGUI() {
		frame = new JFrame("AutomataV4");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setLayout(new BorderLayout());

		JPanel panelSuperior = new JPanel(new GridLayout(4, 3));
		panelSuperior.add(new JLabel("Alfabeto(Simbolos Alfanumericos):"));
		txtAlfabeto = new JTextField();
		panelSuperior.add(txtAlfabeto);
		JButton btnValidarAlfabeto = new JButton("✔ Validar");
		panelSuperior.add(btnValidarAlfabeto);

		panelSuperior.add(new JLabel("Cantidad de Estados(ingresa el total de estados):"));
		txtEstados = new JTextField();
		panelSuperior.add(txtEstados);
		JButton btnValidarEstados = new JButton("✔ Validar");
		panelSuperior.add(btnValidarEstados);

		panelSuperior.add(new JLabel("Estados Finales:(Ingresa solo numeros separados por coma)"));
		txtFinales = new JTextField();
		panelSuperior.add(txtFinales);
		JButton btnValidarFinales = new JButton("✔ Validar");
		panelSuperior.add(btnValidarFinales);

		JButton btnGenerar = new JButton("Generar Tabla");
		panelSuperior.add(btnGenerar);

		frame.add(panelSuperior, BorderLayout.NORTH);

		modeloTabla = new DefaultTableModel();
		tablaTransiciones = new JTable(modeloTabla);
		frame.add(new JScrollPane(tablaTransiciones), BorderLayout.CENTER);

		JPanel panelInferior = new JPanel(new FlowLayout());
		txtCadena = new JTextField(20);
		panelInferior.add(txtCadena);
		JButton btnValidar = new JButton("Validar Cadena");
		panelInferior.add(btnValidar);

		txtAreaTransiciones = new JTextArea(10, 50);
		txtAreaTransiciones.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txtAreaTransiciones);
		panelInferior.add(scrollPane);

		frame.add(panelInferior, BorderLayout.SOUTH);

		btnValidarAlfabeto.addActionListener(e -> validarAlfabeto());
		btnValidarEstados.addActionListener(e -> validarEstados());
		btnValidarFinales.addActionListener(e -> validarFinales());
		btnGenerar.addActionListener(e -> generarTabla());
		btnValidar.addActionListener(e -> validarCadena());

		txtEstados.setEnabled(false);
		txtFinales.setEnabled(false);
		frame.setVisible(true);
	}

	private void validarAlfabeto() {
		alfabeto = txtAlfabeto.getText().replaceAll("[^a-zA-Z0-9]", "");
		Set<Character> alfabetoSet = new HashSet<>();
		for (char c : alfabeto.toCharArray()) {
			alfabetoSet.add(c);
		}
		alfabeto = "";
		for (char c : alfabetoSet) {
			alfabeto += c;
		}

		txtAlfabeto.setText(alfabeto);

		if (alfabeto.isEmpty()) {
			JOptionPane.showMessageDialog(frame,
					"El alfabeto no puede estar vacío y debe contener solo caracteres alfanuméricos.", "<-<",
					JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(frame, "Alfabeto válido: " + alfabeto, "UwU",
					JOptionPane.INFORMATION_MESSAGE);
			txtEstados.setEnabled(true);
		}
	}

	private void validarEstados() {
		try {
			estados = Integer.parseInt(txtEstados.getText());
			if (estados < 1) {
				throw new NumberFormatException();
			}
			JOptionPane.showMessageDialog(frame, "Cantidad de estados válida.", "UwU", JOptionPane.INFORMATION_MESSAGE);
			txtFinales.setEnabled(true);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Debe ingresar un número entero mayor a 0.", "D:<",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void validarFinales() {
		try {
			estadosFinales.clear();
			for (String f : txtFinales.getText().split(",")) {
				int estadoFinal = Integer.parseInt(f.trim());
				if (estadoFinal < 0 || estadoFinal >= estados) {
					throw new NumberFormatException();
				}
				estadosFinales.add(estadoFinal);
			}
			JOptionPane.showMessageDialog(frame, "Estados finales válidos.", "UwU", JOptionPane.INFORMATION_MESSAGE);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame,
					"Ingrese valores numéricos separados por coma dentro del rango válido.", "D:<",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void generarTabla() {
		JOptionPane.showMessageDialog(frame,
				"Llena la taba solo con numeros, dejar una celda vacia se considera sin estado de transicion.", "Aviso",
				JOptionPane.ERROR_MESSAGE);
		alfabeto = txtAlfabeto.getText().replaceAll("[^a-zA-Z0-9]", "");
		Set<Character> alfabetoSet = new HashSet<>();
		for (char c : alfabeto.toCharArray()) {
			alfabetoSet.add(c);
		}
		alfabeto = "";
		for (char c : alfabetoSet) {
			alfabeto += c;
		}
		txtAlfabeto.setText(alfabeto);

		try {
			estados = Integer.parseInt(txtEstados.getText());
			if (estados < 1) {
				JOptionPane.showMessageDialog(frame, "La cantidad de estados debe ser mayor a 0 y entero.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String[] finales = txtFinales.getText().split(",");
			estadosFinales.clear();
			for (String f : finales) {
				int estadoFinal = Integer.parseInt(f.trim());
				if (estadoFinal < 0 || estadoFinal >= estados) {
					JOptionPane.showMessageDialog(
							frame, "Estado final inválido: " + estadoFinal
									+ ". Debe ser un numero entero y estar entre 0 y " + (estados - 1) + ".",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				estadosFinales.add(estadoFinal);
			}

			modeloTabla.setColumnCount(alfabeto.length() + 1);
			modeloTabla.setRowCount(estados);

			modeloTabla.setColumnIdentifiers(getColumnNames());

			for (int i = 0; i < estados; i++) {
				modeloTabla.setValueAt("q" + i, i, 0);
			}

			modeloTabla.addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					int row = e.getFirstRow();
					int column = e.getColumn();

					if (column > 0) {
						Object value = modeloTabla.getValueAt(row, column);

						try {
							int estadoDestino = Integer.parseInt(value.toString().trim());

							if (estadoDestino < 0 || estadoDestino >= estados) {
								throw new NumberFormatException();
							}

						} catch (NumberFormatException ex) {
							SwingUtilities.invokeLater(() -> {
								JOptionPane.showMessageDialog(frame,
										"Valor inválido. El valor debe ser un número entero entre 0 y " + (estados - 1)
												+ ".",
										"Error", JOptionPane.ERROR_MESSAGE);

								modeloTabla.removeTableModelListener(this);

								modeloTabla.setValueAt("", row, column);

								modeloTabla.addTableModelListener(this);
							});
						}
					}
				}
			});
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(frame,
					"Error en los datos ingresados. Asegúrese de ingresar solo números enteros.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private String[] getColumnNames() {
		String[] nombres = new String[alfabeto.length() + 1];
		nombres[0] = "Estados";
		for (int i = 0; i < alfabeto.length(); i++) {
			nombres[i + 1] = String.valueOf(alfabeto.charAt(i));
		}
		return nombres;
	}

	private void validarCadena() {
	    String cadena = txtCadena.getText();
	    int estadoActual = 0;
	    StringBuilder transiciones = new StringBuilder();
	    
	    transiciones.append("Transiciones:\n");

	    for (char simbolo : cadena.toCharArray()) {
	        int simboloIndice = alfabeto.indexOf(simbolo);
	        if (simboloIndice == -1) {
	            transiciones.append("Cadena inválida: símbolo no reconocido.\n");
	            txtAreaTransiciones.setText(transiciones.toString());
	            return;
	        }

	        Object valor = modeloTabla.getValueAt(estadoActual, simboloIndice + 1);
	        if (valor == null || valor.toString().isEmpty()) {
	            estadoActual = estados - 1; // Estado de error
	        } else {
	            estadoActual = Integer.parseInt(valor.toString());
	        }

	        transiciones.append("Estado actual: q").append(estadoActual)
	                    .append(" con símbolo '").append(simbolo)
	                    .append("' -> Estado siguiente: q").append(estadoActual).append("\n");

	        if (estadoActual == estados - 1) { // Si se llega al estado de error, la cadena se rechaza
	            transiciones.append("Cadena rechazada: Se alcanzó el estado de error.\n");
	            txtAreaTransiciones.setText(transiciones.toString());
	            return;
	        }
	    }

	    if (estadosFinales.contains(estadoActual)) {
	        transiciones.append("Cadena aceptada.\n");
	    } else {
	        transiciones.append("Cadena rechazada.\n");
	    }

	    txtAreaTransiciones.setText(transiciones.toString());
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(AutomataGUI::new);
	}
}