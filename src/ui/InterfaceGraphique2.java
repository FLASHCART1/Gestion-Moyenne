package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import gestionmoyenne.model.*;
import gestionmoyenne.service.*;

public class InterfaceGraphique2 extends JFrame {

    JTable table;
    DefaultTableModel model;
    GestionnaireClasse ges;

    public InterfaceGraphique2() {

        setTitle("Gestion Notes");
        setSize(1280, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ges = new GestionnaireClasse();

        String[] col = {"Classe"};
        model = new DefaultTableModel(col, 0);

        // Charger les classes sauvegardées
        for (Classe c : ges.getClasses()) {
            model.addRow(new Object[]{c.getNom()});
        }

        table = new JTable(model);
        add(new JScrollPane(table));

        JButton add = new JButton("Ajouter une Classe");
        JButton open = new JButton("Ouvrir la Classe");
        JButton ret = new JButton("Supprimer une Classe");

        JPanel p = new JPanel();
        p.add(add);
        p.add(open);
        p.add(ret);

        add(p, "South");

        add.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog("Nom de la classe");
            if (nom == null || nom.trim().isEmpty()) return;
            
            ges.creerClasse(nom);
            model.addRow(new Object[]{nom});
        });

        ret.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;
            
            String nomClasse = (String) model.getValueAt(selectedRow, 0);
            ges.supprimer(nomClasse);
            model.removeRow(selectedRow);
        });
        
        open.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;
            
            String nomClasse = (String) model.getValueAt(selectedRow, 0);
            JOptionPane.showMessageDialog(this, "Ouverture de : " + nomClasse);
        });
    }

    public static void main(String[] args) {
        new InterfaceGraphique2().setVisible(true);
    }
}