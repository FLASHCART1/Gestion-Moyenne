package classePrincipal;

public class Evaluation {
	private double note;
	private double coeff;
	private int bonus;

	//Constructeur
	public Evaluation(double n, double c, int b) {
		note = n;
		coeff = c;
		bonus = b;
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

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
