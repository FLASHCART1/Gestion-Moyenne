package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.sun.jdi.connect.Connector.SelectedArgument;

import gestionmoyenne.model.*;
import gestionmoyenne.service.*;

public class InterfaceGraphique3 extends InterfaceGraphique2 {

    JTable table;
    DefaultTableModel model;

    public InterfaceGraphique3() {

        setTitle("");
        setSize(1280, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] col = {"Nom", "Modules"};
        model = new DefaultTableModel(col, 0);

        // Charger les classes sauvegardées
        for (Cours c : ges.getCours(getSelectedClasse())) {
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
        new InterfaceGraphique3().setVisible(true);
    }
}