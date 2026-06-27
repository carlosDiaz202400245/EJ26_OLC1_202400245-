package com.mycompany.golite.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Recorre el AST y arma la tabla de símbolos: variables, parámetros, funciones,
 * métodos, structs y sus campos, cada uno con su rol, tipo, ámbito y posición.
 */
public class TablaSimbolos {

    private final List<String[]> filas = new ArrayList<>();

    /** Devuelve las filas {nombre, rol, tipo, ámbito, línea, columna}. */
    public List<String[]> generar(List<Nodo> raices) {
        filas.clear();
        for (Nodo n : raices) recolectar(n, "global");
        return filas;
    }

    private void recolectar(Nodo n, String ambito) {
        if (n == null) return;

        if (n instanceof NodoStruct) {
            NodoStruct s = (NodoStruct) n;
            fila(s.nombre, "struct", s.nombre, "global", s.linea, s.columna);
            for (NodoDeclVar c : s.campos) {
                fila(c.nombre, "campo", c.tipo, s.nombre, c.linea, c.columna);
            }
        } else if (n instanceof NodoFuncion) {
            NodoFuncion f = (NodoFuncion) n;
            boolean metodo = f.receptorTipo != null;
            String tipo = (f.tipoRetorno != null) ? f.tipoRetorno : "void";
            fila(f.nombre, metodo ? "metodo" : "funcion", tipo,
                 metodo ? f.receptorTipo : "global", f.linea, f.columna);

            String scope = (metodo ? f.receptorTipo + "." : "") + f.nombre;
            if (metodo) {
                fila(f.receptorNombre, "receptor", f.receptorTipo, scope, f.linea, f.columna);
            }
            for (NodoDeclVar p : f.parametros) {
                fila(p.nombre, "parametro", p.tipo, scope, p.linea, p.columna);
            }
            recolectarBloque(f.cuerpo, scope);
        } else if (n instanceof NodoDeclVar) {
            NodoDeclVar v = (NodoDeclVar) n;
            fila(v.nombre, "variable", v.tipo != null ? v.tipo : "inferido",
                 ambito, v.linea, v.columna);
        } else if (n instanceof NodoBloque) {
            recolectarBloque((NodoBloque) n, ambito);
        } else if (n instanceof NodoIf) {
            NodoIf i = (NodoIf) n;
            recolectar(i.bloqueThen, ambito);
            if (i.bloqueElse != null) recolectar(i.bloqueElse, ambito);
        } else if (n instanceof Nodofor) {
            Nodofor f = (Nodofor) n;
            if (f.init != null) recolectar(f.init, ambito);
            recolectar(f.bloque, ambito);
        } else if (n instanceof NodoSwitch) {
            NodoSwitch sw = (NodoSwitch) n;
            for (NodoCaso c : sw.casos) {
                for (Nodo s : c.cuerpo) recolectar(s, ambito);
            }
        }
    }

    private void recolectarBloque(NodoBloque b, String ambito) {
        if (b == null) return;
        for (Nodo s : b.sentencias) recolectar(s, ambito);
    }

    private void fila(String nombre, String rol, String tipo, String ambito, int linea, int columna) {
        filas.add(new String[]{
            nombre, rol, (tipo != null ? tipo : "-"), ambito,
            String.valueOf(linea), String.valueOf(columna)
        });
    }
}
