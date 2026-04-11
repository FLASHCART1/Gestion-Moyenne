package gestionmoyenne.model;

import java.util.ArrayList;

public class Classe {
	private static int compteur = 0;
	private final int id;
	
	private String nom;
	private ArrayList<Cours> modules;
	
	public Classe(String n){
		this.id = ++compteur;
		setNom(n);
		modules = new ArrayList<>();
	}
	
	public static int getCompteur() { return compteur; }
	public static void resetCompteur() { compteur = 0; }
	public int getId() { return id; }
	
	public String getNom() { return nom; }
	public ArrayList<Cours> getModules() { return modules; }
	public void setNom(String nom) { this.nom = nom; }
	
	public int get_index(String nom) {
		for(int i = 0; i < modules.size(); i++) {
			if(modules.get(i).getNom().equals(nom)) {
				return i;
			}
		}
		return -1;
	}
	
	public void ajouter_cours(String nom, int volumeHoraire) {
		modules.add(new Cours(nom, volumeHoraire));
	}
	
	public void supprimer_cours(String nomCours) {
        int index = get_index(nomCours);
        if (index != -1) {
            modules.remove(index);
            System.out.println("✅ Cours supprimé");
        } else {
            System.out.println("❌ Cours non trouvé");
        }
    }
	
	public void afficher() {
		System.out.println("\n========================================");
		System.out.println("CLASSE #" + id + " : " + nom);
		System.out.println("Nombre de cours: " + modules.size());
		System.out.println("----------------------------------------");
		for(Cours m : modules) {
			m.afficher();
		}
		System.out.println("========================================");
	}
	
	public void afficherStatistiques() {
		System.out.println("\n--- Statistiques Globales ---");
		System.out.println("Classes créées: " + Classe.getCompteur());
		System.out.println("Cours créés: " + Cours.getCompteur());
		System.out.println("Étudiants créés: " + Etudiant.getCompteur());
		System.out.println("Évaluations créées: " + Evaluation.getCompteur());
	}
}