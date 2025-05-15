package compilador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class AnalizadorSintactico {
	private final Deque<String> pila = new ArrayDeque<>();
	private final List<Token> tokens;
	private int indiceToken = 0;
	private final List<String> errores = new ArrayList<>();
	private boolean debugMode = false;

	// Terminales
	private static final String[] TERMINALES = { "inicio", "fin", "si", "si_no", "mientras", "para", "mostrar", "leer",
			"entero", "decimal", "cadena", "booleano", "verdadero", "falso", "id", "num", "litcad", ";", "(", ")", "{",
			"}", "=", "&&", "||", ">", "<", ">=", "<=", "==", "!=", "+", "-", "*", "/", "%", "$" };

	// No terminales
	private static final String[] NO_TERMINALES = { "PROGRAMA", "Sentencias", "Sentencia", "Declaracion",
			"Declaracion'", "Asignacion", "Condicional", "Condicional'", "Bucle", "IO", "Expresion", "OpLogExp",
			"ExpresionComp", "OpCompExp", "ExpresionArit", "OpAritExp", "Termino" };

	private static final String[][] TABLA_PREDICTIVA = {
		    /* PROGRAMA */ {
		        "inicio Sentencias fin", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
		    },
		    /* Sentencias */ {
		        "Sentencia ; Sentencias", "", "Sentencia ; Sentencias", "", "Sentencia ; Sentencias", "Sentencia ; Sentencias",
		        "Sentencia ; Sentencias", "Sentencia ; Sentencias", "Sentencia ; Sentencias", "Sentencia ; Sentencias",
		        "Sentencia ; Sentencias", "Sentencia ; Sentencias", "", "", "Sentencia ; Sentencias",
		        "Sentencia ; Sentencias", "Sentencia ; Sentencias", "", "", "", "", "ε", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
		    },
		    /* Sentencia */ {
		        "", "", "Condicional", "", "Bucle", "Bucle", "IO", "IO", "Declaracion", "Declaracion", "Declaracion",
		        "Declaracion", "", "", "Asignacion", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
		    },
		    /* Declaracion */ {
		        "", "", "", "", "", "", "", "", "entero id Declaracion'", "decimal id Declaracion'",
		        "cadena id Declaracion'", "booleano id Declaracion'", "", "", "", "", "", "", "", "", "", "", "",
		        "", "", "", "", "", "", "", "", "", "", "", "", "", ""
		    },
		    /* Declaracion' */ {
		        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ";", 
		        "ε", "", "", 
		        "= Expresion ;", 
		        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "ε"
		    },
		    /* Asignacion */ {
		        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "id = Expresion ;", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
		    },
		    /* Expresion */ {
		        "", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		        "ExpresionArit OpAritExp",  // id
		        "ExpresionArit OpAritExp",  // num
		        "", 
		        "( Expresion )", 
		        "", "", "", "", "", "", "", "", 
		        "ExpresionArit OpAritExp", 
		        "ExpresionArit OpAritExp", 
		        "ExpresionArit OpAritExp", 
		        "ExpresionArit OpAritExp", 
		        "ExpresionArit OpAritExp", 
		        "ExpresionArit OpAritExp", 
		        "ExpresionArit OpAritExp"
		    },
		    /* Termino */ {
		        "", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		        "id",  // id
		        "num",  // num
		        "( Expresion )", 
		        "", "", "", "", "", "", "", 
		        "litcad", 
		        "verdadero", 
		        "falso", 
		        "", "", "", "", "", "", ""
		    }
		};
	// Conjuntos para búsqueda rápida
	private static final Set<String> TERMINALES_SET = new HashSet<>(Arrays.asList(TERMINALES));
	private static final Map<String, Integer> INDICES_NO_TERMINALES = new HashMap<>();
	static {
		for (int i = 0; i < NO_TERMINALES.length; i++) {
			INDICES_NO_TERMINALES.put(NO_TERMINALES[i], i);
		}
	}

	// Tokens de sincronización para recuperación de errores
	private static final Set<String> TOKENS_SINCRONIZACION = new HashSet<>(Arrays.asList(";", "}", "fin", "$"));

	/**
	 * Constructor del analizador sintáctico
	 * 
	 * @param tokens Lista de tokens generados por el analizador léxico
	 */
	public AnalizadorSintactico(List<Token> tokens) {
		this.tokens = new ArrayList<>(tokens);
		inicializarPila();
	}

	/**
	 * Inicializa la pila con el símbolo inicial
	 */
	private void inicializarPila() {
		pila.clear();
		pila.push("$");
		pila.push("PROGRAMA");
	}

	/**
	 * Realiza el análisis sintáctico
	 * 
	 * @return true si el análisis fue exitoso, false si hubo errores
	 */
	public boolean analizar() {
		inicializarPila();
		indiceToken = 0;
		errores.clear();

		System.out.println("\n=== INICIO DEL ANÁLISIS SINTÁCTICO ===");
		System.out.println("Pila inicial: " + pila);

		while (!pila.isEmpty()) {
			String cima = pila.peek();
			Token tokenActual = obtenerTokenActual();
			String claveToken = obtenerClaveToken(tokenActual);

			// Mostrar estado actual
			System.out.println("\n--- Paso actual ---");
			System.out.println("Pila: " + pila);
			System.out.println("Token actual: " + tokenActual.getValor() + " (Tipo: " + tokenActual.getTipo()
					+ ", Línea: " + tokenActual.getFila() + ", Col: " + tokenActual.getColumna() + ")");
			System.out.println("Clave token: " + claveToken);

			if (cima.equals("$") && claveToken.equals("$")) {
				System.out.println("¡Análisis completado con éxito!");
				pila.pop();
				break;
			} else if (cima.equals(claveToken)) {
				System.out.println("Coincidencia encontrada: '" + cima + "'");
				pila.pop();
				indiceToken++;
				System.out.println("Avanzando al siguiente token...");
			} else if (esTerminal(cima)) {
				System.out
						.println("Error: Se esperaba '" + cima + "' pero se encontró '" + tokenActual.getValor() + "'");
				manejarErrorTerminal(cima, tokenActual);
			} else {
				String produccion = obtenerProduccion(cima, claveToken);
				if (produccion == null) {
					System.out.println("Error: No hay producción definida para " + cima + " con '" + claveToken + "'");
					manejarErrorNoTerminal(tokenActual);
				} else {
					System.out.println("Aplicando producción: " + cima + " -> " + produccion);
					pila.pop();
					if (!produccion.equals("ε")) {
						apilarProduccion(produccion);
						System.out.println("Nuevo estado de la pila: " + pila);
					} else {
						System.out.println("Producción épsilon (no se apila nada)");
					}
				}
			}

			// Pequeña pausa para mejor legibilidad
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		verificarTokensSobrantes();

		if (!errores.isEmpty()) {
			System.out.println("\n=== ERRORES ENCONTRADOS ===");
			for (String error : errores) {
				System.out.println(error);
			}
		} else {
			System.out.println("\nAnálisis completado sin errores sintácticos");
		}

		return errores.isEmpty();
	}

	private void manejarErrorTerminal(String terminalEsperado, Token tokenEncontrado) {
		String errorMsg = errorEsperado(terminalEsperado, tokenEncontrado);
		errores.add(errorMsg);
		System.out.println("Manejando error terminal: " + errorMsg);
		System.out.println("Intentando recuperación...");

		sincronizar();
		pila.pop();

		System.out.println("Pila después de recuperación: " + pila);
	}

	private void manejarErrorNoTerminal(Token tokenInesperado) {
		String errorMsg = errorInesperado(tokenInesperado);
		errores.add(errorMsg);
		System.out.println("Manejando error no terminal: " + errorMsg);
		System.out.println("Intentando recuperación...");

		sincronizar();
		if (!pila.isEmpty() && !esTerminal(pila.peek())) {
			pila.pop();
		}

		System.out.println("Pila después de recuperación: " + pila);
	}

	private void sincronizar() {
		System.out.println("Iniciando sincronización...");
		int tokensSaltados = 0;

		while (indiceToken < tokens.size()) {
			Token token = tokens.get(indiceToken);
			if (TOKENS_SINCRONIZACION.contains(token.getValor())) {
				System.out.println("Punto de sincronización encontrado: '" + token.getValor() + "'");
				System.out.println("Tokens saltados: " + tokensSaltados);
				return;
			}
			System.out.println("Saltando token: " + token.getValor());
			indiceToken++;
			tokensSaltados++;
		}
	}

	/**
	 * Obtiene el token actual o un token de fin si no hay más tokens
	 */
	private Token obtenerTokenActual() {
		return indiceToken < tokens.size() ? tokens.get(indiceToken) : new Token(Token.Tipos.Delimitador, "$", -1, -1);
	}

	/**
	 * Apila una producción en orden inverso
	 */
	private void apilarProduccion(String produccion) {
		String[] simbolos = produccion.split("\\s+");
		for (int i = simbolos.length - 1; i >= 0; i--) {
			pila.push(simbolos[i]);
		}
	}

	/**
	 * Verifica si quedaron tokens sin procesar
	 */
	private void verificarTokensSobrantes() {
		if (indiceToken < tokens.size()) {
			StringBuilder sb = new StringBuilder("Tokens no procesados:\n");
			while (indiceToken < tokens.size()) {
				Token extra = tokens.get(indiceToken++);
				sb.append("- ").append(extra.getValor()).append(" en ").append(extra.getUbicacion()).append("\n");
			}
			errores.add(sb.toString());
		}
	}

	/**
	 * Determina la clave del token para la tabla predictiva
	 */
	private String obtenerClaveToken(Token token) {
		if (token == null || token.getValor().equals("$"))
			return "$";

		String valor = token.getValor().toLowerCase();

		// Primero verificar si es un terminal directo
		for (String terminal : TERMINALES) {
			if (terminal.equals(valor))
				return terminal;
		}

		// Luego verificar por tipo de token
		switch (token.getTipo()) {
		case Identificador:
			return "id";
		case LiteralNumerico:
			return "num";
		case LiteralCadena:
			return "litcad";
		case OperadorAritmetico:
		case OperadorComparacion:
		case OperadorLogico:
		case Delimitador:
			return token.getValor();
		default:
			return valor;
		}
	}

	/**
	 * Genera mensaje de error cuando se esperaba un token específico
	 */
	private String errorEsperado(String esperado, Token encontrado) {
		return String.format("Error en %s: Se esperaba '%s' pero se encontró '%s'", encontrado.getUbicacion(), esperado,
				encontrado.getValor());
	}

	/**
	 * Genera mensaje de error para tokens inesperados
	 */
	private String errorInesperado(Token token) {
		return String.format("Error en %s: Token inesperado '%s'", token.getUbicacion(), token.getValor());
	}

	/**
	 * Verifica si un símbolo es terminal
	 */
	private boolean esTerminal(String simbolo) {
		return TERMINALES_SET.contains(simbolo) || simbolo.equals("ε");
	}

	/**
	 * Obtiene la producción de la tabla predictiva
	 */
	private String obtenerProduccion(String noTerminal, String terminal) {
	    Integer fila = INDICES_NO_TERMINALES.get(noTerminal);
	    int columna = Arrays.asList(TERMINALES).indexOf(terminal);
	    
	    if (fila == null || columna == -1 || columna >= TABLA_PREDICTIVA[fila].length) {
	        return null;
	    }
	    return TABLA_PREDICTIVA[fila][columna];
	}

	// Getters y Setters

	public List<String> getErrores() {
		return new ArrayList<>(errores);
	}

	public List<Token> getTokens() {
		return new ArrayList<>(tokens);
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public int getIndiceToken() {
		return indiceToken;
	}

	public void reset() {
		inicializarPila();
		indiceToken = 0;
		errores.clear();
	}
}