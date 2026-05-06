package gestionmoyenne.service;

import gestionmoyenne.model.Evaluation;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Calculateur {
    
    public static final String FORMULE_STANDARD = "STANDARD";
    
    public static double calculerMoyenne(List<Evaluation> evaluations) {
        if(!peutCalculer(evaluations)) {
            return Double.NaN;
        }
        
        double sommePonderee = 0;
        double sommeCoeffs = 0;
        
        for(Evaluation ev : evaluations) {
            double noteAvecBonus = ev.getNoteFinale(); // Utilise la méthode centralisée
            sommePonderee += noteAvecBonus * ev.getCoeff();
            sommeCoeffs += ev.getCoeff();
        }
        
        return sommeCoeffs > 0 ? sommePonderee / sommeCoeffs : 0;
    }
    
    public static boolean peutCalculer(List<Evaluation> evaluations) {
        return evaluations != null && evaluations.size() >= 2;
    }
    
    /**
     * Calcul avec formule personnalisée
     * @param evaluations Liste des évaluations
     * @param formule "STANDARD" ou expression comme "E1*0.4+E2*0.6"
     * @return La moyenne calculée ou NaN si impossible
     */
    public static double calculerAvecFormule(List<Evaluation> evaluations, String formule) {
        if (formule == null || formule.trim().isEmpty() || formule.equalsIgnoreCase(FORMULE_STANDARD)) {
            return calculerMoyenne(evaluations);
        }
        
        if (!peutCalculer(evaluations)) {
            return Double.NaN;
        }
        
        // Parser une formule simple type "E1*0.4+E2*0.6"
        // où E1, E2 correspondent aux indices des évaluations
        try {
            return evaluerFormule(evaluations, formule);
        } catch (Exception e) {
            System.err.println("Erreur parsing formule: " + e.getMessage());
            return calculerMoyenne(evaluations); // Fallback sécurisé
        }
    }
    
    private static double evaluerFormule(List<Evaluation> evaluations, String formule) {
        // Implémentation basique : remplace E0, E1... par les valeurs
        String result = formule.toUpperCase();
        
        for (int i = 0; i < evaluations.size(); i++) {
            String placeholder = "E" + i;
            double valeur = evaluations.get(i).getNoteFinale();
            result = result.replace(placeholder, String.valueOf(valeur));
        }
        
        // Évaluation sécurisée (éviter eval() dangereux)
        return evaluerExpressionSimple(result);
    }
    
    private static double evaluerExpressionSimple(String expr) {
        // Parser simple pour expressions du type "15.5*0.4+12.0*0.6"
        // Pour une vraie application, utiliser une librairie comme exp4j
        String[] parties = expr.split("\\+");
        double total = 0;
        
        for (String partie : parties) {
            String[] facteurs = partie.split("\\*");
            if (facteurs.length == 2) {
                double val1 = Double.parseDouble(facteurs[0].trim());
                double val2 = Double.parseDouble(facteurs[1].trim());
                total += val1 * val2;
            } else {
                total += Double.parseDouble(partie.trim());
            }
        }
        
        return total;
    }
    
    /**
     * Calcule les statistiques de classe
     */
    public static Map<String, Double> calculerStatistiquesClasse(List<Double> moyennes) {
        Map<String, Double> stats = new HashMap<>();
        
        if (moyennes == null || moyennes.isEmpty()) {
            stats.put("moyenne", 0.0);
            stats.put("min", 0.0);
            stats.put("max", 0.0);
            return stats;
        }
        
        double sum = 0, min = 20, max = 0;
        int count = 0;
        
        for (Double m : moyennes) {
            if (!Double.isNaN(m)) {
                sum += m;
                min = Math.min(min, m);
                max = Math.max(max, m);
                count++;
            }
        }
        
        stats.put("moyenne", count > 0 ? sum / count : 0);
        stats.put("min", min);
        stats.put("max", max);
        return stats;
    }
}