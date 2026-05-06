package gestionmoyenne.gui;

import gestionmoyenne.model.Classe;
import gestionmoyenne.service.GestionnaireClasse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final GestionnaireClasse gestionnaire;
    private JPanel cardsContainer;

    public DashboardPanel(MainFrame mainFrame, GestionnaireClasse gestionnaire) {
        this.mainFrame = mainFrame;
        this.gestionnaire = gestionnaire;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 242, 245));
        
        // Header
        JLabel header = new JLabel("Tableau de bord");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);
        
        // Bouton nouvelle classe
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JButton btnNew = new JButton("+ Nouvelle Classe");
        btnNew.setBackground(new Color(0, 123, 255));
        btnNew.setForeground(Color.WHITE);
        btnNew.setFocusPainted(false);
        btnNew.addActionListener(e -> creerClasse());
        topPanel.add(btnNew, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Container des cartes
        cardsContainer = new JPanel(new GridLayout(0, 3, 15, 15));
        cardsContainer.setOpaque(false);
        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
        
        refresh();
    }
    
    public void refresh() {
        cardsContainer.removeAll();
        ArrayList<Classe> classes = gestionnaire.getClasses();
        
        if (classes.isEmpty()) {
            JLabel empty = new JLabel("Aucune classe. Cliquez sur \"+ Nouvelle Classe\" pour commencer.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            cardsContainer.setLayout(new GridLayout(1, 1));
            cardsContainer.add(empty);
        } else {
            cardsContainer.setLayout(new GridLayout(0, 3, 15, 15));
            for (Classe c : classes) {
                cardsContainer.add(createClasseCard(c));
            }
        }
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    private JPanel createClasseCard(Classe classe) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblNom = new JLabel(classe.getNom());
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        int nbCours = classe.getModules().size();
        int nbEtudiants = 0;
        for (var cr : classe.getModules()) nbEtudiants += cr.getEtudiants().size();
        
        JLabel lblStats = new JLabel(String.format("<html>%d Cours<br>%d Étudiants</html>", nbCours, nbEtudiants));
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStats.setForeground(Color.DARK_GRAY);
        
        JButton btnOpen = new JButton("Ouvrir");
        btnOpen.setBackground(new Color(40, 167, 69));
        btnOpen.setForeground(Color.WHITE);
        btnOpen.setFocusPainted(false);
        btnOpen.addActionListener(e -> mainFrame.showClasseDetail(classe));
        
        JButton btnDelete = new JButton("Supprimer");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Supprimer la classe \"" + classe.getNom() + "\" ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gestionnaire.supprimer(classe.getNom());
                refresh();
            }
        });
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnOpen);
        btnPanel.add(btnDelete);
        
        card.add(lblNom, BorderLayout.NORTH);
        card.add(lblStats, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void creerClasse() {
        String nom = JOptionPane.showInputDialog(this, "Nom de la classe :", "Nouvelle Classe", JOptionPane.PLAIN_MESSAGE);
        if (nom != null && !nom.trim().isEmpty()) {
            try {
                gestionnaire.creerClasse(nom.trim());
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}