package gestionmoyenne.model;

import java.util.ArrayList;

public class Classe {
	private String nom;
	private ArrayList<Cours> modules;
	
	//contructeur
	public Classe(String n){
		setNom(n);
		modules = new ArrayList<>();
	}
	
	//getter-setter
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	//methods
	public int get_index(String nom) {

		for(int i = 0; i < modules.size(); i++) {
			if(modules.get(i).getNom().equals(nom)) {
				return i;
			}
		}
		return -1;
	}
	public void ajouter_cours(String n) {
		modules.add(new Cours(n));
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
		for(Cours m : modules) {
			m.afficher();
		}
	}
}
