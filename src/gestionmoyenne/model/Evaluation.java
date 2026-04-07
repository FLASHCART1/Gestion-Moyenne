package gestionmoyenne.model;

public class Evaluation {
	private String nom;
	private double note;
	private double coeff;
	private double bonus;

	//Constructeur
	public Evaluation(String nom, double n, double c, double b) {
		this.nom = nom;
		note = n;
		coeff = c;
		bonus = b;
	}
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	//getters-setters
	public double getNote() {
		return note;
	}
	public void setNote(double note) {
		this.note = note;
	}
	public double getCoeff() {
		return coeff;
	}
	public void setCoeff(double coeff) {
		this.coeff = coeff;
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
}
