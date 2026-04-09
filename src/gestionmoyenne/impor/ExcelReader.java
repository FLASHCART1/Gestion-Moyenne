package gestionmoyenne.impor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.rowset.internal.Row;

import gestionmoyenne.model.Etudiant;

public class ExcelReader {
    
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws Exception {
        ArrayList<Etudiant> liste = new ArrayList<>();
        FileInputStream fis = new FileInputStream(new File(cheminFichier));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0); // Première feuille
        
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            
            String nom = getCellString(row.getCell(0));
            String prenom = getCellString(row.getCell(1));
            
            if (!nom.isEmpty() && !prenom.isEmpty()) {
                liste.add(new Etudiant(nom, prenom));
            }
        }
        
        workbook.close();
        fis.close();
        return liste;
    }
  
    private String getCellString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int)cell.getNumericCellValue());
            default: return "";
        }
    }
}