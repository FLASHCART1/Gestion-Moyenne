package gestionmoyenne.service;

import gestionmoyenne.model.Evaluation;
import java.util.List;

public class Calculateur {
    
    public static double calculerMoyenne(List<Evaluation> evaluations) {
        if(evaluations == null || evaluations.size() < 2) {
            return -1; // Calcul impossible
        }
        
        double sommePonderee = 0;
        double sommeCoeffs = 0;
        
        for(Evaluation ev : evaluations) {
            double noteAvecBonus = ev.getNote() + ev.getBonus();
            // Plafonnement 0-20
            noteAvecBonus = Math.max(0, Math.min(20, noteAvecBonus));
            
            sommePonderee += noteAvecBonus * ev.getCoeff();
            sommeCoeffs += ev.getCoeff();
        }
        
        return sommeCoeffs > 0 ? sommePonderee / sommeCoeffs : 0;
    }
    
    public static boolean peutCalculer(List<Evaluation> evaluations) {
        return evaluations != null && evaluations.size() >= 2;
    }
    
    // Formule personnalisée (basique - peut être étendue)
    public static double calculerAvecFormule(List<Evaluation> evaluations, String formule) {
        // Si formule = "standard", utilise calcul normal
        // Sinon peut implémenter parsing de formule personnalisée
        return calculerMoyenne(evaluations);
    }
}