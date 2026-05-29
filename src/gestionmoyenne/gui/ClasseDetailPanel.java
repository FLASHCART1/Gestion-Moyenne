package gestionmoyenne.gui;
 
import gestionmoyenne.model.Classe;
import gestionmoyenne.model.Cours;
import gestionmoyenne.service.GestionnaireClasse;
import gestionmoyenne.service.ImportService;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
 
@SuppressWarnings("serial")
public class ClasseDetailPanel extends JPanel {
    private final MainFrame mainFrame;
    @SuppressWarnings("unused")
	private final GestionnaireClasse gestionnaire;
    private Classe classe;
 
    private JLabel lblTitre;
    private JPanel coursContainer;
    private ImportService importService;
 
    public ClasseDetailPanel(MainFrame mainFrame, GestionnaireClasse gestionnaire) {
        this.mainFrame    = mainFrame;
        this.gestionnaire = gestionnaire;
        this.importService = new ImportService();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 242, 245));
 
        // ── NORTH : header + barre d'actions dans un seul panneau ──────────────
        // CORRECTION : avant, header et actions étaient tous les deux ajoutés en
        // BorderLayout.NORTH, ce qui faisait disparaître le header (bouton retour
        // + titre). On les regroupe maintenant dans un panneau unique.
        JPanel northPanel = new JPanel(new BorderLayout(0, 8));
        northPanel.setOpaque(false);
 
        // Ligne 1 : bouton retour + titre
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        JButton btnBack = new JButton("← Retour");
        btnBack.addActionListener(e -> mainFrame.showDashboard());
        lblTitre = new JLabel("Classe");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitre, BorderLayout.CENTER);
 
        // Ligne 2 : boutons d'action
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
 
        JButton btnAddCours = new JButton("+ Ajouter un cours");
        btnAddCours.addActionListener(e -> ajouterCours());
 
        JButton btnStats = new JButton("📈 Statistiques");
        btnStats.addActionListener(e -> afficherStats());
 
        actions.add(btnAddCours);
        actions.add(btnStats);
 
        northPanel.add(header,  BorderLayout.NORTH);
        northPanel.add(actions, BorderLayout.SOUTH);
 
        add(northPanel, BorderLayout.NORTH);
 
        // ── CENTER : liste des cours ────────────────────────────────────────────
        coursContainer = new JPanel();
        coursContainer.setLayout(new BoxLayout(coursContainer, BoxLayout.Y_AXIS));
        coursContainer.setOpaque(false);
 
        JScrollPane scroll = new JScrollPane(coursContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }
 
    public void setClasse(Classe classe) {
        this.classe = classe;
        lblTitre.setText("Classe : " + classe.getNom());
        refresh();
    }
 
    private void refresh() {
        coursContainer.removeAll();
        if (classe.getModules().isEmpty()) {
            JLabel empty = new JLabel("Aucun cours dans cette classe.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            coursContainer.add(empty);
        } else {
            for (Cours c : classe.getModules()) {
                coursContainer.add(createCoursRow(c));
                coursContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        coursContainer.revalidate();
        coursContainer.repaint();
    }
 
    private JPanel createCoursRow(Cours cours) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(12, 15, 12, 15)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
 
        int nbEtu  = cours.getEtudiants().size();
        int nbEval = cours.getModelesEvaluations().size();
 
        JLabel lblInfo = new JLabel(String.format(
            "<html><b>%s</b> &nbsp;•&nbsp; %dh &nbsp;•&nbsp; %d étudiants &nbsp;•&nbsp; %d évaluations</html>",
            cours.getNom(), cours.getVolumeHoraire(), nbEtu, nbEval
        ));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
 
        JButton btnOpen = new JButton("📋 Fiche de notes");
        btnOpen.setBackground(new Color(0, 123, 255));
        btnOpen.setForeground(Color.BLACK);
        btnOpen.setFocusPainted(false);
        btnOpen.addActionListener(e -> mainFrame.showCoursNotes(classe, cours));
 
        JButton btnImport = new JButton("📥 Import Excel");
        btnImport.addActionListener(e -> importerExcel(cours));
 
        JButton btnDelete = new JButton("🗑");
        btnDelete.setForeground(Color.RED);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setBorderPainted(false);
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le cours \"" + cours.getNom() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                classe.supprimer_cours(cours.getNom());
                //gestionnaire.sauvegarder();
                refresh();
            }
        });
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnImport);
        btnPanel.add(btnOpen);
        btnPanel.add(btnDelete);
 
        row.add(lblInfo,  BorderLayout.CENTER);
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }
 
    private void ajouterCours() {
        JTextField txtNom = new JTextField();
        JTextField txtVH  = new JTextField("30");
 
        Object[] message = {
            "Nom du cours :",  txtNom,
            "Volume horaire :", txtVH
        };
 
        int option = JOptionPane.showConfirmDialog(this, message, "Nouveau Cours", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = txtNom.getText().trim();
                int vh = Integer.parseInt(txtVH.getText().trim());
                classe.ajouter_cours(nom, vh);
                //gestionnaire.sauvegarder();
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void importerExcel(Cours cours) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                int nb = importService.importerEtudiantsExcel(cours, f.getAbsolutePath());
                //gestionnaire.sauvegarder();
                refresh();
                JOptionPane.showMessageDialog(this, nb + " étudiants importés avec succès !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur import :\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void afficherStats() {
        int totalEtu  = 0;
        int totalEval = 0;
        for (var c : classe.getModules()) {
            totalEtu  += c.getEtudiants().size();
            totalEval += c.getModelesEvaluations().size();
        }
 
        String msg = "<html><b>Classe :</b> " + classe.getNom() + "<br>"
            + "<b>Cours :</b> " + classe.getModules().size() + "<br>"
            + "<b>Étudiants (total) :</b> " + totalEtu + "<br>"
            + "<b>Évaluations (total) :</b> " + totalEval + "</html>";
 
        JOptionPane.showMessageDialog(this, msg, "Statistiques — " + classe.getNom(), JOptionPane.INFORMATION_MESSAGE);
    }
}