package gestionmoyenne.impor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*; // Import correct de Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import gestionmoyenne.model.Etudiant;

public class ExcelReader {
    
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws Exception {
        ArrayList<Etudiant> liste = new ArrayList<>();
        FileInputStream fis = new FileInputStream(new File(cheminFichier));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        
        for(Row row : sheet) { // Row vient de org.apache.poi.ss.usermodel
            if(row.getRowNum() == 0) continue; // Ignorer header
            
            String nom = getCellString(row.getCell(0));
            String prenom = getCellString(row.getCell(1));
            String matricule = getCellString(row.getCell(2)); // Colonne C
            
            if(nom.isEmpty() || prenom.isEmpty()) continue;
            
            // Si pas de matricule, temporaire (à remplacer par génération)
            if(matricule.isEmpty()) {
                matricule = "IMPORT_" + row.getRowNum();
            }
            
            liste.add(new Etudiant(nom, prenom, matricule));
        }
        
        workbook.close();
        fis.close();
        return liste;
    }
    
    private String getCellString(Cell cell) {
        if(cell == null) return "";
        switch(cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int)cell.getNumericCellValue());
            default: return "";
        }
    }
}