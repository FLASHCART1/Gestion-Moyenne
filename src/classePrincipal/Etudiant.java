package classePrincipal;

import java.util.ArrayList;

public class Etudiant {
	private String nom;
	private String prenom;
	private ArrayList<Evaluation> list;
	
	public void ajouter_note(double n, double c, int b) {
		Evaluation a = new Evaluation(n, c, b);
		list.add(a);
		
	}
}
