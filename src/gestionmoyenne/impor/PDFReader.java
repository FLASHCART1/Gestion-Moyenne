package gestionmoyenne.impor;

import java.io.IOException;
import java.util.ArrayList;
import gestionmoyenne.model.Etudiant;

public class PDFReader {
    
    public ArrayList<Etudiant> lireEtudiants(String cheminFichier) throws IOException {
        // TODO: Nécessite OpenPDF ou iText
        // Pour l'instant, méthode non implémentée
        System.out.println("Import PDF non disponible dans cette version");
        System.out.println("Conseil: Convertissez votre PDF en Excel (.xlsx) d'abord");
        return new ArrayList<>();
    }
}