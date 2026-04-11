package gestionmoyenne.service;

import java.util.ArrayList;
import gestionmoyenne.model.*;

public class GestionnaireClasse {
    private ArrayList<Classe> classes;
    private DataStore dataStore;
    
    public GestionnaireClasse() {
        this.dataStore = new DataStore();
        this.classes = dataStore.charger(); // Chargement auto au démarrage
        if(!classes.isEmpty()) {
            System.out.println("📂 " + classes.size() + " classe(s) chargée(s)");
        }
    }
    
    public void creerClasse(String nom_classe) {
        if(getClasse(nom_classe) != null) {
            System.out.println("⚠️ Cette classe existe déjà");
            return;
        }
        Classe a = new Classe(nom_classe);
        classes.add(a);
        sauvegarder();
        System.out.println("Classe créée: " + nom_classe);
    }
    
    public void afficher() {
        System.out.println("\n=== CLASSES ===");
        if(classes.isEmpty()) {
            System.out.println("(Aucune classe - créez-en une nouvelle)");
            return;
        }
        for(Classe c : classes) {
            System.out.println("📚 " + c.getNom() + " - " + c.getModules() + " cours");
        }
    }
    
    public Classe getClasse(String nom) {
        for(Classe c : classes) {
            if(c.getNom().equalsIgnoreCase(nom)) {
                return c;
            }
        }
        return null;
    }
    
    public int get_index(String nom_classe) {
        for(int i = 0; i < classes.size(); i++) {
            if(classes.get(i).getNom().equals(nom_classe)) {
                return i;
            }
        }
        return -1;
    }
    
    public void supprimer(String nom_classe) { // correction orthographe
        int index = get_index(nom_classe);
        if(index != -1) {
            classes.remove(index);
            sauvegarder();
            System.out.println("✅ Classe supprimée");
        } else {
            System.out.println("❌ Classe non trouvée");
        }
    }
    
    public void sauvegarder() {
        dataStore.sauvegarder(classes);
    }
    public void charger() {
    	dataStore.charger();
    }
    
    public ArrayList<Classe> getClasses() { return classes; }
}