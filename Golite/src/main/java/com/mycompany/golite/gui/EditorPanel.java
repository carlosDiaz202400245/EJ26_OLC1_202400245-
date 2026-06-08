package com.mycompany.golite.gui;

import javax.swing.*;
import java.awt.*;

public class EditorPanel extends JTextArea {
    
    public EditorPanel() {
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setTabSize(4);
        setBackground(Color.BLACK);
        setForeground(Color.GREEN);
        setCaretColor(Color.WHITE);
    }
}