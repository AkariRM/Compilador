package compiladorPruebas;

import java.util.*;

public class TablaLL1 {
	private final Map<String, Map<String, String>> tabla;

	public TablaLL1() {
		tabla = new HashMap<>();

		agregar("L", "id", "RL'");
		agregar("L", "num", "RL'");
		agregar("L", "letcad", "RL'");
		agregar("L", "litcar", "RL'");
		agregar("L", "true", "RL'");
		agregar("L", "false", "RL'");
		agregar("L", "(", "RL'");

		agregar("L'", ")", "ε");
		agregar("L'", "$", "ε");
		agregar("L'", "&&", "&&RL'");
		agregar("L'", "\"", "\"RL'");

		agregar("R", "id", "ER'");
		agregar("R", "num", "ER'");
		agregar("R", "letcad", "ER'");
		agregar("R", "litcar", "ER'");
		agregar("R", "true", "ER'");
		agregar("R", "false", "ER'");
		agregar("R", "(", "ER'");

		agregar("R'", "<", "<E");
		agregar("R'", ">", ">E");
		agregar("R'", "<=", "<=E");
		agregar("R'", ">=", ">=E");
		agregar("R'", "==", "==E");
		agregar("R'", "!=", "!=E");
		agregar("R'", "$", "ε");

		agregar("E", "id", "TE'");
		agregar("E", "num", "TE'");
		agregar("E", "letcad", "TE'");
		agregar("E", "litcar", "TE'");
		agregar("E", "true", "TE'");
		agregar("E", "false", "TE'");
		agregar("E", "(", "TE'");

		agregar("E'", "+", "+TE'");
		agregar("E'", "-", "-TE'");
		agregar("E'", "$", "ε");

		agregar("T", "id", "FT'");
		agregar("T", "num", "FT'");
		agregar("T", "letcad", "FT'");
		agregar("T", "litcar", "FT'");
		agregar("T", "true", "FT'");
		agregar("T", "false", "FT'");
		agregar("T", "(", "FT'");

		agregar("T'", "*", "*FT'");
		agregar("T'", "/", "/FT'");
		agregar("T'", "$", "ε");

		agregar("F", "id", "id");
		agregar("F", "num", "num");
		agregar("F", "letcad", "letcad");
		agregar("F", "litcar", "litcar");
		agregar("F", "true", "true");
		agregar("F", "false", "false");
		agregar("F", "(", "(L)");

		
	}

	private void agregar(String noTerminal, String terminal, String produccion) {
		tabla.computeIfAbsent(noTerminal, k -> new HashMap<>()).put(terminal, produccion);
	}

	public String obtenerProduccion(String noTerminal, String terminal) {
		Map<String, String> fila = tabla.get(noTerminal);
		return fila != null ? fila.get(terminal) : null;
	}
}
