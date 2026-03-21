package interFace;

import classePrincipal.*;

public class Main {
	public static void main(String[] arg) {
		Etudiant etu1 = new Etudiant("azerty", "ytreza");
		int a1 = 20, a2 = 345, a3 = 700;
		int b1 = 56720, b2 = 9999, b3 = 20098;
		int c1 = 233, c2 = 205, c3 = 1;
		int d1 = 34, d2 = 0, d3 = 23;
		etu1.ajouter_note(a1, a2, a3);
		etu1.afficher();

	}
}
