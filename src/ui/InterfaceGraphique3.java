package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import gestionmoyenne.model.*;
import gestionmoyenne.service.*;

public class InterfaceGraphique3 extends JFrame {  // ← PLUS InterfaceGraphique2

    JTable table;
    DefaultTableModel model;
    GestionnaireClasse ges;
    Classe selectedClasse;

    // Constructeur qui reçoit directement les données nécessaires
    public InterfaceGraphique3(GestionnaireClasse ges, Classe selectedClasse) {
        this.ges = ges;
        this.selectedClasse = selectedClasse;

        setTitle(selectedClasse.getNom());
        setSize(1280, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ← DISPOSE pas EXIT

        String[] col = {"Nom", "Modules"};
        model = new DefaultTableModel(col, 0);

        // Charger les cours de la classe sélectionnée
        for (Cours c : selectedClasse.getModules()) {
            model.addRow(new Object[]{c.getNom(), c.getEtudiants().size() + " étudiants"});
        }

        table = new JTable(model);
        add(new JScrollPane(table));

        JButton add = new JButton("Ajouter un Cours");
        JButton open = new JButton("Ouvrir le Cours");
        JButton ret = new JButton("Supprimer un Cours");

        JPanel p = new JPanel();
        p.add(add);
        p.add(open);
        p.add(ret);

        add(p, "South");

        add.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(this, "Nom du cours");
            if (nom == null || nom.trim().isEmpty()) return;
            
            selectedClasse.ajouter_cours(nom, 30); // volume horaire par défaut
            model.addRow(new Object[]{nom, "0 étudiants"});
            ges.sauvegarder();
        });

        ret.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un cours à supprimer");
                return;
            }
            
            String nomCours = (String) model.getValueAt(selectedRow, 0);
            selectedClasse.supprimer_cours(nomCours);
            model.removeRow(selectedRow);
            ges.sauvegarder();
        });
        
        open.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un cours");
                return;
            }
            
            String nomCours = (String) model.getValueAt(selectedRow, 0);
            JOptionPane.showMessageDialog(this, "Ouverture de : " + nomCours);
            // TODO: Ouvrir InterfaceGraphique4 pour gérer les étudiants/notes
        });
    }
}