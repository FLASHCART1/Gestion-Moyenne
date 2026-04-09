package gestionmoyenne.model;

import java.util.ArrayList;

public class Etudiant {
	private String nom;
	private String prenom;
	private String matricule;
	private ArrayList<Evaluation> evaluations;
	
	//Constructeur
	public Etudiant(String n, String p, String m) {
		nom = n;
		prenom = p;
		matricule = m;
		evaluations = new ArrayList<>();
	}
	
	//getters-setters
	public String getNom() { return nom; }
	public String getPrenom() { return prenom; }
	public String getMatricule() { return matricule; }
	public ArrayList<Evaluation> getEvaluations(){ return evaluations; }
	public void setNom(String nom) { this.nom = nom; }
	public void setPrenom(String prenom) { this.prenom = prenom; }
	public void setMatricule(String matricule) { this.matricule = matricule; }

	//methods
	public void ajouter_note(String nom, double note, double coeff, double bonus) {
		if(note >= 0 && note <= 20 && coeff > 0) { evaluations.add(new Evaluation(nom, note, coeff, bonus)); }
		else { System.out.println("Erreur: Note entre 0-20 et coeff > 0 requis"); }
	}
	public boolean peutCalculerMoyenne() {
        return evaluations.size() >= 2;
    }
	public double calculerMoyenne() {
        if(evaluations.size() < 2) return -1;
        
        double sommePonderee = 0;
        double sommeCoeffs = 0;
        
        for(Evaluation ev : evaluations) {
            double noteFinale = ev.getNote() + ev.getBonus();
            if(noteFinale < 0) noteFinale = 0;
            if(noteFinale > 20) noteFinale = 20;
            
            sommePonderee += noteFinale * ev.getCoeff();
            sommeCoeffs += ev.getCoeff();
        }
        
        return sommeCoeffs > 0 ? sommePonderee / sommeCoeffs : 0;
    }
    
    public void modifierNote(int index, double nouvelleNote) {
        if(index >= 0 && index < evaluations.size()) {
            if(nouvelleNote >= 0 && nouvelleNote <= 20) {
                evaluations.get(index).setNote(nouvelleNote);
                System.out.println("Note modifiée");
            } else {
                System.out.println("Note doit être entre 0 et 20");
            }
        } else {
            System.out.println("Index d'évaluation invalide");
        }
    }
    
    public void appliquerBonusMalus(int indexEvaluation, double bonus) {
        if(indexEvaluation >= 0 && indexEvaluation < evaluations.size()) {
            evaluations.get(indexEvaluation).setBonus(bonus);
            System.out.println("Bonus/Malus appliqué");
        } else {
            System.out.println("Index invalide");
        }
    }
    public void afficher() {
        StringBuilder notesStr = new StringBuilder();
        for(Evaluation ev : evaluations) {
            double val = ev.getNote();
            double bonus = ev.getBonus();
            double finale = val + bonus;
            if(finale < 0) finale = 0;
            if(finale > 20) finale = 20;
            
            notesStr.append(String.format("%s:%.1f", ev.getNom(), finale));
            if(bonus != 0) notesStr.append(String.format("(%+.1f)", bonus));
            notesStr.append(" ");
        }
        
        double moy = calculerMoyenne();
        String moyStr = (moy >= 0) ? String.format("%.2f/20", moy) : "N/A (min 2 notes)";
        
        System.out.printf("%-10s %-15s %-15s %-30s [%-10s]\n", 
            matricule, nom, prenom, notesStr.toString(), moyStr);
    }
}
