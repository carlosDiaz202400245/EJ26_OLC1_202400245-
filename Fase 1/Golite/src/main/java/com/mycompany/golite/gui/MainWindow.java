package com.mycompany.golite.gui;
 
import com.mycompany.golite.GoliteLexer;
import com.mycompany.golite.parser.sym;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.List;
 
public class MainWindow extends JFrame {
 
    private EditorPanel editorPanel;
    private ConsolePanel consolePanel;
 
    // Archivo actualmente abierto (null = nuevo sin guardar)
    private File archivoActual = null;
 
    public MainWindow() {
        initComponents();
        setupLayout();
    }
 
    // ─── INICIALIZACIÓN ────────────────────────────────────────────────
    private void initComponents() {
        setTitle("GoLite IDE - Fase 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
 
        editorPanel  = new EditorPanel();
        consolePanel = new ConsolePanel();
    }
 
    private void setupLayout() {
        setLayout(new BorderLayout());
 
        // ── Barra de herramientas ──────────────────────────────────
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
 
        JButton btnNew     = new JButton("Nuevo");
        JButton btnOpen    = new JButton("Abrir");
        JButton btnSave    = new JButton("Guardar");
        JButton btnSaveAs  = new JButton("Guardar como");
        JButton btnExecute = new JButton("▶ Ejecutar");
        btnExecute.setForeground(new Color(0, 150, 0));
 
        toolBar.add(btnNew);
        toolBar.add(btnOpen);
        toolBar.add(btnSave);
        toolBar.add(btnSaveAs);
        toolBar.addSeparator();
        toolBar.add(btnExecute);
        add(toolBar, BorderLayout.NORTH);
 
        // ── Editor ────────────────────────────────────────
        JScrollPane editorScroll = new JScrollPane(editorPanel);
        editorScroll.setBorder(BorderFactory.createTitledBorder("Editor"));
        add(editorScroll, BorderLayout.CENTER);
 
        // ── Panel inferior ─────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout());
 
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTokens = new JButton("Ver Tokens");
        JButton btnErrors = new JButton("Ver Errores");
        JButton btnClear  = new JButton("Limpiar consola");
        reportPanel.add(btnTokens);
        reportPanel.add(btnErrors);
        reportPanel.add(btnClear);
 
        bottomPanel.add(reportPanel, BorderLayout.NORTH);
        bottomPanel.add(consolePanel, BorderLayout.CENTER);
        consolePanel.setPreferredSize(new Dimension(1000, 180));
        add(bottomPanel, BorderLayout.SOUTH);
 
        // ─── ACCIONES ──────────────────────────────────────────────────
        btnNew.addActionListener(e -> accionNuevo());
        btnOpen.addActionListener(e -> accionAbrir());
        btnSave.addActionListener(e -> accionGuardar());
        btnSaveAs.addActionListener(e -> accionGuardarComo());
        btnExecute.addActionListener(e -> accionEjecutar());
        btnTokens.addActionListener(e -> accionVerTokens());
        btnErrors.addActionListener(e -> accionVerErrores());
        btnClear.addActionListener(e -> consolePanel.clear());
    }
 
    // ─── NUEVO ARCHIVO ─────────────────────────────────────────────────
    private void accionNuevo() {
        if (!editorPanel.getText().isEmpty()) {
            int op = JOptionPane.showConfirmDialog(this,
                "¿Deseas guardar los cambios antes de crear un nuevo archivo?",
                "Nuevo archivo", JOptionPane.YES_NO_CANCEL_OPTION);
            if (op == JOptionPane.YES_OPTION) accionGuardar();
            else if (op == JOptionPane.CANCEL_OPTION) return;
        }
        editorPanel.setText("");
        archivoActual = null;
        setTitle("GoLite IDE - Fase 1 - [Nuevo archivo]");
        consolePanel.println("Nuevo archivo creado.");
    }
 
    // ─── ABRIR ARCHIVO .glt ────────────────────────────────────────────
    private void accionAbrir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir archivo GoLite");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Archivos GoLite (*.glt)", "glt"));
        chooser.setAcceptAllFileFilterUsed(false);
 
        int resultado = chooser.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;
 
        File archivo = chooser.getSelectedFile();
        try {
            String contenido = Files.readString(archivo.toPath(),
                    StandardCharsets.UTF_8);
            editorPanel.setText(contenido);
            editorPanel.setCaretPosition(0);
            archivoActual = archivo;
            setTitle("GoLite IDE - Fase 1 - [" + archivo.getName() + "]");
            consolePanel.println("Archivo abierto: " + archivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir el archivo:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ─── GUARDAR ARCHIVO ───────────────────────────────────────────────
    private void accionGuardar() {
        if (archivoActual == null) {
            accionGuardarComo();   // sin archivo actual → pedir ruta
        } else {
            guardarEn(archivoActual);
        }
    }
 
    // ─── GUARDAR COMO ──────────────────────────────────────────────────
    private void accionGuardarComo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo GoLite");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Archivos GoLite (*.glt)", "glt"));
        chooser.setAcceptAllFileFilterUsed(false);
 
        // si ya hay un archivo, empezar en su carpeta
        if (archivoActual != null) {
            chooser.setCurrentDirectory(archivoActual.getParentFile());
            chooser.setSelectedFile(archivoActual);
        }
 
        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;
 
        File archivo = chooser.getSelectedFile();

        // forzar extensión .glt
        if (!archivo.getName().toLowerCase().endsWith(".glt")) {
            archivo = new File(archivo.getAbsolutePath() + ".glt");
        }
 
        guardarEn(archivo);
    }
 
    // ─── GUARDAR EN DISCO ──────────────────────────────────────────────
    private void guardarEn(File archivo) {
        try {
            Files.writeString(archivo.toPath(),
                    editorPanel.getText(), StandardCharsets.UTF_8);
            archivoActual = archivo;
            setTitle("GoLite IDE - Fase 1 - [" + archivo.getName() + "]");
            consolePanel.println("Archivo guardado: " + archivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo guardar el archivo:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ─── EJECUTAR (léxico → sintáctico → interpretación) ───────────────
    private void accionEjecutar() {
        consolePanel.clear();
 
        // Limpiar reportes anteriores
        GoliteLexer.listaTokens.clear();
        GoliteLexer.listaErrores.clear();
        com.mycompany.golite.parser.parser.erroresSintacticos.clear();
        com.mycompany.golite.Errores.limpiar();
 
        String codigo = editorPanel.getText();
        if (codigo.trim().isEmpty()) {
            consolePanel.println("El editor está vacío.");
            return;
        }
 
        // ─── 1) ANÁLISIS LÉXICO COMPLETO ──────────────────────────────
        // Escanea todo el archivo aparte del parser, para que la tabla de
        // tokens y los errores léxicos queden completos aunque el parser falle.
        try {
            GoliteLexer.registrar = true;
            GoliteLexer lexerScan = new GoliteLexer(new java.io.StringReader(codigo));
            java_cup.runtime.Symbol tok;
            do {
                tok = lexerScan.next_token();
            } while (tok != null && tok.sym != com.mycompany.golite.parser.sym.EOF);
        } catch (Exception ex) {
            consolePanel.println("Error durante el análisis léxico: " + ex.getMessage());
        }

        // ─── 2) ANÁLISIS SINTÁCTICO ───────────────────────────────────
        // Usa un lexer nuevo con el registro apagado para no duplicar tokens;
        // los errores sintácticos quedan en parser.erroresSintacticos.
        Object resultado = null;
        try {
            GoliteLexer.registrar = false;
            GoliteLexer lexerParse = new GoliteLexer(new java.io.StringReader(codigo));
            com.mycompany.golite.parser.parser parser =
                    new com.mycompany.golite.parser.parser();
            parser.setScanner(lexerParse);
            java_cup.runtime.Symbol res = parser.parse();
            resultado = (res != null) ? res.value : null;
        } catch (Exception ex) {
            // Error sintáctico irrecuperable: ya quedó registrado.
        } finally {
            GoliteLexer.registrar = true;
        }

        int errLex = GoliteLexer.listaErrores.size();
        int errSin = com.mycompany.golite.parser.parser.erroresSintacticos.size();

        // ─── 3) EJECUCIÓN (solo si no hubo errores) ───────────────────
        if (errLex == 0 && errSin == 0 && resultado != null) {
            try {
                List<com.mycompany.golite.ast.Nodo> ast =
                        (List<com.mycompany.golite.ast.Nodo>) resultado;
                new com.mycompany.golite.Interprete(consolePanel).ejecutar(ast);
            } catch (Exception ex) {
                consolePanel.println("Error durante la ejecución: " + ex.getMessage());
            }
        }

        consolePanel.println("=== Análisis completado ===");
        consolePanel.println("Tokens reconocidos  : " + GoliteLexer.listaTokens.size());
        consolePanel.println("Errores léxicos     : " + errLex);
        consolePanel.println("Errores sintácticos : " + errSin);

        if (errLex == 0 && errSin == 0) {
            consolePanel.println("\nCódigo analizado sin errores.");
        } else {
            consolePanel.println("\nSe encontraron errores. Presiona 'Ver Errores' para el reporte.");
        }
    }
 
    // ─── VER TOKENS ────────────────────────────────────────────────────
    private void accionVerTokens() {
        if (GoliteLexer.listaTokens.isEmpty()) {
            consolePanel.println("No hay tokens. Ejecuta el análisis primero.");
            return;
        }
        consolePanel.clear();
        consolePanel.println(String.format("%-5s %-25s %-20s %-8s %-8s",
                "No.", "Lexema", "Tipo", "Línea", "Columna"));
        consolePanel.println("-".repeat(70));
        int i = 1;
        for (var t : GoliteLexer.listaTokens) {
            consolePanel.println(String.format("%-5d %-25s %-20s %-8d %-8d",
                    i++, t.lexema, t.tipo, t.linea, t.columna));
        }
        consolePanel.println("-".repeat(70));
        consolePanel.println("Total: " + GoliteLexer.listaTokens.size() + " tokens.");
    }
 
    private void accionVerErrores() {
        int totalLex = GoliteLexer.listaErrores.size();
        int totalSin = com.mycompany.golite.parser.parser.erroresSintacticos.size();
 
        if (totalLex == 0 && totalSin == 0) {
            consolePanel.println("No se encontraron errores.");
            return;
        }
 
        consolePanel.clear();
        consolePanel.println("=== REPORTE DE ERRORES ===\n");
 
        String header = String.format("%-5s %-10s %-8s %-8s %s",
                "No.", "Tipo", "Línea", "Columna", "Descripción");
        String separador = "-".repeat(75);
 
        int contador = 1;
 
        if (totalLex > 0) {
            consolePanel.println("--- Errores Léxicos (" + totalLex + ") ---");
            consolePanel.println(header);
            consolePanel.println(separador);
            for (var err : GoliteLexer.listaErrores) {
                consolePanel.println(String.format("%-5d %-10s %-8d %-8d %s",
                        contador++, "Léxico", err.linea, err.columna, err.mensaje));
            }
            consolePanel.println("");
        }
 
        if (totalSin > 0) {
            contador = 1;
            consolePanel.println("--- Errores Sintácticos (" + totalSin + ") ---");
            consolePanel.println(header);
            consolePanel.println(separador);
            for (var err : com.mycompany.golite.parser.parser.erroresSintacticos) {
                consolePanel.println(String.format("%-5d %-10s %-8d %-8d %s",
                        contador++, "Sintáctico", err.linea, err.columna, err.descripcion));
            }
        }
    }
}