package gestionmoyenne.gui;

import javax.swing.*;
import java.awt.*;

public class Components {
    public static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
    
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}