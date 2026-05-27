package com.mijuego.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Gestiona el almacenamiento persistente de las puntuaciones de los jugadores
 * utilizando archivos JSON locales en el directorio del juego.
 * Permite registrar a cualquier jugador validando que solo se guarde su récord más alto.
 */
public class LeaderboardManager {

    private static final String FILE_NAME = "leaderboard.json";
    private final FileHandle fileHandle;
    private final Json json;

    public LeaderboardManager() {
        // Gdx.files.local guarda el archivo en el directorio raíz de ejecución del juego
        this.fileHandle = Gdx.files.local(FILE_NAME);
        this.json = new Json();
        this.json.setOutputType(OutputType.json);

        // Inicializa con puntuaciones predeterminadas de filósofos y lógicos si es la primera vez que se abre
        if (!fileHandle.exists()) {
            createDefaultScores();
        }
    }

    /**
     * Recupera la lista completa de puntuaciones guardadas en el archivo JSON.
     * Retorna una lista vacía si hay algún problema o el archivo está vacío.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<PlayerScore> getScores() {
        try {
            if (fileHandle.exists()) {
                ArrayList<PlayerScore> scores = json.fromJson(ArrayList.class, PlayerScore.class, fileHandle);
                if (scores != null) {
                    // Ordenamos de mayor a menor puntuación antes de retornar para mantener la estética
                    sortScores(scores);
                    return scores;
                }
            }
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "Error al leer el archivo de puntuaciones: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Registra la puntuación de cualquier jugador.
     * Si el nombre no existe en la base de datos se registra desde cero.
     * Si ya existe, SOLO reemplaza su puntuación si la nueva es estrictamente MAYOR que su récord actual.
     */
    public void addScore(String name, int score) {
        ArrayList<PlayerScore> currentScores = getScores();
        PlayerScore jugadorExistente = null;
        String nombreLimpio = name.trim();

        // Buscamos si el nombre ya está registrado en el archivo JSON
        for (PlayerScore ps : currentScores) {
            if (ps.getName().equalsIgnoreCase(nombreLimpio)) {
                jugadorExistente = ps;
                break;
            }
        }

        if (jugadorExistente == null) {
            // Caso 1: No está en la base de datos -> Se registra de inmediato
            currentScores.add(new PlayerScore(nombreLimpio, score));
            Gdx.app.log("Leaderboard", "Nuevo jugador registrado: " + nombreLimpio + " con " + score + " puntos.");
        } else {
            // Caso 2: Ya existe -> SOLO se actualiza si la puntuación obtenida supera su récord guardado
            if (score > jugadorExistente.getScore()) {
                Gdx.app.log("Leaderboard", "¡Nuevo récord para " + nombreLimpio + "! Puntuación anterior: "
                        + jugadorExistente.getScore() + " -> Nueva puntuación: " + score);
                jugadorExistente.setScore(score);
            } else {
                Gdx.app.log("Leaderboard", "Puntuación de " + nombreLimpio + " (" + score
                        + ") es menor o igual al récord existente (" + jugadorExistente.getScore() + "). No se actualiza.");
                return; // Cortamos el flujo aquí para evitar sobreescribir el JSON innecesariamente
            }
        }

        // Ordenamos el ranking para que persista organizado jerárquicamente
        sortScores(currentScores);

        // Guardamos la lista completa. Al no recortar la lista, crecerá dinámicamente sin límite de usuarios
        saveScores(currentScores);
    }

    /**
     * Guarda la lista actual de puntuaciones sobrescribiendo el archivo local.
     */
    private void saveScores(ArrayList<PlayerScore> scores) {
        try {
            fileHandle.writeString(json.prettyPrint(scores), false);
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "No se pudo escribir en el archivo de puntuaciones: " + e.getMessage());
        }
    }

    /**
     * Ordena la lista de puntuaciones de mayor a menor utilizando un comparador numérico estándar.
     */
    private void sortScores(ArrayList<PlayerScore> scores) {
        Collections.sort(scores, new Comparator<PlayerScore>() {
            @Override
            public int compare(PlayerScore o1, PlayerScore o2) {
                return Integer.compare(o2.getScore(), o1.getScore()); // Mayor a menor
            }
        });
    }

    /**
     * Llena el archivo con las puntuaciones icónicas iniciales del juego.
     */
    private void createDefaultScores() {
        ArrayList<PlayerScore> defaultScores = new ArrayList<>();
        defaultScores.add(new PlayerScore("Aristoteles", 50000));
        defaultScores.add(new PlayerScore("Boole", 35000));
        defaultScores.add(new PlayerScore("Turing", 28000));
        saveScores(defaultScores);
    }
}