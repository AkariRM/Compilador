package ed;

import java.io.IOException;

public class Datos
{
	private java.io.BufferedReader obb = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

	public byte Byte(String msj)
	{
		byte x = 0;
		try
		{
			System.out.print(msj + " ");
			x = Byte.parseByte(obb.readLine().trim());
		}
		catch (NumberFormatException e)
		{
			x = this.Byte(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public short Corto(String msj)
	{
		short x = 0;
		try
		{
			System.out.print(msj + " ");
			x = Short.parseShort(obb.readLine().trim());
		}
		catch (NumberFormatException e)
		{
			x = this.Corto(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public int Entero(String msj)
	{
		int x = 0;
		try
		{
			System.out.print(msj + " ");
			x = Integer.parseInt(obb.readLine().trim());
		}
		catch (NumberFormatException e)
		{
			x = this.Entero(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public long Largo(String msj)
	{
		long x = 0;
		try
		{
			System.out.print(msj + " ");
			x = Long.parseLong(obb.readLine().trim());
		}
		catch (NumberFormatException e)
		{
			x = this.Largo(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public float Flotante(String msj)
	{
		float x = 0;
		try
		{
			System.out.print(msj + " ");
			x = Float.parseFloat(obb.readLine().trim());
		}
		catch (NumberFormatException e)
		{
			x = this.Flotante(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public double Doble(String msj)
	{
		double x = 0;
		try
		{
			System.out.print(msj + " ");
			x = Double.parseDouble(obb.readLine().trim());
		}
		catch (NumberFormatException e)
		{
			x = this.Doble(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public String Cadena(String msj)
	{
		String x = "";
		try
		{
			System.out.print(msj + " ");
			x = obb.readLine().trim();
			if (x.isBlank())
				throw new NumberFormatException("Error");
		}
		catch (NumberFormatException e)
		{
			x = this.Cadena(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public char Caracter(String msj)
	{
		char x = ' ';
		try
		{
			System.out.print(msj + " ");
			x = obb.readLine().trim().charAt(0);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			x = this.Caracter(msj);
		}
		catch (java.io.IOException e)
		{
		}
		return x;
	}

	public void Enter()
	{
		String cad = "";
		try
		{
			System.out.println("Presiona <<enter>> para continuar...");
			cad = obb.readLine().trim();
			if (!cad.isEmpty())
				throw new StringIndexOutOfBoundsException();
		}
		catch (StringIndexOutOfBoundsException e)
		{
			this.Enter();
		}
		catch (IOException e)
		{
		}
	}
}
