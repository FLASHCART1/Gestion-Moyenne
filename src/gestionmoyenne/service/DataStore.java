package gestionmoyenne.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gestionmoyenne.model.Classe;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import gestionmoyenne.model.Evaluation;

public class DataStore {
    private static final String DOSSIER_DATA       = "data";
    private static final String FICHIER_SAUVEGARDE = DOSSIER_DATA + "/sauvegarde.gdm";
    private static final String FICHIER_BACKUP     = DOSSIER_DATA + "/sauvegarde_backup.gdm";

    private final Gson gson;

    public DataStore() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

        File dossier = new File(DOSSIER_DATA);
        if (!dossier.exists()) {
            dossier.mkdir();
        }
    }

    // =========================================================================
    //  SAUVEGARDE PAR DÉFAUT
    // =========================================================================

    public void sauvegarder(ArrayList<Classe> classes) {
        if (classes == null) return;
        ecrireFichier(classes, FICHIER_SAUVEGARDE, FICHIER_BACKUP);
    }

    // =========================================================================
    //  ENREGISTRER SOUS  (chemin personnalisé — copie, pas remplacement)
    // =========================================================================

    /**
     * Exporte les données vers un fichier JSON choisi par l'utilisateur.
     * Le fichier de sauvegarde par défaut n'est pas modifié.
     *
     * @param cheminDestination Chemin absolu du fichier de destination
     * @throws IOException si l'écriture échoue
     */
    public void sauvegarderVers(ArrayList<Classe> classes, String cheminDestination) throws IOException {
        if (classes == null || cheminDestination == null || cheminDestination.isBlank()) {
            throw new IllegalArgumentException("Paramètres invalides pour l'enregistrement sous");
        }

        File destination = new File(cheminDestination);
        File parent = destination.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Writer writer = new FileWriter(destination)) {
            gson.toJson(classes, writer);
            System.out.println("💾 Enregistrement sous → " + cheminDestination
                               + " (" + classes.size() + " classes)");
        }
    }

    // =========================================================================
    //  CHARGEMENT PAR DÉFAUT
    // =========================================================================

    public ArrayList<Classe> charger() {
        File fichier = new File(FICHIER_SAUVEGARDE);
        if (!fichier.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(FICHIER_SAUVEGARDE)) {
            ArrayList<Classe> classes = lireJson(reader);
            synchroniserCompteurs(classes);
            return classes;
        } catch (IOException e) {
            System.err.println("❌ Erreur chargement : " + e.getMessage());
            return chargerBackup();
        }
    }

    // =========================================================================
    //  CHARGER DEPUIS UN FICHIER PERSONNALISÉ
    // =========================================================================

    /**
     * Charge les données depuis un fichier JSON choisi par l'utilisateur.
     * Les compteurs statiques sont resynchronisés après le chargement.
     *
     * @param cheminSource Chemin absolu du fichier JSON à importer
     * @return La liste des classes chargées
     * @throws IOException si le fichier est illisible ou malformé
     */
    public ArrayList<Classe> chargerDepuis(String cheminSource) throws IOException {
        if (cheminSource == null || cheminSource.isBlank()) {
            throw new IllegalArgumentException("Chemin de fichier invalide");
        }

        File fichier = new File(cheminSource);
        if (!fichier.exists()) {
            throw new IOException("Fichier introuvable : " + cheminSource);
        }
        if (!fichier.getName().toLowerCase().endsWith(".json") && !fichier.getName().toLowerCase().endsWith(".gdm")) {
            throw new IllegalArgumentException("Le fichier doit avoir l'extension '.json' ou '.gdm'");
        }

        try (Reader reader = new FileReader(fichier)) {
            ArrayList<Classe> classes = lireJson(reader);
            synchroniserCompteurs(classes);
            System.out.println("📂 " + classes.size() + " classe(s) chargée(s) depuis : " + cheminSource);
            return classes;
        }
    }

    // =========================================================================
    //  HELPERS PRIVÉS
    // =========================================================================

    /** Écrit les classes dans un fichier, avec gestion de backup. */
    private void ecrireFichier(ArrayList<Classe> classes, String cible, String backup) {
        // Rotation du backup
        File ancienFichier = new File(cible);
        if (ancienFichier.exists()) {
            ancienFichier.renameTo(new File(backup));
        }

        try (Writer writer = new FileWriter(cible)) {
            gson.toJson(classes, writer);
            System.out.println("💾 Sauvegarde effectuée (" + classes.size() + " classes)");
        } catch (IOException e) {
            System.err.println("❌ Erreur sauvegarde : " + e.getMessage());
            // Restaurer le backup si l'écriture a échoué
            File backupFile = new File(backup);
            if (backupFile.exists()) {
                backupFile.renameTo(new File(cible));
            }
        }
    }

    /** Tente de charger le fichier de backup en cas d'échec du fichier principal. */
    private ArrayList<Classe> chargerBackup() {
        File backup = new File(FICHIER_BACKUP);
        if (!backup.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(backup)) {
            ArrayList<Classe> classes = lireJson(reader);
            System.out.println("📂 Chargement depuis backup réussi");
            synchroniserCompteurs(classes);
            return classes;
        } catch (IOException e) {
            System.err.println("❌ Backup corrompu aussi");
            return new ArrayList<>();
        }
    }

    /** Désérialise un Reader JSON en liste de Classe. */
    private ArrayList<Classe> lireJson(Reader reader) {
        ArrayList<Classe> classes = gson.fromJson(reader,
            new com.google.gson.reflect.TypeToken<ArrayList<Classe>>() {}.getType());
        return classes != null ? classes : new ArrayList<>();
    }

    /**
     * Resynchronise tous les compteurs statiques après une désérialisation
     * pour éviter les doublons d'ID.
     */
    private void synchroniserCompteurs(ArrayList<Classe> classes) {
        int maxClasseId = 0, maxCoursId = 0, maxEtudiantId = 0, maxEvalId = 0;

        for (Classe c : classes) {
            maxClasseId = Math.max(maxClasseId, c.getId());
            for (Cours cr : c.getModules()) {
                maxCoursId = Math.max(maxCoursId, cr.getId());
                for (Etudiant e : cr.getEtudiants()) {
                    maxEtudiantId = Math.max(maxEtudiantId, e.getId());
                    for (Evaluation ev : e.getEvaluations()) {
                        maxEvalId = Math.max(maxEvalId, ev.getId());
                    }
                }
            }
        }

        Classe.synchroniserCompteur(maxClasseId);
        Cours.synchroniserCompteur(maxCoursId);
        Etudiant.synchroniserCompteur(maxEtudiantId);
        Evaluation.synchroniserCompteur(maxEvalId);

        System.out.println("🔢 Compteurs synchronisés : C=" + maxClasseId
            + " | Cr=" + maxCoursId
            + " | E="  + maxEtudiantId
            + " | Ev=" + maxEvalId);
    }
}