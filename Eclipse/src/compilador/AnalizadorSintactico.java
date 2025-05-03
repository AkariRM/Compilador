package compilador;

import java.util.List;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class AnalizadorSintactico {

    private final List<Token> tokens;
    private int posicion;
    private Stack<String> pila;

    // Tabla LL(1) de ejemplo. Debes adaptarla a tu gramática.
    private static final Map<String, Map<String, String[]>> tablaLL1 = new HashMap<>();

    static {
        // Ejemplo de una entrada:
        // tablaLL1.get("E").put("id", new String[] {"T", "E'"});
        // Define aquí tu gramática y tabla
    }

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
        this.pila = new Stack<>();
    }

    public boolean analizar() {
        pila.push("$");
        pila.push("Inicio"); // tu símbolo inicial

        Token tokenActual = siguienteToken();

        while (!pila.isEmpty()) {
            String cima = pila.pop();
            String terminal = tokenActual != null ? tokenActual.getTipo().name() : "$";

            if (esTerminal(cima)) {
                if (cima.equals(terminal)) {
                    tokenActual = siguienteToken();
                } else {
                    System.out.println("Error de sintaxis: se esperaba " + cima + " pero se encontró " + terminal);
                    return false;
                }
            } else {
                Map<String, String[]> fila = tablaLL1.get(cima);
                if (fila != null && fila.containsKey(terminal)) {
                    String[] produccion = fila.get(terminal);
                    for (int i = produccion.length - 1; i >= 0; i--) {
                        if (!produccion[i].equals("ε")) {
                            pila.push(produccion[i]);
                        }
                    }
                } else {
                    System.out.println("Error de sintaxis: no hay regla para [" + cima + ", " + terminal + "]");
                    return false;
                }
            }
        }

        return true;
    }

    private boolean esTerminal(String simbolo) {
        // Puedes mejorar esto si defines conjuntos explícitos
        return simbolo.equals("$") || simbolo.matches("[A-Z_]+"); // Por convención
    }

    private Token siguienteToken() {
        if (posicion < tokens.size()) {
            return tokens.get(posicion++);
        }
        return null;
    }
}
