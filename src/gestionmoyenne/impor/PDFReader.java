package gestionmoyenne.impor;

import java.io.IOException;
import java.util.ArrayList;
import gestionmoyenne.model.Etudiant;

public class PDFReader {
    
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws IOException {
        System.err.println("═══════════════════════════════════════════════════");
        System.err.println("  IMPORT PDF NON IMPLÉMENTÉ");
        System.err.println("───────────────────────────────────────────────────");
        System.err.println("  Cette fonctionnalité nécessite l'ajout d'une");
        System.err.println("  dépendance comme OpenPDF ou iText.");
        System.err.println("");
        System.err.println("  SOLUTION: Convertissez votre PDF en Excel (.xlsx)");
        System.err.println("  et utilisez l'import Excel.");
        System.err.println("═══════════════════════════════════════════════════");
        
        return new ArrayList<>();
    }
    
    /**
     * Vérifie si le support PDF est disponible
     */
    public boolean isSupporte() {
        try {
            Class.forName("com.lowagie.text.pdf.PdfReader");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}