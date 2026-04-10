package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import gestionmoyenne.model.*;

public class interfaceGraphique extends JFrame {

    JTable table;
    DefaultTableModel model;
    Cours cours;

    public interfaceGraphique() {

        setTitle("Gestion Notes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cours = new Cours("Java", 30);

        // Colonnes
        String[] col = {"Nom", "Prenom", "Note1", "Note2", "Bonus", "Moyenne"};
        model = new DefaultTableModel(col, 0);

        table = new JTable(model);
        add(new JScrollPane(table));

        // Boutons
        JButton add = new JButton("Ajouter");
        JButton calc = new JButton("Calculer");

        JPanel p = new JPanel();
        p.add(add);
        p.add(calc);

        add(p, "South");

        // Ajouter étudiant
        add.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog("Nom");
            String prenom = JOptionPane.showInputDialog("Prenom");

            cours.ajouter_Etu(nom, prenom, "ID");

            model.addRow(new Object[]{nom, prenom, 0, 0, 0, 0});
        });

        // Calcul moyenne
        calc.addActionListener(e -> {
            for(int i = 0; i < cours.getEtudiants().size(); i++) {

                Etudiant etu = cours.getEtudiants().get(i);

                double n1 = Double.parseDouble(model.getValueAt(i, 2).toString());
                double n2 = Double.parseDouble(model.getValueAt(i, 3).toString());
                double bonus = Double.parseDouble(model.getValueAt(i, 4).toString());

                etu.modifierNote(0, n1);
                etu.modifierNote(1, n2);
                etu.appliquerBonusMalus(0, bonus);

                double moy = etu.calculerMoyenne();

                model.setValueAt(moy, i, 5);
            }
        });
    }

    public static void main(String[] args) {
        new interfaceGraphique().setVisible(true);
    }
}
