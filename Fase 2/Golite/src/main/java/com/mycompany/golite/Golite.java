package com.mycompany.golite;

import com.mycompany.golite.gui.MainWindow;
import javax.swing.SwingUtilities;

public class Golite {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}