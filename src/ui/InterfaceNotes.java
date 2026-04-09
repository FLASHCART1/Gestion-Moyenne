package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InterfaceNotes extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public InterfaceNotes() {
        // Configuration de base
        setTitle("Gestion des Notes - Classe ESATIC");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. D�finition des colonnes
        String[] colonnes = {"Nom de l'étudiant", "Note 1", "Note 2", "Bonus/Malus", "Moyenne"};

        // 2. Cr�ation du Mod�le (Gestion des droits d'�dition)
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // On bloque la colonne 4 (Moyenne) : l'enseignant ne peut pas la modifier
                return column != 4; 
            }
        };

        table = new JTable(model);

        // 3. Boutons d'action
        JButton btnAjouter = new JButton("Ajouter étudiant");
        JButton btnCalculer = new JButton("Calculer Moyennes");
        JButton btnVider = new JButton("Vider la liste");

        // --- LOGIQUE DU BOUTON AJOUTER ---
        btnAjouter.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(this, "Nom de l'étudiant :");
            if (nom != null && !nom.isEmpty()) {
                // On ajoute l'étudiant avec des notes à 0 par defaut
                model.addRow(new Object[]{nom, "0", "0", "0", "---"});
            }
        });

        // --- LOGIQUE DU BOUTON CALCULER ---
        btnCalculer.addActionListener(e -> calculerTout());

        // 4. Mise en page (Layout)
        JPanel panelBas = new JPanel();
        panelBas.add(btnAjouter);
        panelBas.add(btnCalculer);
        panelBas.add(btnVider);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelBas, BorderLayout.SOUTH);
    }

    // M�thode simple pour calculer la moyenne de chaque ligne
    private void calculerTout() {
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                double n1 = Double.parseDouble(model.getValueAt(i, 1).toString());
                double n2 = Double.parseDouble(model.getValueAt(i, 2).toString());
                double bonus = Double.parseDouble(model.getValueAt(i, 3).toString());

                double moyenne = (n1 + n2) / 2 + bonus;
                model.setValueAt(moyenne, i, 4); // On écrit dans la colonne "Moyenne"
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur de saisie à la ligne " + (i+1));
            }
        }
    }

    public static void main(String[] args) {
        // Lancement de l'interface
        SwingUtilities.invokeLater(() -> new InterfaceNotes().setVisible(true));
    }
}