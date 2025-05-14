package compilador;

public class PruebaCompilador {
    public static void main(String[] args) {
        // 1. Configuración del código de prueba
        String codigo = "inicio\n" +
                       "  decimal y = 3.14;\n" +
                       "  y = y * 2;\n" +
                       "fin";
        
        // 2. Análisis léxico
        AnalizadorLexico lex = new AnalizadorLexico();
        lex.analizar(codigo);
        
        // 3. Mostrar tokens (opcional, para depuración)
        System.out.println("=== Tokens Generados ===");
        for (Token t : lex.getTokens()) {
            System.out.println(t.getValor() + " -> " + t.getClaveSintactica());
        }
        
        // 4. Análisis sintáctico
        System.out.println("\n=== Análisis Sintáctico ===");
        AnalizadorSintactico parser = new AnalizadorSintactico(lex.getTokens());
        boolean resultado = parser.analizar();
        
        // 5. Resultados
        System.out.println("\n=== Resultado Final ===");
        System.out.println(resultado ? "✔ Análisis exitoso" : "✖ Errores encontrados");
        
        // Mostrar errores si los hay
        if (!resultado) {
            System.out.println("\nErrores:");
            parser.getErrores().forEach(System.out::println);
        }
    }
}