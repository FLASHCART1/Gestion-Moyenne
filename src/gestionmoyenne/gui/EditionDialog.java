package gestionmoyenne.gui;

import gestionmoyenne.model.Classe;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import gestionmoyenne.model.Evaluation;
import gestionmoyenne.service.PDFExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditionDialog extends JDialog {
    private final StringBuilder rapportTexte;
    
    public EditionDialog(JFrame parent, Cours cours, Classe classe) {
        super(parent, "Aperçu avant impression", true);
        setSize(850, 650);
        setLocationRelativeTo(parent);
        
        // Construction du rapport texte (aperçu)
        rapportTexte = new StringBuilder();
        rapportTexte.append("=============================================================\n");
        rapportTexte.append("              FICHE DE NOTES — EDITION\n");
        rapportTexte.append("=============================================================\n");
        rapportTexte.append("Classe    : ").append(classe.getNom()).append("\n");
        rapportTexte.append("Cours     : ").append(cours.getNom()).append("\n");
        rapportTexte.append("Volume H. : ").append(cours.getVolumeHoraire()).append("h\n");
        rapportTexte.append("Date      : ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        rapportTexte.append("=============================================================\n\n");
        
        rapportTexte.append(String.format("%-12s %-15s %-15s ", "MATRICULE", "NOM", "PRENOM"));
        for (Evaluation ev : cours.getModelesEvaluations()) {
            rapportTexte.append(String.format("%-14s ", ev.getNom() + "(x" + ev.getCoeff() + ")"));
        }
        rapportTexte.append(String.format("%-10s\n", "MOYENNE"));
        rapportTexte.append("-------------------------------------------------------------\n");
        
        for (Etudiant e : cours.getEtudiants()) {
            rapportTexte.append(String.format("%-12s %-15s %-15s ", 
                e.getMatricule(), e.getNom(), e.getPrenom()));
            
            for (Evaluation ev : e.getEvaluations()) {
                double finale = ev.getNoteFinale();
                String noteStr = String.format("%.1f", finale);
                if (ev.getBonus() != 0) noteStr += "*";
                rapportTexte.append(String.format("%-14s ", noteStr));
            }
            
            double moy = e.calculerMoyenne();
            String moyStr = Double.isNaN(moy) ? "N/A" : String.format("%.2f", moy);
            rapportTexte.append(String.format("%-10s\n", moyStr));
        }
        
        rapportTexte.append("\n=============================================================\n");
        rapportTexte.append("* Note avec bonus/malus appliqué\n");
        rapportTexte.append("Formule : Moyenne pondérée avec coefficients\n");
        rapportTexte.append("=============================================================\n");
        
        // UI
        JTextArea textArea = new JTextArea(rapportTexte.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);
        
        // Barre de boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // Impression
        JButton btnPrint = new JButton("🖨 Imprimer");
        styleButton(btnPrint, new Color(0, 123, 255));
        btnPrint.addActionListener(e -> {
            try {
                boolean ok = textArea.print();
                if (ok) JOptionPane.showMessageDialog(this, "Impression envoyée !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur d'impression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Export TXT
        JButton btnExportTxt = new JButton("📄 Exporter TXT");
        styleButton(btnExportTxt, new Color(108, 117, 125));
        btnExportTxt.addActionListener(e -> exporterTxt(cours));
        
        // Export PDF
        JButton btnExportPdf = new JButton("📕 Exporter PDF");
        styleButton(btnExportPdf, new Color(220, 53, 69));
        btnExportPdf.addActionListener(e -> exporterPdf(cours, classe));
        
        // Fermer
        JButton btnClose = new JButton("Fermer");
        btnClose.addActionListener(e -> dispose());
        
        btnPanel.add(btnPrint);
        btnPanel.add(btnExportTxt);
        btnPanel.add(btnExportPdf);
        btnPanel.add(btnClose);
        
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void exporterTxt(Cours cours) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Fiche_" + cours.getNom().replaceAll("\\s+", "_") + ".txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Files.write(chooser.getSelectedFile().toPath(), rapportTexte.toString().getBytes());
                JOptionPane.showMessageDialog(this, "Export TXT réussi !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur export : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exporterPdf(Cours cours, Classe classe) {
        if (!PDFExporter.isDisponible()) {
            JOptionPane.showMessageDialog(this,
                "La bibliothèque OpenPDF n'est pas disponible.\n\n" +
                "Ajoutez cette dépendance Maven :\n\n" +
                "<dependency>\n" +
                "    <groupId>com.github.librepdf</groupId>\n" +
                "    <artifactId>openpdf</artifactId>\n" +
                "    <version>1.3.30</version>\n" +
                "</dependency>",
                "Dépendance manquante",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Fiche_" + cours.getNom().replaceAll("\\s+", "_") + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            // Forcer l'extension .pdf
            if (!f.getName().toLowerCase().endsWith(".pdf")) {
                f = new File(f.getAbsolutePath() + ".pdf");
            }
            try {
                PDFExporter.exporterFicheNotes(cours, classe, f);
                JOptionPane.showMessageDialog(this, 
                    "PDF généré avec succès !\n" + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la génération PDF :\n" + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}