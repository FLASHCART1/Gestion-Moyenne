package classePrincipal;

import java.util.ArrayList;

public class Classe {
	private String nom;
	private ArrayList<Etudiant> list_classe;
	
	//contructeur
	Classe(String n){
		setNom(n);
		list_classe = new ArrayList<>();
	}
	
	//getter-setter
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	//methods
	public void ajouter_Etu(String n, String p) {
		Etudiant e = new Etudiant(n, p);
		list_classe.add(e);
	}
	public void retirer_Etu(Etudiant e) {
		list_classe.remove(e);
	}
	public void afficher() {
		for(Etudiant a : list_classe) {
			a.afficher();
		}
	}
}
