package gestionmoyenne.model;

public class Evaluation {
	private static int compteur = 0;
	private final int id;
	
	private String nom;
	private double note;
	private double coeff;
	private double bonus;

	public Evaluation(String nom, double n, double c, double b) {
		this.id = ++compteur;
		this.nom = nom;
		note = n;
		coeff = c;
		bonus = b;
	}
	
	public static int getCompteur() { return compteur; }
	public static void resetCompteur() { compteur = 0; }
	public int getId() { return id; }
	
	// ... getters/setters existants ...
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public double getNote() { return note; }
	public void setNote(double note) { this.note = note; }
	public double getCoeff() { return coeff; }
	public void setCoeff(double coeff) { this.coeff = coeff; }
	public double getBonus() { return bonus; }
	public void setBonus(double bonus) { this.bonus = bonus; }
	
	@Override
	public String toString() {
		return String.format("Evaluation #%d [%s]", id, nom);
	}
}