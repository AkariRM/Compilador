import java.util.Stack;

public class AutomataPila {

    private Stack<String> pila;
    private String estadoActual;
    private List<Token> tokens;
    private int indice;

    public AutomataPila(List<Token> tokens) {
        this.tokens = tokens;
        this.indice = 0;
        this.pila = new Stack<>();
        this.estadoActual = "q0"; // Estado inicial
    }

    // Método para iniciar el análisis sintáctico
    public void analizar() {
        // Inicializamos la pila con el símbolo inicial de la gramática (por ejemplo, "Expresión")
        pila.push("Expresión");

        // Comenzamos a procesar los tokens de entrada
        while (!pila.isEmpty()) {
            String cimaPila = pila.peek(); // Observamos el símbolo en la cima de la pila

            if (esTerminal(cimaPila)) {
                Token tokenActual = tokens.get(indice);
                if (cimaPila.equals(tokenActual.getValor())) {
                    // Coincide el terminal, lo desapilamos y avanzamos al siguiente token
                    pila.pop();
                    indice++;
                } else {
                    // Error: El terminal no coincide con la entrada
                    throw new ErrorCompilacion("Error sintáctico: se esperaba '" + cimaPila + "', pero se encontró '" + tokenActual.getValor() + "'", -1, -1, ErrorCompilacion.TipoError.SINTACTICO);
                }
            } else {
                // El símbolo en la cima de la pila es un no-terminal, realizamos la expansión
                String regla = aplicarRegla(cimaPila); // Aplicamos la regla correspondiente al no-terminal
                pila.pop(); // Desapilamos el no-terminal

                // Apilamos los símbolos de la producción en orden inverso
                for (int i = regla.length() - 1; i >= 0; i--) {
                    pila.push(String.valueOf(regla.charAt(i)));
                }
            }
        }
        
        // Si llegamos aquí, significa que la pila está vacía y hemos procesado toda la entrada
        if (indice < tokens.size()) {
            throw new ErrorCompilacion("Error sintáctico: no se procesaron todos los tokens", -1, -1, ErrorCompilacion.TipoError.SINTACTICO);
        }
    }

    // Método para verificar si un símbolo es terminal
    private boolean esTerminal(String simbolo) {
        return simbolo.matches("[a-zA-Z0-9]"); // Este ejemplo asume que los terminales son letras o números
    }

    // Método para aplicar una regla de producción
    private String aplicarRegla(String simbolo) {
        // Aquí puedes definir las reglas de producción de la gramática
        if (simbolo.equals("Expresión")) {
            return "Término+Expresión";  // Reglas de ejemplo
        } else if (simbolo.equals("Término")) {
            return "Factor*Término";
        } else if (simbolo.equals("Factor")) {
            return "Número";
        }
        return ""; // Si no hay una regla definida
    }
}
