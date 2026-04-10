package gestionmoyenne.service;

import gestionmoyenne.impor.ExcelReader;
import gestionmoyenne.impor.PDFReader;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import java.util.ArrayList;

public class ImportService {
    private ExcelReader excelReader;
    private PDFReader pdfReader;
    
    public ImportService() {
        this.excelReader = new ExcelReader();
        this.pdfReader = new PDFReader();
    }
    
    public int importerEtudiantsExcel(Cours cours, String cheminFichier) {
        try {
            ArrayList<Etudiant> importes = excelReader.lireEtudiants(cheminFichier);
            for(Etudiant e : importes) {
                cours.ajouter_Etu_Existant(e); // Méthode corrigée dans Cours
            }
            System.out.println("✅ Import Excel réussi: " + importes.size() + " étudiants");
            return importes.size();
        } catch(Exception e) {
            System.out.println("❌ Erreur import Excel: " + e.getMessage());
            return 0;
        }
    }
    
    public int importerEtudiantsPDF(Cours cours, String cheminFichier) {
        try {
            ArrayList<Etudiant> importes = pdfReader.lireEtudiants(cheminFichier);
            // Même logique si implémenté un jour
            return importes.size();
        } catch(Exception e) {
            System.out.println("❌ Erreur import PDF: " + e.getMessage());
            return 0;
        }
    }
}