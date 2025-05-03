package tareas;

import ed.Datos;

public class Flotante
{

	Datos obd = new Datos();

	public String leer()
	{
		return obd.Cadena("Numero: ");
	}

	public boolean esFlotante(String numero)
	{
		boolean tienePunto = false;
		for (char c : numero.toCharArray())
		{
			if (c == '.')
			{
				if (tienePunto)
				{
					return false;
				}
				tienePunto = true;
			}
			else
				if (!Character.isDigit(c))
				{
					return false;
				}
		}
		return tienePunto && !numero.equals("0.0");
	}

	public void verificarNumero()
	{
		String numero = leer();
		if (esFlotante(numero))
		{
			System.out.println("El numero es un float");
		}
		else
		{
			System.out.println("El numero no es float");
		}
	}

}
