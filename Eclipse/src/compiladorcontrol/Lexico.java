package compiladorcontrol;



import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import compilador.CompiladorUI;
import javax.management.RuntimeErrorException;

import compilador.Token.Tipos;
/*
public class Lexico
{
	private void Lex(String entrada) {
	    areaComponentes.setText("");
	    final ArrayList<Token> tokens = new ArrayList<>();
	    
	    int fila = 1;  // Comienza en la primera línea
	    int columna = 1;  // Comienza en la primera columna
	    StringBuilder palabra = new StringBuilder();
	    
	    // Expresión regular para detectar tokens
	    String patronGeneral = "\\b\\w+\\b|==|!=|<=|>=|\\+|\\-|\\*|/|=|\\(|\\)|\\{|\\}|;|<|>";
	    Pattern patron = Pattern.compile(patronGeneral);
	    Matcher matcher = patron.matcher(entrada);
	    
	    while (matcher.find()) {
	        String lexema = matcher.group();
	        
	        boolean encontrado = false;

	        // Verificamos cada tipo de token
	        for (Token.Tipos tokenTipo : Token.Tipos.values()) {
	            Pattern p = Pattern.compile(tokenTipo.patron);
	            Matcher m = p.matcher(lexema);
	            if (m.matches()) {
	                // Creamos el token con la fila y columna
	                Token token = new Token(tokenTipo, lexema, fila, columna);
	                tokens.add(token);
	                encontrado = true;
	                break;
	            }
	        }

	        if (!encontrado) {
	            throw new RuntimeException("Token inválido: " + lexema + " en la fila " + fila + " columna " + columna);
	        }

	        // Actualizamos la columna para el siguiente token
	        columna += lexema.length() + 1;  // Largo del token + 1 espacio
	    }

	    // Mostrar los tokens en el área de componentes
	    areaComponentes.append(String.format("%-20s%-20s%-10s%-10s\n", "TOKEN", "TIPO", "FILA", "COLUMNA"));
	    areaComponentes.append("===============================================\n");

	    for (Token token : tokens) {
	        String linea = String.format("%-20s%-20s%-10d%-10d\n", token.getValor(), token.getTipo(), token.getFila(), token.getColumna());
	        areaComponentes.append(linea);
	    }
	}
	}
*/

