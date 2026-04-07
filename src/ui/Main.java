package ui;

import java.util.Scanner;

import gestionmoyenne.*;
import gestionmoyenne.service.GestionnaireClasse;

public class Main {
	public static void mainMenu() {
		System.out.println("╔══════════════════════════════╗");
		System.out.println("║  GESTION DE MOYENNE EN JAVA  ║");
		System.out.println("╚══════════════════════════════╝");
		System.out.println("1. Selectionner une classe");
		System.out.println("2. Charger une classe");
		System.out.println("3. Créer une classe");
		System.out.println("4. Supprimer une classe");
		System.out.println("5. Afficher les classes ");
		System.out.println("6. Quitter");
		System.out.print("→  Votre choix:");
	}
	public static void main(String[] arg) {
		GestionnaireClasse app = new GestionnaireClasse();
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		int choix;
		do {
			mainMenu();
			choix = in.nextInt();
			switch(choix) {
				case 1: {
					app.afficher();
					
				}
			}
		}while (choix != 6);
		
	}
}
