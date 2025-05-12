package compilador;

import java.util.*;

public class AnalizadorSintactico {

	private Deque<String> pila = new ArrayDeque<>();
	private List<Token> tokens;
	private int indiceToken = 0;
	private List<String> errores = new ArrayList<>();

	public AnalizadorSintactico(List<Token> tokens) {
		this.tokens = tokens;
		pila.push("L"); // símbolo inicial
	}

	public boolean analizar() {
		while (!pila.isEmpty()) {
			String cima = pila.peek();
			String tokenActual = getTokenActual();

			if (cima.equals(tokenActual)) {
				pila.pop();
				indiceToken++;
			} else if (esTerminal(cima)) {
				reportarError("Se esperaba '" + cima + "' pero se encontró '" + tokenActual + "'");
				pila.pop();
			} else {
				String produccion = obtenerProduccion(cima, tokenActual);
				if (produccion == null) {
					reportarError("No hay producción para [" + cima + ", " + tokenActual + "]");
					pila.pop();
				} else {
					pila.pop();
					if (!produccion.equals("Ɛ")) {
						List<String> simbolos = Arrays.asList(produccion.split(" "));
						for (int i = simbolos.size() - 1; i >= 0; i--) {
							pila.push(simbolos.get(i));
						}
					}
				}
			}

			if (indiceToken >= tokens.size()) {
				break;
			}
		}
		return errores.isEmpty();
	}

	private String getTokenActual() {
		if (indiceToken >= tokens.size())
			return "$";
		Token token = tokens.get(indiceToken);
		return token.getTipo().toString(); // Se usa el tipo del token (como "id", "num", etc.)
	}

	private void reportarError(String mensaje) {
		errores.add("Error sintáctico: " + mensaje);
	}

	private boolean esTerminal(String simbolo) {
		return !Character.isUpperCase(simbolo.charAt(0));
	}

	private String obtenerProduccion(String noTerminal, String terminal) {
		Map<String, String> fila = tablaPredictiva.get(noTerminal);
		if (fila != null) {
			return fila.getOrDefault(terminal, null);
		}
		return null;
	}

	private static final Map<String, Map<String, String>> tablaPredictiva = crearTabla();

	private static Map<String, Map<String, String>> crearTabla() {
		Map<String, Map<String, String>> tabla = new HashMap<>();

		tabla.put("L", map("id", "R L'", "num", "R L'", "litcad", "R L'", "litc", "R L'", "true", "R L'", "false",
				"R L'", "(", "R L'", "!", "R L'", "write", "R L'", "if", "R L'", "$", "Ɛ"));

		tabla.put("L'", map("&&", "&& R L'", "||", "|| R L'", "'", "'' RL'", "(", "( R L'", ")", "Ɛ", "*", "Ɛ", "/",
				"Ɛ", "+", "Ɛ", "-", "Ɛ", "<", "Ɛ", ">", "Ɛ", "<=", "Ɛ", ">=", "Ɛ", "==", "Ɛ", "!=", "Ɛ", "$", "Ɛ"));

		tabla.put("R", map("id", "E R'", "num", "E R'", "litcad", "E R'", "litc", "E R'", "true", "E R'", "false",
				"E R'", "(", "E R'", "!", "E R'", "write", "E R'", "if", "E R'"));

		tabla.put("R'", map(">", "> E", "<", "< E", ">=", ">= E", "<=", "<= E", "==", "== E", "!=", "!= E", "(", "( E",
				")", "Ɛ", "*", "Ɛ", "/", "Ɛ", "+", "Ɛ", "-", "Ɛ", "&&", "Ɛ", "||", "Ɛ", "'", "Ɛ", "$", "Ɛ"));

		tabla.put("E", map("id", "T E'", "num", "T E'", "litcad", "T E'", "litc", "T E'", "true", "T E'", "false",
				"T E'", "(", "T E'", "!", "T E'", "write", "T E'", "if", "T E'"));

		tabla.put("E'", map("+", "+ T E'", "-", "- T E'", "(", "( T E'", ")", "Ɛ", "*", "Ɛ", "/", "Ɛ", ">", "Ɛ", "<",
				"Ɛ", ">=", "Ɛ", "<=", "Ɛ", "==", "Ɛ", "!=", "Ɛ", "&&", "Ɛ", "||", "Ɛ", "'", "Ɛ", "$", "Ɛ"));

		tabla.put("T", map("id", "F T'", "num", "F T'", "litcad", "F T'", "litc", "F T'", "true", "F T'", "false",
				"F T'", "(", "F T'", "!", "F T'", "write", "F T'", "if", "F T'"));

		tabla.put("T'", map("*", "* F T'", "/", "/ F T'", "(", "( F T'", ")", "Ɛ", "+", "Ɛ", "-", "Ɛ", ">", "Ɛ", "<",
				"Ɛ", ">=", "Ɛ", "<=", "Ɛ", "==", "Ɛ", "!=", "Ɛ", "&&", "Ɛ", "||", "Ɛ", "'", "Ɛ", "$", "Ɛ"));

		tabla.put("F", map("id", "id", "num", "num", "litcad", "litcad", "litc", "litc", "true", "true", "false",
				"false", "(", "( L )", "!", "! L", "write", "write", "if", "if"));

		tabla.put("bloque", map("{", "{ sentencias }"));

		tabla.put("sentencia",
				map("id", "id = L ;", "if", "if ( L ) then bloque sigif", "write", "write ( lista_arg ) ;"));

		tabla.put("sentencias", map("id", "sentencia sentencias", "if", "sentencia sentencias", "write",
				"sentencia sentencias", "}", "Ɛ"));

		tabla.put("sigif", map("id", "Ɛ", "if", "Ɛ", "else", "else bloque", "write", "Ɛ", "}", "Ɛ"));

		tabla.put("lista_arg",
				map("id", "L lista_arg'", "num", "L lista_arg'", "(", "L lista_arg'", "true", "L lista_arg'", "false",
						"L lista_arg'", "litcad", "L lista_arg'", "litc", "L lista_arg'", "!", "L lista_arg'", "write",
						"L lista_arg'"));

		tabla.put("lista_arg'", map(",", ", L lista_arg'", ")", "Ɛ"));

		return tabla;
	}

	private static Map<String, String> map(String... entries) {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < entries.length; i += 2) {
			map.put(entries[i], entries[i + 1]);
		}
		return map;
	}

	public List<String> getErrores() {
		return errores;
	}
}
