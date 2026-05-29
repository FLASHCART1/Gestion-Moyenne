package gestionmoyenne.service;

import gestionmoyenne.impor.ExcelReader;
import gestionmoyenne.model.Cours;
import gestionmoyenne.model.Etudiant;
import java.util.ArrayList;
import java.util.List;

public class ImportService {
    private ExcelReader excelReader;
    
    public ImportService() {
        this.excelReader = new ExcelReader();
    }
    
    public int importerEtudiantsExcel(Cours cours, String cheminFichier) {
        if (cours == null) {
            throw new IllegalArgumentException("Le cours ne peut pas être null");
        }
        if (cheminFichier == null || cheminFichier.trim().isEmpty()) {
            throw new IllegalArgumentException("Le chemin du fichier est obligatoire");
        }
        
        try {
            ArrayList<Etudiant> importes = excelReader.lireEtudiants(cheminFichier);
            int ajoutes = 0;
            int ignores = 0;
            
            for(Etudiant e : importes) {
                try {
                    cours.ajouter_Etu_Existant(e);
                    ajoutes++;
                } catch (IllegalStateException ex) {
                    // Matricule déjà existant
                    System.out.println("⚠️ Ignoré (doublon): " + e.getMatricule());
                    ignores++;
                }
            }
            
            System.out.println("✅ Import Excel: " + ajoutes + " ajoutés, " + ignores + " ignorés");
            return ajoutes;
        } catch(Exception e) {
            throw new RuntimeException("Erreur import Excel: " + e.getMessage(), e);
        }
    }
    
    /**
     * Import avec rapport détaillé
     */
    public ImportResult importerAvecRapport(Cours cours, String cheminFichier) {
        List<String> erreurs = new ArrayList<>();
        int succes = 0;
        
        try {
            ArrayList<Etudiant> importes = excelReader.lireEtudiants(cheminFichier);
                
            for (Etudiant e : importes) {
                try {
                    cours.ajouter_Etu_Existant(e);
                    succes++;
                } catch (Exception ex) {
                    erreurs.add(e.getMatricule() + ": " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            erreurs.add("FATAL: " + e.getMessage());
        }
        
        return new ImportResult(succes, erreurs);
    }
    
    public static class ImportResult {
        private final int succes;
        private final List<String> erreurs;
        
        public ImportResult(int succes, List<String> erreurs) {
            this.succes = succes;
            this.erreurs = new ArrayList<>(erreurs);
        }
        
        public int getSucces() { return succes; }
        public List<String> getErreurs() { return new ArrayList<>(erreurs); }
        public boolean isSucces() { return erreurs.isEmpty(); }
    }
}