package compiladorcontrol;

import java.util.HashMap;
import java.util.Map;

public class TablaPredictiva {
    private final Map<String, Map<String, String>> tabla = new HashMap<>();

    public TablaPredictiva() {
        inicializarTabla();
    }

    private void inicializarTabla() {
        // L
        Map<String, String> filaL = new HashMap<>();
        filaL.put("id", "R L'");
        filaL.put("num", "R L'");
        filaL.put("letcad", "R L'");
        filaL.put("litcar", "R L'");
        filaL.put("true", "R L'");
        filaL.put("false", "R L'");
        filaL.put("(", "R L'");
        filaL.put("!", "R L'");
        tabla.put("L", filaL);

        // L'
        Map<String, String> filaLPrima = new HashMap<>();
        filaLPrima.put("&&", "&& R L'");
        filaLPrima.put("'", "\" R L'");
        filaLPrima.put("$", "ε");
        filaLPrima.put(")", "ε");
        filaLPrima.put("}", "ε");
        tabla.put("L'", filaLPrima);

        // R
        Map<String, String> filaR = new HashMap<>();
        filaR.put("id", "E R'");
        filaR.put("num", "E R'");
        filaR.put("letcad", "E R'");
        filaR.put("litcar", "E R'");
        filaR.put("true", "E R'");
        filaR.put("false", "E R'");
        filaR.put("(", "E R'");
        filaR.put("!", "E R'");
        tabla.put("R", filaR);

        // R'
        Map<String, String> filaRPrima = new HashMap<>();
        filaRPrima.put("<", "< E");
        filaRPrima.put(">", "> E");
        filaRPrima.put("<=", "<= E");
        filaRPrima.put(">=", ">= E");
        filaRPrima.put("==", "== E");
        filaRPrima.put("!=", "!= E");
        filaRPrima.put("$", "ε");
        filaRPrima.put(")", "ε");
        filaRPrima.put("&&", "ε");
        filaRPrima.put("'", "ε");
        filaRPrima.put("}", "ε");
        tabla.put("R'", filaRPrima);

        // E
        Map<String, String> filaE = new HashMap<>();
        filaE.put("id", "T E'");
        filaE.put("num", "T E'");
        filaE.put("letcad", "T E'");
        filaE.put("litcar", "T E'");
        filaE.put("true", "T E'");
        filaE.put("false", "T E'");
        filaE.put("(", "T E'");
        filaE.put("!", "T E'");
        tabla.put("E", filaE);

        // E'
        Map<String, String> filaEPrima = new HashMap<>();
        filaEPrima.put("+", "+ T E'");
        filaEPrima.put("-", "- T E'");
        filaEPrima.put(")", "ε");
        filaEPrima.put("$", "ε");
        filaEPrima.put("&&", "ε");
        filaEPrima.put("'", "ε");
        filaEPrima.put("}", "ε");
        filaEPrima.put("<", "ε");
        filaEPrima.put(">", "ε");
        filaEPrima.put("<=", "ε");
        filaEPrima.put(">=", "ε");
        filaEPrima.put("==", "ε");
        filaEPrima.put("!=", "ε");
        tabla.put("E'", filaEPrima);

        // T
        Map<String, String> filaT = new HashMap<>();
        filaT.put("id", "F T'");
        filaT.put("num", "F T'");
        filaT.put("letcad", "F T'");
        filaT.put("litcar", "F T'");
        filaT.put("true", "F T'");
        filaT.put("false", "F T'");
        filaT.put("(", "F T'");
        filaT.put("!", "F T'");
        tabla.put("T", filaT);

        // T'
        Map<String, String> filaTPrima = new HashMap<>();
        filaTPrima.put("*", "* F T'");
        filaTPrima.put("/", "/ F T'");
        filaTPrima.put("+", "ε");
        filaTPrima.put("-", "ε");
        filaTPrima.put(")", "ε");
        filaTPrima.put("$", "ε");
        filaTPrima.put("&&", "ε");
        filaTPrima.put("'", "ε");
        filaTPrima.put("}", "ε");
        filaTPrima.put("<", "ε");
        filaTPrima.put(">", "ε");
        filaTPrima.put("<=", "ε");
        filaTPrima.put(">=", "ε");
        filaTPrima.put("==", "ε");
        filaTPrima.put("!=", "ε");
        tabla.put("T'", filaTPrima);

        // F
        Map<String, String> filaF = new HashMap<>();
        filaF.put("id", "id");
        filaF.put("num", "num");
        filaF.put("letcad", "letcad");
        filaF.put("litcar", "litcar");
        filaF.put("true", "true");
        filaF.put("false", "false");
        filaF.put("(", "( L )");
        filaF.put("!", "! L");
        tabla.put("F", filaF);
    }

    public String obtenerProduccion(String noTerminal, String terminal) {
        Map<String, String> fila = tabla.get(noTerminal);
        if (fila != null) {
            return fila.get(terminal);
        }
        return null;
    }

    public boolean contieneNoTerminal(String simbolo) {
        return tabla.containsKey(simbolo);
    }
}
