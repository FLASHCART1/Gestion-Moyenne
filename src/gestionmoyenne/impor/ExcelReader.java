package gestionmoyenne.impor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import gestionmoyenne.model.Etudiant;

public class ExcelReader {
    
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws Exception {
        if (cheminFichier == null || !cheminFichier.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Fichier .xlsx requis");
        }
        
        File file = new File(cheminFichier);
        if (!file.exists()) {
            throw new IllegalArgumentException("Fichier introuvable: " + cheminFichier);
        }
        
        ArrayList<Etudiant> liste = new ArrayList<>();
        Set<String> matriculesVus = new HashSet<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalStateException("La feuille 1 est vide");
            }
            
            int ligneNum = 0;
            for(Row row : sheet) {
                ligneNum++;
                if(row.getRowNum() == 0) continue; // Ignorer header
                
                String nom = getCellString(row.getCell(0));
                String prenom = getCellString(row.getCell(1));
                String matricule = getCellString(row.getCell(2));
                
                if(nom.isEmpty() || prenom.isEmpty()) {
                    System.out.println("⚠️ Ligne " + ligneNum + " ignorée (nom/prénom vides)");
                    continue;
                }
                
                // Génération matricule si absent
                if(matricule.isEmpty()) {
                    matricule = "IMPORT_" + String.format("%04d", ligneNum);
                    System.out.println("ℹ️ Matricule généré pour " + nom + ": " + matricule);
                }
                
                // Normalisation
                matricule = matricule.trim().toUpperCase();
                
                // Vérifier doublon dans le fichier
                if (matriculesVus.contains(matricule)) {
                    System.out.println("⚠️ Doublon dans le fichier ignoré: " + matricule);
                    continue;
                }
                matriculesVus.add(matricule);
                
                liste.add(new Etudiant(nom, prenom, matricule));
            }
        }
        
        System.out.println("📊 " + liste.size() + " étudiants lus depuis Excel");
        return liste;
    }
    
    private String getCellString(Cell cell) {
        if(cell == null) return "";
        switch(cell.getCellType()) {
            case STRING: 
                return cell.getStringCellValue().trim();
            case NUMERIC: 
                // Si c'est un nombre entier, pas de décimale
                double val = cell.getNumericCellValue();
                if (val == (int) val) {
                    return String.valueOf((int) val);
                }
                return String.valueOf(val);
            case BLANK:
                return "";
            default: 
                return "";
        }
    }
}