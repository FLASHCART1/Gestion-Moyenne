package classePrincipal;

import java.util.ArrayList;

public class Etudiant {
	private String nom;
	private String prenom;
	private ArrayList<Evaluation> list;
	
	//Constructeur
	public Etudiant(String n, String p) {
		nom = n;
		prenom = p;
		list = new ArrayList<>();
	}
	
	//getters-setters
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	//methods
	public void ajouter_note(double n, double c, int b) {
		Evaluation eva = new Evaluation(n, c, b);
		list.add(eva);
	}
	public void afficher() {
		String notes = null;
		for(Evaluation a : list) {
			notes = a.getNote() + "  " + a.getCoeff() + "  " + a.getBonus();
		}
		System.out.printf("%-20s %-30s %-9s\n", nom, prenom, notes);
	}
}
