package gestionmoyenne.gui;

import gestionmoyenne.model.Classe;
import gestionmoyenne.service.GestionnaireClasse;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private final GestionnaireClasse gestionnaire;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private DashboardPanel dashboardPanel;
    private ClasseDetailPanel classeDetailPanel;
    private CoursNotesPanel coursNotesPanel;
    private JLabel lblStatus;

    public static final String DASHBOARD     = "DASHBOARD";
    public static final String CLASSE_DETAIL = "CLASSE_DETAIL";
    public static final String COURS_NOTES   = "COURS_NOTES";
    
    public final JMenuBar menuBar;


    public MainFrame() {
        gestionnaire = new GestionnaireClasse();
        
        setTitle("Gestion des Moyennes — Fiche de Notes");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                demanderEtQuitter();
            }
        });

        // Barre latérale
        JPanel sideBar = createSideBar();
        add(sideBar, BorderLayout.WEST);

        // Contenu central
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        menuBar = new JMenuBar();
        
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        dashboardPanel    = new DashboardPanel(this, gestionnaire);
        classeDetailPanel = new ClasseDetailPanel(this, gestionnaire);
        coursNotesPanel   = new CoursNotesPanel(this, gestionnaire);

        contentPanel.add(dashboardPanel,    DASHBOARD);
        contentPanel.add(classeDetailPanel, CLASSE_DETAIL);
        contentPanel.add(coursNotesPanel,   COURS_NOTES);

        add(contentPanel, BorderLayout.CENTER);

        showDashboard();

        // Barre de menu
        setJMenuBar(createMenuBar());
        
    }
    
    // ==========================================================================
    //  BARRE DE STATUS
    // ==========================================================================
    
    private JPanel createStatusBar() {
        JPanel sB = new JPanel(new BorderLayout());
        sB.setBackground(new Color(210, 212, 215));
        sB.setPreferredSize(new Dimension(0, 24));
        sB.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 182, 185)));

        // Zone gauche : message contextuel
        lblStatus = new JLabel("  Pret");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(80, 80, 80));
        sB.add(lblStatus, BorderLayout.CENTER);

        // Separateur vertical + version collés à droite
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        rightPanel.setOpaque(false);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 16));
        sep.setForeground(new Color(170, 172, 175));
        rightPanel.add(sep);

        JLabel lblVersion = new JLabel("v1.0  ");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setForeground(new Color(100, 100, 100));
        rightPanel.add(lblVersion);

        sB.add(rightPanel, BorderLayout.EAST);
        return sB;
    }

    /** Met a jour le message a gauche de la barre de statut. */
    public void setStatus(String message) {
        lblStatus.setText("  " + message);
    }
    

    // ==========================================================================
    //  BARRE DE MENU
    // ==========================================================================

    private JMenuBar createMenuBar() {
        //menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ── Menu Fichier ──────────────────────────────────────────────────────
        JMenu menuFichier = new JMenu("Fichier");
        menuFichier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuFichier.setMnemonic(KeyEvent.VK_F); // Alt+F ouvre le menu 'fichier'
        
        // ── Menu Edition ──────────────────────────────────────────────────────
        JMenu menuEdition = new JMenu("Edition");
        menuEdition.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuEdition.setMnemonic(KeyEvent.VK_E); // Alt+E ouvre le menu 'Edition'
        
        // ── Enregistrer  Ctrl+S ───────────────────────────────────────────────
        JMenuItem itemEnregistrer = new JMenuItem("Enregistrer");
        itemEnregistrer.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        itemEnregistrer.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        itemEnregistrer.addActionListener(e -> actionEnregistrer());

        // ── Enregistrer sous  Ctrl+Maj+S ──────────────────────────────────────
        JMenuItem itemEnregistrerSous = new JMenuItem("Enregistrer sous...");
        itemEnregistrerSous.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        /*itemEnregistrerSous.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));*/
        itemEnregistrerSous.addActionListener(e -> actionEnregistrerSous());

        // ── Charger un fichier JSON ───────────────────────────────────────────
        JMenuItem itemCharger = new JMenuItem("Charger un fichier JSON...");
        itemCharger.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        itemCharger.addActionListener(e -> actionChargerJson());

        // ── Séparateur ────────────────────────────────────────────────────────
        JSeparator separateur = new JSeparator();

        // ── Fermer l'application  Alt+F4 ──────────────────────────────────────
        JMenuItem itemFermer = new JMenuItem("Fermer l'application");
        itemFermer.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        itemFermer.addActionListener(e -> demanderEtQuitter());
        
        // ── Gestion de classe ──────────────────────────────────────────────────
        JMenu gestionClasse = new JMenu("Gestion Classe    ");
        
     // ==========================================================================
     //  BARRE DE SOUS-MENU
     // ==========================================================================
        JMenuItem creerClasse = new JMenuItem("Créer une classe");
        creerClasse.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        creerClasse.addActionListener(e -> dashboardPanel.creerClasse());
        
        gestionClasse.add(creerClasse);
        
        menuFichier.add(itemCharger);
        menuFichier.add(itemEnregistrer);
        menuFichier.add(itemEnregistrerSous);
        menuFichier.add(separateur);
        menuFichier.add(itemFermer);
        
        menuEdition.add(gestionClasse);
        
        menuBar.add(menuFichier);
        menuBar.add(menuEdition);
        return menuBar;
    }

    // =========================================================================
    //  ACTIONS DES ÉLÉMENTS DE MENU
    // =========================================================================

    /** Enregistrer : sauvegarde dans le fichier par défaut. */
    private void actionEnregistrer() {
        gestionnaire.sauvegarder();
        JOptionPane.showMessageDialog(
            this,
            "Les données ont été sauvegardées avec succès.",
            "Enregistrer",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Enregistrer sous : l'utilisateur choisit un fichier .json de destination.
     * Le fichier par défaut reste inchangé ; c'est une copie vers le chemin choisi.
     */
    private void actionEnregistrerSous() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Enregistrer sous — choisir un fichier JSON");
        chooser.setFileFilter(new FileNameExtensionFilter("Fichier de données (*.json, *.gdm)", "json", "gdm"));
        chooser.setSelectedFile(new File("sauvegarde_export.json"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fichier = chooser.getSelectedFile();
            // Forcer l'extension .json si l'utilisateur ne l'a pas saisie
            if (!fichier.getName().toLowerCase().endsWith(".json")) {
                fichier = new File(fichier.getAbsolutePath() + ".json");
            }
            try {
                gestionnaire.sauvegarderVers(fichier.getAbsolutePath());
                JOptionPane.showMessageDialog(
                    this,
                    "Exporté avec succès vers :\n" + fichier.getAbsolutePath(),
                    "Enregistrer sous",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Erreur lors de l'enregistrement :\n" + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Charger : l'utilisateur choisit un fichier .json à importer.
     * Les données actuelles sont remplacées après confirmation.
     */
    private void actionChargerJson() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Charger un fichier JSON");
        chooser.setFileFilter(new FileNameExtensionFilter("Fichier de données (*.json, *.gdm)", "json", "gdm"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fichier = chooser.getSelectedFile();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Le chargement remplacera les données actuelles.\n"
                    + "Fichier sélectionné : " + fichier.getName() + "\n\n"
                    + "Continuer ?",
                "Charger un fichier JSON",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int nbClasses = gestionnaire.chargerDepuis(fichier.getAbsolutePath());
                    dashboardPanel.refresh();
                    cardLayout.show(contentPanel, DASHBOARD);
                    JOptionPane.showMessageDialog(
                        this,
                        nbClasses + " classe(s) chargée(s) depuis :\n" + fichier.getAbsolutePath(),
                        "Chargement réussi",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors du chargement :\n" + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /*
     * Dialogue de fermeture : propose de sauvegarder avant de quitter,
     * utilisé à la fois par le WindowListener et par le menu Fermer.
     */
    private void demanderEtQuitter() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous sauvegarder avant de quitter l'application ?",
            "Quitter",
            JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            gestionnaire.sauvegarder();
            dispose();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            dispose();
            System.exit(0);
        }
        // CANCEL → on ne fait rien, la fenêtre reste ouverte
    }

    // =========================================================================
    //  BARRE LATÉRALE
    // =========================================================================

    private JPanel createSideBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 52, 70));
        panel.setPreferredSize(new Dimension(200, 0));

        JLabel title = new JLabel("GESTION NOTES");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.add(title);
        panel.add(new JSeparator());

        panel.add(createNavButton("Tableau de bord",  e -> showDashboard()));
        //panel.add(createNavButton("Classes & Cours",  e -> showDashboard()));
        panel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("  v1.0");
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(footer);

        return panel;
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 52, 70));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.addActionListener(action);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(60, 70, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 52, 70));
            }
        });
        return btn;
    }

    // =========================================================================
    //  NAVIGATION
    // =========================================================================

    public void showDashboard() {
        dashboardPanel.refresh();
        cardLayout.show(contentPanel, DASHBOARD);
    }

    public void showClasseDetail(Classe classe) {
        classeDetailPanel.setClasse(classe);
        cardLayout.show(contentPanel, CLASSE_DETAIL);
    }

    public void showCoursNotes(Classe classe, gestionmoyenne.model.Cours cours) {
        coursNotesPanel.setCours(classe, cours);
        cardLayout.show(contentPanel, COURS_NOTES);
    }

    public GestionnaireClasse getGestionnaire() {
        return gestionnaire;
    }
}