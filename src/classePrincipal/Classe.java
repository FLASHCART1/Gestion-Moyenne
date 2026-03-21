package classePrincipal;

import java.util.ArrayList;

public class Classe {
	private ArrayList<Etudiant> list_classe;
	
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
