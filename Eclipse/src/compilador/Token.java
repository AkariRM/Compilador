package compilador;

public class Token
{
	private String valor;
	private Tipos tipo;
	private int fila;
	private int columna;

	public Token(Tipos tipo, String valor, int fila, int columna)
	{
		this.tipo = tipo;
		this.valor = valor;
		this.fila = fila;
		this.columna = columna;
	}

	// Getters y setters
	public String getValor()
	{
		return valor;
	}

	public Tipos getTipo()
	{
		return tipo;
	}

	public int getFila()
	{
		return fila;
	}

	public int getColumna()
	{
		return columna;
	}

	// Métodos adicionales para mejor manejo
	public int getLongitud()
	{
		return valor.length();
	}

	public String getUbicacion()
	{
		return "Línea " + fila + ", Columna " + columna;
	}
	
	
	@Override
	public String toString()
	{
		return "Token{" + "tipo=" + tipo + ", valor='" + valor + '\'' + ", fila=" + fila + ", columna=" + columna + '}';
	}

	// Enumeración de tipos de tokens
	public enum Tipos
	{ 
		LiteralNumerico("^-?([1-9][0-9]*|0)(\\.([0-9]+))?"),
		LiteralCadena("\"(\\\\.|[^\"])*\""), 
		LiteralCaracter("'(\\\\.|[^\\\\'])'"),
		OperadorAritmetico("[+\\-*/%]"),
		MainReservado("main"),
		PalabraReservada(
				"(static|new|import|package|class|public|private|if|else|while|for|return|int|float|boolean|char|void|String|public|void|int|double|String|float|final|char)"),
		OperadorLogico("(&&|\\|\\||!|&|\\|)"),
		
		OperadorAsignacion("(=|\\+=|-=|\\*=|/=)"),  
		OperadorComparacion("(==|!=|<=|>=|<|>)"), 
		Delimitador("[{}()\\[\\];,]"), 
		Identificador("[a-zA-Z_][a-zA-Z0-9_]*"),
		CaracterInvalido("[^\\s\\w\\d\\+\\-\\*/%=&|!<>\"'.,;()\\[\\]{}]"),
		EspacioBlanco("[ \\t\\r\\n]+");
		 
		
		public final String patron;

		Tipos(String s)
		{
			this.patron = s;
		}
	}
}