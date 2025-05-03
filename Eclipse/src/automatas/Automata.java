package automatas;

import ed.Datos;

public class Automata {
	private Datos obd = new Datos();

	private int estados, ef;
	private int finales[];
	private String cadena;
	private int[][] transiciones;

	public void Leer() {
		do {
			cadena = obd.Cadena("Ingresa EL alfabeto");
			cadena = cadena.replaceAll("[^a-zA-Z0-9]", "");

			if (cadena.isBlank())
				System.out.println("La Cadena solo puede ser Alfanumerica");
		} while (cadena.isBlank());

		do {
			estados = obd.Entero("Cantidad de estados");
		} while (estados < 1);
		do {
			ef = obd.Entero("Cuantos Estados Finales hay");
		} while (ef < 1);
		finales = new int[ef];
		for (int i = 0; i < ef; i++) {
			do {
				finales[i] = obd.Entero("Ingresa el estado final " + (i + 1) + ": ");
			} while (finales[i] < 0);
		}

		transiciones = new int[estados][cadena.length()];

		for (int i = 0; i < estados; i++) {
			for (int j = 0; j < cadena.length(); j++) {
				boolean estadoValido = false;
				while (!estadoValido) {
					System.out.println(
							"Ingresa el siguiente estado para el estado " + i + " con el símbolo " + cadena.charAt(j));
					transiciones[i][j] = obd.Entero("Siguiente estado: ");
					if (transiciones[i][j] >= -1 && transiciones[i][j] < estados)
						estadoValido = true;
				}
			}
		}
	}

	public void Transiciones() {

		System.out.println("Matriz de transiciones:");
		for (int i = 0; i < estados; i++) {
			for (int j = 0; j < cadena.length(); j++) {
				System.out.print(transiciones[i][j] + " ");
			}
			System.out.println();
		}
	}

	public String Cadena() {
		String validar = obd.Cadena("Cadena a validar");
		return validar;
	}

	public boolean Validar(String cadenaValidar) {
		// Comenzar en el estado inicial
		int estadoActual = 0;

		// Recorrer la cadena
		for (int i = 0; i < cadenaValidar.length(); i++) {
			char simbolo = cadenaValidar.charAt(i);

			int simboloIndice = cadena.indexOf(simbolo);

			if (simboloIndice == -1) {
				System.out
						.println("Transición no válida desde el estado " + estadoActual + " con el símbolo " + simbolo);
				return false;
			}

			int siguienteEstado = transiciones[estadoActual][simboloIndice];
			estadoActual = siguienteEstado;
		}

		for (int i = 0; i < finales.length; i++) {
			if (estadoActual == finales[i]) {
				System.out.println("Cadena aceptada, terminó en estado final " + estadoActual);
				return true;
			}
		}
		System.out.println("Cadena rechazada, terminó en estado " + estadoActual);
		return false;
	}

	public static void main(String[] args) {
		Datos obd = new Datos();
		Automata oba = new Automata();
		oba.Leer();
		oba.Transiciones();
		String r;
		do {
			oba.Validar(oba.Cadena());
			do
				r = obd.Cadena("¿Quieres validar otra cadena? (si/no): ");
			while (!r.equalsIgnoreCase("si") && !r.equalsIgnoreCase("no"));
		} while (r.equalsIgnoreCase("si"));
	}

}