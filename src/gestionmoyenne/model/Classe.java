package gestionmoyenne.model;

import java.util.ArrayList;
import java.util.Objects;

public class Classe {
    private static int compteur = 0; // Package-private, pas d'accès externe
    private final int id;
    
    private String nom;
    private ArrayList<Cours> modules;
    
    public Classe(String n) {
        if (n == null || n.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe ne peut pas être vide");
        }
        this.id = ++compteur;
        this.nom = n.trim();
        this.modules = new ArrayList<>();
    }
    
    // CONSTRUCTEUR pour désérialisation JSON (Gson)
    @SuppressWarnings("unused")
	private Classe() {
        this.id = 0; // Sera recalculé par le gestionnaire
        this.nom = "";
        this.modules = new ArrayList<>();
    }
    
    // Package-private : seul le gestionnaire peut synchroniser après chargement
    public static void synchroniserCompteur(int maxId) {
        if (maxId > compteur) {
            compteur = maxId;
        }
    }
    
    public static int getCompteur() { return compteur; }
    // SUPPRESSION de resetCompteur() - DANGEREUX
    
    public int getId() { return id; }
    public String getNom() { return nom; }
    public ArrayList<Cours> getModules() { return new ArrayList<>(modules); } // Copie défensive
    
    public void setNom(String nom) { 
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.nom = nom.trim(); 
    }
    
    public int get_index(String nom) {
        for(int i = 0; i < modules.size(); i++) {
            if(modules.get(i).getNom().equalsIgnoreCase(nom)) {
                return i;
            }
        }
        return -1;
    }
    
    public void ajouter_cours(String nom, int volumeHoraire) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du cours ne peut pas être vide");
        }
        if (volumeHoraire <= 0) {
            throw new IllegalArgumentException("Le volume horaire doit être positif");
        }
        if (get_index(nom) != -1) {
            throw new IllegalStateException("Un cours avec ce nom existe déjà dans cette classe");
        }
        modules.add(new Cours(nom, volumeHoraire));
    }
    
    public void supprimer_cours(String nomCours) {
        int index = get_index(nomCours);
        if (index != -1) {
            modules.remove(index);
        } else {
            throw new IllegalArgumentException("Cours non trouvé: " + nomCours);
        }
    }
    
    public void afficher() {
        System.out.println("\n========================================");
        System.out.println("CLASSE #" + id + " : " + nom);
        System.out.println("Nombre de cours: " + modules.size());
        System.out.println("----------------------------------------");
        for(Cours m : modules) {
            m.afficher();
        }
        System.out.println("========================================");
    }
    
    public void afficherStatistiques() {
        int totalEtudiants = 0;
        int totalEvaluations = 0;
        for (Cours c : modules) {
            totalEtudiants += c.getEtudiants().size();
            totalEvaluations += c.getModelesEvaluations().size();
        }
        
        System.out.println("\n--- Statistiques Globales ---");
        System.out.println("Classes créées: " + Classe.getCompteur());
        System.out.println("Cours créés: " + Cours.getCompteur());
        System.out.println("Étudiants créés: " + Etudiant.getCompteur());
        System.out.println("Évaluations créées: " + Evaluation.getCompteur());
        System.out.println("Total étudiants dans cette classe: " + totalEtudiants);
        System.out.println("Total évaluations dans cette classe: " + totalEvaluations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classe)) return false;
        Classe classe = (Classe) o;
        return id == classe.id || nom.equalsIgnoreCase(classe.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom.toLowerCase());
    }
}