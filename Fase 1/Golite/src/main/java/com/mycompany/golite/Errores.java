package com.mycompany.golite;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar errores de compilación de forma centralizada
 */
public class Errores {
    
    private static List<ErrorLexico> erroresLexico = new ArrayList<>();
    private static List<ErrorLexico> erroresSintactico = new ArrayList<>();
    private static List<ErrorLexico> erroresSematico = new ArrayList<>();
    
    /**
     * Agrega un error léxico
     */
    public static void agregarLexico(String mensaje, int linea, int columna) {
        erroresLexico.add(new ErrorLexico(mensaje, linea, columna));
    }
    
    /**
     * Agrega un error sintáctico
     */
    public static void agregarSintactico(String mensaje, int linea, int columna) {
        erroresSintactico.add(new ErrorLexico(mensaje, linea, columna));
    }
    
    /**
     * Agrega un error semántico
     */
    public static void agregarSematico(String mensaje, int linea, int columna) {
        erroresSematico.add(new ErrorLexico(mensaje, linea, columna));
    }
    
    /**
     * Obtiene todos los errores
     */
    public static List<ErrorLexico> obtenerErrores() {
        List<ErrorLexico> todos = new ArrayList<>();
        todos.addAll(erroresLexico);
        todos.addAll(erroresSintactico);
        todos.addAll(erroresSematico);
        return todos;
    }
    
    /**
     * Limpia todos los errores
     */
    public static void limpiar() {
        erroresLexico.clear();
        erroresSintactico.clear();
        erroresSematico.clear();
    }
    
    /**
     * Verifica si hay errores
     */
    public static boolean hayErrores() {
        return !erroresLexico.isEmpty() || !erroresSintactico.isEmpty() || !erroresSematico.isEmpty();
    }
}
