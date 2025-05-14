package compilador;

public class PruebaLexer {
    public static void main(String[] args) {
        String codigo = "decimal y = 3.14;";
        AnalizadorLexico lex = new AnalizadorLexico();
        lex.analizar(codigo);
        
        System.out.println("TOKEN -> VALOR -> TIPO -> CLAVE_SINTACTICA");
        for (Token t : lex.getTokens()) {
            System.out.println(
                t.getValor() + " -> " + 
                t.getTipo() + " -> " + 
                t.getClaveSintactica()
            );
        }
    }
}