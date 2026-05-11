package gestionmoyenne.impor;
 
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import gestionmoyenne.model.Etudiant;
 
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
 
/**
 * Lecteur de fichiers PDF pour importer une liste d'étudiants.
 *
 * Format attendu dans le PDF (une ligne par étudiant) :
 *   NOM    PRENOM    MATRICULE
 * Les colonnes peuvent être séparées par des tabulations ou des espaces multiples.
 * La ligne d'en-tête (contenant « nom » et « prenom ») est automatiquement ignorée.
 *
 * Dépendance requise (déjà présente via PDFExporter) :
 *   com.github.librepdf:openpdf:1.3.30
 */
public class PDFReader {
 
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws IOException {
        if (cheminFichier == null || !cheminFichier.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Fichier .pdf requis");
        }
 
        File file = new File(cheminFichier);
        if (!file.exists()) {
            throw new IllegalArgumentException("Fichier introuvable : " + cheminFichier);
        }
 
        ArrayList<Etudiant> liste = new ArrayList<>();
        Set<String> matriculesVus = new HashSet<>();
 
        PdfReader reader = new PdfReader(cheminFichier);
        StringBuilder contenuTotal = new StringBuilder();
 
        try {
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                String texte = extractor.getTextFromPage(page);
                if (texte != null) {
                    contenuTotal.append(texte).append("\n");
                }
            }
        } finally {
            reader.close();
        }
 
        String[] lignes = contenuTotal.toString().split("\n");
        int ligneNum = 0;
 
        for (String ligne : lignes) {
            ligneNum++;
            ligne = ligne.trim();
            if (ligne.isEmpty()) continue;
 
            // Ignorer la ligne d'en-tête
            String lowerLigne = ligne.toLowerCase();
            if (lowerLigne.contains("nom") &&
                (lowerLigne.contains("prenom") || lowerLigne.contains("prénom") || lowerLigne.contains("matricule"))) {
                continue;
            }
 
            // Découpage par tabulations ou espaces multiples (≥ 2)
            String[] parties = ligne.split("\\t|\\s{2,}");
 
            if (parties.length < 2) {
                System.out.println("⚠️ Ligne " + ligneNum + " ignorée (moins de 2 colonnes) : " + ligne);
                continue;
            }
 
            String nom       = parties[0].trim();
            String prenom    = parties[1].trim();
            String matricule = parties.length > 2 ? parties[2].trim() : "";
 
            if (nom.isEmpty() || prenom.isEmpty()) {
                System.out.println("⚠️ Ligne " + ligneNum + " ignorée (nom/prénom vides)");
                continue;
            }
 
            if (matricule.isEmpty()) {
                matricule = "PDF_" + String.format("%04d", ligneNum);
                System.out.println("ℹ️ Matricule généré pour " + nom + " : " + matricule);
            }
 
            matricule = matricule.toUpperCase().trim();
 
            if (matriculesVus.contains(matricule)) {
                System.out.println("⚠️ Doublon ignoré : " + matricule);
                continue;
            }
            matriculesVus.add(matricule);
 
            liste.add(new Etudiant(nom, prenom, matricule));
        }
 
        System.out.println("📊 " + liste.size() + " étudiants lus depuis PDF");
        return liste;
    }
 
    /**
     * Vérifie si OpenPDF est disponible dans le classpath.
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