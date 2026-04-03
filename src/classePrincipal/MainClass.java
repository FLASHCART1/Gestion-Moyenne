package classePrincipal;

import java.util.ArrayList;

public class MainClass {
	private ArrayList<Classe> list;
	
	public MainClass() {
		list = new ArrayList<>();
	}
	
	public void creerClasse(String nom_classe) {
		Classe a = new Classe(nom_classe);
		list.add(a);
	}
	public void afficher() {
		System.out.println("CLASSES:");
		for(Classe c : list) {
			System.out.println("	"+c.getNom());
		}
	}
	public int get_index(String nom_classe) {
		int index = 0;
		for(Classe c : list) {
			if(c.getNom() == nom_classe) {
				break;
			}
			else {
				index++;
			}
		}
		return index;
	}
	public void supprrimer(String nom_classe) {
		int index = get_index(nom_classe);
		list.remove(index);
	}

}
