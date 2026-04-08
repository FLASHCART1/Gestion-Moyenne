package gestionmoyenne.service;

import gestionmoyenne.impor.ExcelReader;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import java.util.ArrayList;

public class ImportService {
    private ExcelReader excelReader;
    
    public ImportService() {
        this.excelReader = new ExcelReader();
    }
    
    public int importerEtudiantsExcel(Cours cours, String cheminFichier) {
        try {
            ArrayList<Etudiant> importes = excelReader.lireEtudiants(cheminFichier);
            for (Etudiant e : importes) {
                cours.ajouterEtudiantExistant(e); // Sans regénérer matricule
            }
            return importes.size();
        } catch (Exception e) {
            System.out.println("❌ Erreur import: " + e.getMessage());
            return 0;
        }
    }
    
    // PDF = extraction texte simple (si tableau PDF structuré)
    public int importerEtudiantsPDF(Cours cours, String cheminFichier) {
        System.out.println("⚠️ Import PDF: extraction basique - vérifiez les données");
        // Implémentation avec OpenPDF si nécessaire
        return 0;
    }
}