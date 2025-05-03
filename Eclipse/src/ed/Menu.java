package ed;

public class Menu
{
	private String titulo, menu[];
	private int pos;
	public Datos obd = new Datos();

	public Menu(String titulo, String menu[])
	{
		this.titulo = titulo.toUpperCase();
		this.menu = new String[menu.length + 1];
		for (pos = 0; pos < menu.length; pos++)
			this.menu[pos] = menu[pos].toUpperCase();
		this.menu[pos] = "SALIR";
	}

	public Menu(String menu[], String titulo)
	{
		this(titulo, menu);
	}

	private void Mostrar()
	{
		System.out.println("<<  " + titulo + "  >>");
		System.out.println();
		for (pos = 0; pos < menu.length; pos++)
			System.out.println((pos + 1) + ".- " + menu[pos]);
	}

	public int Opcion()
	{
		int op;
		do
		{
			this.Mostrar();
			op = obd.Entero("\nEscribe la opcion que deseas ejecutar: ");
			System.out.println();

		}
		while (op < 1 || op > menu.length);
		return op;
	}

	public int Salir()
	{
		return menu.length;
	}

}
