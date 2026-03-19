package classePrincipal;

import java.util.ArrayList;

public class Etudiant {
	private String nom;
	private String prenom;
	private ArrayList<Evaluation> list;
	
	public Etudiant(String n, String p) {
		
	}
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
	public void ajouter_note(double n, double c, int b) {
		Evaluation a = new Evaluation(n, c, b);
		list.add(a);
	}
}
