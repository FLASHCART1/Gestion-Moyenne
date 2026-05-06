package gestionmoyenne.gui;

import gestionmoyenne.model.*;
import gestionmoyenne.service.Calculateur;
import gestionmoyenne.service.GestionnaireClasse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class CoursNotesPanel extends JPanel {
    private final MainFrame mainFrame;
    private final GestionnaireClasse gestionnaire;
    private Classe classe;
    private Cours cours;
    
    private JLabel lblTitre;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<String> nomsEvaluations; // cache des noms pour mapping colonnes
    
    private static final int COL_MATRICULE = 0;
    private static final int COL_NOM = 1;
    private static final int COL_PRENOM = 2;
    private static final int COL_OFFSET_NOTES = 3; // à partir d'ici : les notes
    
    public CoursNotesPanel(MainFrame mainFrame, GestionnaireClasse gestionnaire) {
        this.mainFrame = mainFrame;
        this.gestionnaire = gestionnaire;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 242, 245));
        
        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton btnBack = new JButton("← Retour");
        btnBack.addActionListener(e -> mainFrame.showClasseDetail(classe));
        lblTitre = new JLabel("Fiche de notes");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitre, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        
        // TOOLBAR
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(false);
        
        JButton btnAddEtu = new JButton("+ Étudiant");
        btnAddEtu.addActionListener(e -> ajouterEtudiant());
        
        JButton btnAddEval = new JButton("+ Évaluation");
        btnAddEval.addActionListener(e -> ajouterEvaluation());
        
        JButton btnBonus = new JButton("Bonus/Malus");
        btnBonus.addActionListener(e -> appliquerBonusMalus());
        
        JButton btnFormule = new JButton("Formule calcul");
        btnFormule.addActionListener(e -> configurerFormule());
        
        JButton btnPrint = new JButton("🖨 Imprimer");
        btnPrint.addActionListener(e -> imprimer());
        
        toolbar.add(btnAddEtu);
        toolbar.add(btnAddEval);
        toolbar.addSeparator();
        toolbar.add(btnBonus);
        toolbar.add(btnFormule);
        toolbar.addSeparator();
        toolbar.add(btnPrint);
        
        add(toolbar, BorderLayout.NORTH);
        
        // TABLE
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // La colonne moyenne est NON modifiable (grisée visuellement via renderer)
                if (column == getColumnCount() - 1) return false;
                return true;
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(45, 52, 70));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scroll, BorderLayout.CENTER);
        
        // Footer info
        JLabel lblInfo = new JLabel("💡 Double-cliquez sur une note pour la modifier. La moyenne se calcule automatiquement.");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(Color.DARK_GRAY);
        lblInfo.setBorder(new EmptyBorder(5, 0, 0, 0));
        add(lblInfo, BorderLayout.SOUTH);
    }
    
    public void setCours(Classe classe, Cours cours) {
        this.classe = classe;
        this.cours = cours;
        lblTitre.setText(String.format("Fiche de notes — %s / %s", classe.getNom(), cours.getNom()));
        rebuildTable();
    }
    
    private void rebuildTable() {
        model.setRowCount(0);
        model.setColumnCount(0);
        nomsEvaluations = new ArrayList<>();
        
        // Colonnes fixes
        model.addColumn("Matricule");
        model.addColumn("Nom");
        model.addColumn("Prénom");
        
        // Colonnes dynamiques (évaluations)
        for (Evaluation ev : cours.getModelesEvaluations()) {
            String header = String.format("%s (coef %.1f)", ev.getNom(), ev.getCoeff());
            model.addColumn(header);
            nomsEvaluations.add(ev.getNom());
        }
        
        // Colonne moyenne (grisée / non modifiable)
        model.addColumn("MOYENNE");
        
        // Remplissage des données
        for (Etudiant e : cours.getEtudiants()) {
            ArrayList<Object> row = new ArrayList<>();
            row.add(e.getMatricule());
            row.add(e.getNom());
            row.add(e.getPrenom());
            
            ArrayList<Evaluation> evals = e.getEvaluations();
            for (int i = 0; i < nomsEvaluations.size(); i++) {
                if (i < evals.size()) {
                    row.add(evals.get(i).getNote());
                } else {
                    row.add(0.0);
                }
            }
            
            double moy = e.calculerMoyenne();
            row.add(Double.isNaN(moy) ? "N/A" : String.format("%.2f", moy));
            
            model.addRow(row.toArray());
        }
        
        // Renderer personnalisé pour la colonne moyenne (grisée)
        int lastCol = model.getColumnCount() - 1;
        table.getColumnModel().getColumn(lastCol).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(230, 230, 230));
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        // Listener pour sauvegarde auto quand on édite une note
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getFirstRow() >= 0) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col >= COL_OFFSET_NOTES && col < model.getColumnCount() - 1) {
                    sauvegarderNoteDepuisTable(row, col);
                }
            }
        });
    }
    
    private void sauvegarderNoteDepuisTable(int row, int col) {
        try {
            String matricule = (String) model.getValueAt(row, COL_MATRICULE);
            Etudiant etu = findEtudiant(matricule);
            if (etu == null) return;
            
            String nomEval = nomsEvaluations.get(col - COL_OFFSET_NOTES);
            Object val = model.getValueAt(row, col);
            double note = 0;
            if (val instanceof Number) note = ((Number) val).doubleValue();
            else if (val != null) note = Double.parseDouble(val.toString());
            
            if (note < 0 || note > 20) {
                JOptionPane.showMessageDialog(this, "La note doit être entre 0 et 20", "Erreur", JOptionPane.ERROR_MESSAGE);
                rebuildTable(); // rollback visuel
                return;
            }
            
            etu.modifierNoteParNom(nomEval, note);
            
            // Recalculer la moyenne affichée
            double moy = etu.calculerMoyenne();
            model.setValueAt(Double.isNaN(moy) ? "N/A" : String.format("%.2f", moy), row, model.getColumnCount() - 1);
            
            gestionnaire.sauvegarder();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            rebuildTable();
        }
    }
    
    private Etudiant findEtudiant(String matricule) {
        for (Etudiant e : cours.getEtudiants()) {
            if (e.getMatricule().equalsIgnoreCase(matricule)) return e;
        }
        return null;
    }
    
    private void ajouterEtudiant() {
        JTextField txtNom = new JTextField();
        JTextField txtPrenom = new JTextField();
        JTextField txtMat = new JTextField();
        
        Object[] msg = {
            "Nom :", txtNom,
            "Prénom :", txtPrenom,
            "Matricule :", txtMat
        };
        
        int opt = JOptionPane.showConfirmDialog(this, msg, "Ajouter un étudiant", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                cours.ajouter_Etu(txtNom.getText(), txtPrenom.getText(), txtMat.getText());
                gestionnaire.sauvegarder();
                rebuildTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void ajouterEvaluation() {
        JTextField txtNom = new JTextField();
        JTextField txtCoef = new JTextField("1.0");
        
        Object[] msg = {
            "Nom de l'évaluation :", txtNom,
            "Coefficient :", txtCoef
        };
        
        int opt = JOptionPane.showConfirmDialog(this, msg, "Nouvelle Évaluation", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String nom = txtNom.getText().trim();
                double coef = Double.parseDouble(txtCoef.getText().trim());
                cours.ajouterTypeEvaluation(nom, coef);
                gestionnaire.sauvegarder();
                rebuildTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void appliquerBonusMalus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant dans le tableau.");
            return;
        }
        
        String matricule = (String) model.getValueAt(row, COL_MATRICULE);
        Etudiant etu = findEtudiant(matricule);
        if (etu == null) return;
        
        ArrayList<Evaluation> evals = etu.getEvaluations();
        if (evals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune évaluation disponible.");
            return;
        }
        
        String[] options = new String[evals.size()];
        for (int i = 0; i < evals.size(); i++) {
            options[i] = evals.get(i).getNom();
        }
        
        String choix = (String) JOptionPane.showInputDialog(this, 
            "Choisir l'évaluation :", "Bonus/Malus", 
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (choix == null) return;
        
        String val = JOptionPane.showInputDialog(this, 
            "Valeur du bonus/malus (négatif pour malus) :", "0.0");
        if (val == null) return;
        
        try {
            double bonus = Double.parseDouble(val.trim());
            for (int i = 0; i < evals.size(); i++) {
                if (evals.get(i).getNom().equals(choix)) {
                    etu.appliquerBonusMalus(i, bonus);
                    break;
                }
            }
            gestionnaire.sauvegarder();
            rebuildTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configurerFormule() {
        String formule = JOptionPane.showInputDialog(this,
            "Formule de calcul (laissez vide pour formule standard pondérée) :\n" +
            "Exemple : E0*0.4+E1*0.6\n" +
            "E0 = 1ère évaluation, E1 = 2ème, etc.",
            Calculateur.FORMULE_STANDARD);
        
        if (formule != null) {
            JOptionPane.showMessageDialog(this, 
                "Formule enregistrée : " + (formule.isEmpty() ? "STANDARD" : formule) + 
                "\n(Appliquée lors de l'impression et de l'affichage)");
        }
    }
    
    private void imprimer() {
        new EditionDialog(mainFrame, cours, classe).setVisible(true);
    }
}