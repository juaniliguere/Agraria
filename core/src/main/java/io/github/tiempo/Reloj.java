package io.github.tiempo;

public class Reloj {

    private static final float segsPorMinJuego = 0.01f;
	
    private int minutos;
    private int horas;
    private int dias;

    private float acumuladorSegundos;

    public Reloj() {
        this.minutos = 0;
        this.horas = 6;
        this.dias = 0;

        this.acumuladorSegundos = 0;
    }

    public int actualizar(float delta) {
        acumuladorSegundos += delta;

        int minutosPasados = 0;

        while (acumuladorSegundos >= segsPorMinJuego) {
            acumuladorSegundos -= segsPorMinJuego;

            avanzarUnMinuto();
            minutosPasados++;
        }

        return minutosPasados;
    }

    private void avanzarUnMinuto() {
        minutos++;

        if (minutos >= 60) {
            minutos = 0;
            horas++;

            if (horas >= 24) {
                horas = 0;
                dias++;
            }
        }
    }

    public int getMinutos() {
        return minutos;
    }

    public int getHoras() {
        return horas;
    }

    public int getDias() {
        return dias;
    }
}