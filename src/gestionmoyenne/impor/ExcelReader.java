package gestionmoyenne.impor;
 
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import gestionmoyenne.model.Etudiant;
 
/**
 * Lecteur de fichiers Excel (.xlsx) pour importer une liste d'étudiants.
 *
 * Colonnes attendues (ordre quelconque, détection automatique par en-tête) :
 *   - Matricule  → mots-clés : "matricule", "mat", "id"
 *   - Nom        → mots-clés : "nom", "name", "nom de famille"
 *   - Prénom(s)  → mots-clés : "prenom", "prénom", "prenoms", "prénoms", "first name"
 *
 * Si aucun en-tête n'est trouvé, repli sur l'ordre par défaut :
 *   col 0 = Matricule, col 1 = Nom, col 2 = Prénom
 */
public class ExcelReader {
 
    // Indices de colonnes (-1 = non trouvé)
    private int colMatricule = -1;
    private int colNom       = -1;
    private int colPrenom    = -1;
 
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws Exception {
        if (cheminFichier == null || !cheminFichier.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Fichier .xlsx requis");
        }
 
        File file = new File(cheminFichier);
        if (!file.exists()) {
            throw new IllegalArgumentException("Fichier introuvable : " + cheminFichier);
        }
 
        ArrayList<Etudiant> liste        = new ArrayList<>();
        Set<String>         matriculesVus = new HashSet<>();
 
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook   = new XSSFWorkbook(fis)) {
 
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalStateException("La feuille 1 est vide");
            }
 
            boolean headerLu = false;
 
            for (Row row : sheet) {
 
                // ── Ligne d'en-tête ───────────────────────────────────────────
                if (row.getRowNum() == 0) {
                    lireHeader(row);
                    headerLu = true;
                    System.out.println("📋 Colonnes détectées → Matricule=" + colMatricule
                        + " | Nom=" + colNom + " | Prénom=" + colPrenom);
                    continue;
                }
 
                // CORRECTION #1 : Repli si AU MOINS UNE colonne clé n'est pas trouvée
                // (avant : vérifiait si TOUTES étaient -1, ce qui ne marchait pas 
                //  si seulement 1 ou 2 colonnes étaient trouvées)
                if (headerLu && (colNom == -1 || colPrenom == -1)) {
                    // Si Nom ou Prénom non trouvés, on essaie l'ordre par défaut
                    if (colMatricule == -1 && colNom == -1 && colPrenom == -1) {
                        colMatricule = 0;
                        colNom       = 1;
                        colPrenom    = 2;
                        System.out.println("⚠️ En-tête non reconnu — repli sur l'ordre par défaut (Matricule|Nom|Prénom)");
                    }
                }
 
                // ── Lecture des données ───────────────────────────────────────
                String nom       = colNom       >= 0 ? getCellString(row.getCell(colNom))       : "";
                String prenom    = colPrenom    >= 0 ? getCellString(row.getCell(colPrenom))    : "";
                String matricule = colMatricule >= 0 ? getCellString(row.getCell(colMatricule)) : "";
 
                int ligneNum = row.getRowNum() + 1;
 
                if (nom.isEmpty() && prenom.isEmpty() && matricule.isEmpty()) {
                    continue; // ligne vide, on ignore silencieusement
                }
 
                if (nom.isEmpty() || prenom.isEmpty()) {
                    System.out.println("⚠️ Ligne " + ligneNum + " ignorée (nom ou prénom vide)");
                    continue;
                }
 
                // Génération du matricule si absent
                if (matricule.isEmpty()) {
                    matricule = "IMPORT_" + String.format("%04d", ligneNum);
                    System.out.println("ℹ️ Matricule généré pour " + nom + " : " + matricule);
                }
 
                matricule = matricule.trim().toUpperCase();
 
                if (matriculesVus.contains(matricule)) {
                    System.out.println("⚠️ Doublon dans le fichier ignoré : " + matricule);
                    continue;
                }
                matriculesVus.add(matricule);
 
                liste.add(new Etudiant(nom, prenom, matricule));
            }
        }
 
        System.out.println("📊 " + liste.size() + " étudiants lus depuis Excel");
        return liste;
    }
 
    /**
     * Analyse la ligne d'en-tête et mémorise l'indice de chaque colonne utile.
     * La comparaison est insensible à la casse et aux accents courants.
     */
    private void lireHeader(Row headerRow) {
        colMatricule = -1;
        colNom       = -1;
        colPrenom    = -1;
 
        for (Cell cell : headerRow) {
            String valeur = getCellString(cell).toLowerCase()
                .replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("à", "a").replace("â", "a")
                .replace("'", "").replace("-", " ")
                .trim();
 
            int idx = cell.getColumnIndex();
 
            // CORRECTION #2 : Mots-clés plus robustes pour matricule
            if (valeur.equals("matricule") || valeur.startsWith("mat") 
                || valeur.equals("id") || valeur.equals("numero") || valeur.equals("numéro")) {
                colMatricule = idx;
            } 
            // CORRECTION #3 : Mots-clés plus robustes pour nom
            else if (valeur.equals("nom") || valeur.equals("name") 
                     || valeur.contains("nom de famille") || valeur.contains("last name")) {
                colNom = idx;
            } 
            // CORRECTION #4 : Mots-clés plus robustes pour prénom
            else if (valeur.startsWith("prenom") || valeur.startsWith("prénom")
                     || valeur.startsWith("first name") || valeur.startsWith("firstname")) {
                colPrenom = idx;
            }
        }
    }
 
    private String getCellString(Cell cell) {
        if (cell == null) return "";
        
        // CORRECTION #5 : Gestion du type FORMULA + compatibilité POI
        CellType cellType = cell.getCellType();
        
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double val = cell.getNumericCellValue();
                // Éviter "12345.0" pour les matricules numériques
                if (val == (long) val) return String.valueOf((long) val);
                return String.valueOf(val);
            case FORMULA:
                // CORRECTION #6 : Évaluer la formule et récupérer le résultat
                try {
                    return getCellStringFromFormula(cell);
                } catch (Exception e) {
                    return cell.getStringCellValue().trim();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    /**
     * CORRECTION #7 : Récupère la valeur évaluée d'une cellule formule
     */
    private String getCellStringFromFormula(Cell cell) {
        if (cell == null) return "";
        
        CellType cachedType = cell.getCachedFormulaResultType();
        
        switch (cachedType) {
            case NUMERIC:
                double val = cell.getNumericCellValue();
                if (val == (long) val) return String.valueOf((long) val);
                return String.valueOf(val);
            case STRING:
                return cell.getStringCellValue().trim();
            default:
                return "";
        }
    }
}