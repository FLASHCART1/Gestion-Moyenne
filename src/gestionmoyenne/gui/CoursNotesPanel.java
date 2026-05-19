package gestionmoyenne.gui;

import gestionmoyenne.model.*;
import gestionmoyenne.service.Calculateur;
import gestionmoyenne.service.GestionnaireClasse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CoursNotesPanel extends JPanel {
    private final MainFrame mainFrame;
    @SuppressWarnings("unused")
	private final GestionnaireClasse gestionnaire;
    private Classe classe;
    private Cours cours;

    private JLabel lblTitre;
    private JLabel lblFormule;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<String> nomsEvaluations;

    private static final int COL_MATRICULE    = 0;
    private static final int COL_NOM          = 1;
    private static final int COL_PRENOM       = 2;
    private static final int COL_OFFSET_NOTES = 3;

    // Couleurs pour le renderer des notes
    private static final Color COLOR_NOTE_VIDE   = new Color(255, 243, 205);
    private static final Color COLOR_NOTE_NORMALE = Color.WHITE;
    private static final Color COLOR_MOYENNE_BG   = new Color(230, 230, 230);

    public CoursNotesPanel(MainFrame mainFrame, GestionnaireClasse gestionnaire) {
        this.mainFrame    = mainFrame;
        this.gestionnaire = gestionnaire;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 242, 245));

        // ── NORTH : header + toolbar ──────────────────────────────────────────
        JPanel northPanel = new JPanel(new BorderLayout(0, 6));
        northPanel.setOpaque(false);

        // Ligne 1 : bouton retour + titre
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        JButton btnBack = new JButton("← Retour");
        btnBack.addActionListener(e -> mainFrame.showClasseDetail(classe));
        lblTitre = new JLabel("Fiche de notes");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(btnBack,  BorderLayout.WEST);
        header.add(lblTitre, BorderLayout.CENTER);

        // Ligne 2 : toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(false);

        // ── Boutons existants ─────────────────────────────────────────────────
        JButton btnAddEtu  = new JButton("+ Étudiant");
        btnAddEtu.addActionListener(e -> ajouterEtudiant());

        JButton btnAddEval = new JButton("+ Évaluation");
        btnAddEval.addActionListener(e -> ajouterEvaluation());

        JButton btnBonus   = new JButton("Bonus/Malus");
        btnBonus.addActionListener(e -> appliquerBonusMalus());

        JButton btnFormule = new JButton("⚙ Formule calcul");
        btnFormule.addActionListener(e -> configurerFormule());

        JButton btnPrint   = new JButton("🖨 Imprimer");
        btnPrint.addActionListener(e -> imprimer());

        // ── Nouveaux boutons de suppression ───────────────────────────────────

        // Supprimer l'étudiant sélectionné dans le tableau
        JButton btnSupprimerEtu = new JButton("🗑 Supprimer étudiant");
        btnSupprimerEtu.setForeground(new Color(180, 0, 0));
        btnSupprimerEtu.setToolTipText("Sélectionnez une ligne puis cliquez pour supprimer l'étudiant");
        btnSupprimerEtu.addActionListener(e -> supprimerEtudiantSelectionne());

        // Supprimer le cours courant (retourne à la vue classe)
        JButton btnSupprimerCours = new JButton("🗑 Supprimer ce cours");
        btnSupprimerCours.setForeground(new Color(180, 0, 0));
        btnSupprimerCours.setToolTipText("Supprime définitivement ce cours et toutes ses notes");
        btnSupprimerCours.addActionListener(e -> supprimerCoursCourant());

        // Libellé de la formule active
        lblFormule = new JLabel("Formule : STANDARD");
        lblFormule.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFormule.setForeground(new Color(80, 80, 80));

        toolbar.add(btnAddEtu);
        toolbar.add(btnAddEval);
        toolbar.addSeparator();
        toolbar.add(btnSupprimerEtu);
        toolbar.addSeparator();
        toolbar.add(btnBonus);
        toolbar.add(btnFormule);
        toolbar.addSeparator();
        toolbar.add(btnPrint);
        toolbar.addSeparator();
        toolbar.add(btnSupprimerCours);
        toolbar.addSeparator();
        toolbar.add(lblFormule);

        northPanel.add(header,  BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ── CENTER : tableau ──────────────────────────────────────────────────
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == COL_MATRICULE) return false;
                if (column == COL_NOM)       return false;
                if (column == COL_PRENOM)    return false;
                if (column == getColumnCount() - 1) return false; // MOYENNE
                return true;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(45, 52, 70));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ── Menu contextuel (clic droit sur une ligne) ─────────────────────────
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuSupprimerEtu = new JMenuItem("🗑 Supprimer cet étudiant");
        menuSupprimerEtu.setForeground(new Color(180, 0, 0));
        menuSupprimerEtu.addActionListener(e -> supprimerEtudiantSelectionne());
        popupMenu.add(menuSupprimerEtu);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Sélectionner la ligne sous le curseur avant d'afficher le menu
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }
                if (e.isPopupTrigger()) {
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) table.setRowSelectionInterval(row, row);
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scroll, BorderLayout.CENTER);

        // ── SOUTH : légende ───────────────────────────────────────────────────
        JPanel legendePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        legendePanel.setOpaque(false);

        JLabel carreJaune = new JLabel("  ");
        carreJaune.setOpaque(true);
        carreJaune.setBackground(COLOR_NOTE_VIDE);
        carreJaune.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblLegende = new JLabel("= Note à 0 : veuillez entrer 0 ou saisir une note  |  "
            + "Double-cliquez pour modifier une note  |  Clic droit pour supprimer un étudiant.");
        lblLegende.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLegende.setForeground(Color.DARK_GRAY);

        legendePanel.add(carreJaune);
        legendePanel.add(lblLegende);
        add(legendePanel, BorderLayout.SOUTH);
    }

    // ── API publique ──────────────────────────────────────────────────────────

    public void setCours(Classe classe, Cours cours) {
        this.classe = classe;
        this.cours  = cours;
        lblTitre.setText(String.format("Fiche de notes — %s / %s", classe.getNom(), cours.getNom()));
        mettreAJourLibelleFormule();
        rebuildTable();
    }

    // ── Construction du tableau ───────────────────────────────────────────────

    private void rebuildTable() {
        model.setRowCount(0);
        model.setColumnCount(0);
        nomsEvaluations = new ArrayList<>();

        model.addColumn("Matricule");
        model.addColumn("Nom");
        model.addColumn("Prénom");

        for (Evaluation ev : cours.getModelesEvaluations()) {
            model.addColumn(String.format("%s (coef %.1f)", ev.getNom(), ev.getCoeff()));
            nomsEvaluations.add(ev.getNom());
        }
        model.addColumn("MOYENNE");

        for (Etudiant e : cours.getEtudiants()) {
            ArrayList<Object> row = new ArrayList<>();
            row.add(e.getMatricule());
            row.add(e.getNom());
            row.add(e.getPrenom());

            ArrayList<Evaluation> evals = e.getEvaluations();
            for (int i = 0; i < nomsEvaluations.size(); i++) {
                row.add(i < evals.size() ? evals.get(i).getNote() : 0.0);
            }

            double moy = Calculateur.calculerAvecFormule(e.getEvaluations(), cours.getFormule());
            row.add(Double.isNaN(moy) ? "N/A" : String.format("%.2f", moy));
            model.addRow(row.toArray());
        }

        // Renderer colonnes de notes : jaune si valeur == 0
        DefaultTableCellRenderer noteRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    double note = parseNote(value);
                    if (note == 0.0) {
                        c.setBackground(COLOR_NOTE_VIDE);
                        setToolTipText("Veuillez entrer 0 ou saisir une note");
                    } else {
                        c.setBackground(COLOR_NOTE_NORMALE);
                        setToolTipText(null);
                    }
                }
                return c;
            }
        };

        for (int col = COL_OFFSET_NOTES; col < model.getColumnCount() - 1; col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(noteRenderer);
        }

        // Renderer colonne MOYENNE
        int lastCol = model.getColumnCount() - 1;
        table.getColumnModel().getColumn(lastCol).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(COLOR_MOYENNE_BG);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Listener sauvegarde automatique après édition d'une note
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

    // ── Sauvegarde d'une note modifiée dans le tableau ────────────────────────

    private void sauvegarderNoteDepuisTable(int row, int col) {
        try {
            String matricule = (String) model.getValueAt(row, COL_MATRICULE);
            Etudiant etu = findEtudiant(matricule);
            if (etu == null) return;

            String nomEval = nomsEvaluations.get(col - COL_OFFSET_NOTES);
            double note = parseNote(model.getValueAt(row, col));

            if (note < 0 || note > 20) {
                JOptionPane.showMessageDialog(this,
                    "La note doit être entre 0 et 20", "Erreur", JOptionPane.ERROR_MESSAGE);
                rebuildTable();
                return;
            }

            etu.modifierNoteParNom(nomEval, note);

            double moy = Calculateur.calculerAvecFormule(etu.getEvaluations(), cours.getFormule());
            model.setValueAt(
                Double.isNaN(moy) ? "N/A" : String.format("%.2f", moy),
                row, model.getColumnCount() - 1);

            //gestionnaire.sauvegarder();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            rebuildTable();
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void ajouterEtudiant() {
        JTextField txtNom    = new JTextField();
        JTextField txtPrenom = new JTextField();
        JTextField txtMat    = new JTextField();

        Object[] msg = { "Nom :", txtNom, "Prénom :", txtPrenom, "Matricule :", txtMat };
        int opt = JOptionPane.showConfirmDialog(this, msg, "Ajouter un étudiant", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                cours.ajouter_Etu(txtNom.getText(), txtPrenom.getText(), txtMat.getText());
                //gestionnaire.sauvegarder();
                rebuildTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Supprime l'étudiant correspondant à la ligne sélectionnée dans le tableau.
     * Accessible via le bouton toolbar ET le menu contextuel (clic droit).
     */
    private void supprimerEtudiantSelectionne() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un étudiant dans le tableau.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String matricule = (String) model.getValueAt(row, COL_MATRICULE);
        String nom       = (String) model.getValueAt(row, COL_NOM);
        String prenom    = (String) model.getValueAt(row, COL_PRENOM);

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Supprimer l'étudiant \"%s %s\" (matricule : %s) ?%n"
                + "Cette action supprimera toutes ses notes dans ce cours.",
                nom, prenom, matricule),
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                cours.retirer_Etu(matricule);
                //gestionnaire.sauvegarder();
                rebuildTable();
                JOptionPane.showMessageDialog(this,
                    "Étudiant \"" + nom + " " + prenom + "\" supprimé avec succès.",
                    "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Supprime le cours courant depuis la vue fiche de notes,
     * puis retourne automatiquement à la vue de la classe parente.
     */
    private void supprimerCoursCourant() {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("<html>Supprimer le cours <b>\"%s\"</b> ?<br><br>"
                + "Cette action supprimera :<br>"
                + "&nbsp;&nbsp;• %d étudiant(s) inscrit(s)<br>"
                + "&nbsp;&nbsp;• Toutes leurs notes et évaluations<br><br>"
                + "<b>Cette opération est irréversible.</b></html>",
                cours.getNom(),
                cours.getEtudiants().size()),
            "Confirmation de suppression du cours",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String nomCours = cours.getNom();
            try {
                classe.supprimer_cours(nomCours);
                //gestionnaire.sauvegarder();
                // Retour à la vue détail de la classe (le cours n'existe plus)
                mainFrame.showClasseDetail(classe);
                JOptionPane.showMessageDialog(mainFrame,
                    "Cours \"" + nomCours + "\" supprimé avec succès.",
                    "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterEvaluation() {
        JTextField txtNom  = new JTextField();
        JTextField txtCoef = new JTextField("1.0");

        Object[] msg = { "Nom de l'évaluation :", txtNom, "Coefficient :", txtCoef };
        int opt = JOptionPane.showConfirmDialog(this, msg, "Nouvelle Évaluation", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                double coef = Double.parseDouble(txtCoef.getText().trim());
                cours.ajouterTypeEvaluation(txtNom.getText().trim(), coef);
                //gestionnaire.sauvegarder();
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

        String[] options = evals.stream().map(Evaluation::getNom).toArray(String[]::new);
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
            //gestionnaire.sauvegarder();
            rebuildTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurerFormule() {
        String formuleActuelle = cours.getFormule();

        String nouvelleFormule = JOptionPane.showInputDialog(this,
            "<html>Formule de calcul de la moyenne.<br>"
            + "Laissez <b>STANDARD</b> pour la moyenne pondérée automatique.<br><br>"
            + "Ou saisissez une expression personnalisée :<br>"
            + "&nbsp;&nbsp;Exemple : <code>E0*0.4+E1*0.6</code><br>"
            + "&nbsp;&nbsp;E0 = 1re évaluation, E1 = 2e, etc.</html>",
            formuleActuelle);

        if (nouvelleFormule == null) return;

        cours.setFormule(nouvelleFormule.trim());
        //gestionnaire.sauvegarder();
        mettreAJourLibelleFormule();
        rebuildTable();

        JOptionPane.showMessageDialog(this,
            "Formule enregistrée : " + cours.getFormule(),
            "Formule mise à jour", JOptionPane.INFORMATION_MESSAGE);
    }

    private void imprimer() {
        new EditionDialog(mainFrame, cours, classe).setVisible(true);
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private Etudiant findEtudiant(String matricule) {
        for (Etudiant e : cours.getEtudiants()) {
            if (e.getMatricule().equalsIgnoreCase(matricule)) return e;
        }
        return null;
    }

    private double parseNote(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value != null) {
            try { return Double.parseDouble(value.toString()); }
            catch (NumberFormatException ignored) {}
        }
        return 0.0;
    }

    private void mettreAJourLibelleFormule() {
        if (cours != null) {
            lblFormule.setText("Formule : " + cours.getFormule());
        }
    }
}