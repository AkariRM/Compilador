package ed;

import java.text.NumberFormat;

public class Formato
{
	public String Pesos(double can)
	{
		return NumberFormat.getCurrencyInstance().format(can);
	}

	public String Derecha(String cad, int tam)
	{
		int con;
		if (cad.length() > tam)
			cad = cad.substring(0, tam);
		else
			for (con = cad.length(); con <= tam; con++)
				cad = " " + cad;
		return cad;
	}

	public String Izquierda(String cad, int tam)
	{
		int con;
		if (cad.length() > tam)
			cad = cad.substring(0, tam);
		else
			for (con = cad.length(); con <= tam; con++)
				cad += " ";
		return cad;
	}

}
