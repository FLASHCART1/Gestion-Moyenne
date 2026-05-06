package gestionmoyenne.gui;

import gestionmoyenne.model.Classe;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import gestionmoyenne.model.Evaluation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditionDialog extends JDialog {
    
    public EditionDialog(JFrame parent, Cours cours, Classe classe) {
        super(parent, "Aperçu avant impression", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Construction du rapport texte
        StringBuilder sb = new StringBuilder();
        sb.append("=============================================================\n");
        sb.append("              FICHE DE NOTES — EDITION\n");
        sb.append("=============================================================\n");
        sb.append("Classe    : ").append(classe.getNom()).append("\n");
        sb.append("Cours     : ").append(cours.getNom()).append("\n");
        sb.append("Volume H. : ").append(cours.getVolumeHoraire()).append("h\n");
        sb.append("Date      : ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        sb.append("=============================================================\n\n");
        
        // Header tableau
        sb.append(String.format("%-12s %-15s %-15s ", "MATRICULE", "NOM", "PRENOM"));
        for (Evaluation ev : cours.getModelesEvaluations()) {
            sb.append(String.format("%-12s ", ev.getNom() + "(x" + ev.getCoeff() + ")"));
        }
        sb.append(String.format("%-10s\n", "MOYENNE"));
        sb.append("-------------------------------------------------------------\n");
        
        // Données
        for (Etudiant e : cours.getEtudiants()) {
            sb.append(String.format("%-12s %-15s %-15s ", 
                e.getMatricule(), e.getNom(), e.getPrenom()));
            
            for (Evaluation ev : e.getEvaluations()) {
                double finale = ev.getNoteFinale();
                String noteStr = String.format("%.1f", finale);
                if (ev.getBonus() != 0) noteStr += "*";
                sb.append(String.format("%-12s ", noteStr));
            }
            
            double moy = e.calculerMoyenne();
            String moyStr = Double.isNaN(moy) ? "N/A" : String.format("%.2f", moy);
            sb.append(String.format("%-10s\n", moyStr));
        }
        
        sb.append("\n=============================================================\n");
        sb.append("* Note avec bonus/malus appliqué\n");
        sb.append("Formule : Moyenne pondérée avec coefficients\n");
        sb.append("=============================================================\n");
        
        textArea.setText(sb.toString());
        
        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton btnPrint = new JButton("🖨 Imprimer");
        btnPrint.setBackground(new Color(0, 123, 255));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.addActionListener(e -> {
            try {
                boolean ok = textArea.print();
                if (ok) JOptionPane.showMessageDialog(this, "Impression lancée !");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Erreur d'impression : " + ex.getMessage());
            }
        });
        
        JButton btnExport = new JButton("📄 Exporter TXT");
        btnExport.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("Fiche_" + cours.getNom() + ".txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    java.nio.file.Files.write(chooser.getSelectedFile().toPath(), sb.toString().getBytes());
                    JOptionPane.showMessageDialog(this, "Export réussi !");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur export : " + ex.getMessage());
                }
            }
        });
        
        JButton btnClose = new JButton("Fermer");
        btnClose.addActionListener(e -> dispose());
        
        btnPanel.add(btnPrint);
        btnPanel.add(btnExport);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }
}