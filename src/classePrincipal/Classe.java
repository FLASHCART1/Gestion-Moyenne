package classePrincipal;

import java.util.ArrayList;

public class Classe {
	private String nom;
	private ArrayList<Etudiant> etudiants;
	
	//contructeur
	public Classe(String n){
		setNom(n);
		etudiants = new ArrayList<>();
	}
	
	//getter-setter
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	//methods
	public int get_index(String matricule) {

		for(int i = 0; i < etudiants.size(); i++) {
			if(etudiants.get(i).getMatricule().equals(matricule)) {
				return i;
			}
		}
		return -1;
	}
	public void ajouter_Etu(String n, String p, String m) {
		Etudiant e = new Etudiant(n, p, m);
		etudiants.add(e);
	}
	public void retirer_Etu(String matricule) {
		int index = get_index(matricule);
		etudiants.remove(index);
	}
	public void afficher() {
		for(Etudiant a : etudiants) {
			a.afficher();
		}
	}
}
