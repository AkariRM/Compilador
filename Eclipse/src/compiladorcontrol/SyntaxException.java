package compiladorcontrol;

public class SyntaxException extends Exception {
    private final int fila;
    private final int columna;

    public SyntaxException(String mensaje, int fila, int columna) {
        super(mensaje);
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return String.format("%s (Línea %d, Columna %d)", getMessage(), fila, columna);
    }
}