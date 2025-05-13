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
    private static final String[] TERMINALES = {
        "inicio", "fin", "si", "(", ")", "{", "}", "si_no", "mientras", "para", ";", "=", 
        "entero", "decimal", "cadena", "booleano", "id", "num", "litcad", "verdadero", "falso",
        "mostrar", "leer", "&&", "||", "!", "<", ">", "<=", ">=", "==", "!=", "+", "-", "*", "/", "%", "$"
    };
    
    // No terminales en el orden exacto de las filas de la tabla
    private static final String[] NO_TERMINALES = {
        "PROGRAMA", "SENTENCIAS", "SENTENCIA", "DECLARACION", "ASIGNACION", 
        "CONDICIONAL", "BUCLE", "IO", "EXPR", "EXPR_LOG", "LOG_OP", "EXPR_COMP", 
        "COMP_OP", "EXPR_ARIT", "ARIT_OP", "TERM", "tipo"
    };
    
    // Tabla predictiva (simplificada para el ejemplo)
    private static final String[][] TABLA_PREDICTIVA = {
        // inicio  fin     si      (       )       {       }       si_no   mientras para    ;       =       entero  decimal cadena  booleano id      num     litcad  verdadero falso   mostrar leer    &&      ||      !       <       >       <=      >=      ==      !=      +       -       *       /       %       $
        /* PROGRAMA */  {"inicio SENTENCIAS fin", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* SENTENCIAS */{null, "ε", "SENTENCIA ; SENTENCIAS", null, null, null, "ε", null, "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", null, null, "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", null, null, null, null, "SENTENCIA ; SENTENCIAS", "SENTENCIA ; SENTENCIAS", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* SENTENCIA */ {null, null, "CONDICIONAL", null, null, null, null, null, "BUCLE", "BUCLE", null, null, "DECLARACION", "DECLARACION", "DECLARACION", "DECLARACION", "ASIGNACION", null, null, null, null, "IO", "IO", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* DECLARACION */{null, null, null, null, null, null, null, null, null, null, null, null, "tipo id = EXPR", "tipo id = EXPR", "tipo id = EXPR", "tipo id = EXPR", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* ASIGNACION */ {null, null, null, null, null, null, null, null, null, null, null, "id = EXPR", null, null, null, null, "id = EXPR", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* CONDICIONAL */{null, null, "si ( EXPR ) { SENTENCIAS } si_no { SENTENCIAS }", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* BUCLE */     {null, null, null, null, null, null, null, null, "mientras ( EXPR ) { SENTENCIAS }", "para ( (DECLARACION | ASIGNACION) ; EXPR ; ASIGNACION ) { SENTENCIAS }", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* IO */        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "mostrar EXPR", "leer tipo id", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* EXPR */      {null, null, "EXPR_LOG", "EXPR_LOG", null, null, null, null, "EXPR_LOG", "EXPR_LOG", null, null, null, null, null, null, "EXPR_LOG", "EXPR_LOG", "EXPR_LOG", "EXPR_LOG", "EXPR_LOG", null, null, null, null, "EXPR_LOG", null, null, null, null, null, null, null, null, null, null, null, null},
        /* EXPR_LOG */ {null, null, "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", null, null, null, null, "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", null, null, null, null, null, null, "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", "EXPR_COMP LOG_OP", null, null, null, null, "EXPR_COMP LOG_OP", null, null, null, null, null, null, null, null, null, null, null},
        /* LOG_OP */    {null, null, null, null, "ε", null, "ε", "ε", null, null, "ε", null, null, null, null, null, null, null, null, null, null, null, null, "&& EXPR_COMP LOG_OP", "|| EXPR_COMP LOG_OP", null, null, null, null, null, null, null, null, null, null, null, null, null},
        /* EXPR_COMP */ {null, null, "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", null, null, null, null, "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", null, null, null, null, null, null, "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", "EXPR_ARIT COMP_OP", null, null, null, null, "EXPR_ARIT COMP_OP", null, null, null, null, null, null, null, null, null, null, null},
        /* COMP_OP */   {null, null, null, null, "ε", null, "ε", "ε", null, null, "ε", null, null, null, null, null, null, null, null, null, null, null, null, "ε", "ε", null, "< EXPR_ARIT", "> EXPR_ARIT", "<= EXPR_ARIT", ">= EXPR_ARIT", "== EXPR_ARIT", "!= EXPR_ARIT", null, null, null, null, null, null},
        /* EXPR_ARIT */ {null, null, "TERM ARIT_OP", "TERM ARIT_OP", null, null, null, null, "TERM ARIT_OP", "TERM ARIT_OP", null, null, null, null, null, null, "TERM ARIT_OP", "TERM ARIT_OP", "TERM ARIT_OP", "TERM ARIT_OP", "TERM ARIT_OP", null, null, null, null, "TERM ARIT_OP", null, null, null, null, null, null, null, null, null, null, null},
        /* ARIT_OP */   {null, null, null, null, "ε", null, "ε", "ε", null, null, "ε", null, null, null, null, null, null, null, null, null, null, null, null, "ε", "ε", null, "ε", "ε", "ε", "ε", "ε", "ε", "+ TERM ARIT_OP", "- TERM ARIT_OP", "* TERM ARIT_OP", "/ TERM ARIT_OP", "% TERM ARIT_OP", null},
        /* TERM */      {null, null, null, "( EXPR )", null, null, null, null, null, null, null, null, null, null, null, null, "id", "num", "litcad", "verdadero", "falso", null, null, null, null, "! TERM", null, null, null, null, null, null, null, "- TERM", null, null, null, null},
        /* tipo */      {null, null, null, null, null, null, null, null, null, null, null, null, "entero", "decimal", "cadena", "booleano", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
    };

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
        pila.push("$"); // Símbolo de fin de entrada
        pila.push("PROGRAMA"); // Símbolo inicial
    }

    public boolean analizar() {
        while (!pila.isEmpty()) {
            String cima = pila.peek();
            String tokenActual = obtenerTokenActual().getClaveSintactica();

            if (cima.equals(tokenActual)) {
                pila.pop();
                indiceToken++;
            } else if (esTerminal(cima)) {
                errores.add("Error: Se esperaba " + cima + " pero se encontró " + tokenActual);
                return false; // Error de coincidencia
            } else {
                String produccion = obtenerProduccion(cima, tokenActual);
                if (produccion == null || produccion.equals("null")) {
                    errores.add("Error sintáctico: No se esperaba " + tokenActual + " en este contexto");
                    return false;
                } else if (produccion.equals("ε")) {
                    pila.pop(); // Producción vacía, simplemente sacar de la pila
                } else {
                    pila.pop();
                    // Manejar producciones alternativas (como en DECLARACION | ASIGNACION)
                    if (produccion.contains("|")) {
                        // Estrategia simple: intentar con la primera opción
                        produccion = produccion.split("\\|")[0].trim();
                    }
                    String[] simbolos = produccion.split(" ");
                    for (int i = simbolos.length - 1; i >= 0; i--) {
                        pila.push(simbolos[i]);
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
        return simbolo.equals("ε");
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
        
        return TABLA_PREDICTIVA[fila][columna];
    }

    public List<String> getErrores() {
        return errores;
    }
}