package gestionmoyenne.service;

import java.util.ArrayList;
import java.util.Collections;
import gestionmoyenne.model.*;

public class GestionnaireClasse {
    private ArrayList<Classe> classes;
    private DataStore dataStore;
    
    public GestionnaireClasse() {
        this.dataStore = new DataStore();
        this.classes = dataStore.charger();
        if(!classes.isEmpty()) {
            System.out.println("📂 " + classes.size() + " classe(s) chargée(s)");
        }
    }
    
    public void creerClasse(String nom_classe) {
        if (nom_classe == null || nom_classe.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe est obligatoire");
        }
        if(getClasse(nom_classe) != null) {
            throw new IllegalStateException("Cette classe existe déjà: " + nom_classe);
        }
        Classe a = new Classe(nom_classe);
        classes.add(a);
        sauvegarder();
        System.out.println("✅ Classe créée: " + nom_classe);
    }
    
    public void afficher() {
        System.out.println("\n=== CLASSES ===");
        if(classes.isEmpty()) {
            System.out.println("(Aucune classe - créez-en une nouvelle)");
            return;
        }
        for(Classe c : classes) {
            System.out.println("📚 " + c.getNom() + " - " + c.getModules().size() + " cours");
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
            if(classes.get(i).getNom().equalsIgnoreCase(nom_classe)) {
                return i;
            }
        }
        return -1;
    }
    
    public void supprimer(String nom_classe) {
        int index = get_index(nom_classe);
        if(index != -1) {
            classes.remove(index);
            sauvegarder();
            System.out.println("✅ Classe supprimée");
        } else {
            throw new IllegalArgumentException("Classe non trouvée: " + nom_classe);
        }
    }
    
    public void sauvegarder() {
        dataStore.sauvegarder(classes);
    }
    
    /**
     * Recharge les données depuis le disque (utile après modification externe)
     */
    public void recharger() {
        this.classes = dataStore.charger();
        System.out.println("🔄 Données rechargées");
    }
    
    public ArrayList<Classe> getClasses() { 
        return new ArrayList<>(classes); // Copie défensive
    }
    
    public Classe selectedClasse(int index) {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException("Index invalide: " + index);
        }
        return classes.get(index);
    }
    
    public ArrayList<Cours> getCours(Classe classe) {
        if (classe == null) {
            throw new IllegalArgumentException("La classe ne peut pas être null");
        }
        return classe.getModules();
    }
    
    /**
     * Recherche un étudiant par matricule dans toutes les classes
     */
    public Etudiant trouverEtudiant(String matricule) {
        for (Classe c : classes) {
            for (Cours cr : c.getModules()) {
                for (Etudiant e : cr.getEtudiants()) {
                    if (e.getMatricule().equalsIgnoreCase(matricule)) {
                        return e;
                    }
                }
            }
        }
        return null;
    }
}