package compiladorAlexis;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.undo.*;

public class CompiladorUI {
	private JTextArea areaTexto;
	private JTextArea areaComponentes;
	private JTextArea areaResultados;
	private JTextArea numeracionLineas;
	private File archivoActual = null;
	private JFrame ventana;
	private UndoManager undoManager = new UndoManager();

	public void CrearMostrar() {
		ventana = new JFrame("Compilador - Nuevo Archivo");
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setSize(800, 600);
		ventana.setLayout(new BorderLayout());

		// Barra de menus
		JMenuBar barraMenu = new JMenuBar();

		// Menu de Archivo
		JMenu menuArchivo = new JMenu("Archivo");
		JMenuItem abrirArchivo = new JMenuItem("Abrir");
		abrirArchivo.addActionListener(e -> abrirArchivo());
		abrirArchivo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		JMenuItem guardarArchivo = new JMenuItem("Guardar");
		guardarArchivo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		guardarArchivo.addActionListener(e -> guardarArchivo());
		JMenuItem guardarComo = new JMenuItem("Guardar como...");
		guardarComo.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		guardarComo.addActionListener(e -> guardarArchivoComo());
		JMenuItem nuevoArchivo = new JMenuItem("Nuevo");
		nuevoArchivo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		nuevoArchivo.addActionListener(e -> nuevoArchivo());

		menuArchivo.add(abrirArchivo);
		menuArchivo.add(guardarArchivo);
		menuArchivo.add(guardarComo);
		menuArchivo.add(nuevoArchivo);

		// Menu Acciones
		JMenu menuAcciones = new JMenu("Acciones");

		JMenuItem analizar = new JMenuItem("Analizar");
		analizar.addActionListener(e -> Lex(areaTexto.getText()));
		analizar.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		JMenuItem compilar = new JMenuItem("Compilar");
		compilar.addActionListener(e -> areaResultados.setText("Compilando..."));
		compilar.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		JMenuItem limpiar = new JMenuItem("Limpiar");
		limpiar.addActionListener(e -> {
			int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres borrar todo?",
					"Confirmación", JOptionPane.YES_NO_OPTION);
			if (confirmacion == JOptionPane.YES_OPTION) {
				areaTexto.setText("");
				areaComponentes.setText("");
			}
		});
		limpiar.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

		menuAcciones.add(analizar);
		menuAcciones.add(compilar);
		menuAcciones.add(limpiar);

		JMenu herramientas = new JMenu("Herramientas");

		JMenuItem rehacer = new JMenuItem("Rehacer");
		rehacer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		rehacer.addActionListener(e -> rehacer());
		JMenuItem deshacer = new JMenuItem("Deshacer");
		deshacer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		deshacer.addActionListener(e -> deshacer());
		JMenuItem copiar = new JMenuItem("Copiar");
		copiar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		copiar.addActionListener(e -> Copiar());
		JMenuItem pegar = new JMenuItem("Pegar");
		pegar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		pegar.addActionListener(e -> Pegar());
		JMenuItem cortar = new JMenuItem("Cortar");
		cortar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		cortar.addActionListener(e -> Cortar());
		JMenuItem todo = new JMenuItem("Seleccionar todo");
		todo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		todo.addActionListener(e -> Todo());

		herramientas.add(rehacer);
		herramientas.add(deshacer);
		herramientas.add(copiar);
		herramientas.add(cortar);
		herramientas.add(pegar);
		herramientas.add(todo);

		barraMenu.add(menuArchivo);
		barraMenu.add(menuAcciones);
		barraMenu.add(herramientas);

		ventana.setJMenuBar(barraMenu);

		// Barra de herramientas
		JToolBar barraHerramientas = new JToolBar();
		barraHerramientas.setFloatable(false);

		// Boton abrir
		ImageIcon iconoAbrir = cargarIcono("img/abrir.png");
		JButton botonAbrir = new JButton("Abrir", iconoAbrir);
		botonAbrir.addActionListener(e -> abrirArchivo());
		barraHerramientas.add(botonAbrir);

		// Botón Guardar
		ImageIcon iconoGuardar = cargarIcono("img/guardar.png");
		JButton botonGuardar = new JButton("Guardar", iconoGuardar);
		botonGuardar.addActionListener(e -> guardarArchivo());
		barraHerramientas.add(botonGuardar);

		// Botón Analizar
		ImageIcon iconoAnalizar = cargarIcono("img/analizar.png");
		JButton botonAnalizar = new JButton("Lexico", iconoAnalizar);
		botonAnalizar.addActionListener(e -> Lex(areaTexto.getText()));
		barraHerramientas.add(botonAnalizar);

		// Botón Compilar
		ImageIcon iconoCompilar = cargarIcono("img/compilar.png");
		JButton botonCompilar = new JButton("Analisis Completo", iconoCompilar);
		botonCompilar.addActionListener(e -> {
			String codigo = areaTexto.getText();
			Sin(codigo);
		});
		barraHerramientas.add(botonCompilar);

