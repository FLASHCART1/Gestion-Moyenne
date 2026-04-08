package gestionmoyenne.model;

import java.util.ArrayList;

public class Cours {
	private String nom;
	private int volumeHoraire;
	private ArrayList<Etudiant> etudiants;
	private ArrayList<Evaluation> modelesEvaluations;
	
	//contructeur
	public Cours(String n, int volumeHoraire){
		setNom(n);
		this.volumeHoraire = volumeHoraire;
		etudiants = new ArrayList<>();
		modelesEvaluations = new ArrayList<>();
		
		ajouterTypeEvaluation("Evaluation 1", 1.0);
        ajouterTypeEvaluation("Evaluation 2", 1.0);
	}
	
	//getter-setter
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public int getVolumeHoraire() { return volumeHoraire; }
	public ArrayList<Etudiant> getEtudiants() { return etudiants; }
	public ArrayList<Evaluation> getModelesEvaluations() { return modelesEvaluations; }
	
	//methods
	public int get_index(String matricule) {
		for(int i = 0; i < etudiants.size(); i++) {
			if(etudiants.get(i).getMatricule().equals(matricule)) {
				return i;
			}
		}
		return -1;
	}
	public void ajouterTypeEvaluation(String nomEval, double coef) {
        modelesEvaluations.add(new Evaluation(nomEval, 0, coef, 0));
        for(Etudiant e : etudiants) {
            e.ajouter_note(nomEval, 0, coef, 0);
        }
    }
	public void ajouter_Etu(String nom, String prenom, String matricule) {
        Etudiant e = new Etudiant(nom, prenom, matricule);
        for(Evaluation ev : modelesEvaluations) {
            e.ajouter_note(ev.getNom(), 0, ev.getCoeff(), 0);
        }
        etudiants.add(e);
    }
	public void ajouter_Etu_Existant(Etudiant e) {
        // Compléter avec les évaluations manquantes si besoin
        int nbEvalsManquantes = modelesEvaluations.size() - e.getEvaluations().size();
        for(int i = e.getEvaluations().size(); i < modelesEvaluations.size(); i++) {
            Evaluation modele = modelesEvaluations.get(i);
            e.ajouter_note(modele.getNom(), 0, modele.getCoeff(), 0);
        }
        etudiants.add(e);
    }

	public void retirer_Etu(String matricule) {
		int index = get_index(matricule);if (index != -1) {
            etudiants.remove(index);
            System.out.println("Étudiant retiré");
        } else {
            System.out.println("Matricule non trouvé");
        }
    }
	public void afficher() {
        System.out.println("\n Cours: " + nom + " (" + volumeHoraire + "h)");
        System.out.println("Évaluations: " + modelesEvaluations.size() + " types");
        System.out.println("Étudiants (" + etudiants.size() + "):");
        System.out.printf("%-10s %-15s %-15s %-30s %s\n", "MATRICULE", "NOM", "PRENOM", "NOTES", "MOYENNE");
        for(Etudiant e : etudiants) {
            e.afficher();
        }
    }
}
