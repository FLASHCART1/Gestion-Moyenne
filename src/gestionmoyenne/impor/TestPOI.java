package gestionmoyenne.impor;

public class TestPOI {
    public static void main(String[] args) {
        try {
            Class.forName("org.apache.poi.xssf.usermodel.XSSFWorkbook");
            Class.forName("org.apache.xmlbeans.XmlObject");
            System.out.println("✅ POI + XMLBeans OK");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Manquant : " + e.getMessage());
        }
    }
}