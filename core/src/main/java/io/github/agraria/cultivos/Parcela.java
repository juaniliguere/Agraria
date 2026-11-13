package io.github.agraria.cultivos;


public class Parcela{

    private final boolean esPlantable;

    private Cultivo cultivoActual;
    private int lluviasRecibidas = 0;
    private boolean pendienteDePudrirse = false;

    public Parcela(int x, int y, boolean esPlantable) {

        this.esPlantable = esPlantable;

        this.cultivoActual = null;
        
    }

    public boolean puedePlantar() {
        return esPlantable && cultivoActual == null;
    } 

    public boolean plantar(TipoCultivo tipo) {

        if (!puedePlantar()) {
            return false;
        }

        cultivoActual = new Cultivo(tipo);
        lluviasRecibidas = 0;
        pendienteDePudrirse = false;

        return true;
    }
    
    public void recibirLluvia() {

        if (cultivoActual == null || cultivoActual.isPodrido()) {
            return;
        }

        lluviasRecibidas++;

        System.out.println("Lluvias recibidas: " + lluviasRecibidas);

        if (lluviasRecibidas >= 2) {
            pendienteDePudrirse = true;
            System.out.println("Recibio 2 lluvias cultivo podrido");
        }
    }
    
    public void procesarPudrir() {

        if (cultivoActual != null && pendienteDePudrirse) {
            cultivoActual.pudrir();
            pendienteDePudrirse = false;

            System.out.println("cultivo podrido al comenzar nuevo día");
        }
    }

    public Cultivo cosechar() {

        if (cultivoActual != null && (cultivoActual.isCosechable() || cultivoActual.isPodrido())) {

            Cultivo cosechado = cultivoActual;

            cultivoActual = null;

            return cosechado;
        }

        return null;
        
    }

    
    public boolean limpiarSiEstaPodrido() {
        if (cultivoActual != null && cultivoActual.isPodrido()) {
            cultivoActual = null; 
            return true;
        }
        return false;
    }
    
    public void pasarTiempo(int minutosPasados) {

        if (cultivoActual != null) {
            cultivoActual.hacerCrecer(minutosPasados);
        }
    }

    public boolean isEsPlantable() {
        return esPlantable;
    }

    public boolean tieneCultivo() {
        return cultivoActual != null;
    }

    public Cultivo getCultivoActual() {
        return cultivoActual;
    }
    
}