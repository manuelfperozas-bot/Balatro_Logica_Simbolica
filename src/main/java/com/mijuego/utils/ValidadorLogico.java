package com.mijuego.utils;

import com.badlogic.gdx.utils.Array;

public class ValidadorLogico {

    /**
     * Valida si la secuencia de cartas jugada en el tablero tiene sentido sintáctico.
     * Soporta combinaciones como: "p", "~ p", "p ^ ~ q", "~ p | ~ q -> r", etc.
     */
    public static boolean esFormulaValida(Array<String> tokens) {
        if (tokens == null || tokens.size == 0) return false;

        // Estado inicial: esperamos una variable o una negación
        // true = esperamos operando (variable o '~')
        // false = esperamos conector binario ('^', '|', '->', '<->')
        boolean esperandoOperando = true;

        for (int i = 0; i < tokens.size; i++) {
            String token = tokens.get(i);

            if (esperandoOperando) {
                if (token.equals("~")) {
                    // La negación es válida aquí, y seguimos esperando una variable después de ella
                    // Un operador '~' no puede ser el último elemento de la fórmula
                    if (i == tokens.size - 1) return false;
                    esperandoOperando = true;
                } else if (esVariable(token)) {
                    // Encontró p, q, r o s. Ahora el juego espera un conector binario
                    esperandoOperando = false;
                } else {
                    // Si encuentra un '^', '|', etc. al inicio o tras otro conector, es inválido
                    return false;
                }
            } else {
                // Esperamos un conector binario
                if (esConectorBinario(token)) {
                    // Es válido. El conector no puede estar al final de la jugada
                    if (i == tokens.size - 1) return false;
                    esperandoOperando = true; // Tras el conector, toca otra variable o negación
                } else {
                    // Si encuentra dos variables seguidas (ej: "p q"), es inválido
                    return false;
                }
            }
        }

        // Si terminamos procesando correctamente y no nos quedamos esperando una variable colgada
        return !esperandoOperando;
    }

    /**
     * Calcula el puntaje (Chips y Multiplicador) procesando la combinación de la jugada.
     */
    public static int[] calcularPuntaje(Array<String> tokens) {
        // [0] = Chips base, [1] = Multiplicador
        int[] resultado = new int[]{10, 1};

        // 1. Clonar los tokens para no alterar la mano original al procesar
        Array<String> expresion = new Array<>(tokens);

        // 2. Procesar primero las negaciones combinadas (~p, ~q, etc.)
        // Si detecta un "~" seguido de una variable, calcula su valor o asigna bonus por combo
        for (int i = 0; i < expresion.size - 1; i++) {
            if (expresion.get(i).equals("~")) {
                // Otorgamos un bonus al multiplicador por lograr una combinación unaria
                resultado[1] += 1;
                resultado[0] += 15; // Más puntos de fichas
            }
        }

        // 3. Procesar conectores binarios jugados en cadena
        for (String token : expresion) {
            if (token.equals("^")) {
                resultado[0] += 20;
                resultado[1] *= 2; // La conjunción combinada duplica el multi
            } else if (token.equals("|")) {
                resultado[0] += 30;
                resultado[1] += 1;
            } else if (token.equals("->")) {
                resultado[0] += 40;
                resultado[1] += 2;
            } else if (token.equals("<->")) {
                resultado[0] += 50;
                resultado[1] *= 3; // Gran premio por lograr una equivalencia combinada
            }
        }

        return resultado;
    }

    private static boolean esVariable(String token) {
        return token.equals("p") || token.equals("q") || token.equals("r") || token.equals("s");
    }

    private static boolean esConectorBinario(String token) {
        return token.equals("^") || token.equals("|") || token.equals("->") || token.equals("<->");
    }
}