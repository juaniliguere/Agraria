package io.github.tiempo;

public class Reloj {
    private int dias;
    private int horas;
    private int minutos;
    private float acumuladorTiempo;
    
    // 1 segundo real = X minutos en el juego (ajustá esto a tu gusto)
    private static final float SEGUNDOS_POR_MINUTO = 0.05f; 

    public Reloj() {
        this.dias = 1;
        this.horas = 6; // Arranca a las 6:00 AM
        this.minutos = 0;
        this.acumuladorTiempo = 0f;
    }

    // Actualiza el reloj normalmente y devuelve cuántos minutos pasaron en este frame
    public int actualizar(float delta) {
        acumuladorTiempo += delta;
        int minutosPasadosTotales = 0;

        while (acumuladorTiempo >= SEGUNDOS_POR_MINUTO) {
            acumuladorTiempo -= SEGUNDOS_POR_MINUTO;
            avanzarUnMinuto();
            minutosPasadosTotales++;
        }

        return minutosPasadosTotales;
    }

    public void avanzarUnMinuto() {
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

    public int getDias() {
        return dias;
    }

    public int getHoras() {
        return horas;
    }

    public String getHoraFormateada() {
        String horaStr = horas < 10 ? "0" + horas : String.valueOf(horas);
        String minStr = minutos < 10 ? "0" + minutos : String.valueOf(minutos);
        return horaStr + ":" + minStr;
    }
}