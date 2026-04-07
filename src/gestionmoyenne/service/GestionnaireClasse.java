package gestionmoyenne.service;

import java.util.ArrayList;
import gestionmoyenne.model.Cours;

public class GestionnaireClasse {
	private ArrayList<Cours> cours;
	
	public GestionnaireClasse() {
		cours = new ArrayList<>();
	}
	
	public void creerClasse(String nom_classe) {
		Cours a = new Cours(nom_classe);
		cours.add(a);
	}
	public void afficher() {
		System.out.println("CLASSES:");
		for(Cours c : cours) {
			System.out.println("	"+c.getNom());
		}
	}
	public int get_index(String nom_classe) {
		for(int i = 0; i < cours.size(); i++) {
			if(cours.get(i).getNom().equals(nom_classe)) {
				return i;
			}
		}
		return -1;
	}
	public void supprrimer(String nom_classe) {
		int index = get_index(nom_classe);
		cours.remove(index);
	}

}
