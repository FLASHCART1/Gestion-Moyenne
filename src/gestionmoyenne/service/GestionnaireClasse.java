package gestionmoyenne.service;

import java.io.IOException;
import java.util.ArrayList;
import gestionmoyenne.model.*;

public class GestionnaireClasse {
    private ArrayList<Classe> classes;
    private final DataStore dataStore;

    public GestionnaireClasse() {
        this.dataStore = new DataStore();
        this.classes   = dataStore.charger();
        if (!classes.isEmpty()) {
            System.out.println("📂 " + classes.size() + " classe(s) chargée(s)");
        }
    }

    // =========================================================================
    //  GESTION DES CLASSES
    // =========================================================================

    public void creerClasse(String nom_classe) {
        if (nom_classe == null || nom_classe.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe est obligatoire");
        }
        if (getClasse(nom_classe) != null) {
            throw new IllegalStateException("Cette classe existe déjà : " + nom_classe);
        }
        classes.add(new Classe(nom_classe));
        //sauvegarder();
        System.out.println("✅ Classe créée : " + nom_classe);
    }

    public void afficher() {
        System.out.println("\n=== CLASSES ===");
        if (classes.isEmpty()) {
            System.out.println("(Aucune classe — créez-en une nouvelle)");
            return;
        }
        for (Classe c : classes) {
            System.out.println("📚 " + c.getNom() + " — " + c.getModules().size() + " cours");
        }
    }

    public Classe getClasse(String nom) {
        for (Classe c : classes) {
            if (c.getNom().equalsIgnoreCase(nom)) return c;
        }
        return null;
    }

    public int get_index(String nom_classe) {
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getNom().equalsIgnoreCase(nom_classe)) return i;
        }
        return -1;
    }

    public void supprimer(String nom_classe) {
        int index = get_index(nom_classe);
        if (index != -1) {
            classes.remove(index);
            //sauvegarder();
            System.out.println("✅ Classe supprimée");
        } else {
            throw new IllegalArgumentException("Classe non trouvée : " + nom_classe);
        }
    }

    // =========================================================================
    //  SAUVEGARDE / CHARGEMENT
    // =========================================================================

    /** Sauvegarde dans le fichier par défaut (data/sauvegarde.json). */
    public void sauvegarder() {
        dataStore.sauvegarder(classes);
    }

    /**
     * Enregistrer sous : exporte les données vers un fichier JSON au chemin indiqué.
     * Le fichier de sauvegarde par défaut n'est pas modifié.
     *
     * @param cheminDestination Chemin absolu du fichier cible (.json)
     * @throws IOException si l'écriture échoue
     */
    public void sauvegarderVers(String cheminDestination) throws IOException {
        dataStore.sauvegarderVers(classes, cheminDestination);
    }

    /**
     * Charger depuis un fichier JSON choisi par l'utilisateur.
     * Les données en mémoire sont entièrement remplacées par le contenu du fichier.
     *
     * @param cheminSource Chemin absolu du fichier JSON à importer
     * @return Nombre de classes chargées
     * @throws IOException si le fichier est illisible ou malformé
     */
    public int chargerDepuis(String cheminSource) throws IOException {
        ArrayList<Classe> nouvelles = dataStore.chargerDepuis(cheminSource);
        this.classes = nouvelles;
        System.out.println("🔄 " + classes.size() + " classe(s) chargée(s) depuis : " + cheminSource);
        return classes.size();
    }

    /**
     * Recharge les données depuis le fichier par défaut
     * (utile après une modification externe du fichier JSON).
     */
    public void recharger() {
        this.classes = dataStore.charger();
        System.out.println("🔄 Données rechargées depuis le fichier par défaut");
    }

    // =========================================================================
    //  ACCESSEURS
    // =========================================================================

    public ArrayList<Classe> getClasses() {
        return new ArrayList<>(classes); // copie défensive
    }

    public Classe selectedClasse(int index) {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException("Index invalide : " + index);
        }
        return classes.get(index);
    }

    public ArrayList<Cours> getCours(Classe classe) {
        if (classe == null) throw new IllegalArgumentException("La classe ne peut pas être null");
        return classe.getModules();
    }

    /**
     * Recherche un étudiant par matricule dans toutes les classes et tous les cours.
     */
    public Etudiant trouverEtudiant(String matricule) {
        for (Classe c : classes) {
            for (Cours cr : c.getModules()) {
                for (Etudiant e : cr.getEtudiants()) {
                    if (e.getMatricule().equalsIgnoreCase(matricule)) return e;
                }
            }
        }
        return null;
    }
}