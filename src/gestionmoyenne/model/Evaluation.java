package gestionmoyenne.model;

import java.util.Objects;

public class Evaluation {
    private static int compteur = 0;
    private final int id;
    
    private String nom;
    private double note;
    private double coeff;
    private double bonus;

    public Evaluation(String nom, double n, double c, double b) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20");
        }
        if (c <= 0) {
            throw new IllegalArgumentException("Le coefficient doit être > 0");
        }
        this.id = ++compteur;
        this.nom = nom.trim();
        this.note = n;
        this.coeff = c;
        this.bonus = b;
    }
    
    // Package-private pour désérialisation
    Evaluation() {
        this.id = 0;
        this.nom = "";
    }
    
    public static void synchroniserCompteur(int maxId) {
        if (maxId > compteur) {
            compteur = maxId;
        }
    }
    
    public static int getCompteur() { return compteur; }
    public int getId() { return id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { 
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.nom = nom.trim(); 
    }
    public double getNote() { return note; }
    public void setNote(double note) { 
        if (note < 0 || note > 20) {
            throw new IllegalArgumentException("Note entre 0 et 20 requise");
        }
        this.note = note; 
    }
    public double getCoeff() { return coeff; }
    public void setCoeff(double coeff) { 
        if (coeff <= 0) {
            throw new IllegalArgumentException("Coefficient > 0 requis");
        }
        this.coeff = coeff; 
    }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { 
        if (bonus < -20 || bonus > 20) {
            throw new IllegalArgumentException("Bonus entre -20 et +20");
        }
        this.bonus = bonus; 
    }
    
    public double getNoteFinale() {
        double finale = note + bonus;
        return Math.max(0, Math.min(20, finale));
    }
    
    @Override
    public String toString() {
        return String.format("Evaluation #%d [%s] %.1f/20 (coef %.1f)", id, nom, getNoteFinale(), coeff);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evaluation)) return false;
        Evaluation that = (Evaluation) o;
        return id == that.id || nom.equalsIgnoreCase(that.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom.toLowerCase());
    }
}