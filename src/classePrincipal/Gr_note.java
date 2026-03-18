package classePrincipal;

import java.util.ArrayList;

public class Gr_note {
	private ArrayList<Evaluation> control_continue = new ArrayList<>();
	
	public void ajouter_note(double n, double c, int b) {
		Evaluation a = new Evaluation(n, c, b);
		control_continue.add(a);
		
	}
}
