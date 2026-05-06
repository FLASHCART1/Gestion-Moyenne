package gestionmoyenne.gui;

import gestionmoyenne.model.Classe;
import gestionmoyenne.service.GestionnaireClasse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private final GestionnaireClasse gestionnaire;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    private DashboardPanel dashboardPanel;
    private ClasseDetailPanel classeDetailPanel;
    private CoursNotesPanel coursNotesPanel;
    
    public static final String DASHBOARD = "DASHBOARD";
    public static final String CLASSE_DETAIL = "CLASSE_DETAIL";
    public static final String COURS_NOTES = "COURS_NOTES";

    public MainFrame() {
        gestionnaire = new GestionnaireClasse();
        
        setTitle("Gestion des Moyennes — Fiche de Notes");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "Voulez-vous quitter l'application ?\nLes données sont sauvegardées automatiquement.",
                    "Quitter",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    gestionnaire.sauvegarder();
                    dispose();
                    System.exit(0);
                }
            }
        });
        
        // Barre latérale
        JPanel sideBar = createSideBar();
        add(sideBar, BorderLayout.WEST);
        
        // Contenu central
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        dashboardPanel = new DashboardPanel(this, gestionnaire);
        classeDetailPanel = new ClasseDetailPanel(this, gestionnaire);
        coursNotesPanel = new CoursNotesPanel(this, gestionnaire);
        
        contentPanel.add(dashboardPanel, DASHBOARD);
        contentPanel.add(classeDetailPanel, CLASSE_DETAIL);
        contentPanel.add(coursNotesPanel, COURS_NOTES);
        
        add(contentPanel, BorderLayout.CENTER);
        
        showDashboard();
    }
    
    private JPanel createSideBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 52, 70));
        panel.setPreferredSize(new Dimension(200, 0));
        
        JLabel title = new JLabel("  GESTION NOTES");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.add(title);
        panel.add(new JSeparator());
        
        panel.add(createNavButton("📊 Tableau de bord", e -> showDashboard()));
        panel.add(createNavButton("🏫 Classes & Cours", e -> showDashboard()));
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