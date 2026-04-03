package classePrincipal;

import java.util.ArrayList;

public class Classe {
	private String nom;
	private ArrayList<Etudiant> list_classe;
	
	//contructeur
	public Classe(String n){
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
	public int get_index(String matricule) {
		int index = 0;
		for(Etudiant e : list_classe) {
			if(e.getMatricule() == matricule) {
				break;
			}
			else {
				index++;
			}
		}
		return index;
	}
	public void ajouter_Etu(String n, String p) {
		Etudiant e = new Etudiant(n, p);
		list_classe.add(e);
	}
	public void retirer_Etu(String matricule) {
		int index = get_index(matricule);
		list_classe.remove(index);
	}
	public void afficher() {
		for(Etudiant a : list_classe) {
			a.afficher();
		}
	}
}
