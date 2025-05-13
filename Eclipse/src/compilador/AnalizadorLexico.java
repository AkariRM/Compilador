package compilador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compilador.ErrorCompilacion;

public class AnalizadorLexico {
	private final List<Token> tokens;
	private final List<ErrorCompilacion> errores;
	private int posicionActual;

	public AnalizadorLexico() {
		this.tokens = new ArrayList<>();
		this.errores = new ArrayList<>();
		this.posicionActual = 0;
	}

	public void analizar(String entrada) {
		tokens.clear();
		errores.clear();
		posicionActual = 0;

		String[] lineas = entrada.split("\\n");
		for (int fila = 0; fila < lineas.length; fila++) {
			procesarLinea(lineas[fila], fila + 1);
		}
	}

	private void procesarLinea(String linea, int fila) {
		// Eliminar comentarios
		linea = linea.replaceAll("//.*", "");

		// Detectar caracteres inválidos
		detectarCaracteresInvalidos(linea, fila);

		// Procesar tokens válidos
		procesarTokensValidos(linea, fila);

		// Verificaciones adicionales
		verificarCadenasNoCerradas(linea, fila);
		verificarCaracteresNoCerrados(linea, fila);
		verificarCaracterVacio(linea, fila);
	}

	private void detectarCaracteresInvalidos(String linea, int fila) {
		Pattern patronInvalido = Pattern.compile("[^\\s\\w\\+\\-\\*/%=&|!<>\"'.,;()\\[\\]{}0-9]");
		Matcher matcher = patronInvalido.matcher(linea);

		while (matcher.find()) {
			String caracter = matcher.group();
			if (!caracter.matches("\\d")) {
				errores.add(new ErrorCompilacion("Carácter no permitido: '" + caracter + "'", fila, matcher.start() + 1,
						ErrorCompilacion.TipoError.LEXICO));
			}
		}

		if (linea.matches(".*\\..*") && !linea.matches(".*\\d+\\.\\d+.*")) {
			errores.add(new ErrorCompilacion("Carácter '.' no permitido de manera aislada", fila,
					linea.indexOf('.') + 1, ErrorCompilacion.TipoError.LEXICO));
		}
	}

	private void procesarTokensValidos(String linea, int fila) {
		// Patrón para palabras reservadas y símbolos
		Pattern patronPalabras = Pattern.compile(
				"\\b(inicio|fin|si|mientras|para|mostrar|leer|entero|decimal|cadena|booleano|verdadero|falso)\\b"
						+ "|&&|\\|\\||==|!=|<=|>=|[+\\-*/%=<>();(){}\\[\\]]");

		// Patrón para identificadores, literales y otros tokens
		Pattern patronGeneral = Pattern.compile("(?<palabra>" + patronPalabras.pattern() + ")"
			    + "|(?<identificador>[a-zA-Z_][a-zA-Z0-9_]*)"
			    + "|(?<numero>-?([1-9][0-9]*|0)(\\.[0-9]+)?)"
			    + "|(?<cadena>\".*?(?<!\\\\)\")"
			    + "|(?<caracter>'(\\\\.|[^'])')"
			    + "|(?<espacio>\\s+)");



		Matcher matcher = patronGeneral.matcher(linea);
		int columna = 1;

		while (matcher.find()) {
			if (matcher.group("espacio") != null) {
				columna += matcher.group("espacio").length();
				continue;
			}

			String lexema = matcher.group();
			Token.Tipos tipo = determinarTipoToken(lexema, matcher);

			if (tipo != null) {
				validarTokenEspecifico(tipo, lexema, fila, columna);
				tokens.add(new Token(tipo, lexema, fila, columna));
				columna += lexema.length();
			}
		}
	}

	private Token.Tipos determinarTipoToken(String lexema, Matcher matcher) {
		// Palabras reservadas
		switch (lexema) {
		case "inicio":
			return Token.Tipos.Inicio;
		case "fin":
			return Token.Tipos.Fin;
		case "si":
			return Token.Tipos.Si;
		case "mientras":
			return Token.Tipos.Mientras;
		case "para":
			return Token.Tipos.Para;
		case "mostrar":
			return Token.Tipos.Mostrar;
		case "leer":
			return Token.Tipos.Leer;
		case "entero":
			return Token.Tipos.Entero;
		case "decimal":
			return Token.Tipos.Decimal;
		case "cadena":
			return Token.Tipos.Cadena;
		case "booleano":
			return Token.Tipos.Booleano;
		case "verdadero":
			return Token.Tipos.Verdadero;
		case "falso":
			return Token.Tipos.Falso;
		}

		// Operadores y delimitadores
		if (matcher.group("palabra") != null) {
			switch (lexema) {
			case "&&":
			case "||":
				return Token.Tipos.OperadorLogico;
			case "==":
			case "!=":
			case "<=":
			case ">=":
			case "<":
			case ">":
				return Token.Tipos.OperadorComparacion;
			case "+":
			case "-":
			case "*":
			case "/":
			case "%":
				return Token.Tipos.OperadorAritmetico;
			case "=":
				return Token.Tipos.OperadorAsignacion;
			case ";":
			case "(":
			case ")":
			case "{":
			case "}":
			case "[":
			case "]":
				return Token.Tipos.Delimitador;
			}
		}

		// Identificadores y literales
		if (matcher.group("identificador") != null)
			return Token.Tipos.Identificador;
		if (matcher.group("numero") != null)
			return Token.Tipos.LiteralNumerico;
		if (matcher.group("cadena") != null)
			return Token.Tipos.LiteralCadena;
		if (matcher.group("caracter") != null)
		    return Token.Tipos.LiteralCaracter;
		return null;
	}

