package tareas;

import java.util.Stack;

public class V2
{
	int estadoAceptacion = 2;
	int estado;
	char v; 

	String entradas[] = { "$,Z", // Inicialización de la pila con 'Z'
			"a,Z", // Leer 'a' con 'Z' → Apilar 'a'
			"a,a", // Leer 'a' con 'a' en la pila → Apilar 'a'
			"b,a", // Leer 'b' con 'a' en la pila → No modificar la pila
			"c,a", // Leer 'c' con 'a' en la pila → Desapilar 'a'
			"c,Z" // Leer 'c' con 'Z' → Aceptar la cadena
	};

	String acciones[][] = { { "1,nada", "2,apilar", "0,nada", "3,nada" }, // Estado 0
			{ "1,nada", "2,apilar", "1,nada", "3,desapilar" }, // Estado 1
			{ "aceptar", "2,apilar", "2,nada", "2,desapilar" } // Estado 2
	};

	Stack<Character> pila = new Stack();
	
	private void Evaluar(String cad)
	{
		pila.clear();

		pila.push('Z');

		cad += "$";
		int col;
		estado = 0;

		for (int i = 0; i < cad.length(); i++)
		{
			v = cad.charAt(i);
			col = columnaEntrada(v);
			if (col == 1)
			{
				System.out.println("Cadena rechazada");
				return;
			}
			else
			{
				if (!generacion(col))
				{
					System.out.println("Cadena rechazada");
				}
			}
		}
		if (estado == estadoAceptacion)
		{
			System.out.println("Cadena Aceptada");
		}
		else
		{
			System.out.println("Cadena rechazada");
		}
	}

	int columnaEntrada(char entrada)
	{
		char val, cima;
		for (int i = 0; i < entradas.length; i++)
		{
			val = entradas[i].charAt(0);
			cima = entradas[i].charAt(2);

			if (entrada == val && cima == pila.peek())
				return i;
		}
		return -1;
	}

	boolean generacion(int posicion)
	{
		if (acciones[estado][posicion].isEmpty())
		{
			return false;
		}
		String gen[] = acciones[estado][posicion].split(",");
		estado = Integer.parseInt(gen[1]);
		System.out.println(gen);
		switch (gen[1])
		{
			case "apilar":
				pila.push(v);
				break;

			case "desapilar":
				pila.pop();
				break;
		}
		return true;
	}
}