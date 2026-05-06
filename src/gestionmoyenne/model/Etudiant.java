package gestionmoyenne.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Etudiant {
    private static int compteur = 0;
    private final int id;
    
    private String nom;
    private String prenom;
    private String matricule;
    private ArrayList<Evaluation> evaluations;
    
    public Etudiant(String n, String p, String m) {
        if (n == null || n.trim().isEmpty() || p == null || p.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom et le prénom sont obligatoires");
        }
        if (m == null || m.trim().isEmpty()) {
            throw new IllegalArgumentException("Le matricule est obligatoire");
        }
        this.id = ++compteur;
        this.nom = n.trim();
        this.prenom = p.trim();
        this.matricule = m.trim().toUpperCase(); // Normalisation
        this.evaluations = new ArrayList<>();
    }
    
    // Package-private pour désérialisation
    Etudiant() {
        this.id = 0;
        this.nom = "";
        this.prenom = "";
        this.matricule = "";
        this.evaluations = new ArrayList<>();
    }
    
    public static void synchroniserCompteur(int maxId) {
        if (maxId > compteur) {
            compteur = maxId;
        }
    }
    
    public static int getCompteur() { return compteur; }
    
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getMatricule() { return matricule; }
    
    // Copie défensive
    public ArrayList<Evaluation> getEvaluations() { 
        return new ArrayList<>(evaluations); 
    }
    
    ArrayList<Evaluation> getEvaluationsInterne() { return evaluations; }
    
    public void setNom(String nom) { 
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.nom = nom.trim(); 
    }
    public void setPrenom(String prenom) { 
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        this.prenom = prenom.trim(); 
    }
    public void setMatricule(String matricule) { 
        if (matricule == null || matricule.trim().isEmpty()) {
            throw new IllegalArgumentException("Le matricule ne peut pas être vide");
        }
        this.matricule = matricule.trim().toUpperCase(); 
    }

    public void ajouter_note(String nom, double note, double coeff, double bonus) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'évaluation est obligatoire");
        }
        if (note < 0 || note > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20");
        }
        if (coeff <= 0) {
            throw new IllegalArgumentException("Le coefficient doit être strictement positif");
        }
        // Vérifier doublon
        for (Evaluation ev : evaluations) {
            if (ev.getNom().equalsIgnoreCase(nom)) {
                throw new IllegalStateException("Cet étudiant possède déjà une évaluation: " + nom);
            }
        }
        evaluations.add(new Evaluation(nom, note, coeff, bonus));
    }
    
    public boolean peutCalculerMoyenne() {
        return evaluations.size() >= 2;
    }
    
    public double calculerMoyenne() {
        if (!peutCalculerMoyenne()) {
            return Double.NaN; // NaN plus explicite que -1 pour "non calculable"
        }
        
        double sommePonderee = 0;
        double sommeCoeffs = 0;
        int notesSaisies = 0;
        
        for(Evaluation ev : evaluations) {
            double noteFinale = ev.getNote() + ev.getBonus();
            noteFinale = Math.max(0, Math.min(20, noteFinale));
            
            // Ne prendre en compte que les notes réellement saisies (> 0 ou explicitement 0)
            // Si note = 0 et bonus = 0, considérer comme non saisie sauf si c'est une vraie note 0
            if (ev.getNote() != 0 || ev.getBonus() != 0 || evaluations.stream().allMatch(e -> e.getNote() == 0)) {
                sommePonderee += noteFinale * ev.getCoeff();
                sommeCoeffs += ev.getCoeff();
                notesSaisies++;
            }
        }
        
        if (notesSaisies < 2) {
            return Double.NaN;
        }
        
        return sommeCoeffs > 0 ? sommePonderee / sommeCoeffs : 0;
    }
    
    public void modifierNote(int index, double nouvelleNote) {
        if(index < 0 || index >= evaluations.size()) {
            throw new IndexOutOfBoundsException("Index d'évaluation invalide: " + index);
        }
        if(nouvelleNote < 0 || nouvelleNote > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20");
        }
        evaluations.get(index).setNote(nouvelleNote);
    }
    
    public void modifierNoteParNom(String nomEval, double nouvelleNote) {
        for (int i = 0; i < evaluations.size(); i++) {
            if (evaluations.get(i).getNom().equalsIgnoreCase(nomEval)) {
                modifierNote(i, nouvelleNote);
                return;
            }
        }
        throw new IllegalArgumentException("Évaluation non trouvée: " + nomEval);
    }
    
    public void appliquerBonusMalus(int indexEvaluation, double bonus) {
        if(indexEvaluation < 0 || indexEvaluation >= evaluations.size()) {
            throw new IndexOutOfBoundsException("Index invalide: " + indexEvaluation);
        }
        // Limiter le bonus pour éviter des valeurs absurdes
        if (bonus < -20 || bonus > 20) {
            throw new IllegalArgumentException("Le bonus/malus doit être entre -20 et +20");
        }
        evaluations.get(indexEvaluation).setBonus(bonus);
    }
    
    public void afficher() {
        StringBuilder notesStr = new StringBuilder();
        for(Evaluation ev : evaluations) {
            double val = ev.getNote();
            double bonus = ev.getBonus();
            double finale = val + bonus;
            finale = Math.max(0, Math.min(20, finale));
            
            notesStr.append(String.format("%s:%.1f", ev.getNom(), finale));
            if(bonus != 0) notesStr.append(String.format("(%+.1f)", bonus));
            notesStr.append(" | ");
        }
        
        double moy = calculerMoyenne();
        String moyStr = Double.isNaN(moy) ? "N/A (min 2 notes)" : String.format("%.2f/20", moy);
        
        System.out.printf("#%-3d %-12s %-15s %-15s %-35s [%-10s]\n", 
            id, matricule, nom, prenom, notesStr.toString(), moyStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etudiant)) return false;
        Etudiant etudiant = (Etudiant) o;
        return id == etudiant.id || matricule.equals(etudiant.matricule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, matricule);
    }
}