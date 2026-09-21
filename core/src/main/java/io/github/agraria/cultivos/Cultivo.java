package io.github.agraria.cultivos;

public class Cultivo {

    private static final int ETAPA_COSECHABLE = 3;

    private final TipoCultivo tipo;

    private int etapaActual;
    private int minutosAcumulados;

    public Cultivo(TipoCultivo tipo) {
        this.tipo = tipo;
        this.etapaActual = 0;
        this.minutosAcumulados = 0;
    }

    public void hacerCrecer(int minutosPasados) {

        if (minutosPasados <= 0 || isCosechable()) {
            return;
        }

        minutosAcumulados += minutosPasados;

        while (minutosAcumulados >= tipo.getMinutosPorEtapa()
                && !isCosechable()) {

            minutosAcumulados -= tipo.getMinutosPorEtapa();
            etapaActual++;
        }
    }

    public TipoCultivo getTipo() {
        return tipo;
    }

    public int getEtapaActual() {
        return etapaActual;
    }

    public boolean isCosechable() {
        return etapaActual >= ETAPA_COSECHABLE;
    }
}