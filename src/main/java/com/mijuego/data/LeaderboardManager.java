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
 */
public class LeaderboardManager {

    private static final String FILE_NAME = "leaderboard.json";
    private final FileHandle fileHandle;
    private final Json json;

    public LeaderboardManager() {
        // Gdx.files.local guarda el archivo en el directorio raíz de ejecución del juego
        // Es un directorio seguro con permisos de lectura y escritura garantizados.
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
                    // Ordenamos de mayor a menor puntuación antes de retornar
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
     * Agrega una nueva puntuación. Si califica dentro del top histórico,
     * se insertará, ordenará y guardará de manera permanente.
     */
    public void addScore(String name, int score) {
        ArrayList<PlayerScore> currentScores = getScores();
        currentScores.add(new PlayerScore(name, score));

        // Ordenamos el ranking
        sortScores(currentScores);

        // Opcional: Mantener únicamente el Top 10 para evitar archivos innecesariamente grandes
        if (currentScores.size() > 10) {
            currentScores = new ArrayList<>(currentScores.subList(0, 10));
        }

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
     * Llena el archivo con las puntuaciones icónicas iniciales que diseñaste.
     */
    private void createDefaultScores() {
        ArrayList<PlayerScore> defaultScores = new ArrayList<>();
        defaultScores.add(new PlayerScore("Aristoteles", 50000));
        defaultScores.add(new PlayerScore("Boole", 35000));
        defaultScores.add(new PlayerScore("Turing", 28000));
        saveScores(defaultScores);
    }
}