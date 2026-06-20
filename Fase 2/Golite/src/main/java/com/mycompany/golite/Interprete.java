package com.mycompany.golite;

import com.mycompany.golite.ast.Nodo;
import com.mycompany.golite.gui.ConsolePanel;
import com.mycompany.golite.ast.NodoPrintln;
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

        for (Nodo nodo : nodos) {
            if (nodo == null) continue;
            try {
                nodo.ejecutar(entornoGlobal);
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                consola.println(msg != null ? msg : "Error desconocido.");
                // Agregar a errores semánticos si no fue agregado ya
                if (msg != null && msg.contains("[Error Semántico]")) {
                    Errores.agregarSematico(msg, 0, 0);
                }
            }
        }
    }
}