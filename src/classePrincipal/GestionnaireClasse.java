package classePrincipal;

import java.util.ArrayList;

public class GestionnaireClasse {
	private ArrayList<Classe> classes;
	
	public GestionnaireClasse() {
		classes = new ArrayList<>();
	}
	
	public void creerClasse(String nom_classe) {
		Classe a = new Classe(nom_classe);
		classes.add(a);
	}
	public void afficher() {
		System.out.println("CLASSES:");
		for(Classe c : classes) {
			System.out.println("	"+c.getNom());
		}
	}
	public int get_index(String nom_classe) {
		for(int i = 0; i < classes.size(); i++) {
			if(classes.get(i).getNom().equals(nom_classe)) {
				return i;
			}
		}
		return -1;
	}
	public void supprrimer(String nom_classe) {
		int index = get_index(nom_classe);
		classes.remove(index);
	}

}
