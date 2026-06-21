package com.mycompany.golite;

import com.mycompany.golite.ast.Nodo;
import com.mycompany.golite.gui.ConsolePanel;
import com.mycompany.golite.ast.NodoPrintln;
import com.mycompany.golite.ast.NodoFuncion;
import com.mycompany.golite.ast.NodoLlamada;
import java.util.ArrayList;
import java.util.List;

/**
 * Recorre y ejecuta el AST generado por el parser.
 */
public class Interprete {

    private final ConsolePanel consola;

    public Interprete(ConsolePanel consola) {
        this.consola = consola;
        // Pasar la consola al NodoPrintln para que imprima ahí
        NodoPrintln.consola = consola;
    }

    /**
     * 
     * @param nodos lista de nodos generada por el parser
     */
    public void ejecutar(List<Nodo> nodos) {
        Entorno entornoGlobal = new Entorno();

        // 1) Pre-pass: registrar todas las funciones (permite llamarlas
        //    aunque se declaren después, y soporta recursión).
        for (Nodo nodo : nodos) {
            if (nodo instanceof NodoFuncion) {
                nodo.ejecutar(entornoGlobal);
            }
        }

        // 2) Ejecutar las sentencias de nivel superior (todo lo que no es función).
        for (Nodo nodo : nodos) {
            if (nodo == null || nodo instanceof NodoFuncion) continue;
            ejecutarNodo(nodo, entornoGlobal);
        }

        // 3) Punto de entrada estilo Go: si existe main(), invocarla.
        if (entornoGlobal.obtenerFuncion("main") != null) {
            ejecutarNodo(new NodoLlamada("main", new ArrayList<>(), 0, 0), entornoGlobal);
        }
    }

    /** Ejecuta un nodo capturando errores semánticos de tiempo de ejecución. */
    private void ejecutarNodo(Nodo nodo, Entorno entorno) {
        try {
            nodo.ejecutar(entorno);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            consola.println(msg != null ? msg : "Error desconocido.");
            if (msg != null && msg.contains("[Error Semántico]")) {
                Errores.agregarSematico(msg, 0, 0);
            }
        }
    }
}