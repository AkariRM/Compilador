package compiladorcontrol;

import java.util.Stack;

public class AnalizadorSintactico {

	// Tabla predictiva
	private String[][] tabla_Predictiva = {
			// id num litcad litc true false ( ) ! * / + - < > <= >= == != && || $
			/* L */ { "R L'", "R L'", "R L'", "R L'", "R L'", "R L'", "R L'", "saltar", "R L'", "saltar", "saltar",
					"saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar",
					"saltar" },
			/* L' */ { "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "ε", "saltar", "ε", "ε",
					"ε", "ε", "ε", "ε", "ε", "ε", "ε", "and R L'", "or R L'", "ε" },
			/* R */ { "E R'", "E R'", "E R'", "E R'", "E R'", "E R'", "E R'", "saltar", "E R'", "saltar", "saltar",
					"saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar",
					"saltar" },
			/* R' */ { "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "< E", "> E", "<= E", ">= E",
					"== E", "!= E", "ε", "ε", "ε" },
			/* E */ { "T E'", "T E'", "T E'", "T E'", "T E'", "T E'", "T E'", "saltar", "T E'", "saltar", "saltar",
					"saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar",
					"saltar" },
			/* E' */ { "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "+ T E'", "- T E'", "ε", "ε", "ε", "ε",
					"ε", "ε", "ε", "ε" },
			/* T */ { "F T'", "F T'", "F T'", "F T'", "F T'", "F T'", "F T'", "saltar", "F T'", "saltar", "saltar",
					"saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar",
					"saltar" },
			/* T' */ { "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "* F T'", "/ F T'", "ε", "ε", "ε", "ε", "ε", "ε",
					"ε", "ε", "ε", "ε" },
			/* F */ { "id", "num", "litcad", "litc", "true", "false", "( E )", "saltar", "! F", "saltar", "saltar",
					"saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar",
					"saltar" } };

	public boolean analizar(String[] entrada) {
		Stack<String> pila = new Stack<>();
		pila.push("L");
		int i = 0;
		String token = entrada[i];

		while (!pila.isEmpty()) {
			String cimaPila = pila.peek();
			if (esTerminal(cimaPila)) {
				if (cimaPila.equals(token)) {
					pila.pop();
					i++;
					if (i < entrada.length) {
						token = entrada[i];
					}
				} else {
					return false; // Recuperar el error
				}
			} else {
				// Buscar en la tabla
				int columna = obtenerIndiceTerminal(token);
				if (columna == -1) {
					return false; // Recuperar el error
				}
				String produccion = tabla_Predictiva[getIndiceNoTerminal(cimaPila)][columna];
				if (produccion.equals("ε") || produccion.equals("saltar")) {
					pila.pop(); // Salta ( sacarlo de la pila
				} else {
					pila.pop();
					// Poner en la pila la producción en orden inverso (lo de la auxiliar)
					String[] produccionTokens = produccion.split(" ");
					for (int j = produccionTokens.length - 1; j >= 0; j--) {
						if (!produccionTokens[j].equals("ε")) {
							pila.push(produccionTokens[j]);
						}
					}
				}
			}
		}

		return i == entrada.length; // Si la pila está vacía y la entrada está procesada
	}

	// Comprobar que si e sun terminal
	private boolean esTerminal(String simbolo) {
		String[] terminales = { "id", "num", "litcad", "litc", "true", "false", "(", ")", "!", "*", "/", "+", "-", "<",
				">", "<=", ">=", "==", "!=", "&&", "||", "$" };
		for (String t : terminales) {
			if (t.equals(simbolo)) {
				return true;
			}
		}
		return false;
	}

	// Obtener el indice de la tabla
	private int obtenerIndiceTerminal(String token) {
		String[] terminales = {
				  "id", "num", "litcad", "litc", "true", "false", "(", ")", "!", "*", "/", "+", "-",
				  "<", ">", "<=", ">=", "==", "!=", "&&", "||", "$"
				}; // <-- Esto da exactamente 22 elementos


		for (int i = 0; i < terminales.length; i++) {
			if (terminales[i].equals(token)) {
				return i;
			}
		}
		return -1; // No encontrado
	}

	// Método para obtener el índice del no terminal en la tabla
	private int getIndiceNoTerminal(String noTerminal) {
		String[] noTerminales = { "L", "L'", "R", "R'", "E", "E'", "T", "T'", "F" };
		for (int i = 0; i < noTerminales.length; i++) {
			if (noTerminales[i].equals(noTerminal)) {
				return i;
			}
		}
		return -1; // No encontrado
	}

	// Metodo de prueba
	public static void main(String[] args) {
		AnalizadorSintactico analizador = new AnalizadorSintactico();

		String[] entrada = { "id", "+", "num", "*", "true", "$" };

		if (analizador.analizar(entrada)) {
			System.out.println("Entrada válida");
		} else {
			System.out.println("Error sintáctico");
		}
	}
}
