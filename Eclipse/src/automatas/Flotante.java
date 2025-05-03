package automatas;

import java.util.regex.Pattern;
import ed.Datos;

public class Flotante {
	Datos obd = new Datos();
	//private static final String REGEX ="^-?([1-9][0-9]*|0).[0-9]*[1-9]|-?([1-9][0-9]*|0)(.[0-9]*[1-9])?[eE][+-][1-9][0-9]*|0.0$";
	private static final String REGEX ="^-?([1-9][0-9]*|0).[0-9]*[1-9]([eE][+-][1-9][0-9]*)?|0.0$";
	private static final Pattern pattern = Pattern.compile(REGEX); 

	public String Leer() {
		return obd.Cadena("Numero: ");
	}

	public boolean esFlotanteValido(String numero) {
		return pattern.matcher(numero).matches();
	}

	public void verificarNumero() {
		String continuar;
		do {
			String numero = Leer();
			if (esFlotanteValido(numero)) {
				System.out.println("El número es un float válido.");
			} else {
				System.out.println("El número no es válido.");
			}

			do
				continuar = obd.Cadena("¿Deseas ingresar otro número? (si/no): ").toLowerCase();
			while (!continuar.equals("si") && !continuar.equals("no"));
		} while (continuar.equals("si"));

		System.out.println("Se acabo uwu");
	}

	public static void main(String[] args) {
		Flotante obc = new Flotante();
		obc.verificarNumero();
	}

}