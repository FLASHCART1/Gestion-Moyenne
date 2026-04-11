package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
//import gestionmoyenne.model.*;
import gestionmoyenne.service.*;

public class InterfaceGraphique2 extends JFrame {

    JTable table;
    DefaultTableModel model;
    GestionnaireClasse ges;

    public InterfaceGraphique2() {

        setTitle("Gestion Notes");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ges = new GestionnaireClasse();
        ges.charger();

        // Colonnes
        String[] col = {"Classe"};
        model = new DefaultTableModel(col, 0);

        table = new JTable(model);
        add(new JScrollPane(table));

        // Boutons
        JButton add = new JButton("Ajouter une Classe");
        JButton open = new JButton("ouvrir la Classe");
        JButton ret = new JButton("Supprimer une Classe");

        JPanel p = new JPanel();
        p.add(add);
        p.add(open);
        p.add(ret);

        add(p, "South");

        // Ajouter étudiant
        add.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog("Nom");

            ges.creerClasse(nom);

            model.addRow(new Object[]{nom, 0, 0, 0, 0});
        });

        // Supprimer la classe sélectionnée dans le tableau
        ret.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner une classe à supprimer.", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String nomClasse = (String) model.getValueAt(selectedRow, 0);
            
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer la classe \"" + nomClasse + "\" ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirmation == JOptionPane.YES_OPTION) {
                // Supprimer d'abord dans le gestionnaire
                ges.supprimer(nomClasse);
                
                // Puis supprimer du tableau (seulement si la ligne existe encore)
                if (selectedRow < model.getRowCount()) {
                    model.removeRow(selectedRow);
                }
            }
        });
     // Ouvrir la classe (à implémenter selon tes besoins)
        open.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner une classe à ouvrir.", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nomClasse = (String) model.getValueAt(selectedRow, 0);
            JOptionPane.showMessageDialog(this, 
                "Ouverture de la classe : " + nomClasse,
                "Ouverture", JOptionPane.INFORMATION_MESSAGE);
            // TODO: Ouvrir la fenêtre de détail de la classe
        });
    }

    public static void main(String[] args) {
        new InterfaceGraphique2().setVisible(true);
    }
}
