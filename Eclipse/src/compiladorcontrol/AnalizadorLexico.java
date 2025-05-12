package compiladorcontrol;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compilador.Token.Tipos;

public class AnalizadorLexico
{
	private final List<Token> tokens;
	private final List<ErrorCompilacion> errores;
	private int posicionActual;

	public AnalizadorLexico()
	{
		this.tokens = new ArrayList<>();
		this.errores = new ArrayList<>();
		this.posicionActual = 0;
	}

	public void analizar(String entrada)
	{
		tokens.clear();
		errores.clear();
		posicionActual = 0;

		String[] lineas = entrada.split("\\n");
		for (int fila = 0; fila < lineas.length; fila++)
		{
			procesarLinea(lineas[fila], fila + 1);
		}
	}

	private void procesarLinea(String linea, int fila)
	{
		// 5mentarios
		linea = linea.replaceAll("//.*", "");

		// caracteres inválidos
		detectarCaracteresInvalidos(linea, fila);

		// Procesar tokens válidos
		procesarTokensValidos(linea, fila);

		// cadenas no cerradas
		verificarCadenasNoCerradas(linea, fila);
		
		// char no cerrado
		verificarCaracteresNoCerrados(linea, fila);
		
		//char vacios
		verificarCaracterVacio(linea, fila);
	}

	private void detectarCaracteresInvalidos(String linea, int fila)
	{
		// no c valen
		Pattern patronInvalido = Pattern.compile("[^\\s\\w\\+\\-\\*/%=&|!<>\"'.,;()\\[\\]{}0-9]");
		Matcher matcher = patronInvalido.matcher(linea);

		while (matcher.find())
		{
			String caracter = matcher.group();
			// Solo marcar como error si no es un dígito
			if (!caracter.matches("\\d"))
			{
				errores.add(new ErrorCompilacion("Carácter no permitido: '" + caracter + "'", fila, matcher.start() + 1,
						ErrorCompilacion.TipoError.LEXICO));
			}

		}
		// punto solito = malo
		if (linea.contains("."))
		{
			errores.add(new ErrorCompilacion("Carácter '.' no permitido de manera aislada", fila,
					linea.indexOf(".") + 1, ErrorCompilacion.TipoError.LEXICO));
		}
	}

	private void procesarTokensValidos(String linea, int fila)
	{
		Pattern patronTokens = construirPatronTokens();
		Matcher matcher = patronTokens.matcher(linea);
		int columna = 1;

		while (matcher.find())
		{
			String lexema = matcher.group();
			Token.Tipos tipo = determinarTipoToken(lexema);

			if (tipo != null)
			{
				validarTokenEspecifico(tipo, lexema, fila, columna);
				tokens.add(new Token(tipo, lexema, fila, columna));
				columna += lexema.length();
			}
		}
	}

	private Pattern construirPatronTokens()
	{
		StringBuilder patronBuilder = new StringBuilder();
		for (Token.Tipos tipo : Token.Tipos.values())
		{
			if (tipo != Token.Tipos.EspacioBlanco)
			{
				if (patronBuilder.length() > 0)
					patronBuilder.append("|");
				patronBuilder.append("(").append(tipo.patron).append(")");
			}
		}
		return Pattern.compile(patronBuilder.toString());
	}

	private Token.Tipos determinarTipoToken(String lexema)
	{
		// Chequeo 
		Token.Tipos[] tiposPriorizados = { 
				Token.Tipos.MainReservado, 
				Token.Tipos.LiteralCadena,
				Token.Tipos.LiteralCaracter,
				Token.Tipos.PalabraReservada,
				Token.Tipos.OperadorLogico,
				Token.Tipos.OperadorComparacion,
				Token.Tipos.OperadorAritmetico,
				Token.Tipos.OperadorAsignacion,
				Token.Tipos.LiteralNumerico, 
				Token.Tipos.Delimitador, 
				Token.Tipos.Identificador };

		for (Token.Tipos tipo : tiposPriorizados)
		{
			if (Pattern.matches(tipo.patron, lexema))
			{
				return tipo;
			}
		}
		return null;
	}

