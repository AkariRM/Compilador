package ed;

import java.util.Calendar;

public class Fecha
{
	private Calendar ref;
	private String fecha = "";

	public Fecha()
	{
		ref = Calendar.getInstance();
	}

	public Fecha(int dia, int mes, int año)
	{
		ref = Calendar.getInstance();
		ref.set(año, mes - 1, dia);
	}

	public String HoyL()
	{
		fecha = "";
		this.NombreDia();
		this.NoDia();
		this.NombreMes();
		this.NoAño();
		return fecha;
	}

	public String HoyM()
	{
		fecha = "";
		this.NoDia();
		this.NombreMes();
		this.NoAño();
		return fecha;
	}

	public String HoyS()
	{
		fecha = "";
		this.NoDia2();
		fecha += "-";
		this.NoMes2();
		fecha += "-";
		this.NoAño();
		return fecha;
	}

	// HoySA2 entrega la fecha en formato dd-mm-aa
	public String HoySA2()
	{
		fecha = "";
		this.NoDia2();
		fecha += "-";
		this.NoMes2();
		fecha += "-";
		this.NoAño2();
		return fecha;
	}

	private void NombreDia()
	{
		switch (ref.get(Calendar.DAY_OF_WEEK))
		{
			case 1:
				fecha += "Domingo, ";
				break;
			case 2:
				fecha += "Lunes, ";
				break;
			case 3:
				fecha += "Martes, ";
				break;
			case 4:
				fecha += "Miércoles, ";
				break;
			case 5:
				fecha += "Jueves, ";
				break;
			case 6:
				fecha += "Viernes, ";
				break;
			case 7:
				fecha += "Sábado, ";
				break;
		}
	}

	private void NoDia()
	{
		fecha += ref.get(Calendar.DAY_OF_MONTH) + " de ";
	}

	private void NoDia2()
	{
		fecha += ref.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + ref.get(Calendar.DAY_OF_MONTH)
				: ref.get(Calendar.DAY_OF_MONTH);
	}

	private void NoMes2()
	{
		fecha += ref.get(Calendar.MONTH) < 9 ? "0" + (ref.get(Calendar.MONTH) + 1) : (ref.get(Calendar.MONTH) + 1);
	}

	private void NombreMes()
	{
		switch (ref.get(Calendar.MONTH))
		{
			case 0:
				fecha += "Enero de ";
				break;
			case 1:
				fecha += "Febrero de ";
				break;
			case 2:
				fecha += "Marzo de ";
				break;
			case 3:
				fecha += "Abril de ";
				break;
			case 4:
				fecha += "Mayo de ";
				break;
			case 5:
				fecha += "Junio de ";
				break;
			case 6:
				fecha += "Julio de ";
				break;
			case 7:
				fecha += "Agosto de ";
				break;
			case 8:
				fecha += "Septiembre de ";
				break;
			case 9:
				fecha += "Octubre de ";
				break;
		}
	}

	private void NoAño()
	{
		fecha += ref.get(Calendar.YEAR) % 100;
	}

	private void NoAño2()
	{
		int año = ref.get(Calendar.YEAR) % 100;
		if (año < 10)
			fecha += "0";
		fecha += año;
	}

	public int Edad(int dia, int mes, int año)
	{
		mes--;
		int edad = ref.get(Calendar.YEAR) - año;
		if (mes > ref.get(Calendar.MONTH))
			edad--;
		else
			if (mes == ref.get(Calendar.MONTH) && dia > ref.get(Calendar.DAY_OF_MONTH))
				edad--;
		return edad;
	}
}
