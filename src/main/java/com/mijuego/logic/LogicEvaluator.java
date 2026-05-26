package com.mijuego.logic;

import java.util.Map;

public class LogicEvaluator {

    // Método para normalizar lo que viene de la interfaz gráfica al backend matemático
    public static String normalizarFormula(String formulaVisual) {
        // Convierte la 'v' minúscula o el símbolo lógico '∨' en el pipe '|' requerido por el código
        return formulaVisual.replace("v", "|").replace("∨", "|");
    }

    public static boolean esFBF(String formulaVisual) {
        String formula = normalizarFormula(formulaVisual);
        if (formula.isEmpty()) return false;

        int balance = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        if (balance != 0) return false;

        if (formula.contains("^^") || formula.contains("||") || formula.contains("->->")) return false;
        if (formula.matches(".*[\\^|\\-><\\->]$")) return false;

        return true;
    }
}