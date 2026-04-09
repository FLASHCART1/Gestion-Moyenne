package gestionmoyenne.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gestionmoyenne.model.Classe;
import java.io.*;
import java.util.ArrayList;

public class DataStore {
    private static final String DOSSIER_DATA = "data";
    private static final String FICHIER_SAUVEGARDE = DOSSIER_DATA + "/sauvegarde.json";
    private Gson gson;
    
    public DataStore() {
        // Gson avec pretty printing pour lisibilité
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        // Créer le dossier data s'il n'existe pas
        File dossier = new File(DOSSIER_DATA);
        if(!dossier.exists()) {
            dossier.mkdir();
        }
    }
    
    public void sauvegarder(ArrayList<Classe> classes) {
        try (Writer writer = new FileWriter(FICHIER_SAUVEGARDE)) {
            gson.toJson(classes, writer);
            System.out.println("💾 Sauvegarde automatique effectuée");
        } catch (IOException e) {
            System.out.println("❌ Erreur sauvegarde: " + e.getMessage());
        }
    }
    
    public ArrayList<Classe> charger() {
        File fichier = new File(FICHIER_SAUVEGARDE);
        if(!fichier.exists()) {
            return new ArrayList<>(); // Premier lancement
        }
        
        try (Reader reader = new FileReader(FICHIER_SAUVEGARDE)) {
            ArrayList<Classe> classes = gson.fromJson(reader, 
                new com.google.gson.reflect.TypeToken<ArrayList<Classe>>(){}.getType());
            return classes != null ? classes : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("❌ Erreur chargement: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}