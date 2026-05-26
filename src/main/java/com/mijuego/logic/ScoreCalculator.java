package com.mijuego.logic;

public class ScoreCalculator {

    public static class ScoreResult {
        public final int fichas;
        public final float multiplicador;
        public final String tipoEstructura;

        public ScoreResult(int fichas, float multiplicador, String tipoEstructura) {
            this.fichas = fichas;
            this.multiplicador = multiplicador;
            this.tipoEstructura = tipoEstructura;
        }

        public int getTotal() {
            return Math.round(fichas * multiplicador);
        }
    }

    public static ScoreResult calcular(String formula, boolean esVerdadera, boolean esTautologia, int totalVariablesJugadas) {
        // Si el resultado de la evaluación es Falso, se otorga puntaje mínimo de consolación sin multiplicador
        if (!esVerdadera) {
            return new ScoreResult(totalVariablesJugadas * 5, 1.0f, "Evaluación Falsa (Consolación)");
        }

        int fichasBase = totalVariablesJugadas * 10; // Cada variable atómica aporta +10 fichas
        float mult = 1.0f;
        String tipo = "Fórmula Simple";

        // Determinar el conector principal de la jugada para asignar el multiplicador base
        if (esTautologia) {
            fichasBase += 100; // Bono de +100 fichas por Tautología Molecular
            mult = 8.0f;
            tipo = "¡TAUTOLOGÍA MOLECULAR!";
        } else if (formula.contains("<->")) {
            mult = 4.0f;
            tipo = "Bicondicional";
        } else if (formula.contains("->")) {
            mult = 3.0f;
            tipo = "Condicional";
        } else if (formula.contains("^")) {
            mult = 2.0f;
            tipo = "Conjunción";
        } else if (formula.contains("|")) {
            mult = 1.5f;
            tipo = "Disjunción";
        }

        return new ScoreResult(fichasBase, mult, tipo);
    }
}