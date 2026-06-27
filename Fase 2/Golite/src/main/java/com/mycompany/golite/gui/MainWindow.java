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

    // null cuando es un archivo nuevo sin guardar
    private File archivoActual = null;

    public MainWindow() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setTitle("GoLite IDE - Fase 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        editorPanel  = new EditorPanel();
        consolePanel = new ConsolePanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Barra de herramientas
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnNew     = new JButton("Nuevo");
        JButton btnOpen    = new JButton("Abrir");
        JButton btnSave    = new JButton("Guardar");
        JButton btnSaveAs  = new JButton("Guardar como");
        JButton btnExecute = new JButton("> Ejecutar");
        btnExecute.setForeground(new Color(0, 150, 0));

        toolBar.add(btnNew);
        toolBar.add(btnOpen);
        toolBar.add(btnSave);
        toolBar.add(btnSaveAs);
        toolBar.addSeparator();
        toolBar.add(btnExecute);
        add(toolBar, BorderLayout.NORTH);

        // Editor
        JScrollPane editorScroll = new JScrollPane(editorPanel);
        editorScroll.setBorder(BorderFactory.createTitledBorder("Editor"));

        // Panel inferior con consola y botones de reporte
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTokens = new JButton("Ver Tokens");
        JButton btnErrors = new JButton("Ver Errores");
        JButton btnAST    = new JButton("Ver AST");
        JButton btnSimbolos = new JButton("Ver Simbolos");
        JButton btnClear  = new JButton("Limpiar consola");
        reportPanel.add(btnTokens);
        reportPanel.add(btnErrors);
        reportPanel.add(btnAST);
        reportPanel.add(btnSimbolos);
        reportPanel.add(btnClear);

        bottomPanel.add(reportPanel, BorderLayout.NORTH);
        bottomPanel.add(consolePanel, BorderLayout.CENTER);

        // Editor arriba, consola abajo, con divisor ajustable
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, editorScroll, bottomPanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(420);

        consolePanel.setMinimumSize(new Dimension(100, 60));
        editorScroll.setMinimumSize(new Dimension(100, 120));

        add(splitPane, BorderLayout.CENTER);

        // Acciones
        btnNew.addActionListener(e -> accionNuevo());
        btnOpen.addActionListener(e -> accionAbrir());
        btnSave.addActionListener(e -> accionGuardar());
        btnSaveAs.addActionListener(e -> accionGuardarComo());
        btnExecute.addActionListener(e -> accionEjecutar());
        btnTokens.addActionListener(e -> accionVerTokens());
        btnErrors.addActionListener(e -> accionVerErrores());
        btnAST.addActionListener(e -> accionVerAST());
        btnSimbolos.addActionListener(e -> accionVerSimbolos());
        btnClear.addActionListener(e -> consolePanel.clear());
    }

    private void accionNuevo() {
        if (!editorPanel.getText().isEmpty()) {
            int op = JOptionPane.showConfirmDialog(this,
                "Deseas guardar los cambios antes de crear un nuevo archivo?",
                "Nuevo archivo", JOptionPane.YES_NO_CANCEL_OPTION);
            if (op == JOptionPane.YES_OPTION) accionGuardar();
            else if (op == JOptionPane.CANCEL_OPTION) return;
        }
        editorPanel.setText("");
        archivoActual = null;
        setTitle("GoLite IDE - Fase 2 - [Nuevo archivo]");
        consolePanel.println("Nuevo archivo creado.");
    }

    private void accionAbrir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir archivo GoLite");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos GoLite (*.glt)", "glt"));
        chooser.setAcceptAllFileFilterUsed(false);

        int resultado = chooser.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File archivo = chooser.getSelectedFile();
        try {
            String contenido = Files.readString(archivo.toPath(), StandardCharsets.UTF_8);
            editorPanel.setText(contenido);
            editorPanel.setCaretPosition(0);
            archivoActual = archivo;
            setTitle("GoLite IDE - Fase 2 - [" + archivo.getName() + "]");
            consolePanel.println("Archivo abierto: " + archivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir el archivo:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionGuardar() {
        if (archivoActual == null) {
            accionGuardarComo();
        } else {
            guardarEn(archivoActual);
        }
    }

    private void accionGuardarComo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo GoLite");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos GoLite (*.glt)", "glt"));
        chooser.setAcceptAllFileFilterUsed(false);

        if (archivoActual != null) {
            chooser.setCurrentDirectory(archivoActual.getParentFile());
            chooser.setSelectedFile(archivoActual);
        }

        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".glt")) {
            archivo = new File(archivo.getAbsolutePath() + ".glt");
        }
        guardarEn(archivo);
    }

    private void guardarEn(File archivo) {
        try {
            Files.writeString(archivo.toPath(), editorPanel.getText(), StandardCharsets.UTF_8);
            archivoActual = archivo;
            setTitle("GoLite IDE - Fase 2 - [" + archivo.getName() + "]");
            consolePanel.println("Archivo guardado: " + archivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo guardar el archivo:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ejecutar: lexico -> sintactico -> interpretacion
    private void accionEjecutar() {
        consolePanel.clear();

        GoliteLexer.listaTokens.clear();
        GoliteLexer.listaErrores.clear();
        com.mycompany.golite.parser.parser.erroresSintacticos.clear();
        com.mycompany.golite.Errores.limpiar();

        String codigo = editorPanel.getText();
        if (codigo.trim().isEmpty()) {
            consolePanel.println("El editor esta vacio.");
            return;
        }

        // 1) Analisis lexico completo, independiente del parser
        try {
            GoliteLexer.registrar = true;
            GoliteLexer lexerScan = new GoliteLexer(new java.io.StringReader(codigo));
            java_cup.runtime.Symbol tok;
            do {
                tok = lexerScan.next_token();
            } while (tok != null && tok.sym != com.mycompany.golite.parser.sym.EOF);
        } catch (Exception ex) {
            consolePanel.println("Error durante el analisis lexico: " + ex.getMessage());
        }

        // 2) Analisis sintactico
        Object resultado = null;
        try {
            GoliteLexer.registrar = false;
            GoliteLexer lexerParse = new GoliteLexer(new java.io.StringReader(codigo));
            com.mycompany.golite.parser.parser parser = new com.mycompany.golite.parser.parser();
            parser.setScanner(lexerParse);
            java_cup.runtime.Symbol res = parser.parse();
            resultado = (res != null) ? res.value : null;
        } catch (Exception ex) {
            // error irrecuperable: ya quedo registrado
        } finally {
            GoliteLexer.registrar = true;
        }

        int errLex = GoliteLexer.listaErrores.size();
        int errSin = com.mycompany.golite.parser.parser.erroresSintacticos.size();

        // 3) Ejecucion, solo si no hubo errores
        if (errLex == 0 && errSin == 0 && resultado != null) {
            try {
                List<com.mycompany.golite.ast.Nodo> ast =
                        (List<com.mycompany.golite.ast.Nodo>) resultado;
                new com.mycompany.golite.Interprete(consolePanel).ejecutar(ast);
            } catch (Exception ex) {
                consolePanel.println("Error durante la ejecucion: " + ex.getMessage());
            }
        }

        consolePanel.println("=== Analisis completado ===");
        consolePanel.println("Tokens reconocidos  : " + GoliteLexer.listaTokens.size());
        consolePanel.println("Errores lexicos     : " + errLex);
        consolePanel.println("Errores sintacticos : " + errSin);

        if (errLex == 0 && errSin == 0) {
            consolePanel.println("\nCodigo analizado sin errores.");
        } else {
            consolePanel.println("\nSe encontraron errores. Presiona 'Ver Errores' para el reporte.");
        }
    }

    private void accionVerTokens() {
        if (GoliteLexer.listaTokens.isEmpty()) {
            consolePanel.println("No hay tokens. Ejecuta el analisis primero.");
            return;
        }
        consolePanel.clear();
        consolePanel.println(String.format("%-5s %-25s %-20s %-8s %-8s",
                "No.", "Lexema", "Tipo", "Linea", "Columna"));
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
                "No.", "Tipo", "Linea", "Columna", "Descripcion");
        String separador = "-".repeat(75);

        int contador = 1;
        if (totalLex > 0) {
            consolePanel.println("--- Errores Lexicos (" + totalLex + ") ---");
            consolePanel.println(header);
            consolePanel.println(separador);
            for (var err : GoliteLexer.listaErrores) {
                consolePanel.println(String.format("%-5d %-10s %-8d %-8d %s",
                        contador++, "Lexico", err.linea, err.columna, err.mensaje));
            }
            consolePanel.println("");
        }

        if (totalSin > 0) {
            contador = 1;
            consolePanel.println("--- Errores Sintacticos (" + totalSin + ") ---");
            consolePanel.println(header);
            consolePanel.println(separador);
            for (var err : com.mycompany.golite.parser.parser.erroresSintacticos) {
                consolePanel.println(String.format("%-5d %-10s %-8d %-8d %s",
                        contador++, "Sintactico", err.linea, err.columna, err.descripcion));
            }
        }
    }

    // Genera el AST en formato DOT y lo renderiza con Graphviz
    private void accionVerAST() {
        List<com.mycompany.golite.ast.Nodo> ast = parsearAST();
        if (ast == null) {
            consolePanel.println("No se pudo generar el AST.");
            return;
        }

        String dot = new com.mycompany.golite.ast.GeneradorAST().generar(ast);
        try {
            File dotFile = new File("ast.dot");
            Files.writeString(dotFile.toPath(), dot, StandardCharsets.UTF_8);
            consolePanel.println("AST (.dot) escrito en: " + dotFile.getAbsolutePath());

            File pngFile = new File("ast.png");
            try {
                Process proc = new ProcessBuilder("dot", "-Tpng",
                        dotFile.getAbsolutePath(), "-o", pngFile.getAbsolutePath())
                        .redirectErrorStream(true).start();
                int code = proc.waitFor();
                if (code == 0 && pngFile.exists()) {
                    consolePanel.println("Imagen generada: " + pngFile.getAbsolutePath());
                    abrirArchivo(pngFile);
                } else {
                    consolePanel.println("Graphviz devolvio codigo " + code
                            + ". Verifica que 'dot' este instalado y en el PATH.");
                }
            } catch (Exception ex) {
                consolePanel.println("No se encontro Graphviz. Se genero ast.dot; "
                        + "ejecutalo a mano con:  dot -Tpng ast.dot -o ast.png");
            }
        } catch (IOException ex) {
            consolePanel.println("Error al escribir el AST: " + ex.getMessage());
        }
    }

    // Parsea el editor y devuelve la lista de nodos del AST, o null
    @SuppressWarnings("unchecked")
    private List<com.mycompany.golite.ast.Nodo> parsearAST() {
        String codigo = editorPanel.getText();
        if (codigo.trim().isEmpty()) return null;
        try {
            GoliteLexer.registrar = false;
            GoliteLexer lexer = new GoliteLexer(new java.io.StringReader(codigo));
            com.mycompany.golite.parser.parser p = new com.mycompany.golite.parser.parser();
            p.setScanner(lexer);
            java_cup.runtime.Symbol res = p.parse();
            return (res != null) ? (List<com.mycompany.golite.ast.Nodo>) res.value : null;
        } catch (Exception ex) {
            return null;
        } finally {
            GoliteLexer.registrar = true;
        }
    }
    // Tabla de simbolos en consola
    private void accionVerSimbolos() {
        List<com.mycompany.golite.ast.Nodo> ast = parsearAST();
        if (ast == null) {
            consolePanel.println("No se pudo generar la tabla (editor vacio o sin arbol).");
            return;
        }
        List<String[]> filas = new com.mycompany.golite.ast.TablaSimbolos().generar(ast);
        consolePanel.clear();
        consolePanel.println("=== TABLA DE SIMBOLOS ===\n");
        consolePanel.println(String.format("%-4s %-15s %-11s %-12s %-15s %-6s %-4s",
                "No.", "Nombre", "Rol", "Tipo", "Ambito", "Linea", "Col"));
        consolePanel.println("-".repeat(75));
        int i = 1;
        for (String[] f : filas) {
            consolePanel.println(String.format("%-4d %-15s %-11s %-12s %-15s %-6s %-4s",
                    i++, f[0], f[1], f[2], f[3], f[4], f[5]));
        }
        consolePanel.println("-".repeat(75));
        consolePanel.println("Total: " + filas.size() + " simbolos.");
    }

    // Abre un archivo con la app del sistema, con fallback a Windows
    private void abrirArchivo(File archivo) {
        try {
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(archivo);
                return;
            }
        } catch (Exception ignored) {
        }
        try {
            new ProcessBuilder("cmd", "/c", "start", "", archivo.getAbsolutePath()).start();
        } catch (Exception ex) {
            consolePanel.println("No se pudo abrir la imagen automaticamente: "
                    + archivo.getAbsolutePath());
        }
    }
}
