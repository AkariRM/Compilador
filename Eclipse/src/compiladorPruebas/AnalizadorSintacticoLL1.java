package compiladorPruebas;

import java.util.*;

public class AnalizadorSintacticoLL1 {
	private final TablaLL1 tabla;

	public AnalizadorSintacticoLL1(TablaLL1 tabla) {
		this.tabla = tabla;
	}

	public boolean analizar(List<String> tokens) {
		Stack<String> pila = new Stack<>();
		pila.push("$");
		pila.push("L");

		int indice = 0;
		tokens.add("$"); // Agregar símbolo de fin de cadena

		while (!pila.isEmpty()) {
			String cima = pila.peek();
			String actual = tokens.get(indice);

			if (esTerminal(cima)) {
				if (cima.equals(actual)) {
					pila.pop();
					indice++;
				} else {
					System.out.println("Error: se esperaba '" + cima + "' pero se encontró '" + actual + "'");
					return false;
				}
			} else {
				String produccion = tabla.obtenerProduccion(cima, actual);
				if (produccion == null) {
					System.out.println("Error: no hay producción para (" + cima + ", " + actual + ")");
					return false;
				}

				pila.pop();
				if (!produccion.equals("ε")) {
					List<String> simbolos = Arrays.asList(produccion.trim().split("\\s+"));
					Collections.reverse(simbolos);
					for (String s : simbolos) {
						pila.push(s);
					}
				}
			}
		}

		return indice == tokens.size();
	}

	private boolean esTerminal(String simbolo) {
		return !simbolo.matches("[A-Z][A-Z']*"); // Asume que no terminales son como L, L', E, T'
	}
}
