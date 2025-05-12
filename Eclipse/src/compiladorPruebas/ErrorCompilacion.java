package compiladorPruebas;

public class ErrorCompilacion {
	public enum TipoError {
		LEXICO, SINTACTICO, SEMANTICO
	}

	private final String mensaje;
	private final int fila;
	private final int columna;
	private final TipoError tipo;

	public ErrorCompilacion(String mensaje, int fila, int columna, TipoError tipo) {
		this.mensaje = mensaje;
		this.fila = fila;
		this.columna = columna;
		this.tipo = tipo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public int getFila() {
		return fila;
	}

	public int getColumna() {
		return columna;
	}

	public TipoError getTipo() {
		return tipo;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s (Línea %d, Columna %d)", tipo, mensaje, fila, columna);
	}
}