	private void validarTokenEspecifico(Token.Tipos tipo, String lexema, int fila, int columna) {
		switch (tipo) {
		case LiteralCadena:
			if (!lexema.endsWith("\"")) {
				errores.add(
						new ErrorCompilacion("Cadena no cerrada", fila, columna, ErrorCompilacion.TipoError.LEXICO));
			}
			break;
		case LiteralNumerico:
			try {
				Double.parseDouble(lexema);
			} catch (NumberFormatException e) {
				errores.add(new ErrorCompilacion("Número mal formado: '" + lexema + "'", fila, columna,
						ErrorCompilacion.TipoError.LEXICO));
			}
			break;
		case LiteralCaracter:
		    if (lexema.length() < 3 || lexema.charAt(0) != '\'' || lexema.charAt(lexema.length() - 1) != '\'') {
		        errores.add(new ErrorCompilacion("Literal de carácter mal formado: '" + lexema + "'", fila, columna,
		                ErrorCompilacion.TipoError.LEXICO));
		    }
		    break;

		}
	}

	private void verificarCadenasNoCerradas(String linea, int fila) {
		int countComillas = 0;
		boolean escapado = false;

		for (int i = 0; i < linea.length(); i++) {
			char c = linea.charAt(i);
			if (c == '\\' && !escapado) {
				escapado = true;
			} else {
				if (c == '"' && !escapado) {
					countComillas++;
				}
				escapado = false;
			}
		}

		if (countComillas % 2 != 0) {
			errores.add(new ErrorCompilacion("Cadena no cerrada al final de línea", fila, linea.length(),
					ErrorCompilacion.TipoError.LEXICO));
		}
	}

	private void verificarCaracteresNoCerrados(String linea, int fila) {
		boolean dentroDeCaracter = false;
		boolean escapado = false;

		for (int i = 0; i < linea.length(); i++) {
			char c = linea.charAt(i);

			if (c == '\\' && !escapado) {
				escapado = true;
			} else {
				if (c == '\'' && !escapado) {
					dentroDeCaracter = !dentroDeCaracter;
				}
				escapado = false;
			}
		}

		if (dentroDeCaracter) {
			errores.add(new ErrorCompilacion("Literal de carácter no cerrado al final de línea", fila, linea.length(),
					ErrorCompilacion.TipoError.LEXICO));
		}
	}

	private void verificarCaracterVacio(String linea, int fila) {
		// Verificar si hay un literal de carácter vacío ('')
		boolean escapado = false;

		for (int i = 0; i < linea.length() - 1; i++) { // Recorremos hasta el penúltimo caracter
			char c = linea.charAt(i);

			// Si encontramos un apostrofe y el siguiente caracter también es un apostrofe,
			// es un literal de carácter vacío
			if (c == '\'' && linea.charAt(i + 1) == '\'' && !escapado) {
				// Si encontramos '' entonces agregamos el error
				errores.add(new ErrorCompilacion("Carácter vacío no permitido", fila, i + 1,
						ErrorCompilacion.TipoError.LEXICO));
			}

			// Manejo de escape
			if (c == '\\' && !escapado) {
				escapado = true;
			} else {
				escapado = false;
			}
		}
	}

	// Métodos para el análisis sintáctico
	public Token siguienteToken() {
		if (posicionActual < tokens.size()) {
			return tokens.get(posicionActual++);
		}
		return null;
	}

	public Token mirarToken() {
		if (posicionActual < tokens.size()) {
			return tokens.get(posicionActual);
		}
		return null;
	}

	public void retroceder() {
		if (posicionActual > 0) {
			posicionActual--;
		}
	}

	// Métodos de acceso
	public List<Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}

	public List<ErrorCompilacion> getErrores() {
		return Collections.unmodifiableList(errores);
	}

	public String getResultadoFormateado() {
		StringBuilder sb = new StringBuilder();
		for (Token token : tokens) {
			sb.append(String.format("%-20s%-20s%-20d%-20d\n", token.getValor(), token.getTipo(), token.getFila(),
					token.getColumna()));
		}
		return sb.toString();
	}

}