	private void validarTokenEspecifico(Token.Tipos tipo, String lexema, int fila, int columna)
	{
		switch (tipo)
		{
			case LiteralCadena:
				if (!lexema.endsWith("\""))
				{
					errores.add(new ErrorCompilacion("Cadena no cerrada", fila, columna,
							ErrorCompilacion.TipoError.LEXICO));
				}
				break;
			case LiteralNumerico:
				try
				{
					Double.parseDouble(lexema);
				}
				catch (NumberFormatException e)
				{
					errores.add(new ErrorCompilacion("Número mal formado: '" + lexema + "'", fila, columna,
							ErrorCompilacion.TipoError.LEXICO));
				}
				break;
		}
	}

	private void verificarCadenasNoCerradas(String linea, int fila)
	{
		int countComillas = 0;
		boolean escapado = false;

		for (int i = 0; i < linea.length(); i++)
		{
			char c = linea.charAt(i);
			if (c == '\\' && !escapado)
			{
				escapado = true;
			}
			else
			{
				if (c == '"' && !escapado)
				{
					countComillas++;
				}
				escapado = false;
			}
		}

		if (countComillas % 2 != 0)
		{
			errores.add(new ErrorCompilacion("Cadena no cerrada al final de línea", fila, linea.length(),
					ErrorCompilacion.TipoError.LEXICO));
		}
	}

	private void verificarCaracteresNoCerrados(String linea, int fila)
	{
		boolean dentroDeCaracter = false;
		boolean escapado = false;

		for (int i = 0; i < linea.length(); i++)
		{
			char c = linea.charAt(i);

			if (c == '\\' && !escapado)
			{
				escapado = true;
			}
			else
			{
				if (c == '\'' && !escapado)
				{
					dentroDeCaracter = !dentroDeCaracter;
				}
				escapado = false;
			}
		}

		if (dentroDeCaracter)
		{
			errores.add(new ErrorCompilacion("Literal de carácter no cerrado al final de línea", fila, linea.length(),
					ErrorCompilacion.TipoError.LEXICO));
		}
	}

	private void verificarCaracterVacio(String linea, int fila)
	{
		// Verificar si hay un literal de carácter vacío ('')
		boolean escapado = false;

		for (int i = 0; i < linea.length() - 1; i++)
		{ // Recorremos hasta el penúltimo caracter
			char c = linea.charAt(i);

			// Si encontramos un apostrofe y el siguiente caracter también es un apostrofe,
			// es un literal de carácter vacío
			if (c == '\'' && linea.charAt(i + 1) == '\'' && !escapado)
			{
				// Si encontramos '' entonces agregamos el error
				errores.add(new ErrorCompilacion("Carácter vacío no permitido", fila, i + 1,
						ErrorCompilacion.TipoError.LEXICO));
			}

			// Manejo de escape
			if (c == '\\' && !escapado)
			{
				escapado = true;
			}
			else
			{
				escapado = false;
			}
		}
	}

	// Métodos para el análisis sintáctico
	public Token siguienteToken()
	{
		if (posicionActual < tokens.size())
		{
			return tokens.get(posicionActual++);
		}
		return null;
	}

	public Token mirarToken()
	{
		if (posicionActual < tokens.size())
		{
			return tokens.get(posicionActual);
		}
		return null;
	}

	public void retroceder()
	{
		if (posicionActual > 0)
		{
			posicionActual--;
		}
	}

	// Métodos de acceso
	public List<Token> getTokens()
	{
		return Collections.unmodifiableList(tokens);
	}

	public List<ErrorCompilacion> getErrores()
	{
		return Collections.unmodifiableList(errores);
	}

	public String getResultadoFormateado()
	{
		StringBuilder sb = new StringBuilder();
		for (Token token : tokens)
		{
			sb.append(String.format("%-20s%-20s%-20d%-20d\n", token.getValor(), token.getTipo(), token.getFila(),
					token.getColumna()));
		}
		return sb.toString();
	}
}