		ventana.add(barraHerramientas, BorderLayout.NORTH);

		// Panel principal
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new GridBagLayout());
		ventana.add(panelPrincipal, BorderLayout.CENTER);

		GridBagConstraints restricciones = new GridBagConstraints();
		restricciones.fill = GridBagConstraints.BOTH;
		restricciones.insets = new Insets(5, 5, 5, 5);

		// Numeracion de lineas
		numeracionLineas = new JTextArea("1");
		numeracionLineas.setEditable(false);
		numeracionLineas.setBackground(Color.LIGHT_GRAY);
		numeracionLineas.setFont(new Font("Monospaced", Font.PLAIN, 12));

		// Area de texto
		areaTexto = new JTextArea();
		areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));

		areaTexto.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				actualizarNumeracion();
			}

			public void removeUpdate(DocumentEvent e) {
				actualizarNumeracion();
			}

			public void changedUpdate(DocumentEvent e) {
				actualizarNumeracion();
			}
		});

		areaTexto.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

		JScrollPane desplazamientoTexto = new JScrollPane(areaTexto);
		desplazamientoTexto.setRowHeaderView(numeracionLineas);

		restricciones.gridx = 0;
		restricciones.gridy = 0;
		restricciones.gridwidth = 2;
		restricciones.gridheight = 2;
		restricciones.weightx = 0.7;
		restricciones.weighty = 1.0;
		panelPrincipal.add(desplazamientoTexto, restricciones);

		// Panel con título para Componentes
		JPanel panelComponentes = new JPanel(new BorderLayout());

		// Encabezado con los títulos
		JPanel encabezadoComponentes = new JPanel(new GridLayout(1, 4));
		JLabel labelToken = new JLabel("TOKEN", SwingConstants.CENTER);
		JLabel labelTipo = new JLabel("TIPO", SwingConstants.CENTER);
		JLabel labelFila = new JLabel("FILA", SwingConstants.CENTER);
		JLabel labelColumna = new JLabel("COLUMNA", SwingConstants.CENTER);

		Font fontEncabezado = new Font("Monospaced", Font.BOLD, 12);
		labelToken.setFont(fontEncabezado);
		labelTipo.setFont(fontEncabezado);
		labelFila.setFont(fontEncabezado);
		labelColumna.setFont(fontEncabezado);

		encabezadoComponentes.add(labelToken);
		encabezadoComponentes.add(labelTipo);
		encabezadoComponentes.add(labelFila);
		encabezadoComponentes.add(labelColumna);

		// Área de texto para componentes
		areaComponentes = new JTextArea();
		areaComponentes.setEditable(false);
		areaComponentes.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JScrollPane desplazamientoComponentes = new JScrollPane(areaComponentes);

		// Agregar encabezado y área al panel principal
		panelComponentes.add(encabezadoComponentes, BorderLayout.NORTH);
		panelComponentes.add(desplazamientoComponentes, BorderLayout.CENTER);

		restricciones.gridx = 2;
		restricciones.gridy = 0;
		restricciones.gridwidth = 1;
		restricciones.gridheight = 1;
		restricciones.weightx = 0.3;
		restricciones.weighty = 0.5;
		panelPrincipal.add(panelComponentes, restricciones);

		areaResultados = new JTextArea();
		areaResultados.setEditable(false);
		JScrollPane desplazamientoResultados = new JScrollPane(areaResultados);
		restricciones.gridx = 2;
		restricciones.gridy = 1;
		restricciones.gridwidth = 1;
		restricciones.gridheight = 1;
		restricciones.weightx = 0.3;
		restricciones.weighty = 0.5;
		panelPrincipal.add(desplazamientoResultados, restricciones);

		ventana.setVisible(true);
	}

	private void Lex(String entrada) {
		areaComponentes.setText("");
		areaResultados.setText("");

		AnalizadorLexico analizador = new AnalizadorLexico();
		analizador.analizar(entrada);

		// Mostrar tokens
		areaComponentes.setText(analizador.getResultadoFormateado());

		// Mostrar errores de manera más destacada
		if (!analizador.getErrores().isEmpty()) {
			StringBuilder erroresStr = new StringBuilder("=== ERRORES ===\n");
			for (ErrorCompilacion error : analizador.getErrores()) {
				erroresStr.append("• ").append(error).append("\n");
			}
			areaResultados.setForeground(Color.RED);
			areaResultados.setText(erroresStr.toString());
		} else {
			areaResultados.setForeground(Color.BLACK);
			areaResultados.setText("✓ Análisis léxico completado sin errores");
		}
	}

	private void Sin(String entrada) {
		areaComponentes.setText("");
		areaResultados.setText("");

		// 1. Análisis Léxico
		AnalizadorLexico analizador = new AnalizadorLexico();
		analizador.analizar(entrada);

		// Mostrar tokens en el área de componentes
		areaComponentes.setText(analizador.getResultadoFormateado());

		// Procesar errores léxicos
		if (!analizador.getErrores().isEmpty()) {
			StringBuilder erroresStr = new StringBuilder("=== ERRORES LÉXICOS ===\n");
			for (ErrorCompilacion error : analizador.getErrores()) {
				erroresStr.append("• ").append(error).append("\n");
			}
			areaResultados.setForeground(Color.RED);
			areaResultados.setText(erroresStr.toString());
			return; // Detener si hay errores léxicos
		}

		// 2. Análisis Sintáctico (solo si no hay errores léxicos)
		AnalizadorSintactico sintactico = new AnalizadorSintactico(analizador.getTokens());
		boolean sintaxisCorrecta = sintactico.analizar();

		// Mostrar resultados sintácticos
		if (!sintaxisCorrecta) {
			StringBuilder erroresSintacticos = new StringBuilder("=== ERRORES SINTÁCTICOS ===\n");
			for (String error : sintactico.getErrores()) {
				erroresSintacticos.append("• ").append(error).append("\n");
			}
			areaResultados.setForeground(Color.RED);
			areaResultados.setText(erroresSintacticos.toString());
		} else {
			areaResultados.setForeground(new Color(0, 128, 0)); // Verde oscuro
			areaResultados.setText("✓ Análisis sintáctico completado sin errores");
		}
	}

	private ImageIcon cargarIcono(String ruta) {
		ImageIcon iconoOriginal = new ImageIcon(ruta);
		Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		return new ImageIcon(imagenEscalada);
	}

	private void actualizarTitulo() {
		if (archivoActual != null) {
			ventana.setTitle("Compilador - " + archivoActual.getName());
		} else {
			ventana.setTitle("Compilador - Archivo sin Guardar");
		}
	}

	private void actualizarNumeracion() {
		int totalLineas = areaTexto.getLineCount();
		StringBuilder numeros = new StringBuilder();
		for (int i = 1; i <= totalLineas; i++) {
			numeros.append(i).append("\n");
		}
		numeracionLineas.setText(numeros.toString());
	}

	private void abrirArchivo() {
		JFileChooser selectorArchivos = new JFileChooser();
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos ARM", "arm");
		selectorArchivos.setFileFilter(filtro);

		int resultado = selectorArchivos.showOpenDialog(null);

		if (resultado == JFileChooser.APPROVE_OPTION) {
			archivoActual = selectorArchivos.getSelectedFile();
			try (BufferedReader lector = new BufferedReader(new FileReader(archivoActual))) {
				areaTexto.setText("");
				String linea;
				while ((linea = lector.readLine()) != null) {
					areaTexto.append(linea + "\n");
				}
				actualizarNumeracion();
				actualizarTitulo();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void guardarArchivo() {
		if (archivoActual != null) {
			escribirEnArchivo(archivoActual);
		} else {
			guardarArchivoComo();
		}
	}

	private void guardarArchivoComo() {
		JFileChooser selectorArchivos = new JFileChooser();
		selectorArchivos.setDialogTitle("Guardar archivo");
		selectorArchivos.setFileFilter(new FileNameExtensionFilter("Archivos ARM (*.arm)", "arm"));

		int resultado = selectorArchivos.showSaveDialog(null);

		if (resultado == JFileChooser.APPROVE_OPTION) {
			archivoActual = selectorArchivos.getSelectedFile();

			if (!archivoActual.getName().toLowerCase().endsWith(".arm")) {
				archivoActual = new File(archivoActual.getAbsolutePath() + ".arm");
			}

			escribirEnArchivo(archivoActual);
		}
	}

	private void nuevoArchivo() {
		Object[] opciones = { "Guardar", "No guardar", "Cancelar" };

		int confirmacion = JOptionPane.showOptionDialog(null,
				"¿Quieres crear un nuevo archivo? Se perderán los cambios no guardados.", "Nuevo Archivo",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);

		if (confirmacion == 0) {
			int guardarConfirmacion = JOptionPane.showConfirmDialog(null,
					"¿Quieres guardar los cambios antes de crear un nuevo archivo?", "Guardar Archivo",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (guardarConfirmacion == JOptionPane.YES_OPTION) {
				guardarArchivo();
			}

			areaTexto.setText("");
			archivoActual = null;
			actualizarTitulo();
		} else if (confirmacion == 1) {
			areaTexto.setText("");
			archivoActual = null;
			actualizarTitulo();
		}
	}

	private void escribirEnArchivo(File archivo) {
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
			escritor.write(areaTexto.getText());
			JOptionPane.showMessageDialog(null, "Archivo guardado correctamente", "Éxito",
					JOptionPane.INFORMATION_MESSAGE);
			actualizarTitulo();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deshacer() {
		if (undoManager.canUndo())
			undoManager.undo();
	}

	private void rehacer() {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}

	private void Cortar() {
		areaTexto.cut();
	}

	private void Copiar() {
		areaTexto.copy();
	}

	private void Pegar() {
		areaTexto.paste();
	}

	private void Todo() {
		areaTexto.selectAll();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			CompiladorUI compiladorUI = new CompiladorUI();
			compiladorUI.CrearMostrar();
		});
	}
}