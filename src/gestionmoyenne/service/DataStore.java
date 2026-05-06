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
    private static final String DOSSIER_DATA = "data";
    private static final String FICHIER_SAUVEGARDE = DOSSIER_DATA + "/sauvegarde.json";
    private static final String FICHIER_BACKUP = DOSSIER_DATA + "/sauvegarde_backup.json";
    private Gson gson;
    
    public DataStore() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
            
        File dossier = new File(DOSSIER_DATA);
        if(!dossier.exists()) {
            dossier.mkdir();
        }
    }
    
    public void sauvegarder(ArrayList<Classe> classes) {
        if (classes == null) return;
        
        // Backup de la sauvegarde précédente
        File ancien = new File(FICHIER_SAUVEGARDE);
        if (ancien.exists()) {
            ancien.renameTo(new File(FICHIER_BACKUP));
        }
        
        try (Writer writer = new FileWriter(FICHIER_SAUVEGARDE)) {
            gson.toJson(classes, writer);
            System.out.println("💾 Sauvegarde effectuée (" + classes.size() + " classes)");
        } catch (IOException e) {
            System.err.println("❌ Erreur sauvegarde: " + e.getMessage());
            // Restaurer backup si échec
            File backup = new File(FICHIER_BACKUP);
            if (backup.exists()) {
                backup.renameTo(new File(FICHIER_SAUVEGARDE));
            }
        }
    }
    
    public ArrayList<Classe> charger() {
        File fichier = new File(FICHIER_SAUVEGARDE);
        if(!fichier.exists()) {
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(FICHIER_SAUVEGARDE)) {
            ArrayList<Classe> classes = gson.fromJson(reader, 
                new com.google.gson.reflect.TypeToken<ArrayList<Classe>>(){}.getType());
            
            if (classes == null) {
                return new ArrayList<>();
            }
            
            // SYNCHRONISATION CRITIQUE des compteurs après chargement
            synchroniserCompteurs(classes);
            
            return classes;
        } catch (IOException e) {
            System.err.println("❌ Erreur chargement: " + e.getMessage());
            // Tentative de chargement du backup
            return chargerBackup();
        }
    }
    
    private ArrayList<Classe> chargerBackup() {
        File backup = new File(FICHIER_BACKUP);
        if (!backup.exists()) {
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(FICHIER_BACKUP)) {
            ArrayList<Classe> classes = gson.fromJson(reader, 
                new com.google.gson.reflect.TypeToken<ArrayList<Classe>>(){}.getType());
            System.out.println("📂 Chargement depuis backup réussi");
            synchroniserCompteurs(classes);
            return classes != null ? classes : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("❌ Backup corrompu aussi");
            return new ArrayList<>();
        }
    }
    
    /**
     * Synchronise tous les compteurs statiques après désérialisation
     * pour éviter les doublons d'ID
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
        
        System.out.println("🔢 Compteurs synchronisés: C=" + maxClasseId + 
                          " | Cr=" + maxCoursId + " | E=" + maxEtudiantId + 
                          " | Ev=" + maxEvalId);
    }
}