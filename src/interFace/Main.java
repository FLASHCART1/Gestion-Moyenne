package interFace;

import classePrincipal.*;
import java.util.Scanner;

public class Main {
	public static void mainMenu() {
		System.out.println("╔══════════════════════════════════╗");
		System.out.println("║   GESTION DE MOYENNE EN JAVA     ║");
		System.out.println("╚══════════════════════════════════╝");
		System.out.println("1. Charger une classe");
		System.out.println("2. Créer une classe");
		System.out.println("3. Supprimer une classe");
		System.out.println("4. Afficher les classes ");
		System.out.println("5. Quitter");
		System.out.print("→  Votre choix:");
	}
	public static void main(String[] arg) {
		
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		int choix;
		do {
			mainMenu();
			choix = in.nextInt();
		}while (choix != 5);
		
	}
}
