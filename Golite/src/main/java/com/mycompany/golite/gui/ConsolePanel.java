package com.mycompany.golite.gui;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel {
    
    private JTextArea consoleArea;
    
    public ConsolePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Consola"));
        
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.WHITE);
        
        add(new JScrollPane(consoleArea), BorderLayout.CENTER);
    }
    
    public void print(String text) {
        SwingUtilities.invokeLater(() -> {
            consoleArea.append(text);
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        });
    }
    
    public void println(String text) {
        print(text + "\n");
    }
    
    public void clear() {
        SwingUtilities.invokeLater(() -> consoleArea.setText(""));
    }
}