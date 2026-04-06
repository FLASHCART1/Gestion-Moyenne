package classePrincipal;

import java.util.ArrayList;

public class Etudiant {
	private String nom;
	private String prenom;
	private String matricule;
	private ArrayList<Evaluation> list;
	
	//Constructeur
	public Etudiant(String n, String p, String m) {
		nom = n;
		prenom = p;
		matricule = m;
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
	
	public String getMatricule() {
		return matricule;
	}

	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}

	//methods
	public void ajouter_note(String nom, double n, double c, double b) {
		if((n >= 0 || n <= 20 ) && (c > 0)) { list.add(new Evaluation(nom, n, c, b)); }
	}
	public void afficher() {
		String notes = null;
		for(Evaluation a : list) {
			notes = a.getNote() + "  " + a.getCoeff() + "  " + a.getBonus();
		}
		System.out.printf("%-20s %-20s %-30s %-9s\n",matricule, nom, prenom, notes);
	}
}
