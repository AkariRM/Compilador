package compilador;

public class Token {
    private String valor;
    private Tipos tipo;
    private int fila;
    private int columna;

    public Token(Tipos tipo, String valor, int fila, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.fila = fila;
        this.columna = columna;
    }

    // Getters
    public String getValor() { return valor; }
    public Tipos getTipo() { return tipo; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    public int getLongitud() { return valor.length(); }

    public String getUbicacion() {
        return "Línea " + fila + ", Columna " + columna;
    }

    @Override
    public String toString() {
        return "Token{" + "tipo=" + tipo + ", valor='" + valor + '\'' + ", fila=" + fila + ", columna=" + columna + '}';
    }

    public String getClaveSintactica() {
        switch (tipo) {
     // Palabras reservadas
        	case Inicio:    return "inicio";
        	case Fin:      return "fin";
        	case Si:       return "si";
        	case SiNo:     return "si_no";  // Ahora reconocido
        	case Mientras: return "mientras";
        	case Para:     return "para";
        	case Mostrar:  return "mostrar";
        	case Leer:     return "leer";
        
        // Tipos de datos
        	case Entero:   return "entero";
        	case Decimal: return "decimal";
        	case Cadena:  return "cadena";
        	case Booleano: return "booleano";
            
            // Valores literales
            case LiteralNumerico:
                return valor.contains(".") ? "decimal" : "num";
            case LiteralCadena:    return "litcad";
            case LiteralBooleano:  return valor.equals("verdadero") ? "verdadero" : "falso";
            
            // Identificadores
            case Identificador:    return "id";
            
            // Operadores
            case OperadorAritmetico:  return valor;
            case OperadorComparacion: return valor;
            case OperadorLogico:      return valor;
            case OperadorAsignacion:  return "=";
            
            // Delimitadores
            case Delimitador: return valor; // ";", "(", ")", "{", "}"
            
            default:
                return "ERROR";
        }
    }

    public enum Tipos {
        // Palabras reservadas
        Inicio("inicio"),
        Fin("fin"),
        Si("si"),
        SiNo("si_no"), 
        Mientras("mientras"),
        Para("para"),
        Mostrar("mostrar"),
        Leer("leer"),
        Tipo("entero|decimal|cadena|booleano"),
        
        // Tipos de datos
        Entero("entero"),
        Decimal("decimal"),
        Cadena("cadena"),
        Booleano("booleano"),
        
        // Literales
        LiteralNumerico("^-?([1-9][0-9]*|0)(\\.([0-9]+))?"),
        LiteralCadena("\"(\\\\.|[^\"])*\""),
        LiteralCaracter("'(\\\\.|[^'])'"),
        LiteralBooleano("verdadero|falso"),
        
        // Identificadores
        Identificador("[a-zA-Z_][a-zA-Z0-9_]*"),
        
        // Operadores
        OperadorAritmetico("[+\\-*/%]"),
        OperadorComparacion("(==|!=|<=|>=|<|>)"),
        OperadorLogico("(&&|\\|\\|)"),
        OperadorAsignacion("="),
        
        // Delimitadores
        Delimitador("[;(){}\\[\\]]"),
        
        // Valores booleanos
        Verdadero("verdadero"),
        Falso("falso"),
        
        // Espacios (no se incluyen en los tokens)
        EspacioBlanco("[ \\t\\r\\n]+");

        public final String patron;

        Tipos(String s) {
            this.patron = s;
        }
    }
}