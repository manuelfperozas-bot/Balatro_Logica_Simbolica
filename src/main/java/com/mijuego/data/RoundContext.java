package com.mijuego.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoundContext {
    private Map<Character, Boolean> estadoMundo;
    private int jugadasRestantes;
    private int descartesRestantes;
    private int fichasAcumuladas;
    private int objetivoFichas;

    public RoundContext(int objetivoFichas) {
        this.objetivoFichas = objetivoFichas;
        this.jugadasRestantes = 4; // Valores estándar por ronda
        this.descartesRestantes = 5;
        this.fichasAcumuladas = 0;
        this.estadoMundo = new HashMap<>();
        generarEstadoMundo();
    }

    private void generarEstadoMundo() {
        Random rand = new Random();
        // Definir de forma aleatoria el estado de verdad de las 4 variables atómicas permitidas
        estadoMundo.put('p', rand.nextBoolean());
        estadoMundo.put('q', rand.nextBoolean());
        estadoMundo.put('r', rand.nextBoolean());
        estadoMundo.put('s', rand.nextBoolean());
    }

    public Map<Character, Boolean> getEstadoMundo() { return estadoMundo; }
    public int getJugadasRestantes() { return jugadasRestantes; }
    public int getDescartesRestantes() { return descartesRestantes; }
    public int getFichasAcumuladas() { return fichasAcumuladas; }
    public int getObjetivoFichas() { return objetivoFichas; }

    public void usarJugada() { this.jugadasRestantes--; }
    public void usarDescarte() { this.descartesRestantes--; }
    public void sumarFichas(int cantidad) { this.fichasAcumuladas += cantidad; }

    public boolean estaGanada() { return fichasAcumuladas >= objetivoFichas; }
    public boolean estaPerdida() { return jugadasRestantes <= 0 && fichasAcumuladas < objetivoFichas; }
}