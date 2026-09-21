package io.github.agraria.cultivos;

public enum TipoCultivo {

	    // Definición de las especies:
	    // Nombre, Precio Compra, Precio Venta, Minutos por Etapa (4 etapas), XP, Ruta de Textura
	    ZANAHORIA("Zanahoria", 10, 25, 60, 15, "cultivos/zanahoriaEtapas.png", 4);

	    private final String nombre;
	    private final int precioCompra;
	    private final int precioVenta;
	    private final int minutosPorEtapa;
	    private final int xpRecompensa;
	    private final String rutaTextura;
	    private final int cantidadEtapas;

	    TipoCultivo(String nombre, int precioCompra, int precioVenta, int minutosPorEtapa, int xpRecompensa, String rutaTextura, int cantidadEtapas) {
	        this.nombre = nombre;
	        this.precioCompra = precioCompra;
	        this.precioVenta = precioVenta;
	        this.minutosPorEtapa = minutosPorEtapa;
	        this.xpRecompensa = xpRecompensa;
	        this.rutaTextura = rutaTextura;
	        this.cantidadEtapas = cantidadEtapas;
	    }

	    // Getters para consultar los datos
	    /*
	     GETTERS: función pública que devuelve el valor de un atributo privado de una clase, permitiendo leer ese dato 
	     desde afuera sin dar permiso para modificarlo.
	     */
	    public String getNombre() { return nombre; }
	    public int getPrecioCompra() { return precioCompra; }
	    public int getPrecioVenta() { return precioVenta; }
	    public int getMinutosPorEtapa() { return minutosPorEtapa; }
	    public int getXpRecompensa() { return xpRecompensa; }
	    public String getRutaTextura() { return rutaTextura; }
	    public int getCantidadEtapas() {return cantidadEtapas; }
	}
