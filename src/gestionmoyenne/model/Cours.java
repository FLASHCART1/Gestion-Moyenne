package gestionmoyenne.model;
 
import java.util.ArrayList;
import java.util.Objects;
 
public class Cours {
    private static int compteur = 0;
    private final int id;
 
    private String nom;
    private int volumeHoraire;
 
    /** Formule de calcul de la moyenne. "STANDARD" = moyenne pondérée par défaut. */
    private String formule = "STANDARD";
 
    private ArrayList<Etudiant> etudiants;
    private ArrayList<Evaluation> modelesEvaluations;
 
    public Cours(String n, int volumeHoraire) {
        if (n == null || n.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du cours ne peut pas être vide");
        }
        if (volumeHoraire <= 0) {
            throw new IllegalArgumentException("Le volume horaire doit être positif");
        }
        this.id = ++compteur;
        this.nom = n.trim();
        this.volumeHoraire = volumeHoraire;
        this.etudiants = new ArrayList<>();
        this.modelesEvaluations = new ArrayList<>();
 
        // 2 évaluations par défaut comme exigé
        ajouterTypeEvaluation("Evaluation 1", 1.0);
        ajouterTypeEvaluation("Evaluation 2", 1.0);
    }
 
    // Package-private pour désérialisation Gson
    Cours() {
        this.id = 0;
        this.nom = "";
        this.formule = "STANDARD";
        this.etudiants = new ArrayList<>();
        this.modelesEvaluations = new ArrayList<>();
    }
 
    public static void synchroniserCompteur(int maxId) {
        if (maxId > compteur) compteur = maxId;
    }
 
    public static int getCompteur() { return compteur; }
 
    // ── Getters / Setters ──────────────────────────────────────────────────────
 
    public int getId() { return id; }
 
    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        this.nom = nom.trim();
    }
 
    public int getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(int volumeHoraire) {
        if (volumeHoraire <= 0)
            throw new IllegalArgumentException("Volume horaire doit être positif");
        this.volumeHoraire = volumeHoraire;
    }
 
    /**
     * Retourne la formule de calcul de la moyenne.
     * Vaut "STANDARD" par défaut (moyenne pondérée).
     * Peut valoir une expression du type "E0*0.4+E1*0.6".
     */
    public String getFormule() {
        return (formule == null || formule.trim().isEmpty()) ? "STANDARD" : formule;
    }
 
    /**
     * Définit la formule de calcul.
     * Passer null ou une chaîne vide remet la formule standard.
     */
    public void setFormule(String formule) {
        this.formule = (formule == null || formule.trim().isEmpty()) ? "STANDARD" : formule.trim();
    }
 
    // Copie défensive pour éviter modification externe
    public ArrayList<Etudiant> getEtudiants() {
        return new ArrayList<>(etudiants);
    }
    public ArrayList<Evaluation> getModelesEvaluations() {
        return new ArrayList<>(modelesEvaluations);
    }
 
    // Accès interne uniquement
    ArrayList<Etudiant> getEtudiantsInterne()              { return etudiants; }
    ArrayList<Evaluation> getModelesEvaluationsInterne()   { return modelesEvaluations; }
 
    // ── Gestion étudiants / évaluations ───────────────────────────────────────
 
    public int get_index(String matricule) {
        for (int i = 0; i < etudiants.size(); i++) {
            if (etudiants.get(i).getMatricule().equalsIgnoreCase(matricule)) return i;
        }
        return -1;
    }
 
    public boolean existeEtudiant(String matricule) {
        return get_index(matricule) != -1;
    }
 
    public void ajouterTypeEvaluation(String nomEval, double coef) {
        if (nomEval == null || nomEval.trim().isEmpty())
            throw new IllegalArgumentException("Le nom de l'évaluation ne peut pas être vide");
        if (coef <= 0)
            throw new IllegalArgumentException("Le coefficient doit être strictement positif");
 
        for (Evaluation ev : modelesEvaluations) {
            if (ev.getNom().equalsIgnoreCase(nomEval))
                throw new IllegalStateException("Une évaluation avec ce nom existe déjà");
        }
 
        modelesEvaluations.add(new Evaluation(nomEval, 0, coef, 0));
        for (Etudiant e : etudiants) {
            e.ajouter_note(nomEval, 0, coef, 0);
        }
    }
 
    public void ajouter_Etu(String nom, String prenom, String matricule) {
        if (existeEtudiant(matricule))
            throw new IllegalStateException("Un étudiant avec ce matricule existe déjà : " + matricule);
        Etudiant e = new Etudiant(nom, prenom, matricule);
        for (Evaluation ev : modelesEvaluations) {
            e.ajouter_note(ev.getNom(), 0, ev.getCoeff(), 0);
        }
        etudiants.add(e);
    }
 
    public void ajouter_Etu_Existant(Etudiant e) {
        if (e == null)
            throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        if (existeEtudiant(e.getMatricule()))
            throw new IllegalStateException("Matricule déjà existant : " + e.getMatricule());
 
        // Synchroniser les évaluations manquantes
        for (Evaluation modele : modelesEvaluations) {
            boolean possede = false;
            for (Evaluation evEtu : e.getEvaluations()) {
                if (evEtu.getNom().equalsIgnoreCase(modele.getNom())) { possede = true; break; }
            }
            if (!possede) {
                e.ajouter_note(modele.getNom(), 0, modele.getCoeff(), 0);
            }
        }
        etudiants.add(e);
    }
 
    public void retirer_Etu(String matricule) {
        int index = get_index(matricule);
        if (index != -1) etudiants.remove(index);
        else throw new IllegalArgumentException("Matricule non trouvé : " + matricule);
    }
 
    public void afficher() {
        System.out.println("\n Cours #" + id + ": " + nom + " (" + volumeHoraire + "h)");
        System.out.println("Formule : " + getFormule());
        System.out.println("Évaluations : " + modelesEvaluations.size() + " types");
        System.out.println("Étudiants inscrits : " + etudiants.size());
        System.out.printf("%-5s %-12s %-15s %-15s %-35s %s\n",
            "ID", "MATRICULE", "NOM", "PRENOM", "NOTES", "MOYENNE");
        for (Etudiant e : etudiants) e.afficher();
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cours)) return false;
        Cours cours = (Cours) o;
        return id == cours.id || nom.equalsIgnoreCase(cours.nom);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(id, nom.toLowerCase());
    }
}