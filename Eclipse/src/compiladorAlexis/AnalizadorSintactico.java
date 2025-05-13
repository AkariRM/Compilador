package compiladorAlexis;

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
    private static final String[] TERMINALES = {
        "id", "num", "litcad", "litc", "true", "false", "(", ")", "!", "*", "/", 
        "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", "$"
    };
    
    // No terminales en el orden exacto de las filas de la tabla
    private static final String[] NO_TERMINALES = {
        "L", "L'", "R", "R'", "E", "E'", "T", "T'", "F"
    };
    
    // Tabla predictiva 
    private static final String[][] tabla_Predictiva = {
        // id       num      litcad   litc    true    false   (       )       !       *       /       +       -       <       >       <=      >=      ==      !=      &&      ||      $
        /* L */ { "R L'", "R L'", "R L'", "R L'", "R L'", "R L'", "R L'", "saltar", "R L'", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar" },
        /* L'*/ { "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "ε", "saltar", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "&& R L'", "|| R L'", "ε" },
        /* R */ { "E R'", "E R'", "E R'", "E R'", "E R'", "E R'", "E R'", "saltar", "E R'", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar" },
        /* R'*/ { "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "< E", "> E", "<= E", ">= E", "== E", "!= E", "ε", "ε", "ε" },
        /* E */ { "T E'", "T E'", "T E'", "T E'", "T E'", "T E'", "T E'", "saltar", "T E'", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar" },
        /* E'*/ { "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "+ T E'", "- T E'", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε" },
        /* T */ { "F T'", "F T'", "F T'", "F T'", "F T'", "F T'", "F T'", "saltar", "F T'", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar" },
        /* T'*/ { "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "* F T'", "/ F T'", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε", "ε" },
        /* F */ { "id", "num", "litcad", "litc", "true", "false", "( E )", "saltar", "! F", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar", "saltar" }
    };

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
        pila.push("$"); // Símbolo de fin de entrada
        pila.push("L"); // Símbolo inicial
    }

    public boolean analizar() {
        while (!pila.isEmpty()) {
            String cima = pila.peek();
            String tokenActual = obtenerTokenActual().getClaveSintactica();

            if (cima.equals(tokenActual)) {
                pila.pop();
                indiceToken++;
            } else if (esTerminal(cima)) {
                if (cima.equals("saltar")) {
                    pila.pop(); // Simplemente saltar este terminal
                } else {
                    errores.add("Error: Se esperaba " + cima + " pero se encontró " + tokenActual);
                    pila.pop(); // Recover: sacar el terminal esperado
                }
            } else {
                String produccion = obtenerProduccion(cima, tokenActual);
                if (produccion == null || produccion.isEmpty()) {
                    errores.add("Error: No hay producción para [" + cima + ", " + tokenActual + "]");
                    pila.pop(); // Recover: sacar el no terminal
                } else {
                    pila.pop();
                    if (!produccion.equals("ε")) {
                        String[] simbolos = produccion.split(" ");
                        for (int i = simbolos.length - 1; i >= 0; i--) {
                            if (!simbolos[i].equals("saltar")) {
                                pila.push(simbolos[i]);
                            }
                        }
                    }
                }
            }
        }
        return errores.isEmpty();
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
        return simbolo.equals("ε") || simbolo.equals("saltar");
    }

    private String obtenerProduccion(String noTerminal, String terminal) {
        int fila = -1, columna = -1;
        
        // Buscar fila correspondiente al no terminal
        for (int i = 0; i < NO_TERMINALES.length; i++) {
            if (NO_TERMINALES[i].equals(noTerminal)) {
                fila = i;
                break;
            }
        }
        
        // Buscar columna correspondiente al terminal
        for (int j = 0; j < TERMINALES.length; j++) {
            if (TERMINALES[j].equals(terminal)) {
                columna = j;
                break;
            }
        }
        
        if (fila == -1 || columna == -1) {
            return null;
        }
        
        return tabla_Predictiva[fila][columna];
    }

    public List<String> getErrores() {
        return errores;
    }
}