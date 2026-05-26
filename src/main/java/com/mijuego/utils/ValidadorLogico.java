package com.mijuego.utils;

import com.badlogic.gdx.utils.Array;
import com.mijuego.screens.CartaActor;

public class ValidadorLogico {

    /**
     * Valida sintácticamente la estructura de la mano jugada.
     * Soporta combinaciones sencillas o estructuras complejas (ej: p | q).
     */
    public static boolean esFormulaValida(Array<String> tokens) {
        if (tokens.size == 0) return false;

        // Regla básica de tokens alternos: Variable -> Conectivo -> Variable
        // Evita cosas como "p q" o "^ |"
        for (int i = 0; i < tokens.size; i++) {
            String t = tokens.get(i);
            boolean esVariable = t.equals("p") || t.equals("q") || t.equals("r") || t.equals("s");

            if (i % 2 == 0) {
                // Posiciones pares DEBEN ser variables o negaciones de inicio
                if (!esVariable) return false;
            } else {
                // Posiciones impares DEBEN ser conectivos
                if (esVariable) return false;
            }
        }
        // Una fórmula válida no puede terminar en un conectivo colgado (ej: p ^)
        return tokens.size % 2 != 0;
    }

    /**
     * Calcula los puntos de la mano basado en las propiedades de Balatro (Fichas x Multiplicador)
     */
    public static int[] calcularPuntaje(Array<String> tokens) {
        int fichas = 0;
        int multiplicador = 1;

        for (String t : tokens) {
            if (t.equals("p") || t.equals("q")) {
                fichas += 30;         // Las variables dan base de fichas
            } else if (t.equals("^")) {
                multiplicador += 2;   // La conjunción exige ambos verdaderos (+Mult)
            } else if (t.equals("|")) {
                multiplicador += 1;   // La disyunción es más permisiva
            }
        }

        // Retorna [Fichas, Multiplicador]
        return new int[]{fichas, multiplicador};
    }
}