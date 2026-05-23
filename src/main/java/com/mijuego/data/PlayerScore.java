package com.mijuego.data;

/**
 * Representa la puntuación obtenida por un jugador.
 * Implementa una estructura simple compatible con el serializador JSON de LibGDX.
 */
public class PlayerScore {
    private String name;
    private int score;

    // Constructor vacío obligatorio para la serialización/deserialización de LibGDX Json
    public PlayerScore() {
    }

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}