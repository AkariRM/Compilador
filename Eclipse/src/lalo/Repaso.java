package lalo;

import ed.Datos;

public class Repaso {
	private int x, y;
	private String texto;
	Datos obd = new Datos();

	public void Leer() {
		texto = obd.Cadena("Ingresa el mensaje");
		x = obd.Entero("Numero 1");
		y = obd.Entero("Numero 1");
	}

	public int Operacion() {
		int suma = x + y;
		return suma;
	}
	
	public void Mostrar(int suma)
	{
		System.out.println("EL resultado de la suma es " + suma );
		System.out.println("Y tu mensaje fue " + texto);
	}
}
