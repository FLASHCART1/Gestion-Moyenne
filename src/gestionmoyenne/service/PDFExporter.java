package gestionmoyenne.service;

import gestionmoyenne.model.Classe;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import gestionmoyenne.model.Evaluation;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PDFExporter {
    
    private static final Font FONT_TITRE = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(45, 52, 70));
    private static final Font FONT_SOUS_TITRE = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
    private static final Font FONT_HEADER = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 9, Font.BOLD, Color.BLACK);
    private static final Font FONT_MOYENNE = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(0, 100, 0));
    private static final Font FONT_LEGENDE = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
    private static final Font FONT_FOOTER = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);
    
    private static final Color COLOR_HEADER = new Color(45, 52, 70);
    private static final Color COLOR_ALT_ROW = new Color(245, 247, 250);
    private static final Color COLOR_BORDER = new Color(200, 200, 200);
    private static final Color COLOR_MOYENNE_BG = new Color(230, 230, 230);

    /**
     * Vérifie si OpenPDF est disponible dans le classpath
     */
    public static boolean isDisponible() {
        try {
            Class.forName("com.lowagie.text.Document");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Exporte la fiche de notes au format PDF
     * @param cours Le cours concerné
     * @param classe La classe parente
     * @param fichierSortie Fichier de destination
     * @throws Exception En cas d'erreur d'écriture ou si OpenPDF manque
     */
    public static void exporterFicheNotes(Cours cours, Classe classe, File fichierSortie) throws Exception {
        if (!isDisponible()) {
            throw new IllegalStateException(
                "OpenPDF n'est pas dans le classpath.\n" +
                "Ajoutez la dépendance Maven :\n" +
                "<dependency>\n" +
                "    <groupId>com.github.librepdf</groupId>\n" +
                "    <artifactId>openpdf</artifactId>\n" +
                "    <version>1.3.30</version>\n" +
                "</dependency>"
            );
        }
        
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fichierSortie));
        
        // Pied de page avec numérotation
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Phrase footer = new Phrase(
                    "Gestion des Moyennes — " + 
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " — Page " + writer.getPageNumber(),
                    FONT_FOOTER
                );
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 20, 0);
            }
        });
        
        document.open();
        
        // === EN-TÊTE INSTITUTION ===
        Paragraph titre = new Paragraph("FICHE DE NOTES", FONT_TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(5);
        document.add(titre);
        
        Paragraph sousTitre = new Paragraph("Année Académique " + LocalDate.now().getYear(), FONT_SOUS_TITRE);
        sousTitre.setAlignment(Element.ALIGN_CENTER);
        sousTitre.setSpacingAfter(15);
        document.add(sousTitre);
        
        // === INFORMATIONS GÉNÉRALES ===
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(60);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSpacingAfter(15);
        
        addInfoCell(infoTable, "Classe :", classe.getNom());
        addInfoCell(infoTable, "Cours :", cours.getNom());
        addInfoCell(infoTable, "Volume horaire :", cours.getVolumeHoraire() + " heures");
        addInfoCell(infoTable, "Date d'édition :", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        addInfoCell(infoTable, "Nombre d'étudiants :", String.valueOf(cours.getEtudiants().size()));
        addInfoCell(infoTable, "Nombre d'évaluations :", String.valueOf(cours.getModelesEvaluations().size()));
        
        document.add(infoTable);
        document.add(new Paragraph(" ")); // Espacement
        
        // === TABLEAU DES NOTES ===
        int nbEvals = cours.getModelesEvaluations().size();
        int nbCols = 4 + nbEvals; // Matricule + Nom + Prénom + Evals + Moyenne
        
        PdfPTable table = new PdfPTable(nbCols);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        
        // Largeurs relatives
        float[] widths = new float[nbCols];
        widths[0] = 12f; // Matricule
        widths[1] = 14f; // Nom
        widths[2] = 14f; // Prénom
        for (int i = 0; i < nbEvals; i++) widths[3 + i] = 12f;
        widths[nbCols - 1] = 10f; // Moyenne
        table.setWidths(widths);
        
        // Header
        addHeaderCell(table, "MATRICULE");
        addHeaderCell(table, "NOM");
        addHeaderCell(table, "PRÉNOM");
        
        for (Evaluation ev : cours.getModelesEvaluations()) {
            String header = ev.getNom() + "\n(coef " + formatDouble(ev.getCoeff()) + ")";
            addHeaderCell(table, header);
        }
        addHeaderCell(table, "MOYENNE");
        
        // Données
        int rowCount = 0;
        for (Etudiant e : cours.getEtudiants()) {
            boolean isAlt = rowCount % 2 == 1;
            Color bg = isAlt ? COLOR_ALT_ROW : Color.WHITE;
            
            addDataCell(table, e.getMatricule(), FONT_NORMAL, bg, Element.ALIGN_CENTER);
            addDataCell(table, e.getNom(), FONT_BOLD, bg, Element.ALIGN_LEFT);
            addDataCell(table, e.getPrenom(), FONT_NORMAL, bg, Element.ALIGN_LEFT);
            
            ArrayList<Evaluation> evals = e.getEvaluations();
            for (int i = 0; i < nbEvals; i++) {
                String noteStr;
                if (i < evals.size()) {
                    Evaluation ev = evals.get(i);
                    double finale = ev.getNoteFinale();
                    noteStr = formatDouble(finale);
                    if (ev.getBonus() != 0) {
                        noteStr += " *";
                    }
                } else {
                    noteStr = "-";
                }
                addDataCell(table, noteStr, FONT_NORMAL, bg, Element.ALIGN_CENTER);
            }
            
            // Moyenne (colonne grisée)
            double moy = e.calculerMoyenne();
            String moyStr = Double.isNaN(moy) ? "N/A" : formatDouble(moy);
            addDataCell(table, moyStr, FONT_MOYENNE, COLOR_MOYENNE_BG, Element.ALIGN_CENTER);
            
            rowCount++;
        }
        
        document.add(table);
        
        // === LÉGENDE ===
        Paragraph legende = new Paragraph();
        legende.add(new Chunk("* ", new Font(Font.HELVETICA, 8, Font.BOLD, Color.RED)));
        legende.add(new Chunk("Note avec bonus/malus appliqué  |  ", FONT_LEGENDE));
        legende.add(new Chunk("Moyenne calculée selon la formule pondérée (coef)  |  ", FONT_LEGENDE));
        legende.add(new Chunk("N/A = Moyenne non calculable (moins de 2 notes saisies)", FONT_LEGENDE));
        legende.setSpacingBefore(5);
        document.add(legende);
        
        // === STATISTIQUES GLOBALES ===
        document.add(new Paragraph(" ")); // Saut de ligne
        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(80);
        statsTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        ArrayList<Double> moyennes = new ArrayList<>();
        int nbValides = 0;
        double sum = 0, min = 20, max = 0;
        
        for (Etudiant e : cours.getEtudiants()) {
            double m = e.calculerMoyenne();
            if (!Double.isNaN(m)) {
                moyennes.add(m);
                sum += m;
                min = Math.min(min, m);
                max = Math.max(max, m);
                nbValides++;
            }
        }
        
        double moyenneClasse = nbValides > 0 ? sum / nbValides : 0;
        
        addStatCell(statsTable, "Moyenne de classe", formatDouble(moyenneClasse) + "/20");
        addStatCell(statsTable, "Note minimale", formatDouble(min) + "/20");
        addStatCell(statsTable, "Note maximale", formatDouble(max) + "/20");
        addStatCell(statsTable, "Taux de réussite", calculerTauxReussite(moyennes) + "%");
        
        document.add(statsTable);
        
        document.close();
    }
    
    // === Helpers de mise en forme ===
    
    private static void addInfoCell(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, FONT_BOLD));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setPadding(3);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(value, FONT_NORMAL));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellValue.setPadding(3);
        
        table.addCell(cellLabel);
        table.addCell(cellValue);
    }
    
    private static void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(COLOR_HEADER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setBorderColor(COLOR_BORDER);
        table.addCell(cell);
    }
    
    private static void addDataCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(COLOR_BORDER);
        table.addCell(cell);
    }
    
    private static void addStatCell(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, FONT_BOLD));
        cellLabel.setBackgroundColor(new Color(240, 240, 240));
        cellLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellLabel.setPadding(8);
        cellLabel.setBorderColor(COLOR_BORDER);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 10, Font.BOLD, new Color(0, 123, 255))));
        cellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellValue.setPadding(8);
        cellValue.setBorderColor(COLOR_BORDER);
        
        table.addCell(cellLabel);
        table.addCell(cellValue);
    }
    
    private static String formatDouble(double val) {
        return String.format("%.2f", val);
    }
    
    private static int calculerTauxReussite(ArrayList<Double> moyennes) {
        if (moyennes.isEmpty()) return 0;
        long reussis = moyennes.stream().filter(m -> m >= 10).count();
        return (int) ((reussis * 100.0) / moyennes.size());
    }
}