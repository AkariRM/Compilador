package compilador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AnalizadorSintactico {
	private final Deque<String> pila = new ArrayDeque<>();
	private final List<Token> tokens;
	private int indiceToken = 0;
	private final List<String> errores = new ArrayList<>();

	// Terminales en el orden exacto de las columnas de la tabla
	private static final String[] TERMINALES = { "inicio", "fin", "si", "(", ")", "{", "}", "si_no", "mientras", "para",
			";", "=", "entero", "decimal", "cadena", "booleano", "id", "num", "litcad", "verdadero", "falso", "mostrar",
			"leer", "&&", "||", "!", "<", ">", "<=", ">=", "==", "!=", "+", "-", "*", "/", "%", "$" };

	// No terminales en el orden exacto de las filas de la tabla
	private static final String[] NO_TERMINALES = { "PROGRAMA", "SENTENCIAS", "SENTENCIA", "DECLARACION", "ASIGNACION",
			"CONDICIONAL", "BUCLE", "IO", "EXPR", "EXPR_LOG", "LOG_OP", "EXPR_COMP", "COMP_OP", "EXPR_ARIT", "ARIT_OP",
			"TERM", "tipo" };

	private static final String[][] TABLA_PREDICTIVA = {
			// Columnas: inicio, fin, si, (, ), {, }, si_no, mientras, para, ;, =,
			// entero, decimal, cadena, booleano, id, num, litcad,
			// verdadero, falso, mostrar, leer, &&, ||, !, <, >, <=, >=, ==, !=,
			// +, -, *, /, %, $

			/* PROGRAMA */ { "inicio SENTENCIAS fin", null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null },

			/* SENTENCIAS */{ null, "ε", "SENTENCIA ; SENTENCIAS", null, null, null, "ε", null,
					"SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", null, null, "SENTENCIA ; SENTENCIAS",
					"SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS",
					"SENTENCIA ; SENTENCIAS", null, null, null, null, "SENTENCIA ; SENTENCIAS",
					"SENTENCIA ; SENTENCIAS", null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null },

			/* SENTENCIA */ { null, null, "CONDICIONAL", null, null, null, null, null, "BUCLE", "BUCLE", null, null,
					"DECLARACION", // Para 'entero'
					"DECLARACION", // Para 'decimal'
					"DECLARACION", // Para 'cadena'
					"DECLARACION", // Para 'booleano'
					"ASIGNACION", null, null, null, null, "IO", "IO", null, null, null, null, null, null, null, null,
					null, null, null, null, null, null },

			/* DECLARACION */ { null, null, null, null, null, null, null, null, null, null, null, null,
					"tipo id = EXPR", // Para 'entero'
					"tipo id = EXPR", // Para 'decimal'
					"tipo id = EXPR", // Para 'cadena'
					"tipo id = EXPR", // Para 'booleano'
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null },

			/* tipo */ { null, null, null, null, null, null, null, null, null, null, null, null, "entero", // Para
																											// 'entero'
					"decimal", // Para 'decimal'
					"cadena", // Para 'cadena'
					"booleano", // Para 'booleano'
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null },
			/* DECL_EXT */ { null, null, null, null, null, null, null, null, null, null, null, "= EXPR", null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null },

			/* ASIGNACION */ { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, "id = EXPR", null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null },

			/* CONDICIONAL */{ null, null, "si ( EXPR ) { SENTENCIAS } COND_EXT", null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null },

			/* COND_EXT */ { null, null, null, null, null, null, null, "si_no { SENTENCIAS }", null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null },

			/* BUCLE */ { null, null, null, null, null, null, null, null, "mientras ( EXPR ) { SENTENCIAS }",
					"para ( ASIGNACION ; EXPR ; ASIGNACION ) { SENTENCIAS }", null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null },

			/* IO */ { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, "mostrar EXPR", "leer tipo id", null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null },

			/* EXPR */ { null, null, "EXPR_LOG", "EXPR_LOG", null, null, null, null, "EXPR_LOG", "EXPR_LOG", null, null,
					null, null, null, null, "EXPR_LOG", "EXPR_LOG", "EXPR_LOG", "EXPR_LOG", "EXPR_LOG", null, null,
					null, null, "EXPR_LOG", null, null, null, null, null, null, null, null, null, null, null, null },

			/* EXPR_LOG */ { null, null, "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", null, null, null, null,
					"EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", null, null, null, null, null, null, "EXPR_COMP LOG_OP",
					"EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", null, null, null,
					null, "EXPR_COMP LOG_OP", null, null, null, null, null, null, null, null, null, null, null, null },

			/* LOG_OP */ { null, null, null, null, "ε", null, "ε", "ε", null, null, "ε", null, null, null, null, null,
					null, null, null, null, null, null, null, "&& EXPR_COMP LOG_OP", "|| EXPR_COMP LOG_OP", null, null,
					null, null, null, null, null, null, null, null, null, null, null },

			/* EXPR_COMP */ { null, null, "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", null, null, null, null,
					"EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", null, null, null, null, null, null, "EXPR_ARIT COMP_OP",
					"EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", null, null,
					null, null, "EXPR_ARIT COMP_OP", null, null, null, null, null, null, null, null, null, null, null,
					null },

			/* COMP_OP */ { null, null, null, null, "ε", null, "ε", "ε", null, null, "ε", null, null, null, null, null,
					null, null, null, null, null, null, null, "ε", "ε", null, "< EXPR_ARIT", "> EXPR_ARIT",
					"<= EXPR_ARIT", ">= EXPR_ARIT", "== EXPR_ARIT", "!= EXPR_ARIT", null, null, null, null, null,
					null },

			/* EXPR_ARIT */ { null, null, "TERM ARIT_OP", "TERM ARIT_OP", null, null, null, null, "TERM ARIT_OP",
					"TERM ARIT_OP", null, null, null, null, null, null, "TERM ARIT_OP", "TERM ARIT_OP", "TERM ARIT_OP",
					"TERM ARIT_OP", "TERM ARIT_OP", null, null, null, null, "TERM ARIT_OP", null, null, null, null,
					null, null, null, null, null, null, null, null },

			/* ARIT_OP */ { null, null, null, null, "ε", null, "ε", "ε", null, null, "ε", null, null, null, null, null,
					null, null, null, null, null, null, null, "ε", "ε", null, "ε", "ε", "ε", "ε", "ε", "ε",
					"+ TERM ARIT_OP", "- TERM ARIT_OP", "* TERM ARIT_OP", "/ TERM ARIT_OP", "% TERM ARIT_OP", null },

			/* TERM */ { null, null, null, "( EXPR )", null, null, null, null, null, null, null, null, null, null, null,
					null, "id", "num", "litcad", "verdadero", "falso", null, null, null, null, "! TERM", null, null,
					null, null, null, null, null, "- TERM", null, null, null, null, null },

			/* tipo */ { null, null, null, null, null, null, null, null, null, null, null, null, "entero", "decimal",
					"cadena", "booleano", null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null } };

	public AnalizadorSintactico(List<Token> tokens) {
		this.tokens = tokens;
		pila.push("$");
		pila.push("PROGRAMA");
	}

	public boolean analizar() {
		while (!pila.isEmpty()) {
			String cima = pila.peek();
			Token tokenActual = obtenerTokenActual();
			String claveToken = obtenerClaveToken(tokenActual);

			if (cima.equals(claveToken)) {
				pila.pop();
				indiceToken++;
			} else if (esTerminal(cima)) {
				errores.add(errorEsperado(cima, tokenActual));
				return false;
			} else {
				String produccion = obtenerProduccion(cima, claveToken);
				if (produccion == null) {
					errores.add(errorInesperado(tokenActual));
					return false;
				} else if (produccion.equals("ε")) {
					pila.pop();
				} else {
					pila.pop();
					String[] simbolos = produccion.split(" ");
					for (int i = simbolos.length - 1; i >= 0; i--) {
						if (!simbolos[i].isEmpty()) {
							pila.push(simbolos[i]);
						}
					}
				}
			}
		}
		return errores.isEmpty();
	}

	private String obtenerClaveToken(Token token) {
		// Adaptación específica para tu clase Token
		switch (token.getTipo()) {
		case LiteralNumerico:
			return token.getValor().contains(".") ? "decimal" : "num";
		case LiteralCadena:
			return "litcad";
		case LiteralBooleano:
			return token.getValor(); // "verdadero" o "falso"
		case Identificador:
			return "id";
		case OperadorAritmetico:
		case OperadorComparacion:
		case OperadorLogico:
			return token.getValor();
		case Delimitador:
			return token.getValor(); // ";", "(", ")", etc.
		default:
			return token.getValor().toLowerCase();
		}
	}

	private String errorEsperado(String esperado, Token encontrado) {
		return String.format("Error en %s: Se esperaba '%s' pero se encontró '%s'", encontrado.getUbicacion(), esperado,
				encontrado.getValor());
	}

	private String errorInesperado(Token token) {
		return String.format("Error en %s: No se esperaba '%s' en este contexto", token.getUbicacion(),
				token.getValor());
	}

	private Token obtenerTokenActual() {
		if (indiceToken >= tokens.size()) {
			return new Token(Token.Tipos.Delimitador, "$", -1, -1);
		}
		return tokens.get(indiceToken);
	}

	private boolean esTerminal(String simbolo) {
		for (String t : TERMINALES) {
			if (t.equals(simbolo)) {
				return true;
			}
		}
		return simbolo.equals("ε");
	}

	private String obtenerProduccion(String noTerminal, String terminal) {
		int fila = -1, columna = -1;

		// Buscar fila del no terminal
		for (int i = 0; i < NO_TERMINALES.length; i++) {
			if (NO_TERMINALES[i].equals(noTerminal)) {
				fila = i;
				break;
			}
		}

		// Buscar columna del terminal
		for (int j = 0; j < TERMINALES.length; j++) {
			if (TERMINALES[j].equals(terminal)) {
				columna = j;
				break;
			}
		}

		if (fila == -1) {
			errores.add("Error interno: No terminal '" + noTerminal + "' no definido");
			return null;
		}

		if (columna == -1) {
			errores.add("Error interno: Terminal '" + terminal + "' no definido");
			return null;
		}

		return TABLA_PREDICTIVA[fila][columna];
	}

	public List<String> getErrores() {
		return new ArrayList<>(errores);
	}
}