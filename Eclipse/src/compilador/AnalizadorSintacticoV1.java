package compilador;

import java.util.List;
import java.util.Stack;

public class AnalizadorSintacticoV1 {
    private final TablaPredictiva tabla;

    public AnalizadorSintacticoV1(TablaPredictiva tabla) {
        this.tabla = tabla;
    }

    public boolean analizar(List<String> tokens) {
        Stack<String> pila = new Stack<>();
        pila.push("$");
        pila.push("L");

        tokens.add("$");
        int i = 0;

        while (!pila.isEmpty()) {
            String cima = pila.peek();
            String entrada = tokens.get(i);

            if (!tabla.contieneNoTerminal(cima) || cima.equals("$")) {
                if (cima.equals(entrada)) {
                    pila.pop();
                    i++;
                } else {
                    System.out.println("Error sintáctico: se esperaba '" + cima + "', pero se encontró '" + entrada + "'");
                    return false;
                }
            } else {
                String produccion = tabla.obtenerProduccion(cima, entrada);
                if (produccion == null) {
                    System.out.println("Error: no hay producción para [" + cima + ", " + entrada + "]");
                    return false;
                }

                pila.pop();
                if (!produccion.equals("ε")) {
                    String[] simbolos = produccion.trim().split("\\s+");
                    for (int j = simbolos.length - 1; j >= 0; j--) {
                        pila.push(simbolos[j]);
                    }
                }
            }
        }

        if (i == tokens.size()) {
            System.out.println("Cadena válida.");
            return true;
        } else {
            System.out.println("Error: tokens restantes no procesados.");
            return false;
        }
    }
}
